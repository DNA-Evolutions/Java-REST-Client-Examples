package com.dna.jopt.rest.client.util.secretsmanager.caughtexception;

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

/**
 * Thrown when the secrets JSON file cannot be found at the expected location
 * during {@link com.dna.jopt.rest.client.util.secretsmanager.SecretsManager} initialization.
 */
public class NoSecretFileFoundException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 5399013490884525730L;

    public NoSecretFileFoundException(String errorMessage) {
	super(errorMessage);
    }
}
