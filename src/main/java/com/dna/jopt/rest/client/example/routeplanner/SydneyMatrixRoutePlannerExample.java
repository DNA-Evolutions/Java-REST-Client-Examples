package com.dna.jopt.rest.client.example.routeplanner;

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
import com.dna.jopt.rest.client.util.testinputcreation.TestPositionsInput;

public class SydneyMatrixRoutePlannerExample {

    public static void main(String[] args) throws IOException {

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

	if (useConnectionCorretion) {
	    connections = routePlannerCaller.geoRouteMatrixCorrected(nodePoss, ressPoss);
	} else {
	    connections = routePlannerCaller.geoRouteMatrix(nodePoss, ressPoss);
	}

	/*
	 * Print out connections as JSON
	 */

	String jsonConnections = RestJSONParser.toJSONString(connections, routePlannerCaller.getMapper());

	System.out.println(jsonConnections);

	/*
	 * Save to JSON
	 */
	if (isSave2JSON) {

	    String jsonFile = "matrixTest" + ".json";

	    RestJSONParser.toJsonFile(connections, new File(jsonFile), routePlannerCaller.getMapper());
	}

    }

}
