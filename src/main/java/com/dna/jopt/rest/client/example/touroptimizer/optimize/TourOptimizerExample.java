package com.dna.jopt.rest.client.example.touroptimizer.optimize;

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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.dna.jopt.rest.client.example.touroptimizer.helper.TourOptimizerRestCaller;
import com.dna.jopt.rest.client.model.ElementConnection;
import com.dna.jopt.rest.client.model.Position;
import com.dna.jopt.rest.client.model.RestOptimization;
import com.dna.jopt.rest.client.model.TextSolution;
import com.dna.jopt.rest.client.util.endpoints.Endpoints;
import com.dna.jopt.rest.client.util.io.json.RestJSONParser;
import com.dna.jopt.rest.client.util.secrets.SecretsManager;
import com.dna.jopt.rest.client.util.secrets.caughtexception.NoSecretFileFoundException;
import com.dna.jopt.rest.client.util.secrets.caughtexception.SecretNotFoundException;
import com.dna.jopt.rest.client.util.testinputcreation.TestPositionsInput;

/**
 * The Class TourOptimizerExample. Optimize a list of Nodes and Resources.
 * Connections are not provided; moreover, they will be created using haversine
 * calculations on the server-side.
 */
public class TourOptimizerExample {

    /**
     * The main method of TourOptimizerExample
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
	boolean isSave2JSON = true;

	List<Position> nodePoss = TestPositionsInput.defaultSydneyNodePositions();
	List<Position> resourcePoss = TestPositionsInput.defaultSydneyResourcePositions();

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
	 * Optimize
	 *
	 */
	Optional<TextSolution> textSolutionOpt;

	// We simply provide empty connections => This will trigger automatic haversine
	// distance calculations

	List<ElementConnection> emptyConnections = new ArrayList<>();

	RestOptimization result = tourOptimizerCaller.optimize(nodePoss, resourcePoss, emptyConnections,
		Optional.of(m.get("joptlic")));

	textSolutionOpt = Optional.ofNullable(result.getExtension().getTextSolution());

	// Show result
	textSolutionOpt.ifPresent(ts -> {
	    System.out.println("Printing text solution");
	    System.out.println(ts);
	});

	/*
	 * Save to JSON snapshot
	 */
	if (isSave2JSON) {

	    String jsonFile = "OptimizationTest" + ".json";

	    RestJSONParser.toJsonFile(result, new File(jsonFile), tourOptimizerCaller.getMapper());
	}

    }

}
