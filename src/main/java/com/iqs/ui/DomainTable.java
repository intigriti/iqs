package com.iqs.ui;

import burp.api.montoya.MontoyaApi;
import com.iqs.api.models.Domain;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Specialized table for displaying domains with patterns
 */
public class DomainTable extends JTable {
	
	private final MontoyaApi api;
	private final DomainTableModel model;
	private final String category;
	
	/**
	 * Create a new domain table
	 */
	public DomainTable(MontoyaApi api, String category) {
		this.api = api;
		this.category = category;
		this.model = new DomainTableModel();
		
		// Set model
		setModel(model);
		
		// Configure table appearance
		setFillsViewportHeight(true);
		setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		setAutoCreateRowSorter(true);
		
		// Set column widths
		getColumnModel().getColumn(0).setPreferredWidth(200); // Endpoint
		getColumnModel().getColumn(1).setPreferredWidth(80);  // Type
		getColumnModel().getColumn(2).setPreferredWidth(80);  // Tier
		getColumnModel().getColumn(3).setPreferredWidth(300); // Regex Pattern
		getColumnModel().getColumn(4).setPreferredWidth(300); // Description
		
		// Configure renderers
		setupTableRenderers();
		
		// Add copy pattern context menu
		addContextMenu();
	}
	
	/**
	 * Clear all domains
	 */
	public void clearDomains() {
		model.clearDomains();
	}
	
	/**
	 * Add domains to the table
	 */
	public void addDomains(List<DomainTableModel.DomainRow> domains) {
		model.addDomains(domains);
	}
	
	/**
	 * Get selected domains
	 */
	public List<Domain> getSelectedDomains() {
		int[] selectedRows = getSelectedRows();
		List<Domain> selectedDomains = new ArrayList<>();
		
		for (int row : selectedRows) {
			int modelRow = convertRowIndexToModel(row);
			Domain domain = model.getDomain(modelRow);
			if (domain != null) {
				selectedDomains.add(domain);
			}
		}
		
		return selectedDomains;
	}
	
	/**
	 * Get selected regex patterns
	 */
	public List<String> getSelectedPatterns() {
		int[] selectedRows = getSelectedRows();
		List<String> patterns = new ArrayList<>();
		
		for (int row : selectedRows) {
			int modelRow = convertRowIndexToModel(row);
			String pattern = model.getRegexPattern(modelRow);
			if (pattern != null && !pattern.isEmpty() && !pattern.startsWith("#")) {
				patterns.add(pattern);
			}
		}
		
		return patterns;
	}
	
