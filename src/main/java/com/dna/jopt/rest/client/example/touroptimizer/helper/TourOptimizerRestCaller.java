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
import java.util.stream.Collectors;

import org.springframework.web.reactive.function.client.WebClient;
import com.dna.jopt.rest.client.model.ElementConnection;
import com.dna.jopt.rest.client.model.Node;
import com.dna.jopt.rest.client.model.Position;
import com.dna.jopt.rest.client.model.Resource;
import com.dna.jopt.rest.client.model.RestOptimization;
import com.dna.jopt.rest.client.model.Solution;
import com.dna.jopt.rest.client.util.errorhandling.RestErrorHandler;
import com.dna.jopt.rest.client.util.testinputcreation.TestElementsCreator;
import com.dna.jopt.rest.client.util.testinputcreation.TestRestOptimizationCreator;
import com.dna.jopt.rest.touroptimizer.client.ApiClient;
import com.dna.jopt.rest.touroptimizer.client.api.OptimizationServiceControllerApi;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import reactor.core.publisher.Mono;

public class TourOptimizerRestCaller {

    private OptimizationServiceControllerApi geoOptimizerApi = createOptimizationControllerApi();

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

	// Get the mapper from the generated files
	this.tourOptimizerObjectMapper = this.geoOptimizerApi.getApiClient().getObjectMapper();

	// Invoke api key if desired
	if (azureApiKeyOpt.isPresent()) {
	    String azureApiKey = azureApiKeyOpt.get();

	    this.geoOptimizerApi.getApiClient().addDefaultHeader("Cache-Control", "no-cache")
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

    /*
     *
     * Step - Optimize
     *
     */

    public RestOptimization optimize(List<Position> nodePoss, List<Position> ressPoss,
	    List<ElementConnection> connections, Optional<String> jsonLicenseOpt) {
	// Map to elements
	List<Node> nodes = nodePoss.stream().map(p -> TestElementsCreator.defaultGeoNode(p, p.getLocationId()))
		.collect(Collectors.toList());
	List<Resource> ress = ressPoss.stream()
		.map(p -> TestElementsCreator.defaultCapacityResource(p, p.getLocationId()))
		.collect(Collectors.toList());

	RestOptimization optimization = TestRestOptimizationCreator.defaultTouroptimizerTestInput(nodes, ress,
		jsonLicenseOpt);

	optimization.setElementConnections(connections);

	// This will keep the example alive. Otherwise just subscribe
	return optimize(optimization);
    }

    public Solution optimizeOnlyResult(List<Position> nodePoss, List<Position> ressPoss,
	    List<ElementConnection> connections, Optional<String> jsonLicenseOpt) {
	// Map to elements
	List<Node> nodes = nodePoss.stream().map(p -> TestElementsCreator.defaultGeoNode(p, p.getLocationId()))
		.collect(Collectors.toList());
	List<Resource> ress = ressPoss.stream()
		.map(p -> TestElementsCreator.defaultCapacityResource(p, p.getLocationId()))
		.collect(Collectors.toList());

	RestOptimization optimization = TestRestOptimizationCreator.defaultTouroptimizerTestInput(nodes, ress,
		jsonLicenseOpt);

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

    public void attachToStreams() {

	if(biConsumer!=null) {
	    geoOptimizerApi.runStartedSginal().subscribe(b -> biConsumer.accept(geoOptimizerApi, b));
	}else {
	    geoOptimizerApi.runStartedSginal().subscribe(b -> DEFAULT_STREAM_CONSUMER.accept(geoOptimizerApi, b)); 
	}
	

    }

}
