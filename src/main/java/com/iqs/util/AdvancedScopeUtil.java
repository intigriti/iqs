package com.iqs.util;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.logging.Logging;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import com.iqs.api.models.Domain;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Utility class for manipulating advanced scope settings using project options JSON
 * as Montoya API Scope class does not provide a direct option to add advanced scope
 * rules
 */
public class AdvancedScopeUtil {

	private final MontoyaApi api;
	private final Logging logging;
	private final Gson gson;
	
	/**
	 * Create a new AdvancedScopeUtil instance
	 */
	public AdvancedScopeUtil(MontoyaApi api) {
		this.api = api;
		this.logging = api.logging();
		this.gson = new GsonBuilder().setPrettyPrinting().create();
	}

	/**
	 * Add a target specifically to the include scope
	 * 
	 * @param target The target domain to include
	 * @param include Whether to include or exclude the target
	 * @return true if successful
	 */
	public boolean addTargetToScope(Domain target, boolean include) {
		String endpoint = target.getEndpoint();
		if (endpoint == null || endpoint.isEmpty()) {
			return false;
		}

		try {
			// Process the endpoint into a proper URL pattern
			String urlPattern = generateRegexPattern(endpoint);
			if (urlPattern == null) {
				return false;
			}

			logging.logToOutput("Adding to scope: " + urlPattern);
			return addScopeRule(urlPattern, true, include);
		} catch (Exception e) {
			logging.logToError("Error adding target to scope: " + endpoint + "," + e.getMessage());
			return false;
		}
	}

	/**
	 * Process an endpoint into a regex pattern suitable for scope
	 * 
	 * @param endpoint The endpoint to process
	 * @return A regex pattern suitable for scope rules
	 */
	public String generateRegexPattern(String endpoint) {
		// Normalize endpoint
		endpoint = endpoint.strip();
		if (!endpoint.startsWith("http://") && !endpoint.startsWith("https://") && endpoint.contains(".")) {
			endpoint = "https://" + endpoint;
		}

		try {
			java.net.URL url = new java.net.URL(endpoint);
			String host = url.getHost();

			// Handle different domain types
			if (isMobileUrl(endpoint)) {
				return "# Mobile App: " + endpoint;
			} else if (host.startsWith("*.")) {
				// Wildcard subdomain
				String domain = host.substring(2);
				return "^http(s)?:\\/\\/(.*\\.)?" + domain.replace(".", "\\.") + "(\\/.*)?$";
			} else {
				// Regular domain
				return "^http(s)?:\\/\\/" + host.replace(".", "\\.").replaceAll(",\\s*", "|") + "(\\/.*)?$";
			}
		} catch (Exception e) {

			// Not a valid domain, return a comment
			return "# Non-URL: " + endpoint;
		}
	}

	/**
	 * Check if a URL is for an app store
	 */
	private static boolean isMobileUrl(String url) {
		if (url == null) return false;
		String lower = url.toLowerCase();
		return lower.contains("apps.apple.com") || 
			lower.contains("play.google.com") ||
			lower.contains("/store/apps/") || 
			lower.contains("/in/developer/") ||
			lower.contains("itunes.apple");
	}
	
