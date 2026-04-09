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
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import com.dna.jopt.rest.client.model.CreatorSetting;
import com.dna.jopt.rest.client.model.DatabaseInfoSearch;
import com.dna.jopt.rest.client.model.DatabaseInfoSearchResult;

import com.dna.jopt.rest.client.model.ElementConnection;
import com.dna.jopt.rest.client.model.JSONConfig;
import com.dna.jopt.rest.client.model.JobAcceptedResponse;
import com.dna.jopt.rest.client.model.Node;
import com.dna.jopt.rest.client.model.OptimizationOptions;
import com.dna.jopt.rest.client.model.OptimizationPersistenceSetting;
import com.dna.jopt.rest.client.model.Position;
import com.dna.jopt.rest.client.model.Resource;
import com.dna.jopt.rest.client.model.RestOptimization;
import com.dna.jopt.rest.client.model.RunAcceptedResponse;
import com.dna.jopt.rest.client.model.Solution;
import com.dna.jopt.rest.client.util.errorhandling.RestErrorHandler;
import com.dna.jopt.rest.client.util.testinputcreation.TestElementsCreator;
import com.dna.jopt.rest.client.util.testinputcreation.TestRestOptimizationCreator;
import com.dna.jopt.rest.touroptimizer.client.ApiClient;
import com.dna.jopt.rest.touroptimizer.client.api.JobApi;
import com.dna.jopt.rest.touroptimizer.client.api.OptimizationApi;
import com.dna.jopt.rest.touroptimizer.client.api.StreamApi;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * High-level REST client that wraps the generated TourOptimizer API classes
 * ({@link com.dna.jopt.rest.touroptimizer.client.api.OptimizationApi},
 * {@link com.dna.jopt.rest.touroptimizer.client.api.JobApi},
 * {@link com.dna.jopt.rest.touroptimizer.client.api.StreamApi}) and provides
 * convenient entry points for the most common workflows.
 *
 * <h3>Supported workflows</h3>
 * <ul>
 * <li><b>Synchronous optimization</b> &ndash; {@link #optimize} methods submit
 * a run, automatically subscribe to progress/status/error/warning streams, and
 * block until the result is available (up to 10 minutes).</li>
 * <li><b>Fire-and-forget (async with database)</b> &ndash;
 * {@link #optimizeFireAndForget} methods submit a job via the Job API and
 * return a {@link com.dna.jopt.rest.client.model.JobAcceptedResponse}
 * immediately. Results are persisted in MongoDB and can be retrieved later with
 * {@link #findOptimizationInDatabase} or searched with
 * {@link #findOptimizationInfosInDatabase}.</li>
 * <li><b>Solution-only</b> &ndash; {@link #optimizeOnlyResult} returns just the
 * {@link com.dna.jopt.rest.client.model.Solution} without the full input
 * echo.</li>
 * </ul>
 *
 * <h3>Configuration</h3>
 * <ul>
 * <li>The base URL is set via the constructor (local Docker or Azure
 * endpoint).</li>
 * <li>An optional Azure API subscription key can be injected.</li>
 * <li>A custom stream consumer can be set via {@link #setStreamConsumer} to
 * override the default console-printing subscriber.</li>
 * <li>Maximum response size is capped at
 * {@value #MAX_TOUROPIMIZER_RESPONSESIZE_MB} MB.</li>
 * </ul>
 *
 * @see com.dna.jopt.rest.client.util.endpoints.Endpoints
 */
public class TourOptimizerRestCaller {

    public static final String DEFAULT_XTENANT_ID = "local_tenantid";

    private static final Logger logger = LogManager.getLogger(TourOptimizerRestCaller.class);

    private OptimizationApi geoOptimizerApi = createOptimizationControllerApi();
    private JobApi geoFafOptimizerApi = createFafOptimizationControllerApi();
    private StreamApi streamApi = createStreamControllerApi();

    public final ObjectMapper tourOptimizerObjectMapper;

    private BiConsumer<StreamApi, String> biStreamConsumer;

    public static final int MAX_TOUROPIMIZER_RESPONSESIZE_MB = 16;

    public static final BiConsumer<StreamApi, String> DEFAULT_STREAM_CONSUMER = (api, runId) -> {

	System.out.println("Subscribing to default streams...");

	logger.info("Subscribing to default streams...");

	api.streamProgress(runId).subscribe(
		pr -> System.out.println("  PROGRESS: " + pr.getCallerId() + ", " + pr.getCurProgress()),
		err -> System.err.println("  PROGRESS ERROR: " + err.getMessage() + " / " + err.getClass().getName()));

	api.streamStatus(runId).subscribe(s -> System.out.println("  STATUS: " + s.getMessage()),
		err -> System.err.println("  STATUS ERROR: " + err.getMessage()));

	api.streamErrors(runId).subscribe(s -> System.out.println("  ERRORS: " + s.getMessage()),
		err -> System.err.println("  ERRORS ERROR: " + err.getMessage()));

	api.streamWarnings(runId).subscribe(s -> System.out.println("  WARNINGS: " + s.getMessage()),
		err -> System.err.println("  WARNINGS ERROR: " + err.getMessage()));
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
	this.streamApi.getApiClient().setBasePath(tourOptimizerUrl);

	// Get the mapper from the generated files
	this.tourOptimizerObjectMapper = this.geoOptimizerApi.getApiClient().getObjectMapper();

	this.tourOptimizerObjectMapper.setDefaultPropertyInclusion(Include.NON_ABSENT)
		.registerModule(new JavaTimeModule()).configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

	// Invoke api key if desired
	if (azureApiKeyOpt.isPresent()) {
	    String azureApiKey = azureApiKeyOpt.get();

	    this.geoOptimizerApi.getApiClient().addDefaultHeader("Cache-Control", "no-cache")
		    .addDefaultHeader("Ocp-Apim-Subscription-Key", azureApiKey);

	    this.geoFafOptimizerApi.getApiClient().addDefaultHeader("Cache-Control", "no-cache")
		    .addDefaultHeader("Ocp-Apim-Subscription-Key", azureApiKey);

	    this.streamApi.getApiClient().addDefaultHeader("Cache-Control", "no-cache")
		    .addDefaultHeader("Ocp-Apim-Subscription-Key", azureApiKey);

	}
    }

    public ObjectMapper getMapper() {
	return this.tourOptimizerObjectMapper;
    }

    public void setStreamConsumer(BiConsumer<StreamApi, String> biConsumer) {
	this.biStreamConsumer = biConsumer;
    }

    private static OptimizationApi createOptimizationControllerApi() {

	DateFormat dateFormat = ApiClient.createDefaultDateFormat();
	ObjectMapper objectMapper = ApiClient.createDefaultObjectMapper(dateFormat);

	// Vital step! - We need to the generated mapper how to treat our JSON data
	objectMapper.setDefaultPropertyInclusion(Include.NON_ABSENT).registerModule(new JavaTimeModule())
		.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

	WebClient webClient = ApiClient.buildWebClientBuilder(objectMapper).codecs(configurer -> configurer
		.defaultCodecs().maxInMemorySize(MAX_TOUROPIMIZER_RESPONSESIZE_MB * 1024 * 1024)).build();

	ApiClient myApiClient = new ApiClient(webClient);

	return new OptimizationApi(myApiClient);

    }

    private static StreamApi createStreamControllerApi() {

	DateFormat dateFormat = ApiClient.createDefaultDateFormat();
	ObjectMapper objectMapper = ApiClient.createDefaultObjectMapper(dateFormat);

	// Vital step! - We need to the generated mapper how to treat our JSON data
	objectMapper.setDefaultPropertyInclusion(Include.NON_ABSENT).registerModule(new JavaTimeModule())
		.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

	WebClient webClient = ApiClient.buildWebClientBuilder(objectMapper).codecs(configurer -> configurer
		.defaultCodecs().maxInMemorySize(MAX_TOUROPIMIZER_RESPONSESIZE_MB * 1024 * 1024)).build();

	ApiClient myApiClient = new ApiClient(webClient);
	myApiClient.addDefaultHeader(HttpHeaders.ACCEPT, MediaType.TEXT_EVENT_STREAM_VALUE);
	return new StreamApi(myApiClient);

    }

    private static JobApi createFafOptimizationControllerApi() {

	DateFormat dateFormat = ApiClient.createDefaultDateFormat();
	ObjectMapper objectMapper = ApiClient.createDefaultObjectMapper(dateFormat);

	// Vital step! - We need to the generated mapper how to treat our JSON data
	objectMapper.setDefaultPropertyInclusion(Include.NON_ABSENT).registerModule(new JavaTimeModule())
		.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

	WebClient webClient = ApiClient.buildWebClientBuilder(objectMapper).codecs(configurer -> configurer
		.defaultCodecs().maxInMemorySize(MAX_TOUROPIMIZER_RESPONSESIZE_MB * 1024 * 1024)).build();

	ApiClient myApiClient = new ApiClient(webClient);

	return new JobApi(myApiClient);

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
		.map(p -> TestElementsCreator.defaultCapacityResource(p, p.getLocationId())).toList();

	RestOptimization optimization = TestRestOptimizationCreator.defaultTouroptimizerTestInput(nodes, ress,
		jsonLicenseOpt, Optional.empty());

	optimization.setElementConnections(connections);

	// This will keep the example alive. Otherwise just subscribe
	return optimize(optimization);
    }

    public RestOptimization optimize(List<Node> nodes, List<Resource> ress, List<ElementConnection> connections,
	    Optional<String> jsonLicenseOpt, Optional<OptimizationOptions> optimizationOptionsOpt) {

	RestOptimization optimization = TestRestOptimizationCreator.defaultTouroptimizerTestInput(nodes, ress,
		jsonLicenseOpt, optimizationOptionsOpt);

	optimization.setElementConnections(connections);

	return optimize(optimization);
    }

    public JobAcceptedResponse optimizeFireAndForget(String xTenantId, List<Position> nodePoss, List<Position> ressPoss,
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
	return optimizeFireAndForget(xTenantId, optimization);
    }

    public Solution optimizeOnlyResult(List<Position> nodePoss, List<Position> ressPoss,
	    List<ElementConnection> connections, Optional<String> jsonLicenseOpt) {
	// Map to elements
	List<Node> nodes = nodePoss.stream().map(p -> TestElementsCreator.defaultGeoNode(p, p.getLocationId()))
		.toList();
	List<Resource> ress = ressPoss.stream()
		.map(p -> TestElementsCreator.defaultCapacityResource(p, p.getLocationId())).toList();

	RestOptimization optimization = TestRestOptimizationCreator.defaultTouroptimizerTestInput(nodes, ress,
		jsonLicenseOpt, Optional.empty());

	optimization.setElementConnections(connections);

	Function<Mono<RunAcceptedResponse>, Mono<Solution>> mapper = acceptedMono -> acceptedMono.flatMap(accepted -> {

	    // Let us attach to streams
	    attachToSynchStreams(accepted.getRunId());

	    Mono<Solution> resultMono = this.geoOptimizerApi.getRunSolution(accepted.getRunId());

	    return resultMono;
	});

	return this.optimize(optimization, mapper)
		.onErrorResume(RestErrorHandler.solutionErrorResumer(tourOptimizerObjectMapper))
		.block(Duration.ofMinutes(10));

    }

    /*
     * 
     * 
     * 
     */

    public RestOptimization optimize(RestOptimization optimization) {

	Function<Mono<RunAcceptedResponse>, Mono<RestOptimization>> mapper = acceptedMono -> acceptedMono
		.flatMap(accepted -> {

		    // Let us attach to streams
		    attachToSynchStreams(accepted.getRunId());

		    Mono<RestOptimization> resultMono = this.geoOptimizerApi.getRunResult(accepted.getRunId());

		    return resultMono;
		});

	return this.optimize(optimization, mapper)
		.onErrorResume(RestErrorHandler.restOptimizationErrorResumer(tourOptimizerObjectMapper))
		.block(Duration.ofMinutes(10));

    }

    public <T> Mono<T> optimize(RestOptimization optimization, Function<Mono<RunAcceptedResponse>, Mono<T>> mapper) {

	// Trigger the Optimization
	Mono<RunAcceptedResponse> resultMonoAccepted = this.geoOptimizerApi.startRun(optimization);

	return mapper.apply(resultMonoAccepted);
    }

    public JobAcceptedResponse optimizeFireAndForget(String xTenantId, RestOptimization optimization) {

	// Trigger the Optimization
	Mono<JobAcceptedResponse> resultAcceptenceMono = this.geoFafOptimizerApi.createJob(xTenantId, optimization);

	return resultAcceptenceMono.block(Duration.ofSeconds(20));

    }

    public void attachToSynchStreams(String runId) {

	logger.info("Attaching to streams with runId: " + runId);
	System.out.println("Attaching to streams with runId: " + runId);

	this.geoOptimizerApi.getStartedSignal(runId).subscribe(b -> {
	    System.out.println("Stated Signal: " + b);

	    if (biStreamConsumer != null) {
		biStreamConsumer.accept(streamApi, runId);
	    } else {
		DEFAULT_STREAM_CONSUMER.accept(streamApi, runId);
	    }
	});

    }

    /*
     * Read from database
     */

    public RestOptimization findOptimizationInDatabase(String jobId, String tenantId, String secret, String timeOut) {

	Mono<RestOptimization> resultMono = this.geoFafOptimizerApi.getJobResult(jobId, tenantId, secret, timeOut);

	return resultMono.onErrorResume(RestErrorHandler.restOptimizationErrorResumer(tourOptimizerObjectMapper))
		.block();
    }

    public List<DatabaseInfoSearchResult> findOptimizationInfosInDatabase(String xTenantId,
	    DatabaseInfoSearch searchItem) {

	Flux<DatabaseInfoSearchResult> resultFlux = this.geoFafOptimizerApi.listJobs(searchItem, xTenantId);

	// TODO some error handling
	return resultFlux.collectList().block();
    }

}
