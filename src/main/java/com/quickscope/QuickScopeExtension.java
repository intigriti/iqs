package com.quickscope;

import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;
import burp.api.montoya.extension.ExtensionUnloadingHandler;
import burp.api.montoya.logging.Logging;
import burp.api.montoya.ui.UserInterface;
import burp.api.montoya.ui.menu.MenuItem;

import com.quickscope.config.ExtensionConfig;
import com.quickscope.ui.ConfigPanel;

import javax.swing.*;
import java.awt.*;

/**
 * Main extension class for Intigriti Quick Scope
 * This extension helps users import scopes from Intigriti's bug bounty programs
 * using the latest Intigriti Researcher API
 */
public class QuickScopeExtension implements BurpExtension, ExtensionUnloadingHandler {

	private static final String EXTENSION_NAME = "Quick Scope";
	
	private MontoyaApi api;
	private ExtensionConfig config;
	private JPanel mainPanel;
	private ConfigPanel configPanel;
	
	@Override
	public void initialize(MontoyaApi api) {
		this.api = api;
		
		// Register the extension
		api.extension().setName(EXTENSION_NAME);
		
		// Register this class as an extension unloading handler
		api.extension().registerUnloadingHandler(this);
		
		// Initialize the logger
		Logging logging = api.logging();
		logging.logToOutput("Quick Scope extension is starting up");
		
		try {
			// Initialize configuration
			config = new ExtensionConfig(api);
			
			// Create UI
			SwingUtilities.invokeLater(() -> {
				try {
					createUI();
				} catch (Exception e) {
					logging.logToError("Error creating UI: " + e.getMessage());
					e.printStackTrace();
				}
			});
			
			logging.logToOutput("Quick Scope extension loaded successfully");
		} catch (Exception e) {
			logging.logToError("Error initializing Quick Scope: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	/**
	 * Creates the user interface for the extension
	 */
	private void createUI() {
		UserInterface userInterface = api.userInterface();
		
		// Create main panel
		mainPanel = new JPanel(new BorderLayout());
		
		// Create config panel
		configPanel = new ConfigPanel(api, config);
		mainPanel.add(configPanel, BorderLayout.CENTER);
		
		// Register the main panel as a tab in Burp's UI
		userInterface.registerSuiteTab(EXTENSION_NAME, mainPanel);
		
		// Add a context menu item
		// MenuItem menuItem = userInterface.createMenuItemFor("Import to Quick Scope");
		MenuItem menuItem = MenuItem.basicMenuItem("Import to Quick Scope");
		// TODO: Add action for context menu??
	}
	
	@Override
	public void extensionUnloaded() {
		// Clean up resources when the extension is unloaded
		api.logging().logToOutput("Quick Scope extension is being unloaded");
		
		// Save configuration
		if (config != null) {
			config.save();
		}
		
		// Stop any running threads
		if (configPanel != null) {
			configPanel.stop();
		}
	}
}