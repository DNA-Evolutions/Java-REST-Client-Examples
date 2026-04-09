package com.dna.jopt.rest.client.util.io.json;

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

import java.io.File;
import java.io.IOException;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

/**
 * Utility for serializing JOpt REST model objects to JSON.
 *
 * <p>Provides convenience methods to convert any model object to a pretty-printed
 * JSON string or write it directly to a {@link java.io.File}. The caller must
 * supply the {@link com.fasterxml.jackson.databind.ObjectMapper} obtained from
 * {@link com.dna.jopt.rest.client.example.touroptimizer.helper.TourOptimizerRestCaller#getMapper()}
 * to ensure consistent serialization settings (non-null inclusion, Java Time module, etc.).</p>
 *
 * @see RestJSONLoader
 */
public class RestJSONParser {

    private RestJSONParser() {
	// Nothing to do
    }

    public static <T> String toJSONString(T object, ObjectMapper mapper) throws IOException {

	return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);

    }

    public static <T> void toJsonFile(T object, File out, ObjectMapper mapper) throws IOException {

	ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());

	writer.writeValue(out, object);

    }

}
