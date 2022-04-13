package com.dna.jopt.rest.client.example.fullstack.coderouteoptimize;

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

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import com.dna.jopt.rest.client.example.fullstack.helper.FullStackRestCaller;
import com.dna.jopt.rest.client.model.ElementConnection;
import com.dna.jopt.rest.client.model.GeoAddress;
import com.dna.jopt.rest.client.model.Position;
import com.dna.jopt.rest.client.model.RestOptimization;
import com.dna.jopt.rest.client.model.Solution;
import com.dna.jopt.rest.client.model.Status;
import com.dna.jopt.rest.client.model.TextSolution;
import com.dna.jopt.rest.client.util.endpoints.Endpoints;
import com.dna.jopt.rest.client.util.io.export.kml.RestSolutionKMLExporter;
import com.dna.jopt.rest.client.util.secrets.SecretsManager;
import com.dna.jopt.rest.client.util.secrets.caughtexception.NoSecretFileFoundException;
import com.dna.jopt.rest.client.util.secrets.caughtexception.SecretNotFoundException;
import com.dna.jopt.rest.client.util.testinputcreation.TestAddressInput;

/**
 * The Class SydneyFullstackExample. The first step: Take addresses and geocode
 * them to positions with longitude and latitude. Second step: Create a
 * connections matrix. Third step: Optimize the data and print out result.
 */
public class SydneyFullstackExample {

    /**
     * The main method of SydneyFullstackExample
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
	boolean useConnectionCorretion = true;
	boolean onlyReturnResultAfterOptimization = !true;

	// Define the data to be used
	List<GeoAddress> nodeAddresses = TestAddressInput.defaultSydneyNodeAddresses();
	List<GeoAddress> resourceAddresses = TestAddressInput.defaultSydneyResourceAddresses();

	/*
	 * 
	 * 
	 */
	FullStackRestCaller fullStackCaller;

	SecretsManager m = new SecretsManager();

	if (isAzureCall) {

	    fullStackCaller = new FullStackRestCaller(Endpoints.AZURE_SWAGGER_GEOCODER_URL,
		    Endpoints.AZURE_SWAGGER_GEOROUTER_URL, Endpoints.AZURE_SWAGGER_TOUROPTIMIZER_URL,
		    Optional.of(m.get("azure")));
	} else {
	    fullStackCaller = new FullStackRestCaller(Endpoints.LOCAL_SWAGGER_GEOCODER_URL,
		    Endpoints.LOCAL_SWAGGER_GEOROUTER_URL, Endpoints.LOCAL_SWAGGER_TOUROPTIMIZER_URL);
	}

	/*
	 *
	 * 0) For example check health of geoCoder
	 *
	 */
	Status status = fullStackCaller.checkGeoCoderHealth();

	if (!status.getStatus().equals("UP")) {

	    System.out.println("GeoCoder not healthy - stoping process! (Status: " + status + " )");

	    return;
	} else {
	    System.out.println("GeoCoder healthy! (Status: " + status + " )");
	}

	/*
	 *
	 * 1) Get addresses and GeoCode
	 *
	 */
	System.out.println("1) Geocoding test data");
	List<Position> nodePoss = fullStackCaller.geoCodeNodePositions(nodeAddresses);
	List<Position> ressPoss = fullStackCaller.geoCodeResourcePositions(resourceAddresses);

	/*
	 *
	 * 2) Create element connections via GeoRouting
	 *
	 */
	System.out.println("2) Creating matrix connection data");
	List<ElementConnection> connections;

	connections = fullStackCaller.geoRouteMatrix(nodePoss, ressPoss, useConnectionCorretion);

	/*
	 *
	 * 3) Optimize
	 *
	 */
	Solution solution;
	Optional<TextSolution> textSolutionOpt;

	if (onlyReturnResultAfterOptimization) {
	    solution = fullStackCaller.optimizeOnlyResult(nodePoss, ressPoss, connections,
		    Optional.of(m.get("joptlic")));

	    textSolutionOpt = Optional.empty();
	} else {
	    System.out.println("3) Optimizing input data");
	    RestOptimization result = fullStackCaller.optimize(nodePoss, ressPoss, connections,
		    Optional.of(m.get("joptlic")));

	    solution = result.getSolution();
	    textSolutionOpt = Optional.ofNullable(result.getExtension().getTextSolution());
	}

	/*
	 *
	 * 4) Extract routes for result
	 *
	 */
	System.out.println("4) Creating polylines for optimized routes in solution");
	Solution routedSolution = fullStackCaller.geoRouteSolution(solution);

	/*
	 *
	 * 5) Plot to kml - only available if JOpt Core dependency is present
	 *
	 */
	System.out.println("5) Exporting solution to a kml file");

	RestSolutionKMLExporter exporter = new RestSolutionKMLExporter();
	exporter.export(routedSolution, new FileOutputStream("FullstackExport.kml"));

	// 6) Show result
	// Contains the polylines - "routedSolution";
	textSolutionOpt.ifPresent(ts -> {
	    System.out.println("6) Printing text solution");
	    System.out.println(ts);
	});

    }

}
