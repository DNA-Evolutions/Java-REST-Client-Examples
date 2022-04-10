package com.dna.jopt.rest.client.example.touroptimizer;

import java.io.IOException;
import java.util.Optional;

import com.dna.jopt.rest.client.example.touroptimizer.helper.TourOptimizerRestCaller;
import com.dna.jopt.rest.client.model.OptimizationKeySetting;
import com.dna.jopt.rest.client.model.RestOptimization;
import com.dna.jopt.rest.client.model.TextSolution;
import com.dna.jopt.rest.client.util.endpoints.Endpoints;
import com.dna.jopt.rest.client.util.io.json.RestJSONLoader;
import com.dna.jopt.rest.client.util.secrets.SecretsManager;
import com.dna.jopt.rest.client.util.testinputcreation.TestRestOptimizationCreator;
import com.dna.jopt.rest.client.util.testinputcreation.TestSnapshotInput;

public class TourOptimizerFromJSONSnapshotExample {

    public static void main(String[] args) throws IOException {

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
