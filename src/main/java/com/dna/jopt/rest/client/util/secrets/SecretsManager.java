package com.dna.jopt.rest.client.util.secrets;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

import com.dna.jopt.rest.client.util.secrets.caughtexception.NoSecretFileFoundException;
import com.dna.jopt.rest.client.util.secrets.caughtexception.SecretFileAlreadyPresentException;
import com.dna.jopt.rest.client.util.secrets.caughtexception.SecretNotFoundException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SecretsManager {

    private Map<String, String> secretsMap;

    public static final String DEFAULT_SANDBOX_RELATIVE_PROJECT_DIR = "jopt.rest.examples";

    /*
     * JSON file needs to look like:
     * 
     * { 
     * 	"azure" : "YOUR_DNA_AZURE_API_KEY", "joptlic": "YOUR_JOPT_JSON_LIC",
     * 	"ANY_OTHER_PROVIDER" : "ANY_OTHER_KEY" }
     * 
     */

    public static final String DEFAULT_RELATIVE_SECRETS_PATH = "secrets/secrets.json";

    private static final Function<String, File> SECRET_FILE_FINDER_FUNCTION = path -> {
	File secretsFile = new File(DEFAULT_RELATIVE_SECRETS_PATH);

	if (!secretsFile.exists()) {
	    // If we run the secretsManager inside the sandbox the working directory is NOT
	    // the project directory.
	    // Therefore, we check if the secrets file is insided the workingdir/projectdir/
	    secretsFile = new File(System.getProperty("user.dir") + File.separator
		    + DEFAULT_SANDBOX_RELATIVE_PROJECT_DIR + File.separator + DEFAULT_RELATIVE_SECRETS_PATH);
	}

	return secretsFile;
    };

    public SecretsManager(String secretFilePath) throws NoSecretFileFoundException {
	this(new File(secretFilePath));
    }

    public SecretsManager() throws NoSecretFileFoundException {

	this(SECRET_FILE_FINDER_FUNCTION.apply(DEFAULT_RELATIVE_SECRETS_PATH));
    }

    public SecretsManager(Map<String, String> secretsMap) {
	this.secretsMap = secretsMap;
    }

    private SecretsManager(File secretFile) throws NoSecretFileFoundException {
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

    public static void saveSecretsMap(Map<String, String> secretsMap)
	    throws IOException, SecretFileAlreadyPresentException {

	File targetFile = SECRET_FILE_FINDER_FUNCTION.apply(DEFAULT_RELATIVE_SECRETS_PATH);

	if (targetFile.exists()) {
	    throw new SecretFileAlreadyPresentException("Secrets File already present.");
	}

	ObjectMapper mapper = new ObjectMapper();
	mapper.writerWithDefaultPrettyPrinter().writeValue(targetFile, secretsMap);
    }
}
