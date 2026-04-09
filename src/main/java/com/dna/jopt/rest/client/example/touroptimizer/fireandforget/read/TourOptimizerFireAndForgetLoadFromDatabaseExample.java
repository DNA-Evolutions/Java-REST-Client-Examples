package com.dna.jopt.rest.client.example.touroptimizer.fireandforget.read;

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
import java.util.Optional;

import com.dna.jopt.rest.client.example.touroptimizer.helper.TourOptimizerRestCaller;
import com.dna.jopt.rest.client.model.RestOptimization;
import com.dna.jopt.rest.client.util.endpoints.Endpoints;
import com.dna.jopt.rest.client.util.io.json.RestJSONParser;
import com.dna.jopt.rest.client.util.secretsmanager.SecretsManager;
import com.dna.jopt.rest.client.util.secretsmanager.caughtexception.NoSecretFileFoundException;
import com.dna.jopt.rest.client.util.secretsmanager.caughtexception.SecretNotFoundException;

/**
 * Demonstrates retrieving a completed fire-and-forget optimization result from the database.
 *
 * <p>Uses the {@code jobId} (UUID v4) returned by a prior
 * {@link com.dna.jopt.rest.client.example.touroptimizer.fireandforget.run.TourOptimizerFireAndForgetWriteExample}
 * run to load the full {@link com.dna.jopt.rest.client.model.RestOptimization} from MongoDB
 * via {@link com.dna.jopt.rest.client.example.touroptimizer.helper.TourOptimizerRestCaller#findOptimizationInDatabase}.
 * If the result was encrypted during persistence, the same {@code secret} must be provided.</p>
 *
 * <p><b>Requires</b> TourOptimizer started with an active MongoDB connection. See
 * <a href="https://github.com/DNA-Evolutions/Docker-REST-TourOptimizer/blob/main/TourOptimizerWithDatabase.md">TourOptimizerWithDatabase.md</a>.</p>
 *
 * @see com.dna.jopt.rest.client.example.touroptimizer.fireandforget.run.TourOptimizerFireAndForgetWriteExample
 * @see com.dna.jopt.rest.client.example.touroptimizer.fireandforget.read.TourOptimizerFireAndForgetSearchInDatabaseExample
 */
public class TourOptimizerFireAndForgetLoadFromDatabaseExample {

    /**
     * The main method of TourOptimizerExample
     *
     * @param args the arguments
     * @throws IOException                Signals that an I/O exception has
     *                                    occurred.
     * @throws NoSecretFileFoundException the no secret file found exception
     * @throws SecretNotFoundException    the secret not found exception
     */
    public static void main(String[] args) throws NoSecretFileFoundException, SecretNotFoundException, IOException {

	/*
	 * 
	 * Modify me
	 * 
	 */
	boolean isAzureCall = false;

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
	 * 
	 */
	
	String jobId = "2c835d4f-52c1-41c4-bfe9-052d93e9947f";
	
	String rawCreator = "TEST_CREATOR";
	String xTenantId = TourOptimizerRestCaller.DEFAULT_XTENANT_ID;
	String secret = "";

	boolean doHashCreatorName = false;

	if (doHashCreatorName) {
	    rawCreator = "hash:" + rawCreator;
	}

	RestOptimization result = tourOptimizerCaller.findOptimizationInDatabase(jobId, xTenantId, secret, "PT1M");
	
	// For easier representation with transform the result back to JSON
	String resultJSON = RestJSONParser.toJSONString(result, tourOptimizerCaller.getMapper());
	System.out.println(resultJSON);

    }

}
