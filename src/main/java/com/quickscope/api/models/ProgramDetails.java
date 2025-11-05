package com.quickscope.api.models;

import java.util.List;

import com.quickscope.api.models.Program.EnumerationViewModel;

/**
 * Represents detailed information about a program from the Intigriti API
 * Based on ProgramDetailViewModel from the API
 */
public class ProgramDetails {

	private String id;
	private String handle;
	private String name;
	private boolean following;
	private Program.EnumerationViewModel confidentialityLevel;
	private Program.EnumerationViewModel status;
	private Program.EnumerationViewModel type;
	private VersionViewModel domains;
	private VersionWithAttachmentsViewModel rulesOfEngagement;
	private Program.ProgramWebLinks webLinks;

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
	 * Gets the confidentiality level
	 * 
	 * @return The confidentiality level
	 */
	public Program.EnumerationViewModel getConfidentialityLevel() {
		return confidentialityLevel;
	}

	/**
	 * Sets the confidentiality level
	 * 
	 * @param confidentialityLevel The confidentiality level
	 */
	public void setConfidentialityLevel(Program.EnumerationViewModel confidentialityLevel) {
		this.confidentialityLevel = confidentialityLevel;
	}

	/**
	 * Gets the program status
	 * 
	 * @return The program status
	 */
	public Program.EnumerationViewModel getStatus() {
		return status;
	}

	/**
	 * Sets the program status
	 * 
	 * @param status The program status
	 */
	public void setStatus(Program.EnumerationViewModel status) {
		this.status = status;
	}

	/**
	 * Gets the program type
	 * 
	 * @return The program type
	 */
	public Program.EnumerationViewModel getType() {
		return type;
	}

	/**
	 * Sets the program type
	 * 
	 * @param type The program type
	 */
	public void setType(Program.EnumerationViewModel type) {
		this.type = type;
	}

	/**
	 * Gets the domains information
	 * 
	 * @return The domains information
	 */
	public VersionViewModel getDomains() {
		return domains;
	}

	/**
	 * Sets the domains information
	 * 
	 * @param domains The domains information
	 */
	public void setDomains(VersionViewModel domains) {
		this.domains = domains;
	}

	/**
	 * Gets the rules of engagement
	 * 
	 * @return The rules of engagement
	 */
	public VersionWithAttachmentsViewModel getRulesOfEngagement() {
		return rulesOfEngagement;
	}

	/**
	 * Sets the rules of engagement
	 * 
	 * @param rulesOfEngagement The rules of engagement
	 */
	public void setRulesOfEngagement(VersionWithAttachmentsViewModel rulesOfEngagement) {
		this.rulesOfEngagement = rulesOfEngagement;
	}

	/**
	 * Gets the program web links
	 * 
	 * @return The program web links
	 */
	public Program.ProgramWebLinks getWebLinks() {
		return webLinks;
	}

	/**
	 * Sets the program web links
	 * 
	 * @param webLinks The program web links
	 */
	public void setWebLinks(Program.ProgramWebLinks webLinks) {
		this.webLinks = webLinks;
	}

	/**
	 * Represents a versioned list of domains
	 */
	public static class VersionViewModel {
		private String id;
		private long createdAt;
		private List<Domain> content;

		/**
		 * Gets the version ID
		 * 
		 * @return The version ID
		 */
		public String getId() {
			return id;
		}

		/**
		 * Sets the version ID
		 * 
		 * @param id The version ID
		 */
		public void setId(String id) {
			this.id = id;
		}

		/**
		 * Gets the creation timestamp
		 * 
		 * @return The creation timestamp
		 */
		public long getCreatedAt() {
			return createdAt;
		}

		/**
		 * Sets the creation timestamp
		 * 
		 * @param createdAt The creation timestamp
		 */
		public void setCreatedAt(long createdAt) {
			this.createdAt = createdAt;
		}

		/**
		 * Gets the content
		 * 
		 * @return The content
		 */
		public List<Domain> getContent() {
			return content;
		}

		/**
		 * Sets the content
		 * 
		 * @param content The content
		 */
		public void setContent(List<Domain> content) {
			this.content = content;
		}
	}

	/**
	 * Represents a versioned object with attachments
	 */
	public static class VersionWithAttachmentsViewModel {
		private String id;
		private long createdAt;
		private RulesOfEngagementContent content;
		private List<Attachment> attachments;

		/**
		 * Gets the version ID
		 * 
		 * @return The version ID
		 */
		public String getId() {
			return id;
		}

		/**
		 * Sets the version ID
		 * 
		 * @param id The version ID
		 */
		public void setId(String id) {
			this.id = id;
		}

