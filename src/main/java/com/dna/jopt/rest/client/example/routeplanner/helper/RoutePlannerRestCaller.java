package com.dna.jopt.rest.client.example.routeplanner.helper;

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

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.web.reactive.function.client.WebClient;
import com.dna.jopt.rest.client.model.ElementConnection;
import com.dna.jopt.rest.client.model.GeoNode;
import com.dna.jopt.rest.client.model.MatrixRoutingRequest;
import com.dna.jopt.rest.client.model.Node;
import com.dna.jopt.rest.client.model.Position;
import com.dna.jopt.rest.client.model.RequestDirectionsOptions;
import com.dna.jopt.rest.client.model.Resource;
import com.dna.jopt.rest.client.model.RestOptimization;
import com.dna.jopt.rest.client.model.SmartMatrixRoutingRequest;
import com.dna.jopt.rest.client.model.Solution;
import com.dna.jopt.rest.client.model.TurnByTurnResponseItem;
import com.dna.jopt.rest.client.model.TurnByTurnRoutingRequest;
import com.dna.jopt.rest.client.util.errorhandling.RestErrorHandler;
import com.dna.jopt.rest.georouter.client.ApiClient;
import com.dna.jopt.rest.georouter.client.api.GeoMatrixRoutingServiceControllerApi;
import com.dna.jopt.rest.georouter.client.api.GeoTurnByTurnRoutingServiceControllerApi;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class RoutePlannerRestCaller {

    private GeoMatrixRoutingServiceControllerApi geoRouterMatrixApi = createGeoMatrixRoutingServiceControllerApi();
    private GeoTurnByTurnRoutingServiceControllerApi geoRouterTBTApi = new GeoTurnByTurnRoutingServiceControllerApi();

    public final ObjectMapper routePlannerObjectMapper;

    public static final int MAX_TOUROPIMIZER_RESPONSESIZE_MB = 16;

    /*
     *
     */

    public RoutePlannerRestCaller(String georouterUrl) {
	this(georouterUrl, Optional.empty());
    }

    public RoutePlannerRestCaller(String georouterUrl, Optional<String> azureApiKeyOpt) {

	// Modify endpoint to meet our local server
	this.geoRouterMatrixApi.getApiClient().setBasePath(georouterUrl);

	// Modify endpoint to meet our local server
	this.geoRouterTBTApi.getApiClient().setBasePath(georouterUrl);

	// Get the mapper from the generated files
	this.routePlannerObjectMapper = this.geoRouterMatrixApi.getApiClient().getObjectMapper();
	
	this.routePlannerObjectMapper.setSerializationInclusion(Include.NON_NULL).registerModule(new JavaTimeModule())
		.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

	// Invoke api key if desired
	if (azureApiKeyOpt.isPresent()) {
	    String azureApiKey = azureApiKeyOpt.get();

	    this.geoRouterMatrixApi.getApiClient().addDefaultHeader("Cache-Control", "no-cache")
		    .addDefaultHeader("Ocp-Apim-Subscription-Key", azureApiKey);

	    this.geoRouterTBTApi.getApiClient().addDefaultHeader("Cache-Control", "no-cache")
		    .addDefaultHeader("Ocp-Apim-Subscription-Key", azureApiKey);

	}
    }

    public ObjectMapper getMapper() {
	return this.routePlannerObjectMapper;
    }

    private static GeoMatrixRoutingServiceControllerApi createGeoMatrixRoutingServiceControllerApi() {

	DateFormat dateFormat = ApiClient.createDefaultDateFormat();
	ObjectMapper objectMapper = ApiClient.createDefaultObjectMapper(dateFormat);

	// Vital step! - We need to the generated mapper how to treat our JSON data
	objectMapper.setSerializationInclusion(Include.NON_NULL).setSerializationInclusion(Include.NON_ABSENT)
		.registerModule(new JavaTimeModule()).configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

	WebClient webClient = ApiClient.buildWebClientBuilder(objectMapper).codecs(configurer -> configurer
		.defaultCodecs().maxInMemorySize(MAX_TOUROPIMIZER_RESPONSESIZE_MB * 1024 * 1024)).build();

	ApiClient myApiClient = new ApiClient(webClient);

	return new GeoMatrixRoutingServiceControllerApi(myApiClient);

    }

    /*
     *
     * Step 2) - GeoRouteMatrix
     *
     */

    public List<ElementConnection> geoRouteMatrix(List<Position> nodeSrcPoss, List<Position> ressSrcPoss, boolean isUseCorrection) {

	MatrixRoutingRequest request = createMatrixRequest(nodeSrcPoss, ressSrcPoss);
	request.setUseCorrection(isUseCorrection);
	Flux<ElementConnection> consFlux = geoRouterMatrixApi.connections(request);

	// Wait for the connections
	return consFlux.onErrorResume(RestErrorHandler.connectionListErrorResumer(routePlannerObjectMapper))
		.collectList().block();
    }
    
    public List<ElementConnection> geoRouteSmartMatrix(Set<Node> nodes, Set<Resource> ress, boolean isUseCorrection) {

	SmartMatrixRoutingRequest smartRequest = new SmartMatrixRoutingRequest();

	smartRequest.setNodes(nodes);
	smartRequest.setResources(ress);

	RequestDirectionsOptions directionsOptions = new RequestDirectionsOptions();
	directionsOptions.setNarrative(false);
	smartRequest.setDirectionsOptions(directionsOptions);
	smartRequest.setUseCorrection(isUseCorrection);

	Flux<ElementConnection> consFlux = geoRouterMatrixApi.smartConnections(smartRequest);

	// Wait for the connections
	return consFlux.onErrorResume(RestErrorHandler.connectionListErrorResumer(routePlannerObjectMapper))
		.collectList().block();
    }

    public static MatrixRoutingRequest createMatrixRequest(RestOptimization input) {
	List<Position> nodeSrcPoss = input.getNodes().stream().filter(n -> n.getType().getTypeName().equals("Geo"))
		.map(n -> ((GeoNode) n.getType()).getPosition()).collect(Collectors.toList());

	List<Position> ressSrcPoss = input.getResources().stream().map(Resource::getPosition)
		.collect(Collectors.toList());

	return createMatrixRequest(nodeSrcPoss, ressSrcPoss);
    }

    public static MatrixRoutingRequest createMatrixRequest(List<Position> nodeSrcPoss, List<Position> ressSrcPoss) {

	MatrixRoutingRequest request = new MatrixRoutingRequest();

	List<Position> sourcePositions = new ArrayList<>();
	sourcePositions.addAll(nodeSrcPoss);
	sourcePositions.addAll(ressSrcPoss);

	request.sourcePositions(sourcePositions);

	RequestDirectionsOptions directionsOptions = new RequestDirectionsOptions();
	directionsOptions.setNarrative(false);
	request.setDirectionsOptions(directionsOptions);

	return request;
    }

    /*
     *
     * Step 4) - GeoRouteSolution
     *
     */
    public Solution geoRouteSolution(Solution existing) {

	// Create routes
	Mono<Solution> routedSolutionMono = geoRouterTBTApi.solutionWithDefaultSettings(existing);

	return routedSolutionMono.onErrorResume(RestErrorHandler.solutionErrorResumer(routePlannerObjectMapper)).block();
    }

    public TurnByTurnResponseItem singleTBT(TurnByTurnRoutingRequest request) {
	// Create routes
	Mono<TurnByTurnResponseItem> tbtResponse = geoRouterTBTApi.single(request);

	return tbtResponse.onErrorResume(RestErrorHandler.tbtResponseErrorResumer(routePlannerObjectMapper)).block();
    }

}
