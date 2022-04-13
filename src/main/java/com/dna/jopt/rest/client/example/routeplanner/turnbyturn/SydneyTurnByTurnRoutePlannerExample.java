package com.dna.jopt.rest.client.example.routeplanner.turnbyturn;

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
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.dna.jopt.rest.client.example.routeplanner.helper.RoutePlannerRestCaller;
import com.dna.jopt.rest.client.model.Position;
import com.dna.jopt.rest.client.model.TurnByTurnResponseItem;
import com.dna.jopt.rest.client.model.TurnByTurnRoutingRequest;
import com.dna.jopt.rest.client.util.endpoints.Endpoints;
import com.dna.jopt.rest.client.util.io.export.kml.KMLPolyLineExporter;
import com.dna.jopt.rest.client.util.io.json.RestJSONParser;
import com.dna.jopt.rest.client.util.secrets.SecretsManager;
import com.dna.jopt.rest.client.util.secrets.caughtexception.NoSecretFileFoundException;
import com.dna.jopt.rest.client.util.secrets.caughtexception.SecretNotFoundException;
import com.dna.jopt.rest.client.util.tbtextraction.TBTTripExtraction;
import com.dna.jopt.rest.client.util.testinputcreation.TestPositionsInput;

/**
 * The Class SydneyTurnByTurnRoutePlannerExample.  Creates a turn-by-turn request and saved the shape of the routing as a kml file.
 */
public class SydneyTurnByTurnRoutePlannerExample {

    /**
     * The main method of SydneyTurnByTurnRoutePlannerExample
     *
     * @param args the arguments
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws NoSecretFileFoundException the no secret file found exception
     * @throws SecretNotFoundException the secret not found exception
     */
    public static void main(String[] args) throws IOException , NoSecretFileFoundException, SecretNotFoundException{

	/*
	 * 
	 * Modify me
	 * 
	 */
	boolean isAzureCall = true;
	boolean save2Kml = true;

	List<Position> nodePoss = TestPositionsInput.defaultSydneyNodePositions();

	// Take two positions for routing
	Position fromPos = nodePoss.get(0);
	Position toPos = nodePoss.get(nodePoss.size() - 1);

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
	 * Route
	 *
	 */

	TurnByTurnRoutingRequest request = new TurnByTurnRoutingRequest();
	request.attachRawResponse(true);
	request.addPositionsItem(fromPos);
	request.addPositionsItem(toPos);

	TurnByTurnResponseItem response = routePlannerCaller.singleTBT(request);

	TBTTripExtraction extract = TBTTripExtraction.extractSummaryNode(response);

	/*
	 * 
	 * 
	 */

	String extractJson = RestJSONParser.toJSONString(extract, routePlannerCaller.getMapper());

	// Print result in JSON
	System.out.println(extractJson);

	if (save2Kml) {
	    String kmlFile = "routingTest" + ".kml";

	    KMLPolyLineExporter.exportKML(new FileOutputStream(kmlFile), "TestExtract",
		    Collections.singletonList(extract.getShape()), Collections.singletonList("TestShape"));
	}

    }

}
