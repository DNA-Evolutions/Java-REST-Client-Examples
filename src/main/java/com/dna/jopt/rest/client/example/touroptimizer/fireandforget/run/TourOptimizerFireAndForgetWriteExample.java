package com.dna.jopt.rest.client.example.touroptimizer.fireandforget.run;

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

import com.dna.jopt.rest.client.example.touroptimizer.helper.TourOptimizerRestCaller;
import com.dna.jopt.rest.client.model.CreatorSetting;
import com.dna.jopt.rest.client.model.ElementConnection;
import com.dna.jopt.rest.client.model.JobAcceptedResponse;
import com.dna.jopt.rest.client.model.MongoOptimizationPersistenceSetting;
import com.dna.jopt.rest.client.model.OptimizationPersistenceSetting;
import com.dna.jopt.rest.client.model.OptimizationPersistenceStrategySetting;
import com.dna.jopt.rest.client.model.Position;
import com.dna.jopt.rest.client.model.StreamPersistenceStrategySetting;
import com.dna.jopt.rest.client.util.endpoints.Endpoints;
import com.dna.jopt.rest.client.util.secretsmanager.SecretsManager;
import com.dna.jopt.rest.client.util.secretsmanager.caughtexception.NoSecretFileFoundException;
import com.dna.jopt.rest.client.util.secretsmanager.caughtexception.SecretNotFoundException;
import com.dna.jopt.rest.client.util.testinputcreation.TestPositionsInput;

/**
 * Demonstrates submitting an optimization job in fire-and-forget mode with database persistence.
 *
 * <p>The job is submitted asynchronously via the Job API
 * ({@link com.dna.jopt.rest.client.example.touroptimizer.helper.TourOptimizerRestCaller#optimizeFireAndForget}).
 * The server returns a {@link com.dna.jopt.rest.client.model.JobAcceptedResponse} immediately,
 * containing a {@code jobId} that can later be used to retrieve the result from the database
 * (see {@link com.dna.jopt.rest.client.example.touroptimizer.fireandforget.read.TourOptimizerFireAndForgetLoadFromDatabaseExample}).</p>
 *
 * <p>Persistence settings control encryption, TTL, whether to save connections, and stream
 * cycling behavior. The creator name can optionally be hashed using a {@code "hash:"} prefix
 * for privacy in subscription-based systems.</p>
 *
 * <p><b>Requires</b> TourOptimizer started with an active MongoDB connection. See
 * <a href="https://github.com/DNA-Evolutions/Docker-REST-TourOptimizer/blob/main/TourOptimizerWithDatabase.md">TourOptimizerWithDatabase.md</a>.</p>
 *
 * @see com.dna.jopt.rest.client.example.touroptimizer.fireandforget.read.TourOptimizerFireAndForgetLoadFromDatabaseExample
 * @see com.dna.jopt.rest.client.example.touroptimizer.fireandforget.read.TourOptimizerFireAndForgetSearchInDatabaseExample
 * @see com.dna.jopt.rest.client.example.touroptimizer.webhook.TourOptimizerWebhookExample
 */
public class TourOptimizerFireAndForgetWriteExample {

