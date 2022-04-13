package com.dna.jopt.rest.client.util.secrets;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import com.dna.jopt.rest.client.util.secrets.caughtexception.NoSecretFileFoundException;
import com.dna.jopt.rest.client.util.secrets.caughtexception.SecretNotFoundException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SecretsManager {

    private Map<String, String> secretsMap;

    /*
     * JSON file needs to look like: 
     * 
     * { 
     * 	 "azure" : "YOUR_DNA_AZURE_API_KEY",
     * 	 "joptlic": "YOUR_JOPT_JSON_LIC",
     *   "ANY_OTHER_PROVIDER" : "ANY_OTHER_KEY"
     * }
     * 
     */ 

    public static final String DEFAULT_SECRETS_PATH = "src/main/resources/secrets/secrets.json";

    public SecretsManager(String secretFilePath) throws NoSecretFileFoundException {
	this(new File(secretFilePath));
    }

    public SecretsManager() throws NoSecretFileFoundException {
	this(new File(DEFAULT_SECRETS_PATH));
    }

    public SecretsManager(Map<String, String> secretsMap) {

	this.secretsMap = secretsMap;

    }

    public SecretsManager(File secretFile) throws NoSecretFileFoundException {
	// Read in secrets

	if (secretFile == null) {
	    throw new NoSecretFileFoundException("The secret file cannot be null");
	}

	ObjectMapper mapper = new ObjectMapper();
	TypeReference<LinkedHashMap<String, String>> typeRef = new TypeReference<LinkedHashMap<String, String>>() {
	};

	try {
	    this.secretsMap = mapper.readValue(secretFile, typeRef);
	} catch (IOException e) {
	    throw new NoSecretFileFoundException("There is no secret file present in: " + secretFile.getAbsolutePath());
	}

    }

    public String get(String key) throws SecretNotFoundException {

	if (key == null) {
	    throw new SecretNotFoundException("The key cannot be null");
	}

	String secret = this.secretsMap.get(key);

	if (secret == null) {
	    throw new SecretNotFoundException("The secret value for " + key + " cannot be found.");
	}
	return secret;

    }

    public static void saveSecretsMap(File targetFile, Map<String, String> secretsMap) throws IOException {
	ObjectMapper mapper = new ObjectMapper();

	mapper.writerWithDefaultPrettyPrinter().writeValue(targetFile, secretsMap);
    }
}
