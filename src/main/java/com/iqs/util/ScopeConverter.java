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

			logging.logToOutput("Is Valid: " + endpoint + "," +
				(validationResult.isValid() ? "valid" : "invalid") +
				"," + validationResult.getReadableType());

			if (!validationResult.isValid()) {
				invalidEndpoints.put(endpoint, validationResult.getReadableType());
				continue;
			}

			boolean include = !isOutOfScopeDomain(domain);
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
	 * Gets the current scope rules
	 * 
	 * @return List of scope rules
	 */
	public List<String> getScopeRules() {
		List<Object[]> rules = advancedScopeUtil.getCurrentScopeRules();
		
		// Convert to readable format for display
		return rules.stream()
			.map(rule -> {
				boolean enabled = (boolean) rule[0];
				String pattern = (String) rule[1];
				String type = (String) rule[2];
				
				return (enabled ? "✓ " : "✗ ") + 
					   (type.equals("include") ? "[+] " : "[-] ") + 
					   pattern;
			})
			.collect(Collectors.toList());
	}
	
	/**
	 * Clears the current scope
	 */
	public void clearScope() {
		advancedScopeUtil.clearScope();
		requirementsManager.resetRules();
	}
	
	/**
	 * Adds a scope change handler
	 * 
	 * @param handler The handler to add
	 */
	public void addScopeChangeHandler(ScopeChangeHandler handler) {
		scope.registerScopeChangeHandler(handler);
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
}