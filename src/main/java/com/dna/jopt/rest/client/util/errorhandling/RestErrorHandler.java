package com.dna.jopt.rest.client.util.errorhandling;

import java.util.List;
import java.util.function.Function;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.reactivestreams.Publisher;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.dna.jopt.rest.client.model.ElementConnection;
import com.dna.jopt.rest.client.model.Position;
import com.dna.jopt.rest.client.model.RestOptimization;
import com.dna.jopt.rest.client.model.Solution;
import com.dna.jopt.rest.client.model.Status;
import com.dna.jopt.rest.client.model.TurnByTurnResponseItem;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class RestErrorHandler {

    private static final Logger logger = LogManager.getLogger(RestErrorHandler.class);

    private RestErrorHandler() {
	// Nothing to do
    }

    /*
     * GeoCoder
     * 
     */

    public static Function<Throwable, Publisher<Position>> positionListErrorResumer(ObjectMapper mapper) {

	return t -> {

	    logger.warn("Error while processing. " + t.getMessage());
	    System.out.println("Error while processing. " + t.getMessage());

	    if (t instanceof WebClientResponseException) {

		WebClientResponseException we = (WebClientResponseException) t;

		String responseBody = we.getResponseBodyAsString();

		try {
		    List<Position> poss = mapper.readValue(responseBody, new TypeReference<List<Position>>() {
		    });

		    return Flux.fromIterable(poss);

		} catch (JsonProcessingException e1) {
		    logger.warn("Non-Skippable error while processing. " + e1.getMessage());
		}

	    }

	    return Mono.empty();
	};

    }

    public static Function<Throwable, Mono<Status>> healthErrorResumer(ObjectMapper mapper) {

	return t -> {

	    logger.warn("Error while processing. " + t.getMessage());
	    System.out.println("Error while processing. " + t.getMessage());

	    if (t instanceof WebClientResponseException) {

		WebClientResponseException we = (WebClientResponseException) t;

		String responseBody = we.getResponseBodyAsString();

		try {
		    Status health = mapper.readValue(responseBody, new TypeReference<Status>() {
		    });

		    return Mono.just(health);

		} catch (JsonProcessingException e1) {
		    logger.warn("Non-Skippable error while processing. " + e1.getMessage());
		}

	    }

	    return Mono.empty();
	};

    }

    /*
     * RoutePlanner
     * 
     */

    public static Function<Throwable, Publisher<ElementConnection>> connectionListErrorResumer(ObjectMapper mapper) {

	return t -> {

	    logger.warn("Error while processing. " + t.getMessage());
	    System.out.println("Error while processing. " + t.getMessage());

	    if (t instanceof WebClientResponseException) {

		WebClientResponseException we = (WebClientResponseException) t;

		String responseBody = we.getResponseBodyAsString();

		try {
		    List<ElementConnection> cons = mapper.readValue(responseBody,
			    new TypeReference<List<ElementConnection>>() {
			    });

		    return Flux.fromIterable(cons);

		} catch (JsonProcessingException e1) {
		    logger.warn("Non-Skippable error while processing. " + e1.getMessage());
		}

	    }

	    return Mono.empty();
	};

    }

    public static Function<Throwable, Mono<TurnByTurnResponseItem>> tbtResponseErrorResumer(ObjectMapper mapper) {

	return t -> {

	    logger.warn("Error while processing. " + t.getMessage());
	    System.out.println("Error while processing. " + t.getMessage());

	    if (t instanceof WebClientResponseException) {

		WebClientResponseException we = (WebClientResponseException) t;

		String responseBody = we.getResponseBodyAsString();

		try {
		    TurnByTurnResponseItem tbtResponse = mapper.readValue(responseBody,
			    new TypeReference<TurnByTurnResponseItem>() {
			    });

		    return Mono.just(tbtResponse);

		} catch (JsonProcessingException e1) {
		    logger.warn("Non-Skippable error while processing. " + e1.getMessage());
		}

	    }

	    return Mono.empty();
	};

    }

    /*
     * 
     * TourOptimizer, RoutePlanner
     * 
     */
    public static Function<Throwable, Mono<RestOptimization>> restOptimizationErrorResumer(ObjectMapper mapper) {

	return t -> {

	    logger.warn("Error while processing. " + t.getMessage());
	    System.out.println("Error while processing. " + t.getMessage());

	    if (t instanceof WebClientResponseException) {

		WebClientResponseException we = (WebClientResponseException) t;

		String responseBody = we.getResponseBodyAsString();

		try {
		    RestOptimization opti = mapper.readValue(responseBody, new TypeReference<RestOptimization>() {
		    });

		    return Mono.just(opti);

		} catch (JsonProcessingException e1) {
		    logger.warn("Non-Skippable error while processing. " + e1.getMessage());
		}

	    }

	    return Mono.empty();
	};

    }

    public static Function<Throwable, Mono<Solution>> solutionErrorResumer(ObjectMapper mapper) {

	return t -> {

	    logger.warn("Error while processing. " + t.getMessage());
	    System.out.println("Error while processing. " + t.getMessage());

	    if (t instanceof WebClientResponseException) {

		WebClientResponseException we = (WebClientResponseException) t;

		String responseBody = we.getResponseBodyAsString();

		try {
		    Solution solution = mapper.readValue(responseBody, new TypeReference<Solution>() {
		    });

		    return Mono.just(solution);

		} catch (JsonProcessingException e1) {
		    logger.warn("Non-Skippable error while processing. " + e1.getMessage());
		}

	    }

	    return Mono.empty();
	};

    }

}
