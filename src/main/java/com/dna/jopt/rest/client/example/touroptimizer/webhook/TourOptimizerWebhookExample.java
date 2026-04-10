package com.dna.jopt.rest.client.example.touroptimizer.webhook;

/*-
 * #%L
 * JOpt Java REST Client Examples
 * %%
 * Copyright (C) 2017 - 2023 DNA Evolutions GmbH
 * %%
 * This file is subject to the terms and conditions defined in file 'LICENSE.md',
 * which is part of this repository.
 *
 * If not, see <https://www.dna-evolutions.com/agb-conditions-and-terms/>.
 * #L%
 */

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import com.dna.jopt.rest.client.example.touroptimizer.helper.TourOptimizerRestCaller;
import com.dna.jopt.rest.client.model.CreatorSetting;
import com.dna.jopt.rest.client.model.ElementConnection;
import com.dna.jopt.rest.client.model.JobAcceptedResponse;
import com.dna.jopt.rest.client.model.MongoOptimizationPersistenceSetting;
import com.dna.jopt.rest.client.model.OptimizationPersistenceSetting;
import com.dna.jopt.rest.client.model.OptimizationPersistenceStrategySetting;
import com.dna.jopt.rest.client.model.Position;
import com.dna.jopt.rest.client.model.RestOptimization;
import com.dna.jopt.rest.client.model.StreamPersistenceStrategySetting;
import com.dna.jopt.rest.client.util.endpoints.Endpoints;
import com.dna.jopt.rest.client.util.io.json.RestJSONParser;
import com.dna.jopt.rest.client.util.secretsmanager.SecretsManager;
import com.dna.jopt.rest.client.util.secretsmanager.caughtexception.NoSecretFileFoundException;
import com.dna.jopt.rest.client.util.secretsmanager.caughtexception.SecretNotFoundException;
import com.dna.jopt.rest.client.util.testinputcreation.TestPositionsInput;
import com.dna.jopt.rest.client.util.webhook.WebhookTestServer;

/**
 * Demonstrates the full webhook-based optimization workflow:
 *
 * <ol>
 *   <li>Start a local {@link WebhookTestServer} to receive completion notifications.</li>
 *   <li>Submit a fire-and-forget optimization job whose
 *       {@code completionWebhookUrl} points to the local server
 *       (via {@code http://host.docker.internal:PORT/webhook} so the
 *       Dockerized TourOptimizer can reach back to the host).</li>
 *   <li>Wait for the webhook callback that signals job completion.</li>
 *   <li>Retrieve the optimization result from the database using the job ID.</li>
 *   <li>Print the result and shut down the webhook server.</li>
 * </ol>
 *
 * <p><b>Prerequisites:</b></p>
 * <ul>
 *   <li>TourOptimizer running with an active MongoDB connection
 *       (see <a href="https://github.com/DNA-Evolutions/Docker-REST-TourOptimizer/blob/main/TourOptimizerWithDatabase.md">TourOptimizerWithDatabase.md</a>).</li>
 *   <li>Port 9000 (or whichever port you choose) must be reachable from the
 *       Docker container via {@code host.docker.internal}.</li>
 * </ul>
 */
public class TourOptimizerWebhookExample {

    private static final int WEBHOOK_PORT = 9000;
    private static final String WEBHOOK_SECRET = "YourStr0ng!Secret_Here";

