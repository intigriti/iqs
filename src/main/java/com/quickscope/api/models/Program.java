package com.quickscope.api.models;

import java.util.Objects;

/**
 * Represents a program from the Intigriti API
 * Based on ProgramOverviewViewModel from the API
 */
public class Program {

	private String id;
	private String handle;
	private String name;
	private boolean following;
	private EnumerationViewModel status;
	private EnumerationViewModel type;
	private EnumerationViewModel confidentialityLevel;
	private ProgramWebLinks webLinks;
	
	/**
	 * Creates a new Program instance
	 */
	public Program() {
	}
	
	/**
	 * Gets the program ID
	 * 
	 * @return The program ID
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * Sets the program ID
	 * 
	 * @param id The program ID
	 */
	public void setId(String id) {
		this.id = id;
	}
	
	/**
	 * Gets the program handle
	 * 
	 * @return The program handle
	 */
	public String getHandle() {
		return handle;
	}
	
	/**
	 * Sets the program handle
	 * 
	 * @param handle The program handle
	 */
	public void setHandle(String handle) {
		this.handle = handle;
	}
	
	/**
	 * Gets the program name
	 * 
	 * @return The program name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Sets the program name
	 * 
	 * @param name The program name
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Checks if the user is following the program
	 * 
	 * @return True if the user is following the program
	 */
	public boolean isFollowing() {
		return following;
	}
	
	/**
	 * Sets whether the user is following the program
	 * 
	 * @param following Whether the user is following the program
	 */
	public void setFollowing(boolean following) {
		this.following = following;
	}

	/**
	 * Gets the program status
	 * 
	 * @return The program status
	 */
	public EnumerationViewModel getStatus() {
		return status;
	}
	
	/**
	 * Sets the program status
	 * 
	 * @param status The program status
	 */
	public void setStatus(EnumerationViewModel status) {
		this.status = status;
	}
	
	/**
	 * Gets the program type
	 * 
	 * @return The program type
	 */
	public EnumerationViewModel getType() {
		return type;
	}
	
	/**
	 * Sets the program type
	 * 
	 * @param type The program type
	 */
	public void setType(EnumerationViewModel type) {
		this.type = type;
	}

	/**
	 * Gets the program confidentiality level
	 * 
	 * @return The program confidentiality level
	 */
	public EnumerationViewModel getConfidentialityLevel() {
		return confidentialityLevel;
	}

	/**
	 * Sets the program confidentiality level
	 * 
	 * @param confidentialityLevel The program confidentiality level
	 */
	public void setConfidentialityLevel(EnumerationViewModel confidentialityLevel) {
		this.confidentialityLevel = confidentialityLevel;
	}
	
	/**
	 * Gets the program web links
	 * 
	 * @return The program web links
	 */
	public ProgramWebLinks getWebLinks() {
		return webLinks;
	}
	
	/**
	 * Sets the program web links
	 * 
	 * @param webLinks The program web links
	 */
	public void setWebLinks(ProgramWebLinks webLinks) {
		this.webLinks = webLinks;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Program program = (Program) o;
		return Objects.equals(id, program.id);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
	
	@Override
	public String toString() {
		return name + " (" + handle + ")";
	}
	
	/**
	 * Represents a web link for a program
	 */
	public static class ProgramWebLinks {
		private String detail;
		
		/**
		 * Gets the detail URL
		 * 
		 * @return The detail URL
		 */
		public String getDetail() {
			return detail;
		}
		
		/**
		 * Sets the detail URL
		 * 
		 * @param detail The detail URL
		 */
		public void setDetail(String detail) {
			this.detail = detail;
		}
	}
	
	/**
	 * Represents an enumeration value from the API
	 */
	public static class EnumerationViewModel {
		private int id;
		private String value;
		
		/**
		 * Gets the enumeration ID
		 * 
		 * @return The enumeration ID
		 */
		public int getId() {
			return id;
		}
		
		/**
		 * Sets the enumeration ID
		 * 
		 * @param id The enumeration ID
		 */
		public void setId(int id) {
			this.id = id;
		}
		
		/**
		 * Gets the enumeration value
		 * 
		 * @return The enumeration value
		 */
		public String getValue() {
			return value;
		}
		
		/**
		 * Sets the enumeration value
		 * 
		 * @param value The enumeration value
		 */
		public void setValue(String value) {
			this.value = value;
		}
		
		@Override
		public String toString() {
			return value;
		}
	}
}