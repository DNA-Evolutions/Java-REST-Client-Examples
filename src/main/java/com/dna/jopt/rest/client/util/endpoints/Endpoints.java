package com.dna.jopt.rest.client.util.endpoints;

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

public class Endpoints {

    private Endpoints() {
	// Nothing to do
    }

    public static final String LOCAL_SWAGGER_GEOCODER_URL = "http://localhost:8082";
    public static final String LOCAL_SWAGGER_GEOROUTER_URL = "http://localhost:8099";
    public static final String LOCAL_SWAGGER_TOUROPTIMIZER_URL = "http://localhost:8081";

    public static final String AZURE_SWAGGER_GEOCODER_URL = "https://joptaas.azure-api.net/geocoder/v2/";
    public static final String AZURE_SWAGGER_GEOROUTER_URL = "https://joptaas.azure-api.net/georouter/v2/";
    public static final String AZURE_SWAGGER_TOUROPTIMIZER_URL = "https://joptaas.azure-api.net/touroptimizer/v2/";
    
    

}
