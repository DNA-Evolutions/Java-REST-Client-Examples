package com.dna.jopt.rest.client.example.fullstack.helper;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.MediaType;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.Builder;

import com.dna.jopt.rest.client.model.ElementConnection;
import com.dna.jopt.rest.client.model.GeoAddress;
import com.dna.jopt.rest.client.model.GeoNode;
import com.dna.jopt.rest.client.model.MatrixRoutingRequest;
import com.dna.jopt.rest.client.model.Node;
import com.dna.jopt.rest.client.model.Position;
import com.dna.jopt.rest.client.model.RequestDirectionsOptions;
import com.dna.jopt.rest.client.model.Resource;
import com.dna.jopt.rest.client.model.RestOptimization;
import com.dna.jopt.rest.client.model.Solution;
import com.dna.jopt.rest.client.model.Status;
import com.dna.jopt.rest.client.util.testinput.TestInput;
import com.dna.jopt.rest.geocoder.client.api.BatchForwardGeoCodingServiceControllerApi;
import com.dna.jopt.rest.geocoder.client.api.HealthStatusApi;
import com.dna.jopt.rest.georouter.client.api.GeoMatrixRoutingServiceControllerApi;
import com.dna.jopt.rest.georouter.client.api.GeoTurnByTurnRoutingServiceControllerApi;
import com.dna.jopt.rest.touroptimizer.client.ApiClient;
import com.dna.jopt.rest.touroptimizer.client.api.OptimizationServiceControllerApi;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class FullStackRestCaller {

    /*
     * Define api services
     */
    private BatchForwardGeoCodingServiceControllerApi geoCoderBatchForwardApi = new BatchForwardGeoCodingServiceControllerApi();
    private HealthStatusApi geoCoderHealth = new HealthStatusApi();

    private GeoMatrixRoutingServiceControllerApi geoRouterMatrixApi = new GeoMatrixRoutingServiceControllerApi();
    private GeoTurnByTurnRoutingServiceControllerApi geoRouterTBTApi = new GeoTurnByTurnRoutingServiceControllerApi();

    private OptimizationServiceControllerApi geoOptimizerApi;

    public final ObjectMapper tourOptimizerObjectMapper;

    /*
     *
     */

    public FullStackRestCaller(String geocoderUrl, String georouterUrl, String tourOptimizerUrl) {
	this(geocoderUrl, georouterUrl, tourOptimizerUrl, Optional.empty());
    }

    public FullStackRestCaller(String geocoderUrl, String georouterUrl, String tourOptimizerUrl,
	    Optional<String> azureApiKeyOpt) {

	// Modify endpoint to meet server
	this.geoCoderBatchForwardApi.getApiClient().setBasePath(geocoderUrl);

	this.geoCoderHealth.getApiClient().setBasePath(geocoderUrl);

	// Modify endpoint to meet server
	

//	
//	DateFormat format = ApiClient.createDefaultDateFormat();
//	ObjectMapper curMapper = ApiClient.createDefaultObjectMapper(format);
//	
//	  ExchangeStrategies strategies = ExchangeStrategies
//	            .builder()
//	            .codecs(clientDefaultCodecsConfigurer -> {
//	                clientDefaultCodecsConfigurer.defaultCodecs().jackson2JsonEncoder(new Jackson2JsonEncoder(curMapper, MediaType.APPLICATION_JSON));
//	                clientDefaultCodecsConfigurer.defaultCodecs().jackson2JsonDecoder(new Jackson2JsonDecoder(curMapper, MediaType.APPLICATION_JSON));
//	            }).build();
//	        WebClient.Builder webClientBuilder = WebClient.builder().exchangeStrategies(strategies);
//	        
//	WebClient webClient = ApiClient.buildWebClientBuilder(curMapper)
//		     // .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(16 * 1024 * 1024))
//		      .build();
//	
//	ApiClient apiCLient = new ApiClient(webClientBuilder.build());
//	
//	this.geoOptimizerApi = new OptimizationServiceControllerApi(apiCLient);
//	
//	
//	this.geoOptimizerApi = new OptimizationServiceControllerApi();
//	WebClient mutateClient = this.geoOptimizerApi.getApiClient().getWebClient().mutate().build();
//	
//	ApiClient newApiCLient = new ApiClient(mutateClient);
	
	
	this.geoOptimizerApi = new OptimizationServiceControllerApi();
	
	this.geoOptimizerApi.getApiClient().setBasePath(tourOptimizerUrl);

	// Modify endpoint to meet our local server
	this.geoRouterMatrixApi.getApiClient().setBasePath(georouterUrl);

	// Modify endpoint to meet our local server
	this.geoRouterTBTApi.getApiClient().setBasePath(georouterUrl);

	// Get the mapper from the generated files
	this.tourOptimizerObjectMapper = this.geoOptimizerApi.getApiClient().getObjectMapper();

	// Vital step! - We need to the generated mapper how to treat our JSON data
	this.tourOptimizerObjectMapper.setSerializationInclusion(Include.NON_NULL)
		.setSerializationInclusion(Include.NON_ABSENT).registerModule(new JavaTimeModule())
		.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

	// Invoke api key if desired
	if (azureApiKeyOpt.isPresent()) {
	    String azureApiKey = azureApiKeyOpt.get();

	    this.geoCoderBatchForwardApi.getApiClient().addDefaultHeader("Cache-Control", "no-cache")
		    .addDefaultHeader("Ocp-Apim-Subscription-Key", azureApiKey);

	    this.geoCoderHealth.getApiClient().addDefaultHeader("Cache-Control", "no-cache")
		    .addDefaultHeader("Ocp-Apim-Subscription-Key", azureApiKey);

	    this.geoOptimizerApi.getApiClient().addDefaultHeader("Cache-Control", "no-cache")
		    .addDefaultHeader("Ocp-Apim-Subscription-Key", azureApiKey);

	    this.geoRouterMatrixApi.getApiClient().addDefaultHeader("Cache-Control", "no-cache")
		    .addDefaultHeader("Ocp-Apim-Subscription-Key", azureApiKey);

	    this.geoRouterTBTApi.getApiClient().addDefaultHeader("Cache-Control", "no-cache")
		    .addDefaultHeader("Ocp-Apim-Subscription-Key", azureApiKey);

	}
    }

    /*
     * 
     * Step 0) - Healthcheck
     * 
     */
    public Status checkGeoCoderHealth() {
	return this.geoCoderHealth.healthStatus().block();
    }

    /*
     *
     * Step 1) - GeoCode
     *
     */
    public List<Position> geoCodeNodePositions(List<GeoAddress> nodeAddresses) {
	Flux<Position> nodePositionsFlux = geoCoderBatchForwardApi.batchForward(nodeAddresses);

	// Wait for the connections
	return nodePositionsFlux.collectList().block();
    }

    public List<Position> geoCodeResourcePositions(List<GeoAddress> ressAddresses) {

	Flux<Position> ressPositionsFlux = geoCoderBatchForwardApi.batchForward(ressAddresses);

	// Wait for the connections
	return ressPositionsFlux.collectList().block();
    }

    /*
     *
     * Step 2) - GeoRouteMatrix
     *
     */
    public List<ElementConnection> geoRouteMatrix(List<Position> nodeSrcPoss, List<Position> ressSrcPoss) {

	MatrixRoutingRequest request = createMatrixRequest(nodeSrcPoss, ressSrcPoss);
	Flux<ElementConnection> consFlux = geoRouterMatrixApi.connections(request);

	// Wait for the connections
	return consFlux.collectList().block();
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
     * Step 3) - Optimize
     *
     */

    public RestOptimization optimize(List<Position> nodePoss, List<Position> ressPoss,
	    List<ElementConnection> connections, Optional<String> jsonLicenseOpt) {
	// Map to elements
	List<Node> nodes = nodePoss.stream().map(p -> TestInput.defaultGeoNode(p, p.getLocationId()))
		.collect(Collectors.toList());
	List<Resource> ress = ressPoss.stream().map(p -> TestInput.defaultCapacityResource(p, p.getLocationId()))
		.collect(Collectors.toList());

	RestOptimization optimization = TestInput.defaultTouroptimizerTestInput(nodes, ress, jsonLicenseOpt);
	optimization.setElementConnections(connections);

	// Let us attach to streams - Internally the subscription is done on "real"
	// optimization start
	attachToStreams();

	// Trigger the Optimization
	Mono<RestOptimization> resultMono = geoOptimizerApi.run(optimization);

	// This will keep the example alive. Otherwise just subscribe
	return resultMono.block();
    }
    
    public Solution optimizeOnlyResult(List<Position> nodePoss, List<Position> ressPoss,
	    List<ElementConnection> connections, Optional<String> jsonLicenseOpt) {
	// Map to elements
	List<Node> nodes = nodePoss.stream().map(p -> TestInput.defaultGeoNode(p, p.getLocationId()))
		.collect(Collectors.toList());
	List<Resource> ress = ressPoss.stream().map(p -> TestInput.defaultCapacityResource(p, p.getLocationId()))
		.collect(Collectors.toList());

	RestOptimization optimization = TestInput.defaultTouroptimizerTestInput(nodes, ress, jsonLicenseOpt);
	optimization.setElementConnections(connections);

	// Let us attach to streams - Internally the subscription is done on "real"
	// optimization start
	attachToStreams();

	// Trigger the Optimization
	Mono<Solution> resultMono = geoOptimizerApi.runOnlyResult(optimization);

	// This will keep the example alive. Otherwise just subscribe
	return resultMono.block();
    }


    public void attachToStreams() {

	geoOptimizerApi.runStartedSginal().subscribe(b -> {
	    System.out.print("Optimization - Running");

	    geoOptimizerApi.progress()
		    .subscribe(pr -> System.out.println("  " + pr.getCallerId() + ", " + pr.getCurProgress()));

	    geoOptimizerApi.status().subscribe(s -> System.out.println("  " + s.getMessage()));

	    geoOptimizerApi.error().subscribe(e -> System.out.println("  " + e.getMessage()));

	    geoOptimizerApi.warning().subscribe(w -> System.out.println(" " + w.getMessage()));
	});
    }

    /*
     *
     * Step 4) - GeoRouteSolution
     *
     */
    public Solution geoRouteSolution(Solution existing) {

	// Create routes
	Mono<Solution> routedSolutionMono = geoRouterTBTApi.solutionWithDefaultSettings(existing);

	return routedSolutionMono.block();
    }

}
