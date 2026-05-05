package com.iqs.util;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.logging.Logging;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.iqs.api.models.ProgramDetails;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 * Manages program requirements like headers, user agents, and rate limiting
 */
public class RequirementsManager {

	private static final String[] KNOWN_PLACEHOLDERS = {
		"{username}",
		"<username>",
		"<researcher>",
		"(username)"
	};

	private static final java.util.regex.Pattern[] PLACEHOLDER_PATTERNS = buildPatterns();

	private static java.util.regex.Pattern[] buildPatterns() {
		java.util.regex.Pattern[] patterns = new java.util.regex.Pattern[KNOWN_PLACEHOLDERS.length];
		for (int i = 0; i < KNOWN_PLACEHOLDERS.length; i++) {
			patterns[i] = java.util.regex.Pattern.compile(
				java.util.regex.Pattern.quote(KNOWN_PLACEHOLDERS[i]),
				java.util.regex.Pattern.CASE_INSENSITIVE
			);
		}
		return patterns;
	}

	private String username;
	private final MontoyaApi api;
	private final Logging logging;

	public RequirementsManager(MontoyaApi api) {
		this.api = api;
		this.logging = api.logging();
	}

	public void setUsername(String username) {
		this.username = username;
	}

	private boolean containsKnownPlaceholder(String value) {
		if (value == null) return false;
		String lower = value.toLowerCase();
		for (String p : KNOWN_PLACEHOLDERS) {
			if (lower.contains(p)) return true;
		}
		return false;
	}

