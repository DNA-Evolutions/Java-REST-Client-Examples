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
 * The Class SecretsCreatorExampleHelper.
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
