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
import java.util.ArrayList;
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

/**
 * The Class ForwardGeoCoderExample. Shows how to transform addresses into
 * positions (with latitude and longitude)
 */
public class ForwardGeoCoderFromLabelExample {

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

	// We use a address definition within this class that only uses free text labels
	List<GeoAddress> nodeAddresses = ForwardGeoCoderFromLabelExample.nodeAddressesLabelsOnly();

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

	// Print out as JSON

	System.out.println(geoCoderCaller.getMapper().writerWithDefaultPrettyPrinter().writeValueAsString(nodePoss));

	/*
	 * Save to JSON
	 */
	if (isSave2JSON) {

	    String jsonNodeFile = "nodeCodingLabelTest" + ".json";

	    RestJSONParser.toJsonFile(nodePoss, new File(jsonNodeFile), geoCoderCaller.getMapper());
	}

    }

    public static List<GeoAddress> nodeAddressesLabelsOnly() {
	List<GeoAddress> adds = new ArrayList<>();

	adds.add(new GeoAddress().label("81 Werombi Road, Grasmere, NSW, Australia").locationId("Pos_1"));

	adds.add(new GeoAddress().label("24 Camden Valley Way, Elderslie, NSW, Australia").locationId("Pos_2"));

	adds.add(new GeoAddress().label("16 Carpenter Street, Elderslie, NSW, Australia").locationId("Pos_3"));

	adds.add(new GeoAddress().label("259 Camden Valley Way, Narellan, NSW, Australia").locationId("Pos_4"));

	return adds;
    }
}
