package com.dna.jopt.rest.client.example.fullstack.coderoute;

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
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.dna.jopt.rest.client.example.fullstack.helper.FullStackRestCaller;
import com.dna.jopt.rest.client.model.ElementConnection;
import com.dna.jopt.rest.client.model.GeoAddress;
import com.dna.jopt.rest.client.model.Node;
import com.dna.jopt.rest.client.model.Position;
import com.dna.jopt.rest.client.model.Resource;
import com.dna.jopt.rest.client.model.Status;
import com.dna.jopt.rest.client.util.endpoints.Endpoints;
import com.dna.jopt.rest.client.util.io.json.RestJSONParser;
import com.dna.jopt.rest.client.util.secrets.SecretsManager;
import com.dna.jopt.rest.client.util.secrets.caughtexception.NoSecretFileFoundException;
import com.dna.jopt.rest.client.util.secrets.caughtexception.SecretNotFoundException;
import com.dna.jopt.rest.client.util.testinputcreation.TestAddressInput;
import com.dna.jopt.rest.client.util.testinputcreation.TestElementsCreator;

/**
 * The Class SydneyGeoCodeAndSmarteRouteMatrixExample. The first step: Take
 * addresses and geocode them to positions with longitude and latitude. Second
 * step: Create a connections matrix.
 */
public class SydneyGeoCodeAndSmartRouteMatrixExample {

    /**
     * The main method of SydneyGeoCodeAndSmarteRouteMatrixExample
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
	boolean isSave2JSON = true;

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
	System.out.println("2) Creating smart matrix connection data");

	/*
	 * Map nodePoss and ressPoss to real elements with different opening hours and
	 * working hours to use smart routing feature
	 */
	Set<Node> nodes = new HashSet<>();
	for (int ii = 0; ii < nodePoss.size(); ii++) {
	    int curOpeninghoursIndex = ii % 5;

	    Position curPos = nodePoss.get(ii);

	    nodes.add(TestElementsCreator.defaultGeoNode(curPos, curPos.getLocationId(),
		    TestElementsCreator.defaultTestOpeninghours(curOpeninghoursIndex)));

	}

	Set<Resource> ress = ressPoss.stream()
		.map(p -> TestElementsCreator.defaultCapacityResource(p, p.getLocationId()))
		.collect(Collectors.toSet());

	/*
	 *
	 * Create element connections via GeoRouting
	 *
	 */
	System.out.println("Creating matrix connection data");

	List<ElementConnection> connections = fullStackCaller.geoRouteSmartMatrix(nodes, ress, useConnectionCorretion);

	/*
	 * Print out connections as JSON
	 */

	String jsonConnections = RestJSONParser.toJSONString(connections, fullStackCaller.getGeoRoutingMapper());

	System.out.println(jsonConnections);

	System.out.println("====================================");
	System.out.println("REPORT");
	System.out.println("====================================");
	System.out.println("Number of nodes: " + nodePoss.size());
	System.out.println("Number of resources: " + ressPoss.size());

	System.out.println("Number of real extracted connections: " + connections.size());

	double orginalConnections = Math.pow(nodePoss.size() + ressPoss.size(), 2);

	System.out.println("Full connection request would be: " + orginalConnections + " connections");
	System.out.println("Smart feature saved: " + (1.0 - (connections.size() / orginalConnections)) * 100
		+ " % of connections");

	/*
	 * Save to JSON
	 */
	if (isSave2JSON) {

	    String jsonFile = "matrixGeoCodeAndSmartRouteTest" + ".json";

	    RestJSONParser.toJsonFile(connections, new File(jsonFile), fullStackCaller.getGeoRoutingMapper());
	}

    }

}
