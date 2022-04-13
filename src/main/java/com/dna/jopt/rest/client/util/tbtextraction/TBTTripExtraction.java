package com.dna.jopt.rest.client.util.tbtextraction;

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

import java.time.Duration;
import java.util.List;

import com.dna.jopt.rest.client.model.Position;
import com.dna.jopt.rest.client.model.TurnByTurnResponseItem;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import lombok.Getter;
import lombok.Setter;

public class TBTTripExtraction {

    @Setter
    @Getter
    private Position fromPos;

    @Setter
    @Getter
    private Position toPos;

    @Setter
    @Getter
    Duration time;

    @Setter
    @Getter
    Double distance;

    @Setter
    @Getter
    String shape;

    @Override
    public String toString() {

	StringBuilder builder = new StringBuilder();

	builder.append("\nFrom: " + fromPos + " == " + "To: " + toPos);
	builder.append("\nDistance: " + distance + " Time: " + time + "\n");

	return builder.toString();
    }

    public static TBTTripExtraction extractSummaryNode(TurnByTurnResponseItem item) {

	JsonNode node = new ObjectMapper().valueToTree(item);

	TBTTripExtraction extract = new TBTTripExtraction();

	List<JsonNode> shape = node.findValues("shape");
	extract.setShape(shape.get(0).textValue());

	List<JsonNode> summaries = node.findValues("summary");
	JsonNode timeSeconds = summaries.get(0).findValue("time");
	JsonNode distanceKm = summaries.get(0).findValue("length");

	double timeDouble = timeSeconds.doubleValue();

	double timeMillis = timeDouble * 1000.0;

	extract.setDistance(distanceKm.doubleValue());
	extract.setTime(Duration.ofMillis((long) timeMillis));

	// Locations

	ArrayNode locations = (ArrayNode) node.findValues("locations").get(0);

	JsonNode pos1Node = locations.get(0);
	JsonNode pos2Node = locations.get(1);

	com.dna.jopt.rest.client.model.Position pos1 = new com.dna.jopt.rest.client.model.Position();
	pos1.setLocationId(item.getFromId());
	pos1.setLatitude(pos1Node.findValue("lat").doubleValue());
	pos1.setLongitude(pos1Node.findValue("lon").doubleValue());

	com.dna.jopt.rest.client.model.Position pos2 = new com.dna.jopt.rest.client.model.Position();
	pos2.setLocationId(item.getToId());
	pos2.setLatitude(pos2Node.findValue("lat").doubleValue());
	pos2.setLongitude(pos2Node.findValue("lon").doubleValue());

	extract.setFromPos(pos1);
	extract.setToPos(pos2);

	return extract;
    }

}
