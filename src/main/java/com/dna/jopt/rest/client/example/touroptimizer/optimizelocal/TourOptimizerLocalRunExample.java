package com.dna.jopt.rest.client.example.touroptimizer.optimizelocal;

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
import com.dna.jopt.rest.client.util.secretsmanager.SecretsManager;
import com.dna.jopt.rest.client.util.secretsmanager.caughtexception.NoSecretFileFoundException;
import com.dna.jopt.rest.client.util.secretsmanager.caughtexception.SecretNotFoundException;
import com.dna.jopt.rest.client.util.testinputcreation.TestPositionsInput;

/**
 * The Class TourOptimizerLocalRunExample.
 * 
 * Optimize a list of Nodes and Resources on your local machine. A docker
 * environment is required.
 * 
 */
public class TourOptimizerLocalRunExample {

    /**
     * The main method of TourOptimizerLocalRunExample
     *
     * @param args the arguments
     * @throws IOException                Signals that an I/O exception has
     *                                    occurred.
     * @throws NoSecretFileFoundException the no secret file found exception
     * @throws SecretNotFoundException    the secret not found exception
     */
    public static void main(String[] args) throws IOException, NoSecretFileFoundException, SecretNotFoundException {

	/*
	 * Please refer to https://github.com/DNA-Evolutions/Docker-REST-TourOptimizer
	 * on how to setup a local running Rest TourOptimizer and getting more
	 * information
	 * 
	 * Quick Start: ============== 1) You can also execute:
	 * 
	 * docker run -d --rm --name myJOptTourOptimizer -e
	 * SPRING_PROFILES_ACTIVE="cors" -p 8081:8081 dnaevolutions/jopt_touroptimizer
	 * 
	 * from any console on any machine with reasonable new docker environment.
	 * 
	 * 2) Visit http://localhost:8081/ from any browser, and see if service came up
	 * 
	 * 3) You can run this example :-)
	 * 
	 * 
	 */

	/*
	 * 
	 * Modify me
	 * 
	 */
	boolean isSave2JSON = true;

	// We take the small datasets by default, so that we do not exceed the 15
	// elements limt of the free license
	List<Position> nodePoss = TestPositionsInput.defaultSmallSydneyNodePositions();
	List<Position> resourcePoss = TestPositionsInput.defaultSmallSydneyResourcePositions();

	// Define the data to be used

	/*
	 * 
	 * 
	 */

	// This assumes you are hosting the RestTourOptimizer in your local machine at
	// port 8081. Of course you can use any port you want, but you need to modify
	// the endpoint url
	//
	// BY default we use:
	// LOCAL_SWAGGER_TOUROPTIMIZER_URL = "http://localhost:8081"
	//
	TourOptimizerRestCaller tourOptimizerCaller = new TourOptimizerRestCaller(
		Endpoints.LOCAL_SWAGGER_TOUROPTIMIZER_URL);

	/*
	 *
	 * Optimize
	 *
	 */
	Optional<TextSolution> textSolutionOpt;

	// We simply provide empty connections => This will trigger automatic haversine
	// distance calculations

	List<ElementConnection> emptyConnections = new ArrayList<>();

	SecretsManager m = new SecretsManager();
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

	    String jsonFile = "OptimizationLocalTest" + ".json";

	    RestJSONParser.toJsonFile(result, new File(jsonFile), tourOptimizerCaller.getMapper());
	}

    }

}
