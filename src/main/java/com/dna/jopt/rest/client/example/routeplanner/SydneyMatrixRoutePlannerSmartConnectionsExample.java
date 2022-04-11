package com.dna.jopt.rest.client.example.routeplanner;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.dna.jopt.rest.client.example.routeplanner.helper.RoutePlannerRestCaller;
import com.dna.jopt.rest.client.model.ElementConnection;
import com.dna.jopt.rest.client.model.Node;
import com.dna.jopt.rest.client.model.Position;
import com.dna.jopt.rest.client.model.Resource;
import com.dna.jopt.rest.client.util.endpoints.Endpoints;
import com.dna.jopt.rest.client.util.io.json.RestJSONParser;
import com.dna.jopt.rest.client.util.secrets.SecretsManager;
import com.dna.jopt.rest.client.util.testinputcreation.TestElementsCreator;
import com.dna.jopt.rest.client.util.testinputcreation.TestPositionsInput;

public class SydneyMatrixRoutePlannerSmartConnectionsExample {

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
	 * Map nodePoss and ressPoss to real elements with different opening hours and working hours to use smart routing feature
	 */
	Set<Node> nodes = new HashSet<>();
	for (int ii = 0; ii < nodePoss.size(); ii++) {
	    int curOpeninghoursIndex = ii % 5;

	    Position curPos = nodePoss.get(ii);

	    nodes.add(TestElementsCreator.defaultGeoNode(curPos, curPos.getLocationId(),
		    TestElementsCreator.defaultTestOpeninghours(curOpeninghoursIndex)));

	}
	
	Set<Resource> ress = ressPoss.stream().map(p -> TestElementsCreator.defaultCapacityResource(p, p.getLocationId()))
		.collect(Collectors.toSet());

	/*
	 *
	 * Create element connections via GeoRouting
	 *
	 */
	System.out.println("Creating matrix connection data");
	List<ElementConnection> connections;

	connections = routePlannerCaller.geoRouteSmartMatrix(nodes, ress,useConnectionCorretion);
	

	/*
	 * Print out connections as JSON
	 */

	String jsonConnections = RestJSONParser.toJSONString(connections, routePlannerCaller.getMapper());

	System.out.println(jsonConnections);
	
	System.out.println("====================================");
	System.out.println("REPORT");
	System.out.println("====================================");
	System.out.println("Number of nodes: "+nodePoss.size());
	System.out.println("Number of resources: "+ressPoss.size());
	
	System.out.println("Number of real extracted connections: "+connections.size());
	
	double orginalConnections = Math.pow(nodePoss.size()+ressPoss.size(),2);
	
	System.out.println("Full connection request would be: "+orginalConnections + " connections");
	System.out.println("Smart feature saved: "+(1.0-(connections.size()/orginalConnections))*100 + " % of connections");

	/*
	 * Save to JSON
	 */
	if (isSave2JSON) {

	    String jsonFile = "matrixTest" + ".json";

	    RestJSONParser.toJsonFile(connections, new File(jsonFile), routePlannerCaller.getMapper());
	}

    }

}