	/**
	 * Add a regex pattern to the target scope
	 * 
	 * @param regexPattern The regex pattern to add
	 * @param enabled Whether the rule is enabled
	 * @param include Whether to include or exclude the pattern
	 * @return true if successful
	 */
	public boolean addScopeRule(String regexPattern, boolean enabled, boolean include) {
		try {
			// Get current project options
			String projectOptionsJson = api.burpSuite().exportProjectOptionsAsJson();
			logging.logToOutput("Exported project options JSON");
			
			// Parse the JSON
			logging.logToOutput("Parsing project options JSON");
			JsonObject projectOptions = JsonParser.parseString(projectOptionsJson).getAsJsonObject();
			logging.logToOutput("Successfully parsed project options JSON");
			
			// Get or create the target scope section
			JsonObject targetScope = getOrCreateTargetScope(projectOptions);
			
			// Get the advanced scope rules
			JsonArray advancedModeRules = targetScope.getAsJsonArray(include ? "include" : "exclude");
			
			// Create new rule
			JsonObject newRule = new JsonObject();
			newRule.addProperty("enabled", enabled);
			newRule.addProperty("host", regexPattern);
			newRule.addProperty("protocol", "any");
			
			// Add the rule to the array
			advancedModeRules.add(newRule);
			
			// Prepare the JSON for import
			String updatedJson = gson.toJson(projectOptions);
			
			// Log the relevant section for debugging
			try {
				JsonObject debugScope = JsonParser.parseString(updatedJson)
					.getAsJsonObject()
					.getAsJsonObject("target")
					.getAsJsonObject("scope");
			} catch (Exception e) {
				logging.logToOutput("Could not extract scope section for debug output");
			}
			
			// Import the updated project options
			logging.logToOutput("Importing updated project options");
			api.burpSuite().importProjectOptionsFromJson(updatedJson);

			return true;
		} catch (Exception e) {
			logging.logToError("Error adding scope rule: " + e.getMessage());
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Clear all scope rules and reset to a new set of rules
	 * 
	 * @param includePatterns Patterns to include in scope
	 * @param excludePatterns Patterns to exclude from scope
	 * @return true if successful
	 */
	public boolean resetScopeRules(List<String> includePatterns, List<String> excludePatterns) {
		try {
			// Get current project options
			String projectOptionsJson = api.burpSuite().exportProjectOptionsAsJson();
			
			// Parse the JSON
			JsonObject projectOptions = JsonParser.parseString(projectOptionsJson).getAsJsonObject();
			
			// Get or create the target scope section
			JsonObject targetScope = getOrCreateTargetScope(projectOptions);
			
			// Create new rules array
			JsonArray advancedModeRules = new JsonArray();
			
			// Add include patterns
			for (String pattern : includePatterns) {
				JsonObject rule = new JsonObject();
				rule.addProperty("enabled", true);
				rule.addProperty("host", pattern);
				rule.addProperty("protocol", "any");
				advancedModeRules.add(rule);
			}
			
			// Add exclude patterns
			for (String pattern : excludePatterns) {
				JsonObject rule = new JsonObject();
				rule.addProperty("enabled", true);
				rule.addProperty("host", pattern);
				rule.addProperty("protocol", "exclude");
				advancedModeRules.add(rule);
			}
			
			// Update the JSON
			targetScope.add("include", advancedModeRules);
			targetScope.add("exclude", advancedModeRules);
			
			// Enable advanced mode
			targetScope.addProperty("advanced_mode", true);
			
			// Prepare the JSON for import
			String updatedJson = gson.toJson(projectOptions);
			
			// Import the updated project options
			logging.logToOutput("Importing updated project options");
			api.burpSuite().importProjectOptionsFromJson(updatedJson);
			
			logging.logToOutput("Reset scope rules with " + includePatterns.size() + 
								" include and " + excludePatterns.size() + " exclude rules");
			return true;
		} catch (Exception e) {
			logging.logToError("Error resetting scope rules: " + e.getMessage());
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Clear all scope rules
	 * 
	 * @return true if successful
	 */
	public boolean clearScope() {
		return resetScopeRules(new ArrayList<>(), new ArrayList<>());
	}
	
	/**
	 * Get all current scope rules
	 * 
	 * @return A list of current scope rules as [enabled, pattern, type]
	 */
	public List<Object[]> getCurrentScopeRules() {
		List<Object[]> rules = new ArrayList<>();
		
		try {
			// Get current project options
			String projectOptionsJson = api.burpSuite().exportProjectOptionsAsJson();
			
			// Parse the JSON
			JsonObject projectOptions = JsonParser.parseString(projectOptionsJson).getAsJsonObject();
			
			try {
				// Try to get the target scope section if it exists
				JsonObject target = null;
				if (projectOptions.has("target")) {
					target = projectOptions.getAsJsonObject("target");
				}
				
				if (target != null && target.has("scope")) {
					JsonObject targetScope = target.getAsJsonObject("scope");
					
					// Check if advanced mode is enabled
					boolean advancedMode = targetScope.has("advanced_mode") ? 
						targetScope.get("advanced_mode").getAsBoolean() : false;
						
					// Log advanced mode status
					logging.logToOutput("Advanced scope mode is " + (advancedMode ? "enabled" : "disabled"));
					
					// Get the advanced scope rules
					if (targetScope.has("include")) {
						JsonArray advancedModeRules = targetScope.getAsJsonArray("include");
						
						for (JsonElement element : advancedModeRules) {
							JsonObject rule = element.getAsJsonObject();
							boolean enabled = rule.get("enabled").getAsBoolean();
							String pattern = rule.get("host").getAsString();
							String type = rule.get("protocol").getAsString();
							
							rules.add(new Object[]{enabled, pattern, type});
						}
					} else {
						logging.logToOutput("No advanced scope rules found");
					}
				} else {
					logging.logToOutput("No scope configuration found");
				}
			} catch (Exception e) {
				logging.logToError("Error processing scope rules: " + e.getMessage());
				e.printStackTrace();
			}
		} catch (Exception e) {
			logging.logToError("Error getting scope rules: " + e.getMessage());
			e.printStackTrace();
		}
		
		return rules;
	}
	
	/**
	 * Initialize or get the target scope section from project options
	 * 
	 * @param projectOptions The project options JSON object
	 * @return The target scope JSON object
	 */
	private JsonObject getOrCreateTargetScope(JsonObject projectOptions) {
		// Ensure target object exists
		if (!projectOptions.has("target")) {
			projectOptions.add("target", new JsonObject());
		}
		JsonObject targetObject = projectOptions.getAsJsonObject("target");
		
		// Ensure scope object exists
		if (!targetObject.has("scope")) {
			targetObject.add("scope", new JsonObject());
		}
		JsonObject targetScope = targetObject.getAsJsonObject("scope");
		
		// Ensure include array exists
		if (!targetScope.has("include")) {
			targetScope.add("include", new JsonArray());
		}
		
		// Enable advanced mode
		targetScope.addProperty("advanced_mode", true);
		
		return targetScope;
	}

	/**
	 * Validate if an endpoint is a valid domain, URL, wildcard pattern, or identify
	 * special cases
	 * 
	 * @param endpoint The endpoint to validate
	 * @return ValidationResult containing validation status and type
	 */
	public ValidationResult validateEndpoint(String endpoint) {
		if (endpoint == null || endpoint.isEmpty()) {
			return new ValidationResult(false, ValidationResult.Type.EMPTY);
		}

		// Check for mobile apps
		if (endpoint.toLowerCase().contains("play.google.com") ||
				endpoint.toLowerCase().contains("apps.apple.com") ||
				endpoint.toLowerCase().contains("appstore")) {
			return new ValidationResult(false, ValidationResult.Type.APP_STORE_LINK);
		}

		// Check for valid URL or domain pattern
		if (isValidUrlOrDomain(endpoint)) {
			return new ValidationResult(true, ValidationResult.Type.VALID_URL_OR_DOMAIN);
		}

		// Check for mobile app identifier (e.g., com.company.app)
		if (endpoint.matches("^[a-zA-Z][a-zA-Z0-9_]*(\\.[a-zA-Z][a-zA-Z0-9_]*)+$")) {
			return new ValidationResult(false, ValidationResult.Type.MOBILE_APP_ID);
		}

		// Check for product name (contains spaces, no dots or slashes)
		if (endpoint.contains(" ") && !endpoint.contains("/") && !endpoint.contains(".")) {
			return new ValidationResult(false, ValidationResult.Type.PRODUCT_NAME);
		}

		// Default case - some other descriptive text
		return new ValidationResult(false, ValidationResult.Type.DESCRIPTIVE_TEXT);
	}

	/**
	 * Helper method to check if string is a valid URL or domain
	 */
	private boolean isValidUrlOrDomain(String input) {
		// Normalize endpoint for checking
		String normalizedInput = input;
		if (!normalizedInput.startsWith("http://") && !normalizedInput.startsWith("https://") &&
			normalizedInput.contains(".")) {
			normalizedInput = "https://" + normalizedInput;
		}

		try {
			// Try to parse as URL
			new java.net.URL(normalizedInput);
			return true;
		} catch (Exception e) {
			// Check for domain patterns
			normalizedInput = input; // Use original for domain checks

			// Check for wildcard domain pattern (*.example.com)
			if (normalizedInput.startsWith("*.") && normalizedInput.indexOf('.', 2) > 0) {
				return true;
			}

			// Check for simple domain pattern (example.com)
			if (normalizedInput.contains(".") &&
					normalizedInput.matches("^[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
				return true;
			}

			// IP address pattern
			if (normalizedInput.matches("^(?:[0-9]{1,3}\\.){3}[0-9]{1,3}$")) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Validation result class
	 */
	public static class ValidationResult {
		private final boolean valid;
		private final Type type;

		public enum Type {
			VALID_URL_OR_DOMAIN,
			EMPTY,
			MOBILE_APP_ID,
			APP_STORE_LINK,
			PRODUCT_NAME,
			DESCRIPTIVE_TEXT
		}

		public ValidationResult(boolean valid, Type type) {
			this.valid = valid;
			this.type = type;
		}

		public boolean isValid() {
			return valid;
		}

		public Type getType() {
			return type;
		}

		public String getReadableType() {
			switch (type) {
				case VALID_URL_OR_DOMAIN:
					return "Valid URL or domain";
				case EMPTY:
					return "Empty";
				case MOBILE_APP_ID:
					return "Mobile app ID";
				case APP_STORE_LINK:
					return "App store link";
				case PRODUCT_NAME:
					return "Product name";
				case DESCRIPTIVE_TEXT:
					return "Descriptive text";
				default:
					return "Unknown";
			}
		}
	}
}