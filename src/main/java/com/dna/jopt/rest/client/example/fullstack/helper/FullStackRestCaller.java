package com.dna.jopt.rest.client.example.fullstack.helper;

import java.io.PrintStream;
import java.util.List;
import java.util.Optional;
import com.dna.jopt.rest.client.example.geocoder.helper.GeoCoderRestCaller;
import com.dna.jopt.rest.client.example.routeplanner.helper.RoutePlannerRestCaller;
import com.dna.jopt.rest.client.example.touroptimizer.helper.TourOptimizerRestCaller;
import com.dna.jopt.rest.client.model.ElementConnection;
import com.dna.jopt.rest.client.model.GeoAddress;
import com.dna.jopt.rest.client.model.MatrixRoutingRequest;
import com.dna.jopt.rest.client.model.Position;
import com.dna.jopt.rest.client.model.RestOptimization;
import com.dna.jopt.rest.client.model.Solution;
import com.dna.jopt.rest.client.model.Status;

public class FullStackRestCaller {

    private TourOptimizerRestCaller tourOptimizer;
    private RoutePlannerRestCaller routePlanner;
    private GeoCoderRestCaller geoCoder;

    /*
     *
     */

    public FullStackRestCaller(String geocoderUrl, String georouterUrl, String tourOptimizerUrl) {
	this(geocoderUrl, georouterUrl, tourOptimizerUrl, Optional.empty());
    }

    public FullStackRestCaller(String geocoderUrl, String georouterUrl, String tourOptimizerUrl,
	    Optional<String> azureApiKeyOpt) {

	this.tourOptimizer = new TourOptimizerRestCaller(tourOptimizerUrl, azureApiKeyOpt);
	this.routePlanner = new RoutePlannerRestCaller(georouterUrl, azureApiKeyOpt);
	this.geoCoder = new GeoCoderRestCaller(geocoderUrl, azureApiKeyOpt);
    }

    /*
     * 
     * Step 0) - Healthcheck
     * 
     */
    public Status checkGeoCoderHealth() {
	return this.geoCoder.checkGeoCoderHealth();
    }

    /*
     *
     * Step 1) - GeoCode
     *
     */
    public List<Position> geoCodeNodePositions(List<GeoAddress> nodeAddresses) {

	return this.geoCoder.geoCodeNodePositions(nodeAddresses);
    }

    public List<Position> geoCodeResourcePositions(List<GeoAddress> ressAddresses) {

	return this.geoCoder.geoCodeResourcePositions(ressAddresses);
    }

    /*
     *
     * Step 2) - GeoRouteMatrix
     *
     */
    public List<ElementConnection> geoRouteMatrix(List<Position> nodeSrcPoss, List<Position> ressSrcPoss, boolean isUseCorrection) {

	return this.routePlanner.geoRouteMatrix(nodeSrcPoss, ressSrcPoss,isUseCorrection);
    }

    public static MatrixRoutingRequest createMatrixRequest(RestOptimization input) {

	return RoutePlannerRestCaller.createMatrixRequest(input);

    }

    /*
     *
     * Step 3) - Optimize
     *
     */

    public RestOptimization optimize(List<Position> nodePoss, List<Position> ressPoss,
	    List<ElementConnection> connections, Optional<String> jsonLicenseOpt) {

	return this.tourOptimizer.optimize(nodePoss, ressPoss, connections, jsonLicenseOpt);

    }

    public Solution optimizeOnlyResult(List<Position> nodePoss, List<Position> ressPoss,
	    List<ElementConnection> connections, Optional<String> jsonLicenseOpt) {

	return this.tourOptimizer.optimizeOnlyResult(nodePoss, ressPoss, connections, jsonLicenseOpt);
    }

    public void attachToStreams(PrintStream outstream) {

	this.tourOptimizer.attachToStreams(outstream);
    }

    /*
     *
     * Step 4) - GeoRouteSolution
     *
     */
    public Solution geoRouteSolution(Solution existing) {

	return this.routePlanner.geoRouteSolution(existing);
    }

}
