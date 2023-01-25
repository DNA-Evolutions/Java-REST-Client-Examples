package com.dna.jopt.rest.client.example.fullstack.helper;

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

import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.dna.jopt.rest.client.example.geocoder.helper.GeoCoderRestCaller;
import com.dna.jopt.rest.client.example.routeplanner.helper.RoutePlannerRestCaller;
import com.dna.jopt.rest.client.example.touroptimizer.helper.TourOptimizerRestCaller;
import com.dna.jopt.rest.client.model.ElementConnection;
import com.dna.jopt.rest.client.model.GeoAddress;
import com.dna.jopt.rest.client.model.MatrixRoutingRequest;
import com.dna.jopt.rest.client.model.Node;
import com.dna.jopt.rest.client.model.OptimizationOptions;
import com.dna.jopt.rest.client.model.Position;
import com.dna.jopt.rest.client.model.Resource;
import com.dna.jopt.rest.client.model.RestOptimization;
import com.dna.jopt.rest.client.model.Solution;
import com.dna.jopt.rest.client.model.Status;
import com.dna.jopt.rest.client.util.testinputcreation.TestRestOptimizationCreator;
import com.fasterxml.jackson.databind.ObjectMapper;

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

    public ObjectMapper getTourOptimizerMapper() {
	return this.tourOptimizer.getMapper();
    }

    public ObjectMapper getGeoRoutingMapper() {
	return this.routePlanner.getMapper();
    }

    public ObjectMapper getGeoCodingMapper() {
	return this.geoCoder.getMapper();
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
    public List<ElementConnection> geoRouteMatrix(List<Position> nodeSrcPoss, List<Position> ressSrcPoss,
	    boolean isUseCorrection) {

	return this.routePlanner.geoRouteMatrix(nodeSrcPoss, ressSrcPoss, isUseCorrection);
    }

    public List<ElementConnection> geoRouteSmartMatrix(Set<Node> nodes, Set<Resource> ress, boolean isUseCorrection) {

	return this.routePlanner.geoRouteSmartMatrix(nodes, ress, isUseCorrection);
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
    
    public RestOptimization optimize(List<Node> nodes, List<Resource> ress,
	    List<ElementConnection> connections, Optional<String> jsonLicenseOpt, Optional<OptimizationOptions> optimizationOptionsOpt) {

	return this.tourOptimizer.optimize(nodes, ress, connections, jsonLicenseOpt,optimizationOptionsOpt);
    }

    public Solution optimizeOnlyResult(List<Position> nodePoss, List<Position> ressPoss,
	    List<ElementConnection> connections, Optional<String> jsonLicenseOpt) {

	return this.tourOptimizer.optimizeOnlyResult(nodePoss, ressPoss, connections, jsonLicenseOpt);
    }

    public void attachToStreams() {

	this.tourOptimizer.attachToStreams();
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
