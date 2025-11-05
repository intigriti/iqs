package com.quickscope.config;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.persistence.Preferences;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles configuration storage and retrieval for the extension
 */
public class ExtensionConfig {

	private static final String EXT_CFG = "quickscope_cfg";

	private final MontoyaApi api;
	private final Preferences preferences;
	private final Gson gson;
	
	private QuickScopeConfig extCfg;
	
	/**
	 * Creates a new configuration handler
	 * 
	 * @param api The Montoya API instance
	 */
	public ExtensionConfig(MontoyaApi api) {
		this.api = api;
		this.preferences = api.persistence().preferences();
		this.gson = new GsonBuilder().create();
		
		loadConfig();
	}
	
	/**
	 * Loads configuration from Burp's persistent storage
	 */
	private void loadConfig() {
		String JSONCfg = preferences.getString(EXT_CFG);
		if (JSONCfg != null && !JSONCfg.isEmpty()) {
			extCfg = gson.fromJson(JSONCfg, QuickScopeConfig.class);
		} else {
			extCfg = new QuickScopeConfig();
		}
	}
	
	/**
	 * Saves the current configuration to Burp's persistent storage
	 */
	public void save() {
		String JSONCfg = gson.toJson(extCfg);
		preferences.setString(EXT_CFG, JSONCfg);
	}

	// Add this explicit method to save the API key
	public void saveApiKey(String apiKey) {
		extCfg.setApiKey(apiKey);
		save();
	}

	// Add this method to retrieve a saved API key
	public String getApiKey() {
		return extCfg.getApiKey();
	}
	
	/**
	 * Gets the Intigriti API configuration
	 * 
	 * @return The Intigriti configuration
	 */
	public QuickScopeConfig getQuickScopeConfig() {
		return extCfg;
	}
	
	/**
	 * Sets a new Intigriti API configuration
	 * 
	 * @param extensionCfg The new configuration
	 */
	public void setQuickScopeConfig(QuickScopeConfig extensionCfg) {
		this.extCfg = extensionCfg;
		save();
	}
}