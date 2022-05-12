package com.dna.jopt.rest.client.example.touroptimizer.fireandforget;

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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.dna.jopt.rest.client.example.touroptimizer.helper.TourOptimizerRestCaller;
import com.dna.jopt.rest.client.model.CreatorSetting;
import com.dna.jopt.rest.client.model.ElementConnection;
import com.dna.jopt.rest.client.model.Position;
import com.dna.jopt.rest.client.util.endpoints.Endpoints;
import com.dna.jopt.rest.client.util.secretsmanager.SecretsManager;
import com.dna.jopt.rest.client.util.secretsmanager.caughtexception.NoSecretFileFoundException;
import com.dna.jopt.rest.client.util.secretsmanager.caughtexception.SecretNotFoundException;
import com.dna.jopt.rest.client.util.testinputcreation.TestPositionsInput;

/**
 * The Class TourOptimizerExample. Optimize a list of Nodes and Resources in fire and forget mode. If the TourOptimizer is not
 * started with an active Mongo Database connection, the call will result in a 404 not found exception.
 * Connections are not provided; moreover, they will be created using haversine
 * calculations on the server-side.
 */
public class TourOptimizerFireAndForgetExample {

    /**
     * The main method of TourOptimizerExample
     *
     * @param args the arguments
     * @throws IOException                Signals that an I/O exception has
     *                                    occurred.
     * @throws NoSecretFileFoundException the no secret file found exception
     * @throws SecretNotFoundException    the secret not found exception
     */
    public static void main(String[] args) throws NoSecretFileFoundException, SecretNotFoundException {

	/*
	 * 
	 * Modify me
	 * 
	 */
	boolean isAzureCall = !true;

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

	// We simply provide empty connections => This will trigger automatic haversine
	// distance calculations

	List<ElementConnection> emptyConnections = new ArrayList<>();
	
	CreatorSetting creatorSettings = new CreatorSetting().creator("MY_TEST_CREATOR");
	String myOptiIdent = "MY_OPTIMIZATION";

	Boolean wasStarted = tourOptimizerCaller.optimizeFireAndForget(nodePoss, resourcePoss, emptyConnections, myOptiIdent, creatorSettings,
		Optional.of(m.get("joptlic")));
	
	System.out.print("wasStarted: "+wasStarted);

    }

}
