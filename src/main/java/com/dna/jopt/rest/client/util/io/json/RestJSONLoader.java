package com.dna.jopt.rest.client.util.io.json;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.dna.jopt.rest.client.model.ElementConnection;
import com.dna.jopt.rest.client.model.Position;
import com.dna.jopt.rest.client.model.RestOptimization;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RestJSONLoader {

    private RestJSONLoader() {
	// Nothing to do
    }

    public static RestOptimization readRestOptimization(File src, ObjectMapper mapper) throws IOException {

	return mapper.readValue(src, new TypeReference<RestOptimization>() {
	});

    }

    public static RestOptimization readRestOptimization(String jsonOpti, ObjectMapper mapper) throws IOException {

	return mapper.readValue(jsonOpti, new TypeReference<RestOptimization>() {
	});

    }

    public static List<ElementConnection> readConnections(String jsonString, ObjectMapper mapper) throws IOException {

	return mapper.readValue(jsonString, new TypeReference<List<ElementConnection>>() {
	});

    }

    public static List<ElementConnection> readConnections(File src, ObjectMapper mapper) throws IOException {

	return mapper.readValue(src, new TypeReference<List<ElementConnection>>() {
	});

    }

    public static List<Position> readPositions(File src, ObjectMapper mapper) throws IOException {

	return mapper.readValue(src, new TypeReference<List<Position>>() {
	});

    }

    public static List<Position> readPositions(String jsonString, ObjectMapper mapper) throws IOException {

	return mapper.readValue(jsonString, new TypeReference<List<Position>>() {
	});

    }

}
