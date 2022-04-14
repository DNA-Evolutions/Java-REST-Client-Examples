package com.dna.jopt.rest.client.example.geocoder.forward;

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

import com.dna.jopt.rest.client.example.geocoder.helper.GeoCoderRestCaller;
import com.dna.jopt.rest.client.model.GeoAddress;
import com.dna.jopt.rest.client.model.Position;
import com.dna.jopt.rest.client.model.Status;
import com.dna.jopt.rest.client.util.endpoints.Endpoints;
import com.dna.jopt.rest.client.util.io.json.RestJSONParser;
import com.dna.jopt.rest.client.util.secretsmanager.SecretsManager;
import com.dna.jopt.rest.client.util.secretsmanager.caughtexception.NoSecretFileFoundException;
import com.dna.jopt.rest.client.util.secretsmanager.caughtexception.SecretNotFoundException;
import com.dna.jopt.rest.client.util.testinputcreation.TestAddressInput;

/**
 * The Class ForwardGeoCoderExample. Shows how to transform addresses into positions (with latitude and longitude)
 */
public class ForwardGeoCoderExample {

    /**
     * The main method of ForwardGeoCoderExample
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

	List<GeoAddress> nodeAddresses = TestAddressInput.defaultSydneyNodeAddresses();
	List<GeoAddress> resourceAddresses = TestAddressInput.defaultSydneyResourceAddresses();

	/*
	 * Run
	 * 
	 */

	SecretsManager m = new SecretsManager();

	GeoCoderRestCaller geoCoderCaller;

	if (isAzureCall) {

	    geoCoderCaller = new GeoCoderRestCaller(Endpoints.AZURE_SWAGGER_GEOCODER_URL, Optional.of(m.get("azure")));
	} else {
	    geoCoderCaller = new GeoCoderRestCaller(Endpoints.LOCAL_SWAGGER_GEOCODER_URL);
	}

	/*
	 *
	 * 0) For example check health of geoCoder
	 *
	 */
	Status status = geoCoderCaller.checkGeoCoderHealth();

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
	System.out.println("Geocoding test data");
	List<Position> nodePoss = geoCoderCaller.geoCodeNodePositions(nodeAddresses);
	List<Position> ressPoss = geoCoderCaller.geoCodeResourcePositions(resourceAddresses);

	// Print out as JSON

	System.out.println(geoCoderCaller.getMapper().writerWithDefaultPrettyPrinter().writeValueAsString(nodePoss));
	System.out.println(geoCoderCaller.getMapper().writerWithDefaultPrettyPrinter().writeValueAsString(ressPoss));

	/*
	 * Save to JSON
	 */
	if (isSave2JSON) {

	    String jsonNodeFile = "nodeCodingTest" + ".json";
	    String jsonResFile = "ressCodingTest" + ".json";

	    RestJSONParser.toJsonFile(nodePoss, new File(jsonNodeFile), geoCoderCaller.getMapper());
	    RestJSONParser.toJsonFile(ressPoss, new File(jsonResFile), geoCoderCaller.getMapper());
	}

    }
}
