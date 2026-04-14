package com.iqs.api;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.logging.Logging;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import com.iqs.api.models.Domain;
import com.iqs.api.models.Program;
import com.iqs.api.models.ProgramDetails;
import com.iqs.config.QuickScopeConfig;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Client for interacting with the Intigriti Researcher API
 */
public class IntigritiApiClient {

	private static final String API_VERSION = "v1";
	
	private final MontoyaApi api;
	private final Logging logging;
	private final QuickScopeConfig config;
	private final OkHttpClient client;
	private final Gson gson;
	
	/**
	 * Creates a new API client
	 * 
	 * @param api The Montoya API instance
	 * @param config The Intigriti API configuration
	 */
	public IntigritiApiClient(MontoyaApi api, QuickScopeConfig config) {
		this.api = api;
		this.logging = api.logging();
		this.config = config;
		
		// Configure HTTP client
		this.client = new OkHttpClient.Builder()
			.connectTimeout(30, TimeUnit.SECONDS)
			.readTimeout(30, TimeUnit.SECONDS)
			.writeTimeout(30, TimeUnit.SECONDS)
			.build();
		
		// Configure JSON parser
		this.gson = new GsonBuilder().create();
	}

	/**
	 * Tests the connection to the Intigriti API
	 * 
	 * @return True if the connection is successful
	 */
	public boolean testConnection() {
		try {
			// Try to get the program list
			this.getPrograms();
			return true;
		} catch (Exception e) {
			logging.logToError("Connection test failed: " + e.getMessage());
			return false;
		}
	}
	
	/**
	 * Gets the list of available programs
	 * 
	 * @return The list of programs
	 * @throws IOException If an I/O error occurs
	 * @throws ApiException If an API error occurs
	 */
	public List<Program> getPrograms() throws IOException, ApiException {
		// Build request parameters
		StringBuilder urlBuilder = new StringBuilder(config.getApiUrl());
		urlBuilder.append("/").append(API_VERSION).append("/programs");
		
		// Add query parameters
		boolean hasParams = false;
		
		if (config.isOnlyActive()) {
			urlBuilder.append("?statusId=3");
			hasParams = true;
		}
		
		if (config.isOnlyFollowing()) {
			urlBuilder.append(hasParams ? "&" : "?").append("following=true");
			hasParams = true;
		}
		
		// Add pagination parameters
		urlBuilder.append(hasParams ? "&" : "?").append("limit=500&offset=0");
		
		// Execute request
		Request request = new Request.Builder()
			.url(urlBuilder.toString())
			.addHeader("Authorization", "Bearer " + config.getApiKey())
			.build();
		
		try (Response response = client.newCall(request).execute()) {
			if (!response.isSuccessful()) {
				throw new ApiException("API request failed with status code " + response.code());
			}
			
			String responseBody = response.body().string();
			logging.logToOutput("API response: " + responseBody.substring(0, Math.min(100, responseBody.length())) + "...");
			
			// Parse response
			JsonObject responseJson = gson.fromJson(responseBody, JsonObject.class);
			JsonArray records = responseJson.getAsJsonArray("records");
			
			Type listType = new TypeToken<ArrayList<Program>>(){}.getType();
			List<Program> allPrograms = gson.fromJson(records, listType);

			// Apply confidentiality filter in code if needed
			if (config.isOnlyPrivate()) {
				return allPrograms.stream()
					.filter(p -> p.getConfidentialityLevel() != null && 
							p.getConfidentialityLevel().getId() == 1 && 
							"InviteOnly".equals(p.getConfidentialityLevel().getValue()))
					.collect(Collectors.toList());
			}

			return allPrograms;
		}
	}
	
	/**
	 * Gets the details of a program
	 * 
	 * @param programId The ID of the program
	 * @return The program details
	 * @throws IOException If an I/O error occurs
	 * @throws ApiException If an API error occurs
	 */
	public ProgramDetails getProgramDetails(String programId) throws IOException, ApiException {
		// Build request URL
		String url = config.getApiUrl() + "/" + API_VERSION + "/programs/" + programId;
		
		// Execute request
		Request request = new Request.Builder()
			.url(url)
			.addHeader("Authorization", "Bearer " + config.getApiKey())
			.build();
		
		try (Response response = client.newCall(request).execute()) {
			if (!response.isSuccessful()) {
				throw new ApiException("API request failed with status code " + response.code());
			}
			
			String responseBody = response.body().string();
			
			// Parse response
			return gson.fromJson(responseBody, ProgramDetails.class);
		}
	}
	
	/**
	 * Gets the domains of a program
	 * 
	 * @param programId The ID of the program
	 * @param versionId The ID of the version
	 * @return The domains
	 * @throws IOException If an I/O error occurs
	 * @throws ApiException If an API error occurs
	 */
	public List<Domain> getProgramDomains(String programId, String versionId) throws IOException, ApiException {
		// Build request URL
		String url = config.getApiUrl() + "/" + API_VERSION + "/programs/" + programId + "/domains/" + versionId;
		
		// Execute request
		Request request = new Request.Builder()
			.url(url)
			.addHeader("Authorization", "Bearer " + config.getApiKey())
			.build();
		
		try (Response response = client.newCall(request).execute()) {
			if (!response.isSuccessful()) {
				throw new ApiException("API request failed with status code " + response.code());
			}
			
			String responseBody = response.body().string();
			
			// Parse response
			JsonObject responseJson = gson.fromJson(responseBody, JsonObject.class);
			JsonObject domains = responseJson.getAsJsonObject("domains");
			JsonArray content = domains.getAsJsonArray("content");
			
			Type listType = new TypeToken<ArrayList<Domain>>(){}.getType();
			return gson.fromJson(content, listType);
		}
	}
	
	/**
	 * Exception thrown when an API error occurs
	 */
	public static class ApiException extends Exception {
		
		/**
		 * Creates a new API exception
		 * 
		 * @param message The error message
		 */
		public ApiException(String message) {
			super(message);
		}
		
		/**
		 * Creates a new API exception
		 * 
		 * @param message The error message
		 * @param cause The cause of the exception
		 */
		public ApiException(String message, Throwable cause) {
			super(message, cause);
		}
	}
}