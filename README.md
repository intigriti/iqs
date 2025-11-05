# Intigriti Quick Scope

Intigriti Quick Scope is a Burp Suite extension that automates project setup by pulling data from the Intigriti Researcher API. This extension makes it easy to import target scopes from bug bounty programs directly into Burp Suite.

Intigriti Quick Scope is capable of:
- Applying target scope settings (including in-scope and out-of-scope rules)
- Adding mandatory scope request headers to Burp Suite (such as the User-Agent header)

## Features
- Fetch available programs (private & public) from Intigriti using the Intigriti Researcher API
- Auto-apply scope configuration directly into Burp Suite with a single click
- Inspect program scope requirements

## Installation

### From JAR File

1. Download the latest release JAR file from the releases page
2. Open Burp Suite
3. Go to the "Extensions" tab
4. Click "Add" in the "Installed" tab (underneath the "Burp Extensions" section)
5. Set the extension type to "Java"
6. Select the downloaded JAR file
7. Click "Next" to load the extension

### Building from Source

1. Clone the repository
2. Build using Gradle:
   ```
   gradle build
   ```
3. The JAR file will be created in the `build/libs/` folder
4. Load the JAR file into Burp Suite as described above

## Usage

1. Configure the Intigriti API connection:
   - Enter your Intigriti API key
   - Test the connection

2. Load programs:
   - Click "Load Programs" to fetch available programs
   - Filter programs by status, follow state, or program type (public, invite only, ...)

3. View and import domains:
   - Select a program to inspect its details and scope configuration
   - Filter domains by type
   - Select domains to add to scope or add all domains

4. Manage scope:
   - Use the "Reset Scope" button to remove all items from scope
   - Import domains from multiple programs

## Obtaining an Intigriti API Key

To use this extension, you need an Intigriti API key:

1. Log in to your Intigriti account
2. Go to your profile settings
3. Navigate to the "API Keys" section
4. Generate a new API key with the necessary permissions
5. Copy the API key to use in the Intigriti Quick Scope extension

## Requirements

- Burp Suite Community Edition (CE) or Professional
- Java 11 or later

## License

This project is licensed and available under the [MIT License](LICENSE.md).