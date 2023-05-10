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
import com.dna.jopt.rest.client.model.DatabaseItemSearch;
import com.dna.jopt.rest.client.model.RestOptimization;
import com.dna.jopt.rest.client.util.endpoints.Endpoints;
import com.dna.jopt.rest.client.util.secretsmanager.SecretsManager;
import com.dna.jopt.rest.client.util.secretsmanager.caughtexception.NoSecretFileFoundException;
import com.dna.jopt.rest.client.util.secretsmanager.caughtexception.SecretNotFoundException;

/**
 * The Class TourOptimizerFireAndForgetLoadFromDatabaseExample. If the TourOptimizer is not started with an active
 * Mongo Database connection, the call will result in a 404 not found exception.
 * 
 * Find the first Optimization that matches the criteria defined in DatabaseItemSearch.
 * As it is mandatory to provide the database generated unique id, only one Optimization should match the criteria.
 * 
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
    public static void main(String[] args) throws NoSecretFileFoundException, SecretNotFoundException {

	/*
	 * 
	 * Modify me
	 * 
	 */
	boolean isAzureCall = !true;

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

	DatabaseItemSearch searchItem = new DatabaseItemSearch();
	searchItem.setCreator("TEST_CREATOR");
	searchItem.setId("645b5b7dc73a8250a670deb1"); // Needs to be a valid id. Either save externally, or search it first
	searchItem.setTimeOut("PT1M");

	RestOptimization result = tourOptimizerCaller.findOptimizationInDatabase(searchItem);

	System.out.print(result);

    }

}
