package com.iqs.ui;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.logging.Logging;
import burp.api.montoya.ui.UserInterface;

import com.iqs.api.IntigritiApiClient;
import com.iqs.api.models.Domain;
import com.iqs.api.models.Program;
import com.iqs.api.models.ProgramDetails;
import com.iqs.config.QuickScopeConfig;
import com.iqs.util.AdvancedScopeUtil;
import com.iqs.util.EndpointClassifier;
import com.iqs.util.ScopeConverter;
import com.iqs.util.RequirementsManager;
import com.iqs.ui.DomainTable;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.GeneralPath;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Panel for displaying categorized domain tables
 */
public class DomainsPanel extends JPanel {

	private final MontoyaApi api;
	private final Logging logging;
	private final UserInterface userInterface;
	private final QuickScopeConfig quickScopeConfig;
	private final AdvancedScopeUtil advancedScopeUtil;
	private final ScopeConverter scopeConverter;
	
	private Program currentProgram;
	private ProgramDetails currentProgramDetails;
	private JLabel programLabel;
	private JLabel requirementsLabel;
	private JLabel rateLimitLabel;
	private JLabel headersRequiredLabel;
	private JButton urlButton;
	
	private JTabbedPane tabbedPane;
	private Map<EndpointClassifier.EndpointType, DomainTable> domainTables;
	private JButton addSelectedToScopeButton;
	private JButton addAllToScopeButton;
	
	/**
	 * Creates a new domains panel
	 */
	public DomainsPanel(MontoyaApi api, QuickScopeConfig quickScopeConfig, AdvancedScopeUtil advancedScopeUtil, ScopeConverter scopeConverter) {
		this.api = api;
		this.logging = api.logging();
		this.userInterface = api.userInterface();
		this.quickScopeConfig = quickScopeConfig;
		this.advancedScopeUtil = advancedScopeUtil;
		this.scopeConverter = scopeConverter;
		
		// Set up the layout
		setLayout(new BorderLayout());
		setBorder(new TitledBorder("Program Assets"));
		
		// Create program info panel with label and URL button
		JPanel programInfoPanel = createProgramInfoPanel();
		add(programInfoPanel, BorderLayout.NORTH);
		
		// Create tabbed pane with domain categories
		tabbedPane = new JTabbedPane();
		
		// Initialize domain tables for each category
		domainTables = new HashMap<>();
		
		// Add tabs for main categories
		JPanel webPanel = createCategoryPanel("Web");
		JPanel nonWebPanel = createCategoryPanel("Other");
		JPanel outOfScopePanel = createCategoryPanel("Out of Scope");
		
		tabbedPane.addTab("Web", webPanel);
		tabbedPane.addTab("Others", nonWebPanel);
		tabbedPane.addTab("Out of Scope", outOfScopePanel);
		
		add(tabbedPane, BorderLayout.CENTER);
		
		// Create control panel
		JPanel controlPanel = createControlPanel();
		add(controlPanel, BorderLayout.SOUTH);
	}
	
