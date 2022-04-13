package com.dna.jopt.rest.client.example.geocoder.reverse;

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
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import com.dna.jopt.rest.client.example.geocoder.helper.GeoCoderRestCaller;
import com.dna.jopt.rest.client.model.LocationParameters;
import com.dna.jopt.rest.client.model.Position;
import com.dna.jopt.rest.client.model.Status;
import com.dna.jopt.rest.client.util.endpoints.Endpoints;
import com.dna.jopt.rest.client.util.io.json.RestJSONParser;
import com.dna.jopt.rest.client.util.secrets.SecretsManager;
import com.dna.jopt.rest.client.util.secrets.caughtexception.NoSecretFileFoundException;
import com.dna.jopt.rest.client.util.secrets.caughtexception.SecretNotFoundException;
import com.dna.jopt.rest.client.util.testinputcreation.TestPositionsInput;

/**
 * The Class ReverseGeoCoderExample. Shows how to transform positions (with latitude and longitude) into addresses.
 */
public class ReverseGeoCoderExample {

    /**
     * The Constant DEFAULT_LOCATION_PARAM is used to create default location
     * parameters for reverse geo coding.
     */
    public static final LocationParameters DEFAULT_LOCATION_PARAM = new LocationParameters().layers("address")
	    .radius(200.0).size(1).sources("all");

    /**
     * 
     * The Constant LOCATION_PARAMETER_ATTACHER defines how to attach location
     * parameters to positions before reverse geo coding
     * 
     */
    public static final UnaryOperator<Position> LOCATION_PARAMETER_ATTACHER = p -> {
	p.setLocationParameters(DEFAULT_LOCATION_PARAM);

	return p;
    };

    /**
     * The main method of ReverseGeoCoderExample
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

	List<Position> positions = TestPositionsInput.defaultSydneyNodePositions();

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
	System.out.println("Reverse Geocoding test data");

	// Lets attach a location parameter object

	List<Position> positionsWithLocationParams = positions.stream().map(LOCATION_PARAMETER_ATTACHER)
		.collect(Collectors.toList());

	List<Position> reverseCodedPoss = geoCoderCaller.reverseGeoCodePositions(positionsWithLocationParams);

	// Print out as json

	System.out.println(
		geoCoderCaller.getMapper().writerWithDefaultPrettyPrinter().writeValueAsString(reverseCodedPoss));

	/*
	 * Save to JSON
	 */
	if (isSave2JSON) {

	    String jsonFile = "GeoCodingTest" + ".json";

	    RestJSONParser.toJsonFile(reverseCodedPoss, new File(jsonFile), geoCoderCaller.getMapper());
	}

    }
}
