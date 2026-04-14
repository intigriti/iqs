package com.iqs.ui;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.logging.Logging;
import burp.api.montoya.ui.UserInterface;
import burp.api.montoya.ui.swing.SwingUtils;

import com.iqs.api.IntigritiApiClient;
import com.iqs.api.models.Program;
import com.iqs.config.ExtensionConfig;
import com.iqs.config.QuickScopeConfig;
import com.iqs.util.AdvancedScopeUtil;
import com.iqs.util.ScopeConverter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Main configuration panel for the extension
 */
public class ConfigPanel extends JPanel {

	private final MontoyaApi api;
	private final Logging logging;
	private final UserInterface userInterface;
	private final ExtensionConfig config;
	private final QuickScopeConfig quickScopeConfig;
	private final AdvancedScopeUtil advancedScopeUtil;
	private final ScopeConverter scopeConverter;
	private final ExecutorService executorService;
	
	private JTextField searchField;
	private JLabel matchCountLabel;
	private final JTextField usernameField;
	private final JTextField apiKeyField;
	private final JCheckBox onlyActiveCheckBox;
	private final JCheckBox onlyFollowingCheckBox;
	private final JCheckBox onlyPrivateCheckBox;
	private final JButton testConnectionButton;
	private final JButton loadProgramsButton;
	private final JButton clearScopeButton;
	
	private final ProgramTable programTable;
	private final DomainsPanel domainsPanel;
	
	/**
	 * Creates a new configuration panel
	 * 
	 * @param api    The Montoya API instance
	 * @param config The extension configuration
	 */
	public ConfigPanel(MontoyaApi api, ExtensionConfig config) {
		this.api = api;
		this.logging = api.logging();
		this.userInterface = api.userInterface();
		this.config = config;
		this.quickScopeConfig = config.getQuickScopeConfig();
		this.advancedScopeUtil = new AdvancedScopeUtil(api);
		this.scopeConverter = new ScopeConverter(api, quickScopeConfig);
		this.executorService = Executors.newCachedThreadPool();

		// Set up the layout
		setLayout(new BorderLayout());
		setBorder(new EmptyBorder(10, 10, 10, 10));

		// Create components
		JPanel configPanel = new JPanel(new GridBagLayout());
		configPanel.setBorder(new TitledBorder("Intigriti Researcher API Configuration"));

		GridBagConstraints gbc = new GridBagConstraints();

		// Username label + field (left half of row 0)
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 0.0;
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(2, 5, 2, 5);
		configPanel.add(new JLabel("Username:"), gbc);

		gbc.gridx = 1;
		gbc.weightx = 0.3;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(2, 0, 2, 15);
		usernameField = new JTextField(15);
		usernameField.setText(config.getQuickScopeConfig().getUsername());
		usernameField.setToolTipText("Your Intigriti username. This will be auto-inserted into scope headers.");
		configPanel.add(usernameField, gbc);

		// API token label + field (right half of row 0)
		gbc.gridx = 2;
		gbc.weightx = 0.0;
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(2, 5, 2, 5);
		configPanel.add(new JLabel("API Token:"), gbc);

		gbc.gridx = 3;
		gbc.weightx = 0.7;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(2, 5, 2, 5);
		apiKeyField = new JPasswordField(30);
		apiKeyField.setText(config.getQuickScopeConfig().getApiKey());
		configPanel.add(apiKeyField, gbc);

		// Checkboxes
		gbc.gridx = 0;
		gbc.gridy++;
		gbc.gridwidth = 4;
		gbc.weightx = 1.0;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(5, 5, 5, 5);
		onlyActiveCheckBox = new JCheckBox("Only load active programs", quickScopeConfig.isOnlyActive());
		configPanel.add(onlyActiveCheckBox, gbc);

		gbc.gridy++;
		onlyFollowingCheckBox = new JCheckBox("Only load followed programs", quickScopeConfig.isOnlyFollowing());
		configPanel.add(onlyFollowingCheckBox, gbc);

		gbc.gridy++;
		onlyPrivateCheckBox = new JCheckBox("Only load private programs", quickScopeConfig.isOnlyPrivate());
		configPanel.add(onlyPrivateCheckBox, gbc);

		// Buttons
		gbc.gridy++;
		gbc.gridx = 0;
		gbc.gridwidth = 4;
		gbc.weightx = 0.0;
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(5, 2, 5, 5);

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
		testConnectionButton = new JButton("Test Researcher API");
		testConnectionButton.setMargin(new Insets(1, 8, 1, 8));
		buttonPanel.add(testConnectionButton);

		loadProgramsButton = new JButton("Load Programs");
		loadProgramsButton.setMargin(new Insets(1, 8, 1, 8));
		buttonPanel.add(loadProgramsButton);

		configPanel.add(buttonPanel, gbc);

		// Add to the main panel
		add(configPanel, BorderLayout.NORTH);

		// Create split pane for programs and domains
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitPane.setResizeWeight(0.5);

		// Create the programs panel with search and table
		JPanel programsPanel = new JPanel(new BorderLayout());
		programsPanel.setBorder(new TitledBorder("Programs"));

		// Add search panel at the top
		JPanel searchPanel = createSearchPanel();
		programsPanel.add(searchPanel, BorderLayout.NORTH);

		// Create programs table
		programTable = new ProgramTable(api);
		JScrollPane programScrollPane = new JScrollPane(programTable);
		programsPanel.add(programScrollPane, BorderLayout.CENTER);

		// Add to split pane
		splitPane.setLeftComponent(programsPanel);

		// Add domains panel
		domainsPanel = new DomainsPanel(api, quickScopeConfig, advancedScopeUtil, scopeConverter);
		splitPane.setRightComponent(domainsPanel);

		// Add to main panel
		add(splitPane, BorderLayout.CENTER);

		// Scope actions panel
		JPanel scopeActionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		scopeActionsPanel.setBorder(new TitledBorder("Scope Actions"));

		clearScopeButton = new JButton("Reset Scope");
		scopeActionsPanel.add(clearScopeButton);

		add(scopeActionsPanel, BorderLayout.SOUTH);

		// Set up event handlers
		setupEventHandlers();
	}

