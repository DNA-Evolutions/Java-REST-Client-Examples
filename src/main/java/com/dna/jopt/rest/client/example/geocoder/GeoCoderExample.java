package com.dna.jopt.rest.client.example.geocoder;

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
import com.dna.jopt.rest.client.util.secrets.SecretsManager;
import com.dna.jopt.rest.client.util.testinputcreation.TestAddressInput;

public class GeoCoderExample {

    public static void main(String[] args) throws IOException {

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
	System.out.println("Geocoding test data");
	List<Position> nodePoss = geoCoderCaller.geoCodeNodePositions(nodeAddresses);
	List<Position> ressPoss = geoCoderCaller.geoCodeResourcePositions(resourceAddresses);

	// Print out as json

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
	
	
	
	

	// XXX temp
	List<Position> joined = nodePoss;
	joined.addAll(ressPoss);

	for (int ii = 0; ii < joined.size(); ii++) {
	    
	    Position p = joined.get(ii);
	    
	    System.out.println("poss.add(new Position().latitude(" + p.getLatitude() + ").longitude(" + p.getLongitude()
	    + ").locationId(\"Position_" + ii + "\"));");

	}



    }
}
