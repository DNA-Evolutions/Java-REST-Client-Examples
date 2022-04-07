package com.dna.jopt.rest.client.example.fullstack;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import com.dna.jopt.rest.client.example.fullstack.helper.FullStackRestCaller;
import com.dna.jopt.rest.client.model.ElementConnection;
import com.dna.jopt.rest.client.model.Position;
import com.dna.jopt.rest.client.model.RestOptimization;
import com.dna.jopt.rest.client.model.Solution;
import com.dna.jopt.rest.client.model.Status;
import com.dna.jopt.rest.client.model.TextSolution;
import com.dna.jopt.rest.client.util.endpoints.Endpoints;
import com.dna.jopt.rest.client.util.export.kml.RestSolutionKMLExporter;
import com.dna.jopt.rest.client.util.secrets.SecretsManager;
import com.dna.jopt.rest.client.util.testinput.TestInput;

public class SydneyFllstackExample {

    public static void main(String[] args) throws IOException {

	// Modify me
	boolean isAzureCall = true;
	boolean useConnectionCorretion = true;
	boolean onlyReturnResultAfterOptimization = !true;

	/*
	 * 
	 * 
	 */
	FullStackRestCaller optimzerExample;

	SecretsManager m = new SecretsManager();

	if (isAzureCall) {

	    optimzerExample = new FullStackRestCaller(Endpoints.AZURE_SWAGGER_GEOCODER_URL,
		    Endpoints.AZURE_SWAGGER_GEOROUTER_URL, Endpoints.AZURE_SWAGGER_TOUROPTIMIZER_URL,
		    Optional.of(m.get("azure")));
	} else {
	    optimzerExample = new FullStackRestCaller(Endpoints.LOCAL_SWAGGER_GEOCODER_URL,
		    Endpoints.LOCAL_SWAGGER_GEOROUTER_URL, Endpoints.LOCAL_SWAGGER_TOUROPTIMIZER_URL);
	}

	/*
	 *
	 * 0) For example check health of geoCoder
	 *
	 */
	Status status = optimzerExample.checkGeoCoderHealth();

	if (status.getStatus().equals("up")) {

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
	List<Position> nodePoss = optimzerExample.geoCodeNodePositions(TestInput.defaultSydneyNodeAddresses());
	List<Position> ressPoss = optimzerExample.geoCodeResourcePositions(TestInput.defaultSydneyResourceAddresses());

	/*
	 *
	 * 2) Create element connections via GeoRouting
	 *
	 */
	System.out.println("2) Creating matrix connection data");
	List<ElementConnection> connections;

	if (useConnectionCorretion) {
	    connections = optimzerExample.geoRouteMatrixCorrected(nodePoss, ressPoss);
	} else {
	    connections = optimzerExample.geoRouteMatrix(nodePoss, ressPoss);
	}

	/*
	 *
	 * 3) Optimize
	 *
	 */
	Solution solution;
	Optional<TextSolution> textSolutionOpt;

	if (onlyReturnResultAfterOptimization) {
	    solution = optimzerExample.optimizeOnlyResult(nodePoss, ressPoss, connections,
		    Optional.of(m.get("joptlic")));

	    textSolutionOpt = Optional.empty();
	} else {
	    System.out.println("3) Optimizing input data");
	    RestOptimization result = optimzerExample.optimize(nodePoss, ressPoss, connections,
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
	Solution routedSolution = optimzerExample.geoRouteSolution(solution);

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
