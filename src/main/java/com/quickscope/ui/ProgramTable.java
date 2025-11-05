package com.quickscope.ui;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.logging.Logging;

import com.quickscope.api.models.Program;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

/**
 * Table for displaying programs from the Intigriti API
 */
public class ProgramTable extends JTable {

	private final MontoyaApi api;
	private final Logging logging;
	private final ProgramTableModel model;
	private final List<Consumer<Program>> selectionListeners;
	private List<Program> allPrograms;
	private String currentFilter;
	
	/**
	 * Creates a new program table
	 * 
	 * @param api The Montoya API instance
	 */
	public ProgramTable(MontoyaApi api) {
		this.api = api;
		this.logging = api.logging();
		this.model = new ProgramTableModel();
		this.selectionListeners = new ArrayList<>();
		this.allPrograms = new ArrayList<>();
		this.currentFilter = "";
		
		// Set the model
		setModel(model);
		
		// Set up sorting
		TableRowSorter<ProgramTableModel> sorter = new TableRowSorter<>(model);
		sorter.setComparator(0, Comparator.comparing(String::toString));
		sorter.setComparator(1, Comparator.comparing(Object::toString));
		sorter.setComparator(2, (o1, o2) -> Boolean.compare((Boolean) o1, (Boolean) o2));
		sorter.setComparator(3, (o1, o2) -> Boolean.compare((Boolean) o1, (Boolean) o2));
		setRowSorter(sorter);
		
		// Customize appearance
		setFillsViewportHeight(true);
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		setAutoCreateRowSorter(true);
		
		// Set up column widths
		getColumnModel().getColumn(0).setPreferredWidth(350);
		getColumnModel().getColumn(1).setPreferredWidth(100);
		getColumnModel().getColumn(2).setPreferredWidth(80);
		getColumnModel().getColumn(3).setPreferredWidth(80);
		
		// Set up custom renderers
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(JLabel.CENTER);
		getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
		getColumnModel().getColumn(2).setCellRenderer(new BooleanIconRenderer());
		getColumnModel().getColumn(3).setCellRenderer(new BooleanIconRenderer());
		
		// Set up selection listener
		getSelectionModel().addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting()) {
				int selectedRow = getSelectedRow();
				if (selectedRow >= 0) {
					selectedRow = convertRowIndexToModel(selectedRow);
					Program selectedProgram = model.getProgram(selectedRow);
					notifySelectionListeners(selectedProgram);
				}
			}
		});
		
		// Add double-click listener for opening program in browser
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					int row = rowAtPoint(e.getPoint());
					if (row >= 0) {
						row = convertRowIndexToModel(row);
						Program program = model.getProgram(row);
						if (program != null && program.getWebLinks() != null) {
							String url = program.getWebLinks().getDetail();
							if (url != null && !url.isEmpty()) {
								try {
									Desktop.getDesktop().browse(java.net.URI.create(url));
								} catch (Exception ex) {
									logging.logToError("Error opening URL: " + ex.getMessage());
								}
							}
						}
					}
				}
			}
		});
	}
	
	/**
	 * Sets the programs to display in the table
	 * 
	 * @param programs The programs to display
	 */
	public void setPrograms(List<Program> programs) {
		this.allPrograms = new ArrayList<>(programs);
		applyFilter();
	}
	
	/**
	 * Adds a selection listener
	 * 
	 * @param listener The listener to add
	 */
	public void addSelectionListener(Consumer<Program> listener) {
		selectionListeners.add(listener);
	}
	
	/**
	 * Notifies all selection listeners
	 * 
	 * @param program The selected program
	 */
	private void notifySelectionListeners(Program program) {
		for (Consumer<Program> listener : selectionListeners) {
			listener.accept(program);
		}
	}
	
	/**
	 * Method to set and apply filter 
	*/
	public void setFilter(String filter) {
		this.currentFilter = filter != null ? filter.toLowerCase() : "";
		applyFilter();
	}

	/**
	 * Method to apply the current filter
	*/
	private void applyFilter() {
		if (currentFilter == null || currentFilter.isEmpty()) {
			// No filter - show all programs
			model.setPrograms(allPrograms);
		} else {
			// Apply filter
			List<Program> filteredPrograms = allPrograms.stream()
					.filter(p -> programMatchesFilter(p, currentFilter))
					.collect(java.util.stream.Collectors.toList());
			model.setPrograms(filteredPrograms);
		}
	}

	/**
	 * Method to check if a program matches the filter
	*/
	private boolean programMatchesFilter(Program program, String filter) {
		// Check if name or handle contains the filter text
		return (program.getName() != null && program.getName().toLowerCase().contains(filter)) ||
				(program.getHandle() != null && program.getHandle().toLowerCase().contains(filter));
	}
	
	/**
	 * Table model for programs
	 */
	private static class ProgramTableModel extends AbstractTableModel {
		
		private static final String[] COLUMN_NAMES = {"Name", "Status", "Following", "Private Program"};
		private List<Program> programs;
		
		/**
		 * Creates a new program table model
		 */
		public ProgramTableModel() {
			this.programs = new ArrayList<>();
		}
		
		/**
		 * Sets the programs to display
		 * 
		 * @param programs The programs to display
		 */
		public void setPrograms(List<Program> programs) {
			this.programs = new ArrayList<>(programs);
			fireTableDataChanged();
		}
		
		/**
		 * Gets a program by row index
		 * 
		 * @param row The row index
		 * @return The program
		 */
		public Program getProgram(int row) {
			if (row >= 0 && row < programs.size()) {
				return programs.get(row);
			}
			return null;
		}
		
		@Override
		public int getRowCount() {
			return programs.size();
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
		public Class<?> getColumnClass(int columnIndex) {
			switch (columnIndex) {
				case 0: // Name
					return String.class;
				case 1: // Status
					return Program.EnumerationViewModel.class;
				case 2: // Following
				case 3: // Private
					return Boolean.class;
				default:
					return Object.class;
			}
		}
		
		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			if (rowIndex < 0 || rowIndex >= programs.size()) {
				return null;
			}
			
			Program program = programs.get(rowIndex);
			
			switch (columnIndex) {
				case 0: // Name
					return program.getName() + " (" + program.getHandle() + ")";
				case 1: // Status
					return program.getStatus();
				case 2: // Following
					return program.isFollowing();
				case 3: // Private Program
					return program.getConfidentialityLevel() != null && 
					   program.getConfidentialityLevel().getId() == 1 && 
					   "InviteOnly".equals(program.getConfidentialityLevel().getValue());
				default:
					return null;
			}
		}
	}
	
	/**
	 * Renderer for boolean values using icons
	 */
	private static class BooleanIconRenderer extends DefaultTableCellRenderer {
		
		private final Icon checkedIcon;
		private final Icon uncheckedIcon;
		
		/**
		 * Creates a new boolean icon renderer
		 */
		public BooleanIconRenderer() {
			setHorizontalAlignment(JLabel.CENTER);
			
			// Create simple icons for checked/unchecked state
			checkedIcon = new CheckIcon(true);
			uncheckedIcon = new CheckIcon(false);
		}
		
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			super.getTableCellRendererComponent(table, "", isSelected, hasFocus, row, column);
			
			if (value instanceof Boolean) {
				setIcon((Boolean) value ? checkedIcon : uncheckedIcon);
			} else {
				setIcon(null);
			}
			
			return this;
		}
		
		/**
		 * Simple check icon
		 */
		private static class CheckIcon implements Icon {
			
			private final boolean checked;
			
			/**
			 * Creates a new check icon
			 * 
			 * @param checked Whether the icon is checked
			 */
			public CheckIcon(boolean checked) {
				this.checked = checked;
			}
			
			@Override
			public void paintIcon(Component c, Graphics g, int x, int y) {
				Graphics2D g2d = (Graphics2D) g.create();
				
				// Set up rendering
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				
				if (checked) {
					g2d.setColor(new Color(16, 185, 129));
					g2d.fillOval(x, y, getIconWidth(), getIconHeight());
					
					g2d.setColor(Color.WHITE);
					g2d.drawLine(x + 4, y + 7, x + 6, y + 10);
					g2d.drawLine(x + 6, y + 10, x + 12, y + 4);
				} else {
					g2d.setColor(Color.LIGHT_GRAY);
					g2d.drawOval(x, y, getIconWidth() - 1, getIconHeight() - 1);
				}
				
				g2d.dispose();
			}
			
			@Override
			public int getIconWidth() {
				return 16;
			}
			
			@Override
			public int getIconHeight() {
				return 16;
			}
		}
	}
}