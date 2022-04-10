package com.dna.jopt.rest.client.example.touroptimizer.helper;

import java.io.PrintStream;
import java.text.DateFormat;
import java.util.List;
import java.util.Optional;
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
    private PrintStream printOutStream;

    public static final int MAX_TOUROPIMIZER_RESPONSESIZE_MB = 16;

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

    public void setPrintOutStream(PrintStream outstream) {
	this.printOutStream = outstream;
    }

    private PrintStream getPrintOutStream() {
	if (this.printOutStream != null) {
	    return this.printOutStream;
	} else {
	    return System.out;
	}
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
	List<Resource> ress = ressPoss.stream().map(p -> TestElementsCreator.defaultCapacityResource(p, p.getLocationId()))
		.collect(Collectors.toList());

	RestOptimization optimization = TestRestOptimizationCreator.defaultTouroptimizerTestInput(nodes, ress, jsonLicenseOpt);

	optimization.setElementConnections(connections);

	// This will keep the example alive. Otherwise just subscribe
	return optimize(optimization);
    }
    
    
    public RestOptimization optimize(RestOptimization optimization) {

	// Let us attach to streams - Internally the subscription is done on "real"
	// optimization start
	attachToStreams(this.getPrintOutStream());

	// Trigger the Optimization
	Mono<RestOptimization> resultMono = geoOptimizerApi.run(optimization);

	// This will keep the example alive. Otherwise just subscribe
	return resultMono.onErrorResume(RestErrorHandler.restOptimizationErrorResumer(tourOptimizerObjectMapper)).block();
    }

    public Solution optimizeOnlyResult(List<Position> nodePoss, List<Position> ressPoss,
	    List<ElementConnection> connections, Optional<String> jsonLicenseOpt) {
	// Map to elements
	List<Node> nodes = nodePoss.stream().map(p -> TestElementsCreator.defaultGeoNode(p, p.getLocationId()))
		.collect(Collectors.toList());
	List<Resource> ress = ressPoss.stream().map(p -> TestElementsCreator.defaultCapacityResource(p, p.getLocationId()))
		.collect(Collectors.toList());

	RestOptimization optimization = TestRestOptimizationCreator.defaultTouroptimizerTestInput(nodes, ress, jsonLicenseOpt);

	optimization.setElementConnections(connections);

	// Let us attach to streams - Internally the subscription is done on "real"
	// optimization start
	attachToStreams(this.getPrintOutStream());

	// Trigger the Optimization
	Mono<Solution> resultMono = geoOptimizerApi.runOnlyResult(optimization);

	// This will keep the example alive. Otherwise just subscribe
	return resultMono.onErrorResume(RestErrorHandler.solutionErrorResumer(tourOptimizerObjectMapper)).block();
    }

    public void attachToStreams(PrintStream outstream) {

	geoOptimizerApi.runStartedSginal().subscribe(b -> {

	    geoOptimizerApi.progress()
		    .subscribe(pr -> outstream.println("  " + pr.getCallerId() + ", " + pr.getCurProgress()));

	    geoOptimizerApi.status().subscribe(s -> outstream.println("  " + s.getMessage()));

	    geoOptimizerApi.error().subscribe(e -> outstream.println("  " + e.getMessage()));

	    geoOptimizerApi.warning().subscribe(w -> outstream.println(" " + w.getMessage()));
	});
    }

}
