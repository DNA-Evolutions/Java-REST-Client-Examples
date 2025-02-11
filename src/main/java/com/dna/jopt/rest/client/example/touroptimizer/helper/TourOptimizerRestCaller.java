package com.dna.jopt.rest.client.example.touroptimizer.helper;

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
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import org.springframework.web.reactive.function.client.WebClient;

import com.dna.jopt.rest.client.model.CreatorSetting;
import com.dna.jopt.rest.client.model.DatabaseInfoSearch;
import com.dna.jopt.rest.client.model.DatabaseInfoSearchResult;
import com.dna.jopt.rest.client.model.DatabaseItemSearch;
import com.dna.jopt.rest.client.model.ElementConnection;
import com.dna.jopt.rest.client.model.JSONConfig;
import com.dna.jopt.rest.client.model.Node;
import com.dna.jopt.rest.client.model.OptimizationOptions;
import com.dna.jopt.rest.client.model.OptimizationPersistenceSetting;
import com.dna.jopt.rest.client.model.Position;
import com.dna.jopt.rest.client.model.Resource;
import com.dna.jopt.rest.client.model.RestOptimization;
import com.dna.jopt.rest.client.model.Solution;
import com.dna.jopt.rest.client.util.errorhandling.RestErrorHandler;
import com.dna.jopt.rest.client.util.testinputcreation.TestElementsCreator;
import com.dna.jopt.rest.client.util.testinputcreation.TestRestOptimizationCreator;
import com.dna.jopt.rest.touroptimizer.client.ApiClient;
import com.dna.jopt.rest.touroptimizer.client.api.OptimizationFafServiceControllerApi;
import com.dna.jopt.rest.touroptimizer.client.api.OptimizationServiceControllerApi;
import com.dna.jopt.rest.touroptimizer.client.api.ReadDatabaseServiceControllerApi;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class TourOptimizerRestCaller {

    private OptimizationServiceControllerApi geoOptimizerApi = createOptimizationControllerApi();
    private OptimizationFafServiceControllerApi geoFafOptimizerApi = createFafOptimizationControllerApi();
    private ReadDatabaseServiceControllerApi geoReadDatabaseApi = createReadDatabaseServiceControllerApi();

    public final ObjectMapper tourOptimizerObjectMapper;

    private BiConsumer<OptimizationServiceControllerApi, Boolean> biConsumer;

    public static final int MAX_TOUROPIMIZER_RESPONSESIZE_MB = 16;

    public static final BiConsumer<OptimizationServiceControllerApi, Boolean> DEFAULT_STREAM_CONSUMER = (api, b) -> {

	api.progress().subscribe(pr -> System.out.println("  " + pr.getCallerId() + ", " + pr.getCurProgress()));

	api.status().subscribe(s -> System.out.println("  " + s.getMessage()));

	api.error().subscribe(e -> System.out.println("  " + e.getMessage()));

	api.warning().subscribe(w -> System.out.println(" " + w.getMessage()));
    };

    /*
     *
     */

    public TourOptimizerRestCaller(String tourOptimizerUrl) {
	this(tourOptimizerUrl, Optional.empty());
    }

    public TourOptimizerRestCaller(String tourOptimizerUrl, Optional<String> azureApiKeyOpt) {

	// Modify endpoint to meet server
	this.geoOptimizerApi.getApiClient().setBasePath(tourOptimizerUrl);
	
	// Modify endpoint to meet server
	this.geoFafOptimizerApi.getApiClient().setBasePath(tourOptimizerUrl);
	
	// Modify endpoint to meet server
	this.geoReadDatabaseApi.getApiClient().setBasePath(tourOptimizerUrl);

	// Get the mapper from the generated files
	this.tourOptimizerObjectMapper = this.geoOptimizerApi.getApiClient().getObjectMapper();
	
	this.tourOptimizerObjectMapper.setSerializationInclusion(Include.NON_NULL)
		.setSerializationInclusion(Include.NON_ABSENT).registerModule(new JavaTimeModule())
		.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

	// Invoke api key if desired
	if (azureApiKeyOpt.isPresent()) {
	    String azureApiKey = azureApiKeyOpt.get();

	    this.geoOptimizerApi.getApiClient().addDefaultHeader("Cache-Control", "no-cache")
		    .addDefaultHeader("Ocp-Apim-Subscription-Key", azureApiKey);

	    this.geoFafOptimizerApi.getApiClient().addDefaultHeader("Cache-Control", "no-cache")
		    .addDefaultHeader("Ocp-Apim-Subscription-Key", azureApiKey);

	    this.geoReadDatabaseApi.getApiClient().addDefaultHeader("Cache-Control", "no-cache")
		    .addDefaultHeader("Ocp-Apim-Subscription-Key", azureApiKey);

	}
    }

    public ObjectMapper getMapper() {
	return this.tourOptimizerObjectMapper;
    }

    public void setStreamConsumer(BiConsumer<OptimizationServiceControllerApi, Boolean> biConsumer) {
	this.biConsumer = biConsumer;
    }

    private static OptimizationServiceControllerApi createOptimizationControllerApi() {

	DateFormat dateFormat = ApiClient.createDefaultDateFormat();
	ObjectMapper objectMapper = ApiClient.createDefaultObjectMapper(dateFormat);

	// Vital step! - We need to the generated mapper how to treat our JSON data
	objectMapper.setSerializationInclusion(Include.NON_NULL).setSerializationInclusion(Include.NON_ABSENT)
		.registerModule(new JavaTimeModule()).configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

	WebClient webClient = ApiClient.buildWebClientBuilder(objectMapper).codecs(configurer -> configurer
		.defaultCodecs().maxInMemorySize(MAX_TOUROPIMIZER_RESPONSESIZE_MB * 1024 * 1024)).build();

	ApiClient myApiClient = new ApiClient(webClient);

	return new OptimizationServiceControllerApi(myApiClient);

    }

    private static OptimizationFafServiceControllerApi createFafOptimizationControllerApi() {

	DateFormat dateFormat = ApiClient.createDefaultDateFormat();
	ObjectMapper objectMapper = ApiClient.createDefaultObjectMapper(dateFormat);

	// Vital step! - We need to the generated mapper how to treat our JSON data
	objectMapper.setSerializationInclusion(Include.NON_NULL).setSerializationInclusion(Include.NON_ABSENT)
		.registerModule(new JavaTimeModule()).configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

	WebClient webClient = ApiClient.buildWebClientBuilder(objectMapper).codecs(configurer -> configurer
		.defaultCodecs().maxInMemorySize(MAX_TOUROPIMIZER_RESPONSESIZE_MB * 1024 * 1024)).build();

	ApiClient myApiClient = new ApiClient(webClient);

	return new OptimizationFafServiceControllerApi(myApiClient);

    }
    
    
    private static ReadDatabaseServiceControllerApi createReadDatabaseServiceControllerApi() {

	DateFormat dateFormat = ApiClient.createDefaultDateFormat();
	ObjectMapper objectMapper = ApiClient.createDefaultObjectMapper(dateFormat);

	// Vital step! - We need to the generated mapper how to treat our JSON data
	objectMapper.setSerializationInclusion(Include.NON_NULL).setSerializationInclusion(Include.NON_ABSENT)
		.registerModule(new JavaTimeModule()).configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

	WebClient webClient = ApiClient.buildWebClientBuilder(objectMapper).codecs(configurer -> configurer
		.defaultCodecs().maxInMemorySize(MAX_TOUROPIMIZER_RESPONSESIZE_MB * 1024 * 1024)).build();

	ApiClient myApiClient = new ApiClient(webClient);

	return new ReadDatabaseServiceControllerApi(myApiClient);

    }

    /*
     *
     * Step - Optimize
     *
     */

    public RestOptimization optimize(List<Position> nodePoss, List<Position> ressPoss,
	    List<ElementConnection> connections, Optional<String> jsonLicenseOpt) {
	// Map to elements
	List<Node> nodes = nodePoss.stream().map(p -> TestElementsCreator.defaultGeoNode(p, p.getLocationId()))
		.toList();
	List<Resource> ress = ressPoss.stream()
		.map(p -> TestElementsCreator.defaultCapacityResource(p, p.getLocationId()))
		.toList();

	RestOptimization optimization = TestRestOptimizationCreator.defaultTouroptimizerTestInput(nodes, ress,
		jsonLicenseOpt, Optional.empty());

	optimization.setElementConnections(connections);

	// This will keep the example alive. Otherwise just subscribe
	return optimize(optimization);
    }
    
    
    public RestOptimization optimize(List<Node> nodes, List<Resource> ress,
	    List<ElementConnection> connections, Optional<String> jsonLicenseOpt, Optional<OptimizationOptions> optimizationOptionsOpt) {

	RestOptimization optimization = TestRestOptimizationCreator.defaultTouroptimizerTestInput(nodes, ress,
		jsonLicenseOpt,optimizationOptionsOpt);

	optimization.setElementConnections(connections);
	
	return optimize(optimization);
    }

    public Boolean optimizeFireAndForget(List<Position> nodePoss, List<Position> ressPoss,
	    List<ElementConnection> connections, String optiIdent, CreatorSetting creatorSettings,
	    OptimizationPersistenceSetting persistenceSetting, Optional<String> jsonLicenseOpt) {
	// Map to elements
	List<Node> nodes = nodePoss.stream().map(p -> TestElementsCreator.defaultGeoNode(p, p.getLocationId()))
		.toList();
	List<Resource> ress = ressPoss.stream()
		.map(p -> TestElementsCreator.defaultCapacityResource(p, p.getLocationId())).toList();

	RestOptimization optimization = TestRestOptimizationCreator.defaultTouroptimizerTestInput(nodes, ress,
		jsonLicenseOpt, Optional.empty());

	optimization.setElementConnections(connections);

	// Modify defaults
	optimization.setIdent(optiIdent);
	JSONConfig curExt = optimization.getExtension();

	curExt.setCreatorSetting(creatorSettings);

	curExt.setPersistenceSetting(persistenceSetting);

	// This will keep the example alive. Otherwise just subscribe
	return optimizeFireAndForget(optimization);
    }

    public Solution optimizeOnlyResult(List<Position> nodePoss, List<Position> ressPoss,
	    List<ElementConnection> connections, Optional<String> jsonLicenseOpt) {
	// Map to elements
	List<Node> nodes = nodePoss.stream().map(p -> TestElementsCreator.defaultGeoNode(p, p.getLocationId()))
		.toList();
	List<Resource> ress = ressPoss.stream()
		.map(p -> TestElementsCreator.defaultCapacityResource(p, p.getLocationId()))
		.toList();

	RestOptimization optimization = TestRestOptimizationCreator.defaultTouroptimizerTestInput(nodes, ress,
		jsonLicenseOpt, Optional.empty());

	optimization.setElementConnections(connections);

	// Let us attach to streams - Internally the subscription is done on "real"
	// optimization start
	attachToStreams();

	// Trigger the Optimization
	Mono<Solution> resultMono = geoOptimizerApi.runOnlyResult(optimization);

	// This will keep the example alive. Otherwise just subscribe
	return resultMono.onErrorResume(RestErrorHandler.solutionErrorResumer(tourOptimizerObjectMapper)).block();
    }

    public RestOptimization optimize(RestOptimization optimization) {

	// Let us attach to streams - Internally the subscription is done on "real"
	// optimization start
	attachToStreams();

	// Trigger the Optimization
	Mono<RestOptimization> resultMono = geoOptimizerApi.run(optimization);

	// This will keep the example alive. Otherwise just subscribe
	return resultMono.onErrorResume(RestErrorHandler.restOptimizationErrorResumer(tourOptimizerObjectMapper))
		.block();
    }

    public Boolean optimizeFireAndForget(RestOptimization optimization) {

	// Trigger the Optimization
	Mono<Boolean> resultMono = geoFafOptimizerApi.runFAF(optimization);

	// This will keep the example alive. Otherwise just subscribe
	// This will keep the example alive. Otherwise just subscribe
	return resultMono.block();

    }

    public void attachToStreams() {

	if (biConsumer != null) {
	    geoOptimizerApi.runStartedSignal().subscribe(b -> biConsumer.accept(geoOptimizerApi, b));
	} else {
	    geoOptimizerApi.runStartedSignal().subscribe(b -> DEFAULT_STREAM_CONSUMER.accept(geoOptimizerApi, b));
	}

    }
    
    
    /*
     * Read from database
     */
    
    public RestOptimization findOptimizationInDatabase(DatabaseItemSearch searchItem) {

	Mono<RestOptimization> resultMono = this.geoReadDatabaseApi.findOptimization(searchItem);

	return resultMono.onErrorResume(RestErrorHandler.restOptimizationErrorResumer(tourOptimizerObjectMapper))
		.block();
    }
    
    public List<DatabaseInfoSearchResult> findOptimizationInfosInDatabase(DatabaseInfoSearch searchItem) {
	Flux<DatabaseInfoSearchResult> resultFlux = this.geoReadDatabaseApi.findsOptimizationInfos(searchItem);
	
	// TODO some error handling
	return resultFlux.collectList().block();
    }

}