    public static void main(String[] args)
	    throws NoSecretFileFoundException, SecretNotFoundException, IOException {

	/*
	 *
	 * Modify me
	 *
	 */
	boolean isAzureCall = !true;

	/*
	 * 1) Start the local webhook server
	 */
	WebhookTestServer webhookServer = new WebhookTestServer(WEBHOOK_PORT, WEBHOOK_SECRET);
	webhookServer.start();

	try {
	    /*
	     * 2) Set up the TourOptimizer REST caller
	     */
	    TourOptimizerRestCaller tourOptimizerCaller;
	    SecretsManager m = new SecretsManager();

	    if (isAzureCall) {
		tourOptimizerCaller = new TourOptimizerRestCaller(
			Endpoints.AZURE_SWAGGER_TOUROPTIMIZER_URL,
			Optional.of(m.get("azure")));
	    } else {
		tourOptimizerCaller = new TourOptimizerRestCaller(
			Endpoints.LOCAL_SWAGGER_TOUROPTIMIZER_URL);
	    }

	    /*
	     * 3) Prepare test data — small Sydney node and resource positions
	     */
	    List<Position> nodePoss = TestPositionsInput.defaultSmallSydneyNodePositions();
	    List<Position> resourcePoss = TestPositionsInput.defaultSmallSydneyResourcePositions();
	    List<ElementConnection> emptyConnections = new ArrayList<>();

	    String rawCreator = "WEBHOOK_EXAMPLE_CREATOR";
	    String xTenantId = TourOptimizerRestCaller.DEFAULT_XTENANT_ID;
	    CreatorSetting creatorSettings = new CreatorSetting().creator(rawCreator);
	    String myOptiIdent = "MY_WEBHOOK_OPTIMIZATION";

	    /*
	     * 4) Submit the fire-and-forget job with webhook settings
	     */
	    OptimizationPersistenceSetting persistenceSetting = createPersistenceSettingWithWebhook();

	    JobAcceptedResponse jobResponse = tourOptimizerCaller.optimizeFireAndForget(
		    xTenantId, nodePoss, resourcePoss, emptyConnections,
		    myOptiIdent, creatorSettings, persistenceSetting,
		    Optional.of(m.get("joptlic")));

	    String jobId = jobResponse.getJobId();
	    System.out.println("Job submitted - jobId: " + jobId);
	    System.out.println("Status: " + jobResponse.getStatus());
	    System.out.println();
	    System.out.println("Waiting for completion webhook...");

	    /*
	     * 5) Block until the webhook arrives (or time out after 10 minutes)
	     */
	    CompletableFuture<String> webhookFuture = webhookServer.waitForWebhook();
	    String webhookPayload = webhookFuture.get(10, TimeUnit.MINUTES);

	    System.out.println();
	    System.out.println("Webhook received! Retrieving optimization result...");
	    System.out.println("webhookPayload: "+webhookPayload);
	    System.out.println();

	    /*
	     * 6) Fetch the result from the database using the job ID
	     */
	    String encryptionSecret = ""; // must match what was set in persistence settings
	    RestOptimization result = tourOptimizerCaller.findOptimizationInDatabase(
		    jobId, xTenantId, encryptionSecret, "PT1M");

	    String resultJSON = RestJSONParser.toJSONString(result, tourOptimizerCaller.getMapper());
	    System.out.println("=== Optimization Result ===");
	    System.out.println(resultJSON);

	} catch (Exception e) {
	    System.err.println("Error: " + e.getMessage());
	    e.printStackTrace();
	} finally {
	    /*
	     * 7) Clean up and stop the webhook server
	     */
	    webhookServer.stop();
	}
    }

    /**
     * Creates persistence settings with the webhook URL and secret configured.
     * The webhook URL uses {@code host.docker.internal} so the Dockerized
     * TourOptimizer can call back to the host machine.
     */
    public static OptimizationPersistenceSetting createPersistenceSettingWithWebhook() {

	OptimizationPersistenceSetting persistenceSetting = new OptimizationPersistenceSetting();

	MongoOptimizationPersistenceSetting mongoSettings = new MongoOptimizationPersistenceSetting();
	mongoSettings.setEnablePersistence(true);
	mongoSettings.setSecret("");
	mongoSettings.setExpiry("PT48H");

	// Webhook configuration — TourOptimizer will POST to this URL when the job completes
	mongoSettings.setCompletionWebhookUrl(
		"http://host.docker.internal:" + WEBHOOK_PORT + "/webhook");
	mongoSettings.setCompletionWebhookSecret(WEBHOOK_SECRET);

	OptimizationPersistenceStrategySetting optimizationStrategy = new OptimizationPersistenceStrategySetting();
	optimizationStrategy.setSaveConnections(false);

	StreamPersistenceStrategySetting streamStrategy = new StreamPersistenceStrategySetting();
	streamStrategy.saveProgress(true);
	streamStrategy.cycleProgress(true);
	streamStrategy.saveStatus(true);
	streamStrategy.cycleStatus(true);
	streamStrategy.saveWarning(true);
	streamStrategy.saveError(true);

	mongoSettings.setOptimizationPersistenceStrategySetting(optimizationStrategy);
	mongoSettings.setStreamPersistenceStrategySetting(streamStrategy);
	persistenceSetting.setMongoSettings(mongoSettings);

	return persistenceSetting;
    }
}