	private void showWarning(String title, String message) {
		SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(
			api.userInterface().swingUtils().suiteFrame(),
			message,
			title,
			JOptionPane.WARNING_MESSAGE
		));
	}

	private String replacePlaceholders(String value) {
		if (value == null || username == null || username.isEmpty()) return value;
		String result = value;
		for (java.util.regex.Pattern p : PLACEHOLDER_PATTERNS) {
			result = p.matcher(result).replaceAll(username);
		}
		return result;
	}

	public boolean applyRequirements(ProgramDetails programDetails) {
		if (programDetails == null || programDetails.getRulesOfEngagement() == null ||
				programDetails.getRulesOfEngagement().getContent() == null ||
				programDetails.getRulesOfEngagement().getContent().getTestingRequirements() == null) {
			return false;
		}

		ProgramDetails.TestingRequirements requirements = programDetails.getRulesOfEngagement().getContent()
				.getTestingRequirements();

		try {
			String projectOptionsJson = api.burpSuite().exportProjectOptionsAsJson();
			JsonObject rootObj = JsonParser.parseString(projectOptionsJson).getAsJsonObject();
			boolean modified = false;

			if (requirements.getUserAgent() != null && !requirements.getUserAgent().isEmpty()) {
				rootObj = addUserAgentRule(rootObj, requirements.getUserAgent());
				modified = true;
			}

			if (requirements.getRequestHeader() != null && !requirements.getRequestHeader().isEmpty()) {
				rootObj = addCustomHeaderRule(rootObj, requirements.getRequestHeader());
				modified = true;
			}

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

	private JsonObject addUserAgentRule(JsonObject rootObj, String userAgent) {
		try {
			if (!rootObj.has("proxy")) {
				rootObj.add("proxy", new JsonObject());
			}
			JsonObject proxy = rootObj.getAsJsonObject("proxy");

			if (!proxy.has("match_replace_rules")) {
				proxy.add("match_replace_rules", new JsonArray());
			}
			JsonArray matchReplaceRules = proxy.getAsJsonArray("match_replace_rules");

			JsonObject rule = new JsonObject();
			rule.addProperty("enabled", true);
			rule.addProperty("is_simple_match", false);
			rule.addProperty("rule_type", "request_header");
			rule.addProperty("string_match", "^User-Agent:.*$");
			rule.addProperty("string_replace", "User-Agent: " + replacePlaceholders(userAgent));
			rule.addProperty("comment", "Added by IQS (Intigriti Quick Scope)");

			boolean ruleExists = false;
			for (int i = 0; i < matchReplaceRules.size(); i++) {
				JsonObject existingRule = matchReplaceRules.get(i).getAsJsonObject();
				if (existingRule.has("string_match") &&
						existingRule.get("string_match").getAsString().equals("^User-Agent:.*$")) {
					matchReplaceRules.set(i, rule);
					ruleExists = true;
					break;
				}
			}

			if (!ruleExists) {
				matchReplaceRules.add(rule);
			}

			return rootObj;
		} catch (Exception e) {
			logging.logToError("Failed to add User-Agent rule: " + e.getMessage());
			return rootObj;
		}
	}

	private JsonObject addCustomHeaderRule(JsonObject rootObj, String headerSpec) {
		try {
			String[] parts = headerSpec.split(":", 2);
			if (parts.length != 2) {
				logging.logToError("Invalid header format: " + headerSpec);
				return rootObj;
			}

			String headerName = parts[0].trim();
			String rawValue = parts[1].trim();
			String headerValue = replacePlaceholders(rawValue);

			if (containsKnownPlaceholder(rawValue) && (username == null || username.isEmpty())) {
				logging.logToError("Custom header requires a username but none is saved: " + headerSpec);
				showWarning(
					"Username required",
					"This program requires a custom request header containing your Intigriti " +
					"username, but no username is saved.\n\n" +
					"The header rule has been added, but the placeholder was left unresolved.\n" +
					"Set your username in the configuration panel and reload the program."
				);
			}

			if (!rootObj.has("proxy")) {
				rootObj.add("proxy", new JsonObject());
			}
			JsonObject proxy = rootObj.getAsJsonObject("proxy");

			if (!proxy.has("match_replace_rules")) {
				proxy.add("match_replace_rules", new JsonArray());
			}
			JsonArray matchReplaceRules = proxy.getAsJsonArray("match_replace_rules");

			JsonObject rule = new JsonObject();
			rule.addProperty("enabled", true);
			rule.addProperty("is_simple_match", false);
			rule.addProperty("rule_type", "request_header");
			rule.addProperty("string_match", "^" + headerName + ":.*$");
			rule.addProperty("string_replace", headerName + ": " + headerValue);
			rule.addProperty("comment", "Added by IQS (Intigriti Quick Scope)");

			boolean ruleExists = false;
			for (int i = 0; i < matchReplaceRules.size(); i++) {
				JsonObject existingRule = matchReplaceRules.get(i).getAsJsonObject();
				if (existingRule.has("string_match") &&
						existingRule.get("string_match").getAsString().equals("^" + headerName + ":.*$")) {
					matchReplaceRules.set(i, rule);
					ruleExists = true;
					break;
				}
			}

			if (!ruleExists) {
				matchReplaceRules.add(rule);
			}

			return rootObj;
		} catch (Exception e) {
			logging.logToError("Failed to add custom header rule: " + e.getMessage());
			return rootObj;
		}
	}

	public boolean resetRules() {
		try {
			String projectOptionsJson = api.burpSuite().exportProjectOptionsAsJson();
			JsonObject rootObj = JsonParser.parseString(projectOptionsJson).getAsJsonObject();
			boolean modified = false;

			if (!rootObj.has("proxy")) {
				rootObj.add("proxy", new JsonObject());
			}
			JsonObject proxy = rootObj.getAsJsonObject("proxy");

			if (!proxy.has("match_replace_rules")) {
				proxy.add("match_replace_rules", new JsonArray());
			}
			JsonArray matchReplaceRules = proxy.getAsJsonArray("match_replace_rules");
			JsonArray newRules = new JsonArray();

			for (JsonElement element : matchReplaceRules) {
				if (element.isJsonObject()) {
					JsonObject rule = element.getAsJsonObject();

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

			proxy.add("match_replace_rules", newRules);

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