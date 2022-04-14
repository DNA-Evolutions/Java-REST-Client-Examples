package com.dna.jopt.rest.client.example.touroptimizer.optimizefromsnapshot;

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
import java.util.Optional;

import com.dna.jopt.rest.client.example.touroptimizer.helper.TourOptimizerRestCaller;
import com.dna.jopt.rest.client.model.OptimizationKeySetting;
import com.dna.jopt.rest.client.model.RestOptimization;
import com.dna.jopt.rest.client.model.TextSolution;
import com.dna.jopt.rest.client.util.endpoints.Endpoints;
import com.dna.jopt.rest.client.util.io.json.RestJSONLoader;
import com.dna.jopt.rest.client.util.secretsmanager.SecretsManager;
import com.dna.jopt.rest.client.util.secretsmanager.caughtexception.NoSecretFileFoundException;
import com.dna.jopt.rest.client.util.secretsmanager.caughtexception.SecretNotFoundException;
import com.dna.jopt.rest.client.util.testinputcreation.TestRestOptimizationCreator;
import com.dna.jopt.rest.client.util.testinputcreation.TestSnapshotInput;

/**
 * The Class TourOptimizerFromJSONSnapshotExample.
 * 
 * Optimize a list of Nodes and Resources. The complete optimization definition
 * is loaded from a single snapshot. This snapshot is compatible with our core
 * Java and core .NET JOpt version.
 */
public class TourOptimizerFromJSONSnapshotExample {

    /**
     * The main method of TourOptimizerFromJSONSnapshotExample
     *
     * @param args the arguments
     * @throws IOException                Signals that an I/O exception has
     *                                    occurred.
     * @throws NoSecretFileFoundException the no secret file found exception
     * @throws SecretNotFoundException    the secret not found exception
     */
    public static void main(String[] args) throws IOException, NoSecretFileFoundException, SecretNotFoundException {

	/*
	 * 
	 * Modify me
	 * 
	 */
	boolean isAzureCall = true;

	// Define the data to be used

	/*
	 * 
	 * 
	 */
	TourOptimizerRestCaller tourOptimizerCaller;

	SecretsManager m = new SecretsManager();

	if (isAzureCall) {

	    tourOptimizerCaller = new TourOptimizerRestCaller(Endpoints.AZURE_SWAGGER_TOUROPTIMIZER_URL,
		    Optional.of(m.get("azure")));
	} else {
	    tourOptimizerCaller = new TourOptimizerRestCaller(Endpoints.LOCAL_SWAGGER_TOUROPTIMIZER_URL);
	}

	/*
	 * 
	 * Load snapshot
	 * 
	 */

	RestOptimization opti = RestJSONLoader.readRestOptimization(
		TestSnapshotInput.DEFAULT_PUBLIC_INPUT_JSON_WITHOUT_LICENSE, tourOptimizerCaller.getMapper());

	// Attach a key

	OptimizationKeySetting keySettings = new OptimizationKeySetting();
	keySettings.setJsonLicense(TestRestOptimizationCreator.PUBLIC_JSON_LICENSE);
	opti.getExtension().setKeySetting(keySettings);

	/*
	 *
	 * Optimize
	 *
	 */
	Optional<TextSolution> textSolutionOpt;

	// We simply provide empty connections => This will trigger automatic haversine
	// distance calculations

	RestOptimization result = tourOptimizerCaller.optimize(opti);

	if (result.getExtension() != null) {
	    textSolutionOpt = Optional.ofNullable(result.getExtension().getTextSolution());

	    // Show result
	    textSolutionOpt.ifPresent(ts -> {
		System.out.println("Printing text solution");
		System.out.println(ts);
	    });
	}
    }

}
