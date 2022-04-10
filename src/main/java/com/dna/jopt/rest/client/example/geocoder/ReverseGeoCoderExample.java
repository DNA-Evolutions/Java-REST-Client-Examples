package com.dna.jopt.rest.client.example.geocoder;

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
import com.dna.jopt.rest.client.util.testinputcreation.TestPositionsInput;

public class ReverseGeoCoderExample {

    private static final LocationParameters DEFAULT_LOCATION_PARAM = new LocationParameters().layers("address")
	    .radius(200.0).size(1).sources("all");

    private static final UnaryOperator<Position> LOCATION_PARAMETER_ATTACHER = p -> {
	p.setLocationParameters(DEFAULT_LOCATION_PARAM);

	return p;
    };

    public static void main(String[] args) throws IOException {

	/*
	 * 
	 * Modify me
	 * 
	 */
	boolean isAzureCall = !true;
	boolean isSave2JSON = true;

	List<Position> positions = TestPositionsInput.defaultSydneyNodePositions();

	/*
	 * Run
	 * 
	 */

	GeoCoderRestCaller geoCoderCaller;

	SecretsManager m = new SecretsManager();

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

	// XXX
	// Find missing positions

	List<String> differences = positionsWithLocationParams.stream().map(Position::getLocationId)
		.collect(Collectors.toList()).stream().filter(element -> !reverseCodedPoss.stream()
			.map(Position::getLocationId).collect(Collectors.toList()).contains(element))
		.collect(Collectors.toList());

	System.out.println(differences);

    }
}
