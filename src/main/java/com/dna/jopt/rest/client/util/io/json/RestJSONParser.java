package com.dna.jopt.rest.client.util.io.json;

import java.io.File;
import java.io.IOException;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

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
