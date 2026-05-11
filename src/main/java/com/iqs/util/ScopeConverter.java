package com.iqs.util;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.logging.Logging;
import burp.api.montoya.http.HttpService;
import burp.api.montoya.scope.Scope;
import burp.api.montoya.scope.ScopeChange;
import burp.api.montoya.scope.ScopeChangeHandler;

import com.iqs.config.QuickScopeConfig;
import com.iqs.api.models.Domain;
import com.iqs.api.models.ProgramDetails;
import com.iqs.util.RequirementsManager;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Converts Intigriti domains to Burp scope items
 */
public class ScopeConverter {

	private final MontoyaApi api;
	private final Logging logging;
	private final Scope scope;
	private final QuickScopeConfig quickScopeConfig;
	private final AdvancedScopeUtil advancedScopeUtil;
	private final RequirementsManager requirementsManager;
	
	/**
	 * Creates a new scope converter
	 * 
	 * @param api The Montoya API instance
	 */
	public ScopeConverter(MontoyaApi api, QuickScopeConfig quickScopeConfig) {
		this.api = api;
		this.logging = api.logging();
		this.scope = api.scope();
		this.quickScopeConfig = quickScopeConfig;
		this.advancedScopeUtil = new AdvancedScopeUtil(api);
		this.requirementsManager = new RequirementsManager(api);
		this.requirementsManager.setUsername(quickScopeConfig.getUsername());
	}

	public int addDomainsToScope(List<Domain> domains) {
		Map<Domain, Boolean> targets = new java.util.LinkedHashMap<>();
		Map<String, String> invalidEndpoints = new HashMap<>(); // Maps endpoint to reason

		// Validate and classify every domain
		for (Domain domain : domains) {
			String endpoint = domain.getEndpoint();

			AdvancedScopeUtil.ValidationResult validationResult =
				advancedScopeUtil.validateEndpoint(endpoint);

<<<<<<< HEAD
=======
			logging.logToOutput("Is Valid: " + endpoint + "," +
				(validationResult.isValid() ? "valid" : "invalid") +
				"," + validationResult.getReadableType());

>>>>>>> bapp-fork-latest/main
			if (!validationResult.isValid()) {
				invalidEndpoints.put(endpoint, validationResult.getReadableType());
				continue;
			}

			boolean include = !isOutOfScopeDomain(domain);

			if (include) {
				// Only generate scope rules for Url and Wildcard types
				EndpointClassifier.EndpointType endpointType = EndpointClassifier.classifyDomain(domain);
				if (endpointType != EndpointClassifier.EndpointType.WEB_URL &&
						endpointType != EndpointClassifier.EndpointType.WEB_WILDCARD) {
					continue;
				}
			} else {
				String rawType = domain.getType() != null ? domain.getType().getValue() : null;
				if (rawType == null) continue;
				String rawTypeLower = rawType.toLowerCase();
				if (!rawTypeLower.equals("url") && !rawTypeLower.equals("wildcard")) {
					continue;
				}
			}

			targets.put(domain, include);
		}

		return advancedScopeUtil.addTargetsToScope(targets);
	}

	/**
	 * Check if a domain should be considered "out of scope" based on its tier
	 * 
	 * @param domain The domain to check
	 * @return True if it's an "out of scope" domain
	 */
	public boolean isOutOfScopeDomain(Domain domain) {
		if (domain.getTier() != null) {
			String tierValue = domain.getTier().getValue();
			return tierValue != null &&
					tierValue.toLowerCase().contains("out of scope");
		}
		return false;
	}
	
	/**
	 * Clears the current scope
	 */
	public void clearScope() {
		advancedScopeUtil.clearScope();
		requirementsManager.resetRules();
	}

	/**
	 * Add program requirements from rules of engagement
	 * 
	 * @param details The program details
	 * @return True if requirements were applied
	 */
	public boolean addProgramRequirements(ProgramDetails details) {
		return requirementsManager.applyRequirements(details);
	}

	/**
	 * Resets any match-and-replace rules previously added by IQS, then applies
	 * the requirements for the given program.
	 *
	 * @param details The program details
	 * @return True if requirements were applied
	 */
	public boolean replaceProgramRequirements(ProgramDetails details) {
		requirementsManager.resetRules();
		return requirementsManager.applyRequirements(details);
	}
}