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

public class NoSecretFileFoundException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 5399013490884525730L;

    public NoSecretFileFoundException(String errorMessage) {
	super(errorMessage);
    }
}
