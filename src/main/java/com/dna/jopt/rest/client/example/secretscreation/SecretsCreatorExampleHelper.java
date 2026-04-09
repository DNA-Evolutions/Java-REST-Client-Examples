package com.dna.jopt.rest.client.example.secretscreation;



/*-
 * #%L
 * JOpt Java REST Client Examples
 * %%
 * Copyright (C) 2017 - 2022 DNA Evolutions GmbH
 * %%
 * This file is subject to the terms and conditions defined in file 'LICENSE.md',
 * which is part of this repository.
 * 
 * If not, see <https://www.dna-evolutions.com/agb-conditions-and-terms/>.
 * #L%
 */

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.dna.jopt.rest.client.util.secretsmanager.SecretsManager;
import com.dna.jopt.rest.client.util.secretsmanager.caughtexception.SecretFileAlreadyPresentException;
import com.dna.jopt.rest.client.util.testinputcreation.TestRestOptimizationCreator;

/**
 * Bootstrap helper that creates the {@code secrets/secrets.json} file required by all examples.
 *
 * <p>Run this class once before running any optimization example. It writes a JSON map
 * with two entries:</p>
 * <ul>
 *   <li>{@code "azure"} &ndash; your DNA Evolutions Azure API subscription key (leave as
 *       placeholder if running locally only).</li>
 *   <li>{@code "joptlic"} &ndash; the JOpt license JSON. Defaults to
 *       {@link com.dna.jopt.rest.client.util.testinputcreation.TestRestOptimizationCreator#PUBLIC_JSON_LICENSE},
 *       a free evaluation license limited to 20 elements.</li>
 * </ul>
 *
 * <p>The file is written via
 * {@link com.dna.jopt.rest.client.util.secretsmanager.SecretsManager#saveSecretsMap} and will
 * fail with a
 * {@link com.dna.jopt.rest.client.util.secretsmanager.caughtexception.SecretFileAlreadyPresentException}
 * if it already exists, to prevent accidental overwrites.</p>
 *
 * @see com.dna.jopt.rest.client.util.secretsmanager.SecretsManager
 */
public class SecretsCreatorExampleHelper {

    /**
     * The main method of SecretsCreatorExampleHelper
     *
     * @param args the arguments
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws SecretFileAlreadyPresentException 
     */
    public static void main(String[] args) throws IOException, SecretFileAlreadyPresentException {

	/*
	 *  Fill me out
	 */

	String myAzureApiSecret = "YOUR_DNA_PROVIDED_API_KEY"; // Leave blank, if not present

	// By default we use the limited PUBLIC_JSON_LICENSE. 
	// 
	// Please note: You will only be able to run the RestTourOptimizer in your local docker 
	// environment if no azureApiSecret is provided using this license.
	String myJoptTourOptimizerSecret = TestRestOptimizationCreator.PUBLIC_JSON_LICENSE;

	/*
	 * 
	 * 
	 */

	// Create a json file and save to target directory
	Map<String, String> secretsmap = new HashMap<>();

	if (!myAzureApiSecret.isEmpty()) {
	    secretsmap.put("azure", myAzureApiSecret);
	}

	if (!myJoptTourOptimizerSecret.isEmpty()) {
	    secretsmap.put("joptlic", myJoptTourOptimizerSecret);
	}

	if (!secretsmap.isEmpty()) {
	    SecretsManager.saveSecretsMap(secretsmap);
	} else {
	    System.out.println("Nothing was saved");
	}

    }
}
