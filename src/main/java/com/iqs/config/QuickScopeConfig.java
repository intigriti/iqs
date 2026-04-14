package com.iqs.config;

/**
 * Configuration for Intigriti Quick Scope API access
 */
public class QuickScopeConfig {

	private String username;
	private String apiKey;
	private String apiUrl;
	private boolean onlyFollowing;
	private boolean onlyActive;
	private boolean onlyPrivate;
	
	/**
	 * Creates a new configuration with default values
	 */
	public QuickScopeConfig() {
		this.username = "";
		this.apiKey = "";
		this.apiUrl = "https://api.intigriti.com/external/researcher";
		this.onlyFollowing = false;
		this.onlyActive = true;
		this.onlyPrivate = false;
	}
	
	/**
	 * Creates a new configuration with the specified values
	 * 
	 * @param username The researcher's Intigriti username
	 * @param apiKey The Intigriti API key
	 * @param apiUrl The Intigriti API URL
	 * @param onlyFollowing Whether to only include followed programs
	 * @param onlyActive Whether to only include active programs
	 * @param onlyPrivate Whether to only include private programs
	 */
	public QuickScopeConfig(String username, String apiKey, String apiUrl, boolean onlyFollowing, boolean onlyActive, boolean onlyPrivate) {
		this.username = username;
		this.apiKey = apiKey;
		this.apiUrl = apiUrl;
		this.onlyFollowing = onlyFollowing;
		this.onlyActive = onlyActive;
		this.onlyPrivate = onlyPrivate;
	}

	/**
	 * Gets the researcher's Intigriti username
	 * 
	 * @return The researcher's Intigriti username
	 */
	public String getUsername() {
    	return username;
	}

	/**
	 * Sets the researcher's Intigriti username
	 * 
	 * @param apiKey The researcher's Intigriti username
	 */
	public void setUsername(String username) {
		this.username = username;
	}
	
	/**
	 * Gets the Intigriti API key
	 * 
	 * @return The API key
	 */
	public String getApiKey() {
		return apiKey;
	}
	
	/**
	 * Sets the Intigriti API key
	 * 
	 * @param apiKey The API key
	 */
	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}
	
	/**
	 * Gets the Intigriti API URL
	 * 
	 * @return The API URL
	 */
	public String getApiUrl() {
		return apiUrl;
	}
	
	/**
	 * Sets the Intigriti API URL
	 * 
	 * @param apiUrl The API URL
	 */
	public void setApiUrl(String apiUrl) {
		this.apiUrl = apiUrl;
	}
	
	/**
	 * Checks if only followed programs should be included
	 * 
	 * @return True if only followed programs should be included
	 */
	public boolean isOnlyFollowing() {
		return onlyFollowing;
	}
	
	/**
	 * Sets whether only followed programs should be included
	 * 
	 * @param onlyFollowing Whether only followed programs should be included
	 */
	public void setOnlyFollowing(boolean onlyFollowing) {
		this.onlyFollowing = onlyFollowing;
	}
	
	/**
	 * Checks if only active programs should be included
	 * 
	 * @return True if only active programs should be included
	 */
	public boolean isOnlyActive() {
		return onlyActive;
	}
	
	/**
	 * Sets whether only active programs should be included
	 * 
	 * @param onlyActive Whether only active programs should be included
	 */
	public void setOnlyActive(boolean onlyActive) {
		this.onlyActive = onlyActive;
	}

	/**
	 * Checks if only private programs should be included
	 * 
	 * @return True if only private programs should be included
	 */
	public boolean isOnlyPrivate() {
		return onlyPrivate;
	}
	
	/**
	 * Sets whether only private programs should be included
	 * 
	 * @param onlyPrivate Whether only private programs should be included
	 */
	public void setOnlyPrivate(boolean onlyPrivate) {
		this.onlyPrivate = onlyPrivate;
	}
}