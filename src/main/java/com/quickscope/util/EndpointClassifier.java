package com.quickscope.util;

import com.quickscope.api.models.Domain;

import java.util.Arrays;
import java.util.List;

/**
 * Utility class to classify endpoints by type
 */
public class EndpointClassifier {
	
	/**
	 * Possible endpoint types
	 */
	public enum EndpointType {
		WEB_URL,             // Standard URLs (example.com)
		WEB_WILDCARD,        // Wildcard domains (*.example.com)
		WEB_API,             // API endpoints (api.example.com)
		WEB_IP_RANGE,        // IP addresses/ranges
		MOBILE_APP,          // Mobile app identifiers
		DESKTOP_APP,         // Desktop app references
		DESCRIPTIVE,         // Generic descriptions
		OUT_OF_SCOPE,        // Items marked as out of scope
		UNKNOWN              // Unclassified
	}
	
	/**
	 * Classify a domain based on its endpoint and type
	 * 
	 * @param domain The domain to classify
	 * @return The endpoint type
	 */
	public static EndpointType classifyDomain(Domain domain) {
		// First check if it's out of scope based on tier
		if (isTierOutOfScope(domain)) {
			return EndpointType.OUT_OF_SCOPE;
		}
		
		String endpoint = domain.getEndpoint();
		String type = domain.getType() != null ? domain.getType().getValue() : null;

		// Check for specific mobile/platform types first
		if (type != null) {
			String typeLower = type.toLowerCase();
			// Explicitly handle Android, iOS, and other mobile platforms
			if (typeLower.equals("android") || 
				typeLower.equals("ios") || 
				typeLower.equals("mobile") ||
				typeLower.contains("app")) {
				return EndpointType.MOBILE_APP;
			}
		}
		
		// Check for app store URLs next
		if (endpoint != null) {
			String lowerEndpoint = endpoint.toLowerCase();
			if (lowerEndpoint.contains("apps.apple.com") || 
				lowerEndpoint.contains("itunes.apple.com") || 
				lowerEndpoint.contains("play.google.com") || 
				lowerEndpoint.contains("market.android.com") ||
				lowerEndpoint.contains("/store/apps/") || 
				lowerEndpoint.contains("/in/developer/")) {
				return EndpointType.MOBILE_APP;
			}
		}
		
		// Always classify items with URL, Wildcard, API, or IP Range type as web
		// even if they don't have valid URLs as we later display them in the "Web"
		// column
		if (type != null) {
			String typeLower = type.toLowerCase();
			if (typeLower.equals("url") ||
					typeLower.equals("wildcard") ||
					typeLower.equals("api") ||
					typeLower.equals("iprange")) {

				// This will be displayed in the Web tab regardless of content
				if (typeLower.equals("url")) return EndpointType.WEB_URL;
				if (typeLower.equals("wildcard")) return EndpointType.WEB_WILDCARD;
				if (typeLower.equals("api")) return EndpointType.WEB_API;
				if (typeLower.equals("iprange")) return EndpointType.WEB_IP_RANGE;
			}
		}
		
		// Fallback to classifying based on endpoint content
		return classifyEndpoint(endpoint);
	}
	
	/**
	 * Check if the domain's tier indicates it's out of scope
	 */
	private static boolean isTierOutOfScope(Domain domain) {
		if (domain.getTier() != null) {
			String tierValue = domain.getTier().getValue();
			return tierValue != null && 
				   tierValue.toLowerCase().contains("out of scope");
		}
		return false;
	}
	
	/**
	 * Classify an endpoint based on its content
	 */
	private static EndpointType classifyEndpoint(String endpoint) {
		if (endpoint == null || endpoint.isEmpty()) {
			return EndpointType.UNKNOWN;
		}

		// Check for app store URLs first (highest priority)
		if (endpoint.toLowerCase().contains("apps.apple.com") ||
				endpoint.toLowerCase().contains("itunes.apple.com") ||
				endpoint.toLowerCase().contains("play.google.com") ||
				endpoint.toLowerCase().contains("market.android.com")) {
			return EndpointType.MOBILE_APP; // Categorize as mobile app instead of web
		}
		
		// Check for URL patterns
		if (endpoint.contains(".") && !endpoint.contains(" ")) {
			if (endpoint.startsWith("*.")) {
				return EndpointType.WEB_WILDCARD;
			}
			
			if (endpoint.matches(".*\\.(com|org|net|io|app|co|gov|edu).*")) {
				// Contains common TLDs
				return EndpointType.WEB_URL;
			}
			
			if (endpoint.toLowerCase().contains("api.")) {
				return EndpointType.WEB_API;
			}
		}
		
		// Check for IP addresses
		if (endpoint.matches("^(?:[0-9]{1,3}\\.){3}[0-9]{1,3}$")) {
			return EndpointType.WEB_IP_RANGE;
		}
		
		// Check for mobile app identifiers
		if (endpoint.matches("^[a-zA-Z][a-zA-Z0-9_]*(\\.[a-zA-Z][a-zA-Z0-9_]*)+$")) {
			return EndpointType.MOBILE_APP;
		}
		
		// Check for desktop app references
		if (endpoint.toLowerCase().contains("desktop") || 
			endpoint.toLowerCase().contains("app") || 
			endpoint.toLowerCase().contains("client")) {
			return EndpointType.DESKTOP_APP;
		}
		
		// Default to descriptive
		return EndpointType.DESCRIPTIVE;
	}
	
	/**
	 * Generate a human-readable category name for an endpoint type
	 */
	public static String getCategoryName(EndpointType type) {
		switch (type) {
			case WEB_URL: return "Web URLs";
			case WEB_WILDCARD: return "Wildcard Domains";
			case WEB_API: return "API Endpoints";
			case WEB_IP_RANGE: return "IP Ranges";
			case MOBILE_APP: return "Mobile Applications";
			case DESKTOP_APP: return "Desktop Applications";
			case DESCRIPTIVE: return "Other Items";
			case OUT_OF_SCOPE: return "Out of Scope Items";
			case UNKNOWN: return "Uncategorized";
			default: return "Miscellaneous";
		}
	}
	
	/**
	 * Get a list of all web-related endpoint types
	 */
	public static List<EndpointType> getWebEndpointTypes() {
		return Arrays.asList(
			EndpointType.WEB_URL, 
			EndpointType.WEB_WILDCARD,
			EndpointType.WEB_API,
			EndpointType.WEB_IP_RANGE
		);
	}
}