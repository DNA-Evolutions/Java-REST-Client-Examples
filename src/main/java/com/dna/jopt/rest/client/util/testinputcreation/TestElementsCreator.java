package com.dna.jopt.rest.client.util.testinputcreation;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.dna.jopt.rest.client.model.CapacityResource;
import com.dna.jopt.rest.client.model.GeoNode;
import com.dna.jopt.rest.client.model.Node;
import com.dna.jopt.rest.client.model.OpeningHours;
import com.dna.jopt.rest.client.model.Position;
import com.dna.jopt.rest.client.model.Resource;
import com.dna.jopt.rest.client.model.WorkingHours;

public class TestElementsCreator {

    private TestElementsCreator() {
	// Nothing to do
    }

    /*
     *
     * Resource specific
     *
     */

    public static Resource defaultCapacityResource(Position pos, String id) {

	Resource res = new Resource();

	res.setWorkingHours(defaultTestWorkinghours());
	res.setId(id);
	res.setPosition(pos);
	res.setLocationId(pos.getLocationId());
	res.setMaxTime("PT12H");
	res.setMaxDistance("1200.0 km");

	CapacityResource type = new CapacityResource();
	type.setTypeName("Capacity");

	res.setType(type);

	return res;
    }

    public static List<WorkingHours> defaultTestWorkinghours() {

	String zone = "Europe/Berlin";
	ZoneId zoneId = ZoneId.of(zone);

	int numDays = 5;

	/*
	 *
	 */
	return IntStream.range(0, numDays).boxed().map(ii -> {
	    ZonedDateTime start = ZonedDateTime.of(2100, 1, 1 + ii, 8, 0, 0, 0, zoneId);
	    ZonedDateTime end = ZonedDateTime.of(2100, 1, 1 + ii, 20, 0, 0, 0, zoneId);

	    return createWorkingHours(start, end, zoneId);
	}).collect(Collectors.toList());
    }

    public static WorkingHours createWorkingHours(ZonedDateTime begin, ZonedDateTime end, ZoneId zoneId) {

	WorkingHours hr = new WorkingHours();

	hr.setBegin(begin.toInstant());
	hr.setEnd(end.toInstant());
	hr.setZoneId(zoneId.getId());

	return hr;
    }

    /*
     *
     * Node specific
     *
     */

    public static Node defaultGeoNode(Position pos, String id) {

	Node node = new Node();

	node.setOpeningHours(defaultTestOpeninghours());
	node.setId(id);
	node.setLocationId(pos.getLocationId());
	node.setVisitDuration("PT30M");
	node.setPriority(1);

	GeoNode geoPart = new GeoNode();
	geoPart.setPosition(pos);
	geoPart.setTypeName("Geo");

	node.setType(geoPart);

	return node;
    }

    public static List<OpeningHours> defaultTestOpeninghours() {

	String zone = "Europe/Berlin";
	ZoneId zoneId = ZoneId.of(zone);

	int numDays = 5;

	/*
	 *
	 */
	return IntStream.range(0, numDays).boxed().map(ii -> {
	    ZonedDateTime start = ZonedDateTime.of(2100, 1, 1 + ii, 8, 0, 0, 0, zoneId);
	    ZonedDateTime end = ZonedDateTime.of(2100, 1, 1 + ii, 20, 0, 0, 0, zoneId);

	    return createOpeningHours(start, end, zoneId);
	}).collect(Collectors.toList());
    }

    public static OpeningHours createOpeningHours(ZonedDateTime begin, ZonedDateTime end, ZoneId zoneId) {

	OpeningHours hr = new OpeningHours();

	hr.setBegin(begin.toInstant());
	hr.setEnd(end.toInstant());
	hr.setZoneId(zoneId.getId());

	return hr;
    }
}
