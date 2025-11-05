package com.quickscope.ui;

import com.quickscope.api.models.Domain;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * Table model for domain rows with regex patterns
 */
public class DomainTableModel extends AbstractTableModel {
	
	private static final String[] COLUMN_NAMES = {"Endpoint", "Type", "Tier", "Regex Pattern", "Description"};
	private final List<DomainRow> domains = new ArrayList<>();
	
	/**
	 * Create a new domain table model
	 */
	public DomainTableModel() {
	}
	
	/**
	 * Clear all domains
	 */
	public void clearDomains() {
		domains.clear();
		fireTableDataChanged();
	}
	
	/**
	 * Add a list of domains
	 */
	public void addDomains(List<DomainRow> domainRows) {
		domains.addAll(domainRows);
		fireTableDataChanged();
	}
	
	@Override
	public int getRowCount() {
		return domains.size();
	}
	
	@Override
	public int getColumnCount() {
		return COLUMN_NAMES.length;
	}
	
	@Override
	public String getColumnName(int column) {
		return COLUMN_NAMES[column];
	}
	
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (rowIndex < 0 || rowIndex >= domains.size()) {
			return null;
		}
		
		DomainRow row = domains.get(rowIndex);
		Domain domain = row.getDomain();
		
		switch (columnIndex) {
			case 0: // Endpoint
				return domain.getEndpoint();
			case 1: // Type
				// Check if this has an invalid regex pattern (starts with #)
				String pattern = row.getRegexPattern();
				if (pattern != null && pattern.startsWith("#")) {
					return "Unknown";
				}

				// Check if this is an app store URL
				String endpoint = domain.getEndpoint();
				if (endpoint != null) {
					String lower = endpoint.toLowerCase();
					if (lower.contains("apps.apple.com") || 
						lower.contains("play.google.com") ||
						lower.contains("/store/apps/") || 
						lower.contains("/in/developer/") ||
						lower.contains("itunes.apple")) {
						return "Mobile";  // Override the type display
					}
				}

            	return domain.getType() != null ? domain.getType().getValue() : "";
			case 2: // Tier
				return domain.getTier() != null ? domain.getTier().getValue() : "";
			case 3: // Regex Pattern
				return row.getRegexPattern();
			case 4: // Description
				return domain.getDescription();
			default:
				return null;
		}
	}
	
	/**
	 * Get a domain by row index
	 */
	public Domain getDomain(int row) {
		if (row >= 0 && row < domains.size()) {
			return domains.get(row).getDomain();
		}
		return null;
	}
	
	/**
	 * Get the regex pattern for a row
	 */
	public String getRegexPattern(int row) {
		if (row >= 0 && row < domains.size()) {
			return domains.get(row).getRegexPattern();
		}
		return null;
	}
	
	/**
	 * Domain row containing domain and regex pattern
	 */
	public static class DomainRow {
		private final Domain domain;
		private final String regexPattern;
		
		public DomainRow(Domain domain, String regexPattern) {
			this.domain = domain;
			this.regexPattern = regexPattern;
		}
		
		public Domain getDomain() {
			return domain;
		}
		
		public String getRegexPattern() {
			return regexPattern;
		}
	}
}