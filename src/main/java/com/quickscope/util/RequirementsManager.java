package com.quickscope.util;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.logging.Logging;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.quickscope.api.models.ProgramDetails;

/**
 * Manages program requirements like headers, user agents, and rate limiting
 */
public class RequirementsManager {

	private final MontoyaApi api;
	private final Logging logging;

	public RequirementsManager(MontoyaApi api) {
		this.api = api;
		this.logging = api.logging();
	}

	/**
	 * Applies all program requirements from the rules of engagement
	 * 
	 * @param programDetails The program details containing rules of engagement
	 * @return True if requirements were successfully applied
	 */
	public boolean applyRequirements(ProgramDetails programDetails) {
		if (programDetails == null || programDetails.getRulesOfEngagement() == null ||
				programDetails.getRulesOfEngagement().getContent() == null ||
				programDetails.getRulesOfEngagement().getContent().getTestingRequirements() == null) {
			return false;
		}

		ProgramDetails.TestingRequirements requirements = programDetails.getRulesOfEngagement().getContent()
				.getTestingRequirements();

		try {
			// Export current project options
			String projectOptionsJson = api.burpSuite().exportProjectOptionsAsJson();
			JsonObject rootObj = JsonParser.parseString(projectOptionsJson).getAsJsonObject();
			boolean modified = false;

			// Apply User-Agent header if specified
			if (requirements.getUserAgent() != null && !requirements.getUserAgent().isEmpty()) {
				rootObj = addUserAgentRule(rootObj, requirements.getUserAgent());
				modified = true;
			}

			// Apply custom request header if specified
			if (requirements.getRequestHeader() != null && !requirements.getRequestHeader().isEmpty()) {
				rootObj = addCustomHeaderRule(rootObj, requirements.getRequestHeader());
				modified = true;
			}

			// Import the modified project options if changes were made
			if (modified) {
				logging.logToOutput("Applying program requirements from rules of engagement");
				api.burpSuite().importProjectOptionsFromJson(rootObj.toString());
				return true;
			}

			return false;
		} catch (Exception e) {
			logging.logToError("Error applying requirements: " + e.getMessage());
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Adds a match and replace rule for the User-Agent header
	 * 
	 * @param rootObj   The root JSON object of the project options
	 * @param userAgent The user agent to set
	 * @return The modified JSON object
	 */
	private JsonObject addUserAgentRule(JsonObject rootObj, String userAgent) {
		try {
			// Ensure proxy section exists
			if (!rootObj.has("proxy")) {
				rootObj.add("proxy", new JsonObject());
			}
			JsonObject proxy = rootObj.getAsJsonObject("proxy");

			// Ensure match_replace_rules section exists
			if (!proxy.has("match_replace_rules")) {
				proxy.add("match_replace_rules", new JsonArray());
			}
			JsonArray matchReplaceRules = proxy.getAsJsonArray("match_replace_rules");

			// Create new rule
			JsonObject rule = new JsonObject();
			rule.addProperty("enabled", true);
			rule.addProperty("is_simple_match", false);
			rule.addProperty("rule_type", "request_header");
			rule.addProperty("string_match", "^User-Agent:.*$");
			rule.addProperty("string_replace", "User-Agent: " + userAgent);
			rule.addProperty("comment", "Added by IQS (Intigriti Quick Scope)");

			// Check if a similar rule already exists
			boolean ruleExists = false;
			for (int i = 0; i < matchReplaceRules.size(); i++) {
				JsonObject existingRule = matchReplaceRules.get(i).getAsJsonObject();
				if (existingRule.has("string_match") &&
						existingRule.get("string_match").getAsString().equals("^User-Agent:.*$")) {
					// Update existing rule
					matchReplaceRules.set(i, rule);
					ruleExists = true;
					break;
				}
			}

			// Add new rule if no similar rule exists
			if (!ruleExists) {
				matchReplaceRules.add(rule);
			}

			return rootObj;
		} catch (Exception e) {
			logging.logToError("Failed to add User-Agent rule: " + e.getMessage());
			return rootObj;
		}
	}

	/**
	 * Adds a match and replace rule for a custom header
	 * 
	 * @param rootObj    The root JSON object of the project options
	 * @param headerSpec The header specification (format: "Name: Value")
	 * @return The modified JSON object
	 */
	private JsonObject addCustomHeaderRule(JsonObject rootObj, String headerSpec) {
		try {
			// Parse header name and value
			String[] parts = headerSpec.split(":", 2);
			if (parts.length != 2) {
				logging.logToError("Invalid header format: " + headerSpec);
				return rootObj;
			}

			String headerName = parts[0].trim();
			String headerValue = parts[1].trim();

			// Ensure proxy section exists
			if (!rootObj.has("proxy")) {
				rootObj.add("proxy", new JsonObject());
			}
			JsonObject proxy = rootObj.getAsJsonObject("proxy");

			// Ensure match_replace_rules section exists
			if (!proxy.has("match_replace_rules")) {
				proxy.add("match_replace_rules", new JsonArray());
			}
			JsonArray matchReplaceRules = proxy.getAsJsonArray("match_replace_rules");

			// Create new rule
			JsonObject rule = new JsonObject();
			rule.addProperty("enabled", true);
			rule.addProperty("is_simple_match", false);
			rule.addProperty("rule_type", "request_header");
			rule.addProperty("string_match", "^" + headerName + ":.*$");
			rule.addProperty("string_replace", headerName + ": " + headerValue);
			rule.addProperty("comment", "Added by IQS (Intigriti Quick Scope)");

			// Check if a similar rule already exists
			boolean ruleExists = false;
			for (int i = 0; i < matchReplaceRules.size(); i++) {
				JsonObject existingRule = matchReplaceRules.get(i).getAsJsonObject();
				if (existingRule.has("string_match") &&
						existingRule.get("string_match").getAsString().equals("^" + headerName + ":.*$")) {
					// Update existing rule
					matchReplaceRules.set(i, rule);
					ruleExists = true;
					break;
				}
			}

			// Add new rule if no similar rule exists
			if (!ruleExists) {
				matchReplaceRules.add(rule);
			}

			return rootObj;
		} catch (Exception e) {
			logging.logToError("Failed to add custom header rule: " + e.getMessage());
			return rootObj;
		}
	}

	/**
	 * Removes all enabled match and replace rules
	 * 
	 * @return True if rules were successfully removed
	 */
	public boolean resetRules() {
		try {
			// Export current project options
			String projectOptionsJson = api.burpSuite().exportProjectOptionsAsJson();
			JsonObject rootObj = JsonParser.parseString(projectOptionsJson).getAsJsonObject();
			boolean modified = false;

			// Ensure proxy section exists
			if (!rootObj.has("proxy")) {
				rootObj.add("proxy", new JsonObject());
			}
			JsonObject proxy = rootObj.getAsJsonObject("proxy");

			// Ensure match_replace_rules section exists
			if (!proxy.has("match_replace_rules")) {
				proxy.add("match_replace_rules", new JsonArray());
			}
			JsonArray matchReplaceRules = proxy.getAsJsonArray("match_replace_rules");
			JsonArray newRules = new JsonArray();

			// Keep only disabled rules and those not added by our extension
			for (JsonElement element : matchReplaceRules) {
				if (element.isJsonObject()) {
					JsonObject rule = element.getAsJsonObject();

					// Skip rules that are enabled or added by our extension
					boolean isEnabled = rule.has("enabled") && rule.get("enabled").getAsBoolean();
					boolean isOurRule = rule.has("comment") &&
							rule.get("comment").getAsString().contains("Added by IQS");

					if (!isEnabled || !isOurRule) {
						newRules.add(rule);
					} else {
						modified = true;
					}
				}
			}

			// Replace the rules array
			proxy.add("match_replace_rules", newRules);

			// Import the modified project options if changes were made
			if (modified) {
				logging.logToOutput("Removing program requirements rules");
				api.burpSuite().importProjectOptionsFromJson(rootObj.toString());
				return true;
			}

			return false;
		} catch (Exception e) {
			logging.logToError("Error removing rules: " + e.getMessage());
			e.printStackTrace();
			return false;
		}
	}
}