	/**
	 * Creates a new search bar with enhanced features
	 */
	private JPanel createSearchPanel() {
		JPanel searchPanel = new JPanel(new BorderLayout(5, 0));
		searchPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

		// Create label
		JLabel searchLabel = new JLabel("Filter Programs:");
		searchPanel.add(searchLabel, BorderLayout.WEST);

		// Create search field
		searchField = new JTextField(20);
		searchField.setToolTipText("Filter by program name or handle (Ctrl+F)");

		// Add search icon
		searchField.setMargin(new Insets(2, 24, 2, 2));
		searchField.putClientProperty("JTextField.leadingIcon", createSearchIcon());

		// Create match count label
		matchCountLabel = new JLabel("");
		matchCountLabel.setForeground(new Color(100, 100, 100));
		matchCountLabel.setBorder(new EmptyBorder(0, 10, 0, 5));

		// Add clear button
		JButton clearButton = new JButton("✕");
		clearButton.setToolTipText("Clear search");
		clearButton.setBorderPainted(false);
		clearButton.setContentAreaFilled(false);
		clearButton.setFocusPainted(false);
		clearButton.setMargin(new Insets(0, 0, 0, 0));
		clearButton.addActionListener(e -> {
			searchField.setText("");
			applyProgramFilter("");
		});

		// Add search field and clear button to panel
		JPanel fieldPanel = new JPanel(new BorderLayout());
		fieldPanel.add(searchField, BorderLayout.CENTER);
		fieldPanel.add(clearButton, BorderLayout.EAST);
		
		// Create a container for field and match count
		JPanel searchContainer = new JPanel(new BorderLayout());
		searchContainer.add(fieldPanel, BorderLayout.CENTER);
		searchContainer.add(matchCountLabel, BorderLayout.EAST);
		
		searchPanel.add(searchContainer, BorderLayout.CENTER);

		// Add document listener to search field
		searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
			@Override
			public void insertUpdate(javax.swing.event.DocumentEvent e) {
				applyFilter();
			}

			@Override
			public void removeUpdate(javax.swing.event.DocumentEvent e) {
				applyFilter();
			}

			@Override
			public void changedUpdate(javax.swing.event.DocumentEvent e) {
				applyFilter();
			}

			private void applyFilter() {
				String searchText = searchField.getText();
				applyProgramFilter(searchText);
			}
		});

		// Register keyboard shortcut (Ctrl+F) to focus the search field
		KeyStroke ctrlF = KeyStroke.getKeyStroke("control F");
		searchPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(ctrlF, "focusSearch");
		searchPanel.getActionMap().put("focusSearch", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				searchField.requestFocusInWindow();
				searchField.selectAll();
			}
		});

		return searchPanel;
	}

	/**
	 * Creates a new search bar icon
	 */
	private Icon createSearchIcon() {
		return new Icon() {
			@Override
			public void paintIcon(Component c, Graphics g, int x, int y) {
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

				// Set color
				g2.setColor(new Color(120, 120, 120));
				g2.setStroke(new BasicStroke(1.5f));

				// Draw the magnifying glass circle
				g2.drawOval(x + 2, y + 2, 10, 10);

				// Draw the handle
				g2.drawLine(x + 11, y + 11, x + 15, y + 15);

				g2.dispose();
			}

			@Override
			public int getIconWidth() {
				return 18;
			}

			@Override
			public int getIconHeight() {
				return 18;
			}
		};
	}

	/**
	 * Method to apply filter to program table and update match count
	 */
	private void applyProgramFilter(String searchText) {
		if (programTable != null) {
			programTable.setFilter(searchText);
			
			// Update match count display
			int matches = programTable.getRowCount();
			if (searchText != null && !searchText.isEmpty()) {
				matchCountLabel.setText(matches + " match" + (matches == 1 ? "" : "es"));
			} else {
				matchCountLabel.setText("");
			}
		}
	}
	
	/**
	 * Sets up event handlers for UI components
	 */
	private void setupEventHandlers() {
		// Test connection button
		testConnectionButton.addActionListener(e -> {
			// Update the configuration with the current values
			updateConfig();
			
			// Test the connection
			executorService.execute(() -> {
				try {
					IntigritiApiClient apiClient = new IntigritiApiClient(api, quickScopeConfig);
					boolean success = apiClient.testConnection();
					
					SwingUtilities.invokeLater(() -> {
						if (success) {
							JOptionPane.showMessageDialog(api.userInterface().swingUtils().suiteFrame(),
								"The connection to the Intigriti API was successful",
								"Connection successful",
								JOptionPane.INFORMATION_MESSAGE);
						} else {
							JOptionPane.showMessageDialog(api.userInterface().swingUtils().suiteFrame(),
								"The connection to the Intigriti API failed",
								"Connection failed",
								JOptionPane.ERROR_MESSAGE);
						}
					});
				} catch (Exception ex) {
					logging.logToError("Error testing connection: " + ex.getMessage());
					SwingUtilities.invokeLater(() -> {
						JOptionPane.showMessageDialog(api.userInterface().swingUtils().suiteFrame(),
							"Error: " + ex.getMessage(),
							"Connection error",
							JOptionPane.ERROR_MESSAGE);
					});
				}
			});
		});
		
		// Load programs button
		loadProgramsButton.addActionListener(e -> {
			// Update the configuration with the current values
			updateConfig();
			
			// Load the programs
			executorService.execute(() -> {
				try {
					IntigritiApiClient apiClient = new IntigritiApiClient(api, quickScopeConfig);
					List<Program> programs = apiClient.getPrograms();
					
					SwingUtilities.invokeLater(() -> {
						programTable.setPrograms(programs);
						// Update match count after loading programs
						applyProgramFilter(searchField.getText());
					});
				} catch (Exception ex) {
					logging.logToError("Error loading programs: " + ex.getMessage());
					SwingUtilities.invokeLater(() -> {
						JOptionPane.showMessageDialog(api.userInterface().swingUtils().suiteFrame(),
							"Error loading programs: " + ex.getMessage(),
							"Loading error",
							JOptionPane.ERROR_MESSAGE);
					});
				}
			});
		});
		
		// Program selection
		programTable.addSelectionListener(program -> {
			if (program != null) {
				executorService.execute(() -> {
					try {
						IntigritiApiClient apiClient = new IntigritiApiClient(api, quickScopeConfig);
						domainsPanel.loadDomainsForProgram(apiClient, program);
					} catch (Exception ex) {
						logging.logToError("Error loading domains: " + ex.getMessage());
						SwingUtilities.invokeLater(() -> {
							JOptionPane.showMessageDialog(api.userInterface().swingUtils().suiteFrame(),
								"Error loading domains: " + ex.getMessage(),
								"Loading error",
								JOptionPane.ERROR_MESSAGE);
						});
					}
				});
			}
		});
		
		// Reset scope button
		clearScopeButton.addActionListener(e -> {
			int result = JOptionPane.showConfirmDialog(
				api.userInterface().swingUtils().suiteFrame(),
				"Are you sure you want to reset the entire scope? This will remove all scope filters and headers!",
				"Reset Scope",
				JOptionPane.YES_NO_OPTION,
				JOptionPane.WARNING_MESSAGE
			);
			
			if (result == JOptionPane.YES_OPTION) {
				scopeConverter.clearScope();
				JOptionPane.showMessageDialog(api.userInterface().swingUtils().suiteFrame(),
					"Scope configuration reset successfully!",
					"Success",
					JOptionPane.INFORMATION_MESSAGE);
			}
		});
	}
	
	/**
	 * Updates the configuration with the current values from the UI
	 */
	private void updateConfig() {
		// Get the Researcher username + API key
		String username = usernameField.getText();
		String apiKey = apiKeyField.getText();

		// Save Researcher username + API key
		quickScopeConfig.setUsername(username);
		quickScopeConfig.setApiKey(apiKey);
		
		// Update other settings
		quickScopeConfig.setOnlyActive(onlyActiveCheckBox.isSelected());
		quickScopeConfig.setOnlyFollowing(onlyFollowingCheckBox.isSelected());
		quickScopeConfig.setOnlyPrivate(onlyPrivateCheckBox.isSelected());
		
		// Save the configuration
		config.setQuickScopeConfig(quickScopeConfig);
	}
	
	/**
	 * Stops any running background tasks
	 */
	public void stop() {
		executorService.shutdown();
	}
}