		/**
		 * Gets the creation timestamp
		 * 
		 * @return The creation timestamp
		 */
		public long getCreatedAt() {
			return createdAt;
		}

		/**
		 * Sets the creation timestamp
		 * 
		 * @param createdAt The creation timestamp
		 */
		public void setCreatedAt(long createdAt) {
			this.createdAt = createdAt;
		}

		/**
		 * Gets the content
		 * 
		 * @return The content
		 */
		public RulesOfEngagementContent getContent() {
			return content;
		}

		/**
		 * Sets the content
		 * 
		 * @param content The content
		 */
		public void setContent(RulesOfEngagementContent content) {
			this.content = content;
		}

		/**
		 * Gets the attachments
		 * 
		 * @return The attachments
		 */
		public List<Attachment> getAttachments() {
			return attachments;
		}

		/**
		 * Sets the attachments
		 * 
		 * @param attachments The attachments
		 */
		public void setAttachments(List<Attachment> attachments) {
			this.attachments = attachments;
		}
	}

	/**
	 * Represents rules of engagement content
	 */
	public static class RulesOfEngagementContent {
		private String description;
		private TestingRequirements testingRequirements;
		private boolean safeHarbour;

		/**
		 * Gets the description
		 * 
		 * @return The description
		 */
		public String getDescription() {
			return description;
		}

		/**
		 * Sets the description
		 * 
		 * @param description The description
		 */
		public void setDescription(String description) {
			this.description = description;
		}

		/**
		 * Gets the testing requirements
		 * 
		 * @return The testing requirements
		 */
		public TestingRequirements getTestingRequirements() {
			return testingRequirements;
		}

		/**
		 * Sets the testing requirements
		 * 
		 * @param testingRequirements The testing requirements
		 */
		public void setTestingRequirements(TestingRequirements testingRequirements) {
			this.testingRequirements = testingRequirements;
		}

		/**
		 * Checks if safe harbour is enabled
		 * 
		 * @return True if safe harbour is enabled
		 */
		public boolean isSafeHarbour() {
			return safeHarbour;
		}

		/**
		 * Sets whether safe harbour is enabled
		 * 
		 * @param safeHarbour Whether safe harbour is enabled
		 */
		public void setSafeHarbour(boolean safeHarbour) {
			this.safeHarbour = safeHarbour;
		}
	}

	/**
	 * Represents testing requirements
	 */
	public static class TestingRequirements {
		private boolean intigritiMe;
		private Integer automatedTooling;
		private String userAgent;
		private String requestHeader;

		/**
		 * Checks if intigritiMe is required
		 * 
		 * @return True if intigritiMe is required
		 */
		public boolean isIntigritiMe() {
			return intigritiMe;
		}

		/**
		 * Sets whether intigritiMe is required
		 * 
		 * @param intigritiMe Whether intigritiMe is required
		 */
		public void setIntigritiMe(boolean intigritiMe) {
			this.intigritiMe = intigritiMe;
		}

		/**
		 * Gets the automated tooling value
		 * 
		 * @return The automated tooling value
		 */
		public Integer getAutomatedTooling() {
			return automatedTooling;
		}

		/**
		 * Sets the automated tooling value
		 * 
		 * @param automatedTooling The automated tooling value
		 */
		public void setAutomatedTooling(Integer automatedTooling) {
			this.automatedTooling = automatedTooling;
		}

		/**
		 * Gets the user agent
		 * 
		 * @return The user agent
		 */
		public String getUserAgent() {
			return userAgent;
		}

		/**
		 * Sets the user agent
		 * 
		 * @param userAgent The user agent
		 */
		public void setUserAgent(String userAgent) {
			this.userAgent = userAgent;
		}

		/**
		 * Gets the request header
		 * 
		 * @return The request header
		 */
		public String getRequestHeader() {
			return requestHeader;
		}

		/**
		 * Sets the request header
		 * 
		 * @param requestHeader The request header
		 */
		public void setRequestHeader(String requestHeader) {
			this.requestHeader = requestHeader;
		}
	}

	/**
	 * Represents an attachment
	 */
	public static class Attachment {
		private String url;
		private int code;

		/**
		 * Gets the URL
		 * 
		 * @return The URL
		 */
		public String getUrl() {
			return url;
		}

		/**
		 * Sets the URL
		 * 
		 * @param url The URL
		 */
		public void setUrl(String url) {
			this.url = url;
		}

		/**
		 * Gets the code
		 * 
		 * @return The code
		 */
		public int getCode() {
			return code;
		}

		/**
		 * Sets the code
		 * 
		 * @param code The code
		 */
		public void setCode(int code) {
			this.code = code;
		}
	}
}