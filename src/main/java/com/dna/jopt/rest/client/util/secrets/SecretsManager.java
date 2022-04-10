package com.dna.jopt.rest.client.util.secrets;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SecretsManager {

    private Map<String, String> secretsMap;

    // JSON file needs to look like:
    //    {
    //	  	"azure" : "YOUR_DNA_AZURE_API_KEY",
    //	  	"ANY_OTHER_PROVIDER" : "ANY_OTHER_KEY"
    //    }

    private static final String DEFAULT_SECRETS_PATH = "src/main/resources/secrets/secrets.json";

    public SecretsManager() throws  IOException {
	this(new File(DEFAULT_SECRETS_PATH));
    }

    public SecretsManager(File secretFile) throws IOException {
	// Read in secrets

	ObjectMapper mapper = new ObjectMapper();
	TypeReference<LinkedHashMap<String, String>> typeRef = new TypeReference<LinkedHashMap<String, String>>() {
	};

	this.secretsMap = mapper.readValue(secretFile, typeRef);

    }

    public String get(String key) {

	return this.secretsMap.getOrDefault(key, "");

    }
}