	/**
	 * Set up custom renderers for the table
	 */
	private void setupTableRenderers() {
		// Create a custom renderer for all columns
		DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, 
														 boolean isSelected, boolean hasFocus, 
														 int row, int column) {
				Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				
				// Get the tier value
				Object tierValue = table.getModel().getValueAt(
					table.convertRowIndexToModel(row), 
					2 // Tier column
				);
				
				boolean isOutOfScope = false;
				if (tierValue != null && tierValue.toString().toLowerCase().contains("out of scope")) {
					isOutOfScope = true;
				}
				
				// Apply special formatting for the regex column
				if (column == 3) { // Regex pattern column
					String text = value != null ? value.toString() : "";
					if (text.startsWith("#")) {
						// Comment for non-URL items
						setForeground(new Color(150, 150, 150));
						setFont(getFont().deriveFont(Font.ITALIC));
					} else {
						// Regular pattern
						// setForeground(new Color(0, 0, 180));
						setFont(getFont().deriveFont(Font.PLAIN));
					}
				} else if (isOutOfScope && !isSelected) {
					// Out of scope items
					setBackground(new Color(255, 200, 200));
					setForeground(new Color(150, 0, 0));
				} else if (!isSelected) {
					// Reset colors
					setBackground(table.getBackground());
					setForeground(table.getForeground());
				}
				
				return c;
			}
		};
		
		// Apply the renderer to all columns
		for (int i = 0; i < getColumnCount(); i++) {
			getColumnModel().getColumn(i).setCellRenderer(renderer);
		}
	}
	
	/**
	 * Add a context menu for copying regex patterns
	 */
	private void addContextMenu() {
		JPopupMenu contextMenu = new JPopupMenu();
		
		// Single pattern copy item
		JMenuItem copyItem = new JMenuItem("Copy Regex Pattern");
		copyItem.addActionListener(e -> {
			int row = getSelectedRow();
			if (row >= 0) {
				int modelRow = convertRowIndexToModel(row);
				String pattern = model.getRegexPattern(modelRow);
				if (pattern != null && !pattern.isEmpty() && !pattern.startsWith("#")) {
					StringSelection selection = new StringSelection(pattern);
					Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);
				}
			}
		});
		
		// Multi-pattern copy item
		JMenuItem copySelectedItem = new JMenuItem("Copy All Selected Patterns");
		copySelectedItem.addActionListener(e -> {
			int[] rows = getSelectedRows();
			if (rows.length > 0) {
				StringBuilder patterns = new StringBuilder();
				
				for (int row : rows) {
					int modelRow = convertRowIndexToModel(row);
					String pattern = model.getRegexPattern(modelRow);
					if (pattern != null && !pattern.isEmpty() && !pattern.startsWith("#")) {
						patterns.append(pattern).append("\n");
					}
				}
				
				if (patterns.length() > 0) {
					StringSelection selection = new StringSelection(patterns.toString());
					Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);
					
					// Show a small notification
					JOptionPane.showMessageDialog(
						SwingUtilities.getWindowAncestor(this),
						rows.length + " patterns copied to clipboard",
						"Patterns Copied",
						JOptionPane.INFORMATION_MESSAGE
					);
				}
			}
		});
		
		// Enable/disable menu items based on selection
		contextMenu.addPopupMenuListener(new PopupMenuListener() {
			@Override
			public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
				int[] rows = getSelectedRows();
				copyItem.setEnabled(rows.length == 1);
				copySelectedItem.setEnabled(rows.length > 1);
			}
			
			@Override
			public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
			}
			
			@Override
			public void popupMenuCanceled(PopupMenuEvent e) {
			}
		});
		
		contextMenu.add(copyItem);
		contextMenu.add(copySelectedItem);
		
		setComponentPopupMenu(contextMenu);
	}

	/**
	 * Add filter toolbar to the table
	 */
	private void addFilterToolbar(JPanel parent) {
		JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

		// Add type filter
		JComboBox<String> typeFilter = new JComboBox<>(new String[] {
				"All Types", "URL", "Wildcard", "API", "IP Range", "Unknown"
		});
		typeFilter.addActionListener(e -> {
			String selectedType = (String) typeFilter.getSelectedItem();
			filterByType(selectedType);
		});

		filterPanel.add(new JLabel("Filter by Type:"));
		filterPanel.add(typeFilter);

		// Add a button to show only valid items
		JButton showValidButton = new JButton("Show Only Valid URLs");
		showValidButton.addActionListener(e -> {
			filterValidUrls();
		});
		filterPanel.add(showValidButton);

		parent.add(filterPanel, BorderLayout.NORTH);
	}

	/**
	 * Filter table by type
	 */
	private void filterByType(String typeFilter) {
		TableRowSorter<DomainTableModel> sorter = (TableRowSorter<DomainTableModel>) getRowSorter();

		if (typeFilter.equals("All Types")) {
			sorter.setRowFilter(null);
		} else if (typeFilter.equals("Unknown")) {
			sorter.setRowFilter(RowFilter.regexFilter("^Unknown$", 1));
		} else {
			sorter.setRowFilter(RowFilter.regexFilter("^" + Pattern.quote(typeFilter) + "$", 1));
		}
	}

	/**
	 * Filter to show only valid URLs (no comments in regex)
	 */
	private void filterValidUrls() {
		TableRowSorter<DomainTableModel> sorter = (TableRowSorter<DomainTableModel>) getRowSorter();

		sorter.setRowFilter(RowFilter.regexFilter("^(?!#).*$", 3)); // Column 3 is Regex Pattern
	}
}