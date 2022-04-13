package com.dna.jopt.rest.client.example.routeplanner.fullmatrix;

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
import java.util.List;
import java.util.Optional;

import com.dna.jopt.rest.client.example.routeplanner.helper.RoutePlannerRestCaller;
import com.dna.jopt.rest.client.model.ElementConnection;
import com.dna.jopt.rest.client.model.Position;
import com.dna.jopt.rest.client.util.endpoints.Endpoints;
import com.dna.jopt.rest.client.util.io.json.RestJSONParser;
import com.dna.jopt.rest.client.util.secrets.SecretsManager;
import com.dna.jopt.rest.client.util.secrets.caughtexception.NoSecretFileFoundException;
import com.dna.jopt.rest.client.util.secrets.caughtexception.SecretNotFoundException;
import com.dna.jopt.rest.client.util.testinputcreation.TestPositionsInput;

/**
 * The Class SydneyMatrixRoutePlannerExample. Transform a list of source and
 * targeted positions into a list of connections representing every possible
 * connection between each point (representing the full n x n matrix).
 */
public class SydneyMatrixRoutePlannerExample {

    /**
     * The main method of SydneyMatrixRoutePlannerExample
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
	boolean useConnectionCorretion = !true;
	boolean isSave2JSON = true;

	List<Position> nodePoss = TestPositionsInput.defaultSydneyNodePositions();
	List<Position> ressPoss = TestPositionsInput.defaultSydneyResourcePositions();

	// Define the data to be used

	/*
	 * 
	 * 
	 */
	RoutePlannerRestCaller routePlannerCaller;

	SecretsManager m = new SecretsManager();

	if (isAzureCall) {

	    routePlannerCaller = new RoutePlannerRestCaller(Endpoints.AZURE_SWAGGER_GEOROUTER_URL,
		    Optional.of(m.get("azure")));
	} else {
	    routePlannerCaller = new RoutePlannerRestCaller(Endpoints.LOCAL_SWAGGER_GEOROUTER_URL);
	}

	/*
	 *
	 * Create element connections via GeoRouting
	 *
	 */
	System.out.println("Creating matrix connection data");
	List<ElementConnection> connections;

	connections = routePlannerCaller.geoRouteMatrix(nodePoss, ressPoss, useConnectionCorretion);

	/*
	 * Print out connections as JSON
	 */

	String jsonConnections = RestJSONParser.toJSONString(connections, routePlannerCaller.getMapper());

	System.out.println(jsonConnections);

	System.out.println("Number of extracted connections: " + connections.size());

	/*
	 * Save to JSON
	 */
	if (isSave2JSON) {

	    String jsonFile = "matrixTest" + ".json";

	    RestJSONParser.toJsonFile(connections, new File(jsonFile), routePlannerCaller.getMapper());
	}

    }

}