	/**
	 * Create program info panel with properly aligned elements
	 */
	private JPanel createProgramInfoPanel() {
		// Use a more flexible layout
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
		panel.setBorder(new EmptyBorder(5, 5, 5, 5));

		// Create left side panel for program name and URL button
		JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));

		// Create the program label
		programLabel = new JLabel("No program selected");
		leftPanel.add(programLabel);

		// Create URL button
		urlButton = createUrlButton();
		urlButton.setVisible(false); // Initially hidden
		leftPanel.add(urlButton);

		panel.add(leftPanel, BorderLayout.WEST);

		// Create right side panel for requirements label
		JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));

		// Add a requirements label
		requirementsLabel = createLabel("⚠️ Intigriti.Me Required");
		requirementsLabel.setVisible(false);
		rightPanel.add(requirementsLabel);

		// Margin
		panel.add(new JLabel("   "));

		// Add a requirements label
		rateLimitLabel = createLabel("Max 1 req/s");
		rateLimitLabel.setVisible(false);
		rightPanel.add(rateLimitLabel);

		// Margin
		panel.add(new JLabel("   "));

		// Add a requirements label for request headers
		headersRequiredLabel = createLabel("⚠️ Scope Headers Required");
		headersRequiredLabel.setVisible(false);
		rightPanel.add(headersRequiredLabel);
		
		panel.add(rightPanel, BorderLayout.EAST);

		return panel;
	}

	/**
	 * Create a button to open the program URL with improved icon
	 */
	private JButton createUrlButton() {
		JButton button = new JButton();
		button.setToolTipText("Open program URL in browser");
		button.setBorderPainted(false);
		button.setContentAreaFilled(false);
		button.setFocusPainted(false);
		button.setMargin(new Insets(0, 0, 0, 0));
		button.setIconTextGap(0);
		button.setIcon(createLinkIcon());
		button.setCursor(new Cursor(Cursor.HAND_CURSOR));
		
		// Ensure vertical alignment
		button.setAlignmentY(Component.CENTER_ALIGNMENT);
		
		// Action listener
		button.addActionListener(e -> {
			if (currentProgram != null && currentProgram.getWebLinks() != null) {
				String url = currentProgram.getWebLinks().getDetail();
				if (url != null && !url.isEmpty()) {
					try {
						Desktop.getDesktop().browse(new URI(url));
					} catch (Exception ex) {
						logging.logToError("Error opening URL: " + ex.getMessage());
					}
				}
			}
		});
		
		return button;
	}
	
	/**
	 * Create an external link icon
	 */
	private Icon createLinkIcon() {
		return new Icon() {
			@Override
			public void paintIcon(Component c, Graphics g, int x, int y) {
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

				Color iconColor = new Color(66, 156, 227);
				g2.setColor(iconColor);
				g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

				// Box (bottom-left portion, leaving top-right open for the arrow)
				int bx = x + 2, by = y + 6, bw = 10, bh = 10;
				// Draw three sides of the box (left, bottom, right) + partial top
				GeneralPath box = new GeneralPath();
				box.moveTo(bx + 4, by);          // partial top edge
				box.lineTo(bx, by);               // top-left corner
				box.lineTo(bx, by + bh);          // left side
				box.lineTo(bx + bw, by + bh);     // bottom side
				box.lineTo(bx + bw, by + 4);      // right side (partial)
				g2.draw(box);

				// Arrow: from inside box to upper-right
				int ax = x + 16, ay = y + 3;
				g2.drawLine(bx + 5, by + 5, ax, ay);

				// Arrowhead
				g2.drawLine(ax, ay, ax - 4, ay);
				g2.drawLine(ax, ay, ax, ay + 4);

				g2.dispose();
			}

			@Override
			public int getIconWidth() {
				return 20;
			}

			@Override
			public int getIconHeight() {
				return 20;
			}
		};
	}

	/**
	 * Creates a customised label
	 * @param text The text to be displayed by the label
	 */
	private JLabel createLabel(String text) {
		JLabel label = new JLabel(text);

		// Style the label
		label.setOpaque(true); // Allow background color to show
		label.setBackground(new Color(50, 50, 50)); // Dark gray background
		label.setForeground(Color.WHITE); // White text
		label.setFont(label.getFont().deriveFont(Font.BOLD, 12f)); // Bold text, slightly smaller

		// Add padding with border
		label.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(new Color(70, 70, 70), 1), // Subtle border
				BorderFactory.createEmptyBorder(4, 8, 4, 8) // Inner padding
		));

		label.setMinimumSize(new Dimension(label.getPreferredSize().width, 24));
    	label.setPreferredSize(null); // Let the preferred size be calculated based on content

		label.setHorizontalAlignment(SwingConstants.CENTER);

		return label;
	}
	
	/**
	 * Create a panel for a domain category
	 */
	private JPanel createCategoryPanel(String category) {
		JPanel panel = new JPanel(new BorderLayout());
		
		// Create domain table for this category
		DomainTable table = new DomainTable(api, category);
		JScrollPane scrollPane = new JScrollPane(table);
		panel.add(scrollPane, BorderLayout.CENTER);

		// In the createCategoryPanel method
		if (category.equals("Web")) {
			for (EndpointClassifier.EndpointType type : EndpointClassifier.getWebEndpointTypes()) {
				domainTables.put(type, table);
			}
		} else if (category.equals("Out of Scope")) {
			domainTables.put(EndpointClassifier.EndpointType.OUT_OF_SCOPE, table);
		} else { // "Non-Web Items"
			domainTables.put(EndpointClassifier.EndpointType.MOBILE_APP, table);
			domainTables.put(EndpointClassifier.EndpointType.DESKTOP_APP, table);
			domainTables.put(EndpointClassifier.EndpointType.DESCRIPTIVE, table);
			domainTables.put(EndpointClassifier.EndpointType.UNKNOWN, table);
		}
		
		return panel;
	}
	
	/**
	 * Create the control panel with buttons
	 */
	private JPanel createControlPanel() {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		panel.setBorder(new TitledBorder("Scope Actions"));
		
		// Add scope buttons
		addAllToScopeButton = new JButton("Apply program to project");
		addAllToScopeButton.addActionListener(e -> addAllToScope());
		panel.add(addAllToScopeButton);

		addSelectedToScopeButton = new JButton("Add Selected to Scope");
		addSelectedToScopeButton.addActionListener(e -> addSelectedToScope());
		panel.add(addSelectedToScopeButton);
		
		return panel;
	}
	
	/**
	 * Add selected domains to scope
	 */
	private void addSelectedToScope() {
		// Get the current tab index
		int tabIndex = tabbedPane.getSelectedIndex();
		
		// Get the appropriate table from the tab content
		Component comp = tabbedPane.getComponentAt(tabIndex);
		if (!(comp instanceof JPanel)) return;
		
		JPanel panel = (JPanel)comp;
		Component viewComp = panel.getComponent(0);
		if (!(viewComp instanceof JScrollPane)) return;
		
		JScrollPane scrollPane = (JScrollPane)viewComp;
		Component view = scrollPane.getViewport().getView();
		if (!(view instanceof DomainTable)) return;
		
		DomainTable table = (DomainTable)view;
		
		// Get selected domains
		List<Domain> selectedDomains = table.getSelectedDomains();
		
		if (selectedDomains.isEmpty()) {
			JOptionPane.showMessageDialog(
				SwingUtilities.getWindowAncestor(this),
				"Please select one or more assets to add to scope",
				"No assets selected", 
				JOptionPane.INFORMATION_MESSAGE
			);
			return;
		}
		
		// Process domains
		int addedCount = scopeConverter.addDomainsToScope(selectedDomains);
		
		JOptionPane.showMessageDialog(
			SwingUtilities.getWindowAncestor(this),
			"Program scope configuration (" + addedCount + ") applied to scope! " + 
				"Please verify that processed scope headers include your Intigriti username!",
			"Scope configured",
			JOptionPane.INFORMATION_MESSAGE
		);
	}
	
	/**
	 * Add all domains from current tab to scope
	 */
	private void addAllToScope() {
		// Get the current tab index
		int tabIndex = tabbedPane.getSelectedIndex();

		// Get the appropriate table from the tab content
		Component comp = tabbedPane.getComponentAt(tabIndex);
		if (!(comp instanceof JPanel)) return;

		JPanel panel = (JPanel) comp;
		Component viewComp = panel.getComponent(0);
		if (!(viewComp instanceof JScrollPane)) return;

		JScrollPane scrollPane = (JScrollPane) viewComp;
		Component view = scrollPane.getViewport().getView();
		if (!(view instanceof DomainTable)) return;

		DomainTable table = (DomainTable) view;

		// Get all domains from the table
		List<Domain> allDomains = new ArrayList<>();
		for (int i = 0; i < table.getModel().getRowCount(); i++) {
			Domain domain = ((DomainTableModel) table.getModel()).getDomain(i);
			if (domain != null) {
				allDomains.add(domain);
			}
		}

		if (allDomains.isEmpty()) {
			JOptionPane.showMessageDialog(
					SwingUtilities.getWindowAncestor(this),
					"No domains available to add to scope",
					"No domains",
					JOptionPane.INFORMATION_MESSAGE);
			return;
		}

		// Process domains
		int addedCount = scopeConverter.addDomainsToScope(allDomains);

		// Apply program requirements if we have program details
		try {
			if (currentProgram != null) {
				scopeConverter.addProgramRequirements(currentProgramDetails);
			}
		} catch (Exception ex) {
			logging.logToError("Error applying program requirements: " + ex.getMessage());
		}

		JOptionPane.showMessageDialog(
				SwingUtilities.getWindowAncestor(this),
				"Program scope configuration (" + addedCount + ") applied to scope! " + 
					"Please verify that processed scope headers include your Intigriti username!",
				"Scope processed",
				JOptionPane.INFORMATION_MESSAGE);
	}

	private String truncate(String text, int maxLength) {
		if (text == null || text.length() <= maxLength) return text;
		return text.substring(0, maxLength) + "...";
	}
	
	/**
	 * Loads domains for a program and organizes them into categories
	 */
	public void loadDomainsForProgram(IntigritiApiClient apiClient, Program program) {
		try {
			// Update current program
			currentProgram = program;
			
			// Update program label and URL button
			SwingUtilities.invokeLater(() -> {
				programLabel.setText(truncate(program.getName(), 40));
				programLabel.setToolTipText(program.getName() + " (" + program.getHandle() + ")");
				
				// Show URL button if program has a URL
				boolean hasUrl = program.getWebLinks() != null && 
								program.getWebLinks().getDetail() != null && 
								!program.getWebLinks().getDetail().isEmpty();
				urlButton.setVisible(hasUrl);

				// Clear labels
				requirementsLabel.setText("");
				requirementsLabel.setVisible(false);
				rateLimitLabel.setText("");
				rateLimitLabel.setVisible(false);
				headersRequiredLabel.setText("");
				headersRequiredLabel.setVisible(false);
			});
			
			// Load program details
			ProgramDetails details = apiClient.getProgramDetails(program.getId());
			currentProgramDetails = details;

			// Check for intigritiMe requirement
			boolean intigritiMeRequired = false;
			Integer rateLimit = null;
			boolean headersRequired = false;

			if (details.getRulesOfEngagement() != null &&
					details.getRulesOfEngagement().getContent() != null &&
					details.getRulesOfEngagement().getContent().getTestingRequirements() != null) {
				// Check for intigritiMe requirement
				intigritiMeRequired = details.getRulesOfEngagement()
						.getContent().getTestingRequirements().isIntigritiMe();
				
				// Check for rate limit
				rateLimit = details.getRulesOfEngagement()
						.getContent().getTestingRequirements().getAutomatedTooling();

				// Check for required scope headers
            	headersRequired = details.getRulesOfEngagement()
						.getContent().getTestingRequirements().getRequestHeader() != "" ||
						details.getRulesOfEngagement()
						.getContent().getTestingRequirements().getRequestHeader() != "";
			}

			// Update requirements label if needed
			final boolean finalIntigritiMeRequired = intigritiMeRequired;
			final Integer finalRateLimit = rateLimit;
			final boolean finalHeadersRequired = headersRequired;

			SwingUtilities.invokeLater(() -> {
				if (finalIntigritiMeRequired) {
					requirementsLabel.setText("⚠️ Intigriti.Me Required");
					requirementsLabel.setToolTipText("⚠️ Intigriti.Me Required");
					requirementsLabel.setVisible(true);
				} else {
					requirementsLabel.setVisible(false);
				}

				if (finalRateLimit != null && finalRateLimit > 0) {
					rateLimitLabel.setText("⏱️ Max " + finalRateLimit + " req/s");
					rateLimitLabel.setToolTipText("⏱️ Max " + finalRateLimit + " req/s");
					rateLimitLabel.setVisible(true);
				} else {
					rateLimitLabel.setVisible(false);
				}

				if (finalHeadersRequired) {
					headersRequiredLabel.setText("⚠️ Scope Headers Required");
					headersRequiredLabel.setToolTipText("⚠️ Scope Headers Required");
					headersRequiredLabel.setVisible(true);
				} else {
					headersRequiredLabel.setVisible(false);
				}
			});

			// Apply program requirements
			RequirementsManager requirementsManager = new RequirementsManager(api);
			requirementsManager.setUsername(quickScopeConfig.getUsername());
			requirementsManager.applyRequirements(details);
			
			if (details.getDomains() != null && details.getDomains().getId() != null) {
				// Load domains for the version
				List<Domain> domains = apiClient.getProgramDomains(
					program.getId(),
					details.getDomains().getId()
				);
				
				// Clear all tables
				SwingUtilities.invokeLater(() -> {
					for (DomainTable table : domainTables.values()) {
						table.clearDomains();
					}
				});
				
				// Organize domains by category
				Map<EndpointClassifier.EndpointType, List<DomainTableModel.DomainRow>> categorizedDomains = new HashMap<>();
				
				for (Domain domain : domains) {
					EndpointClassifier.EndpointType type = EndpointClassifier.classifyDomain(domain);
					
					// Create a row with endpoint and regex pattern
					String regexPattern = advancedScopeUtil.generateRegexPattern(domain.getEndpoint());
					DomainTableModel.DomainRow row = new DomainTableModel.DomainRow(
						domain,
						regexPattern
					);
					
					// Add to appropriate category
					if (!categorizedDomains.containsKey(type)) {
						categorizedDomains.put(type, new ArrayList<>());
					}
					categorizedDomains.get(type).add(row);
				}
				
				// Update each table with its domains
				SwingUtilities.invokeLater(() -> {
					for (Map.Entry<EndpointClassifier.EndpointType, List<DomainTableModel.DomainRow>> entry : 
						 categorizedDomains.entrySet()) {
						EndpointClassifier.EndpointType type = entry.getKey();
						List<DomainTableModel.DomainRow> rows = entry.getValue();
						
						DomainTable table = domainTables.get(type);
						if (table != null) {
							table.addDomains(rows);
						}
					}
					
					// Select the appropriate tab based on available data
					selectAppropriateTab(categorizedDomains);
				});
			} else {
				// No domains available
				SwingUtilities.invokeLater(() -> {
					for (DomainTable table : domainTables.values()) {
						table.clearDomains();
					}
					
					JOptionPane.showMessageDialog(
						SwingUtilities.getWindowAncestor(this),
						"No domains found for this program",
						"No domains", 
						JOptionPane.INFORMATION_MESSAGE
					);
				});
			}
		} catch (Exception e) {
			logging.logToError("Error loading domains: " + e.getMessage());
			SwingUtilities.invokeLater(() -> {
				JOptionPane.showMessageDialog(
					SwingUtilities.getWindowAncestor(this),
					"Error loading domains: " + e.getMessage(),
					"Error", 
					JOptionPane.ERROR_MESSAGE
				);
			});
		}
	}
	
	/**
	 * Select the appropriate tab based on available data
	 */
	private void selectAppropriateTab(Map<EndpointClassifier.EndpointType, List<DomainTableModel.DomainRow>> categorizedDomains) {
		// Count items in each category
		int webCount = 0;
		int nonWebCount = 0;
		int outOfScopeCount = 0;
		
		for (Map.Entry<EndpointClassifier.EndpointType, List<DomainTableModel.DomainRow>> entry : 
			 categorizedDomains.entrySet()) {
			EndpointClassifier.EndpointType type = entry.getKey();
			int count = entry.getValue().size();
			
			if (type == EndpointClassifier.EndpointType.OUT_OF_SCOPE) {
				outOfScopeCount += count;
			} else if (EndpointClassifier.getWebEndpointTypes().contains(type)) {
				webCount += count;
			} else {
				nonWebCount += count;
			}
		}
		
		// Select tab with most content
		if (webCount > 0) {
			tabbedPane.setSelectedIndex(0); // Web Domains
		} else if (nonWebCount > 0) {
			tabbedPane.setSelectedIndex(1); // Non-Web Items
		} else if (outOfScopeCount > 0) {
			tabbedPane.setSelectedIndex(2); // Out of Scope
		}
	}
}