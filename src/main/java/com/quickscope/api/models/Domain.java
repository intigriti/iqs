package com.quickscope.api.models;

import java.util.Objects;

/**
 * Represents a domain in a program from the Intigriti API
 * Based on DomainViewModel from the API
 */
public class Domain {

	private String id;
	private Program.EnumerationViewModel type;
	private String endpoint;
	private Program.EnumerationViewModel tier;
	private String description;
	
	/**
	 * Gets the domain ID
	 * 
	 * @return The domain ID
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * Sets the domain ID
	 * 
	 * @param id The domain ID
	 */
	public void setId(String id) {
		this.id = id;
	}
	
	/**
	 * Gets the domain type
	 * 
	 * @return The domain type
	 */
	public Program.EnumerationViewModel getType() {
		return type;
	}
	
	/**
	 * Sets the domain type
	 * 
	 * @param type The domain type
	 */
	public void setType(Program.EnumerationViewModel type) {
		this.type = type;
	}
	
	/**
	 * Gets the domain endpoint
	 * 
	 * @return The domain endpoint
	 */
	public String getEndpoint() {
		return endpoint.strip();
	}
	
	/**
	 * Sets the domain endpoint
	 * 
	 * @param endpoint The domain endpoint
	 */
	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}
	
	/**
	 * Gets the domain tier
	 * 
	 * @return The domain tier
	 */
	public Program.EnumerationViewModel getTier() {
		return tier;
	}
	
	/**
	 * Sets the domain tier
	 * 
	 * @param tier The domain tier
	 */
	public void setTier(Program.EnumerationViewModel tier) {
		this.tier = tier;
	}
	
	/**
	 * Gets the domain description
	 * 
	 * @return The domain description
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * Sets the domain description
	 * 
	 * @param description The domain description
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Domain domain = (Domain) o;
		return Objects.equals(id, domain.id);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
	
	@Override
	public String toString() {
		return endpoint + (description != null && !description.isEmpty() ? " - " + description : "");
	}
}