    /**
     * The main method of TourOptimizerExample
     *
     * @param args the arguments
     * @throws IOException                Signals that an I/O exception has
     *                                    occurred.
     * @throws NoSecretFileFoundException the no secret file found exception
     * @throws SecretNotFoundException    the secret not found exception
     */
    public static void main(String[] args) throws NoSecretFileFoundException, SecretNotFoundException {

	/*
	 * 
	 * Modify me
	 * 
	 */
	boolean isAzureCall = !true;

	List<Position> nodePoss = TestPositionsInput.defaultSmallSydneyNodePositions();
	List<Position> resourcePoss = TestPositionsInput.defaultSmallSydneyResourcePositions();

	// Define the data to be used

	/*
	 * 
	 * 
	 */
	TourOptimizerRestCaller tourOptimizerCaller;

	SecretsManager m = new SecretsManager();

	if (isAzureCall) {

	    tourOptimizerCaller = new TourOptimizerRestCaller(Endpoints.AZURE_SWAGGER_TOUROPTIMIZER_URL,
		    Optional.of(m.get("azure")));
	} else {
	    tourOptimizerCaller = new TourOptimizerRestCaller(Endpoints.LOCAL_SWAGGER_TOUROPTIMIZER_URL);
	}

	/*
	 *
	 * Optimize
	 *
	 */

	// We simply provide empty connections => This will trigger automatic haversine
	// distance calculations

	List<ElementConnection> emptyConnections = new ArrayList<>();

	/*
	 * A special feature is the magic use of "hash:" as prefix to the creator. The
	 * creator string will not saved, instead only the hash of the creator will be
	 * saved using SHA-256.
	 * 
	 * This can be used, for example, in a subscription system. A user is identified
	 * by a unique subscription key. For security reasons, the key, is not saved
	 * inside a snapshot, instead the hash value is used allowing identification of
	 * the original user without revealing the key.
	 */

	String rawCreator = "TEST_CREATOR";
	
	String xTenantId = TourOptimizerRestCaller.DEFAULT_XTENANT_ID;

	/*
	 * Optionally -  Later on, when searching for the Optimization, you can direclty search for the hashed creator or use
	 * the magic keyword hash: in front of the unhashed creator.
	 */
	boolean doHashCreatorName = false;

	if (doHashCreatorName) {
	    rawCreator = "hash:" + rawCreator;
	}

	CreatorSetting creatorSettings = new CreatorSetting().creator(rawCreator);
	String myOptiIdent = "MY_OPTIMIZATION";

	JobAcceptedResponse wasStartedResponse = tourOptimizerCaller.optimizeFireAndForget(xTenantId, nodePoss, resourcePoss, emptyConnections,
		myOptiIdent, creatorSettings, createPersistenceSetting(), Optional.of(m.get("joptlic")));

	System.out.print("wasStartedResponse: " + wasStartedResponse);
	System.out.print("creator: " + rawCreator);

    }

    public static OptimizationPersistenceSetting createPersistenceSetting() {

	/*
	 * The general object wrapping the database persistence settings
	 */
	OptimizationPersistenceSetting persistenceSetting = new OptimizationPersistenceSetting();

	/*
	 * Settings regarding encryption etc.
	 */
	MongoOptimizationPersistenceSetting mongoSettings = new MongoOptimizationPersistenceSetting();
	// Do we want to save anything to the database?
	mongoSettings.setEnablePersistence(true);

	// A secret encrypting the content of the final snapshot/result. If empty, no
	// encryption is used.
	// Important: Metadata and stream information like progress is always saved as
	// decrypted clear text.
	// Attention: The secret is not saved by DNA evolutions. If you loose the
	// secret, the file CAN NOT be restored.
	mongoSettings.setSecret("");

	// Once saved, the snapshot/result will be deleted automatically after this time
	mongoSettings.setExpiry("PT48H");

	/*
	 * Saving the element connections etc.
	 */
	OptimizationPersistenceStrategySetting optimizationPersistenceStrategySetting = new OptimizationPersistenceStrategySetting();

	// Element connections usually make up most of the data size, therefore, when
	// targeting to not further process the result, it might be a good idea
	// to skip the connections saving to reduce space
	optimizationPersistenceStrategySetting.setSaveConnections(false);


	/*
	 * How to treat streams? For example, do we want to continuously write the
	 * current progress into a database? Do we want to cycle the progress?
	 */
	StreamPersistenceStrategySetting streamPersistenceStrategySetting = new StreamPersistenceStrategySetting();
	streamPersistenceStrategySetting.saveProgress(true);
	streamPersistenceStrategySetting.cycleProgress(true);
	streamPersistenceStrategySetting.saveStatus(true);
	streamPersistenceStrategySetting.cycleStatus(true);
	streamPersistenceStrategySetting.saveWarning(true);
	streamPersistenceStrategySetting.saveError(true);

	/*
	 * Collect settings
	 */
	mongoSettings.setOptimizationPersistenceStrategySetting(optimizationPersistenceStrategySetting);
	mongoSettings.setStreamPersistenceStrategySetting(streamPersistenceStrategySetting);

	/*
	 * Wrap
	 */
	persistenceSetting.setMongoSettings(mongoSettings);

	return persistenceSetting;
    }

}
