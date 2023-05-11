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
import java.util.List;
import java.util.Optional;

import com.dna.jopt.rest.client.example.touroptimizer.helper.TourOptimizerRestCaller;
import com.dna.jopt.rest.client.model.DatabaseInfoSearch;
import com.dna.jopt.rest.client.model.DatabaseInfoSearchResult;
import com.dna.jopt.rest.client.util.endpoints.Endpoints;
import com.dna.jopt.rest.client.util.secretsmanager.SecretsManager;
import com.dna.jopt.rest.client.util.secretsmanager.caughtexception.NoSecretFileFoundException;
import com.dna.jopt.rest.client.util.secretsmanager.caughtexception.SecretNotFoundException;

/**
 * The Class TourOptimizerFireAndForgetSearchInDatabaseExample. If the TourOptimizer is not started with an active
 * Mongo Database connection, the call will result in a 404 not found exception.
 * 
 * Find all Optimizations metadata that matches the criteria defined in DatabaseInfoSearch.
 * 
 */
public class TourOptimizerFireAndForgetSearchInDatabaseExample {

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

	/*
	 * 
	 * Not all fields are mandatory. The ident IS NOT equal to the id. The ident is
	 * a user defined String to identify the optimization (it does NOT need to be
	 * unique). The id is a unique string automatically generated by the database.
	 * 
	 * JSON-Example: { 
	 * 	"creator": "DEFAULT_DNA_CREATOR",
	 *  	"ident": "My-JOpt-Run",
	 * 	"limit": 10, "sortDirection": "DESC", 
	 * 	"createdDateStart": "2023-05-10T09:11:38.445Z",
	 *  	"createdDateEnd": "2023-05-10T09:11:38.445Z",
	 * 	"timeOut": "PT1M" 
	 * }
	 */

	DatabaseInfoSearch searchItem = new DatabaseInfoSearch();
	searchItem.creator("TEST_CREATOR");

	List<DatabaseInfoSearchResult> result = tourOptimizerCaller.findOptimizationInfosInDatabase(searchItem);

	System.out.print(result);

    }

}
