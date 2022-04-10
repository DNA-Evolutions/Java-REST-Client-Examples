package com.dna.jopt.rest.client.example.geocoder.helper;

import java.text.DateFormat;
import java.util.List;
import java.util.Optional;
import org.springframework.web.reactive.function.client.WebClient;
import com.dna.jopt.rest.client.model.GeoAddress;
import com.dna.jopt.rest.client.model.Position;
import com.dna.jopt.rest.client.model.Status;
import com.dna.jopt.rest.client.util.errorhandling.RestErrorHandler;
import com.dna.jopt.rest.geocoder.client.ApiClient;
import com.dna.jopt.rest.geocoder.client.api.BatchForwardGeoCodingServiceControllerApi;
import com.dna.jopt.rest.geocoder.client.api.BatchReverseGeoCodingServiceControllerApi;
import com.dna.jopt.rest.geocoder.client.api.HealthStatusApi;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import reactor.core.publisher.Flux;

public class GeoCoderRestCaller {

    /*
     * Define api services
     */
    private BatchForwardGeoCodingServiceControllerApi geoCoderBatchForwardApi = createBatchForwardGeoCodingServiceControllerApi();
    private BatchReverseGeoCodingServiceControllerApi geoCoderBatchReverseApi = createBatchReverseGeoCodingServiceControllerApi();

    private HealthStatusApi geoCoderHealth = new HealthStatusApi();

    public final ObjectMapper geoCoderObjectMapper;

    public static final int MAX_TOUROPIMIZER_RESPONSESIZE_MB = 16;

    /*
     *
     */

    public GeoCoderRestCaller(String geocoderUrl) {
	this(geocoderUrl, Optional.empty());
    }

    public ObjectMapper getMapper() {
	return this.geoCoderObjectMapper;
    }

    public GeoCoderRestCaller(String geocoderUrl, Optional<String> azureApiKeyOpt) {

	// Modify endpoint to meet server
	this.geoCoderBatchForwardApi.getApiClient().setBasePath(geocoderUrl);

	this.geoCoderBatchReverseApi.getApiClient().setBasePath(geocoderUrl);

	this.geoCoderHealth.getApiClient().setBasePath(geocoderUrl);

	// Get the mapper from the generated files
	this.geoCoderObjectMapper = this.geoCoderBatchForwardApi.getApiClient().getObjectMapper();

	// Invoke api key if desired
	if (azureApiKeyOpt.isPresent()) {
	    String azureApiKey = azureApiKeyOpt.get();

	    this.geoCoderBatchForwardApi.getApiClient().addDefaultHeader("Cache-Control", "no-cache")
		    .addDefaultHeader("Ocp-Apim-Subscription-Key", azureApiKey);

	    this.geoCoderHealth.getApiClient().addDefaultHeader("Cache-Control", "no-cache")
		    .addDefaultHeader("Ocp-Apim-Subscription-Key", azureApiKey);

	    this.geoCoderBatchReverseApi.getApiClient().addDefaultHeader("Cache-Control", "no-cache")
		    .addDefaultHeader("Ocp-Apim-Subscription-Key", azureApiKey);

	}
    }

    private static BatchForwardGeoCodingServiceControllerApi createBatchForwardGeoCodingServiceControllerApi() {

	DateFormat dateFormat = ApiClient.createDefaultDateFormat();
	ObjectMapper objectMapper = ApiClient.createDefaultObjectMapper(dateFormat);

	// Vital step! - We need to the generated mapper how to treat our JSON data
	objectMapper.setSerializationInclusion(Include.NON_NULL).registerModule(new JavaTimeModule())
		.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

	WebClient webClient = ApiClient.buildWebClientBuilder(objectMapper).codecs(configurer -> configurer
		.defaultCodecs().maxInMemorySize(MAX_TOUROPIMIZER_RESPONSESIZE_MB * 1024 * 1024)).build();

	ApiClient myApiClient = new ApiClient(webClient);

	return new BatchForwardGeoCodingServiceControllerApi(myApiClient);

    }

    private static BatchReverseGeoCodingServiceControllerApi createBatchReverseGeoCodingServiceControllerApi() {

	DateFormat dateFormat = ApiClient.createDefaultDateFormat();
	ObjectMapper objectMapper = ApiClient.createDefaultObjectMapper(dateFormat);

	// Vital step! - We need to the generated mapper how to treat our JSON data
	objectMapper.setSerializationInclusion(Include.NON_NULL).registerModule(new JavaTimeModule())
		.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

	WebClient webClient = ApiClient.buildWebClientBuilder(objectMapper).codecs(configurer -> configurer
		.defaultCodecs().maxInMemorySize(MAX_TOUROPIMIZER_RESPONSESIZE_MB * 1024 * 1024)).build();

	ApiClient myApiClient = new ApiClient(webClient);

	return new BatchReverseGeoCodingServiceControllerApi(myApiClient);

    }

    /*
     * 
     * Step 0) - Healthcheck
     * 
     */
    public Status checkGeoCoderHealth() {
	return this.geoCoderHealth.healthStatus().onErrorResume(RestErrorHandler.healthErrorResumer(geoCoderObjectMapper)).block();
    }

    /*
     *
     * Step 1) - GeoCode
     *
     */
    public List<Position> geoCodeNodePositions(List<GeoAddress> nodeAddresses) {
	Flux<Position> nodePositionsFlux = geoCoderBatchForwardApi.batchForward(nodeAddresses);

	// Wait for the connections
	return nodePositionsFlux.onErrorResume(RestErrorHandler.positionListErrorResumer(geoCoderObjectMapper)).collectList().block();
    }

    public List<Position> geoCodeResourcePositions(List<GeoAddress> ressAddresses) {

	Flux<Position> ressPositionsFlux = geoCoderBatchForwardApi.batchForward(ressAddresses);

	// Wait for the connections
	return ressPositionsFlux.onErrorResume(RestErrorHandler.positionListErrorResumer(geoCoderObjectMapper)).collectList().block();
    }

    public List<Position> reverseGeoCodePositions(List<Position> positions) {

	Flux<Position> fullPositionsFlux = geoCoderBatchReverseApi.batchReverse(positions);

	return fullPositionsFlux.onErrorResume(RestErrorHandler.positionListErrorResumer(geoCoderObjectMapper)).collectList().block();

    }

}
