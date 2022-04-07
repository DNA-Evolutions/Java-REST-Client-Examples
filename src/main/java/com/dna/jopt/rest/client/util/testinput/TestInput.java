package com.dna.jopt.rest.client.util.testinput;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.dna.jopt.rest.client.model.CapacityResource;
import com.dna.jopt.rest.client.model.GeoAddress;
import com.dna.jopt.rest.client.model.GeoNode;
import com.dna.jopt.rest.client.model.JSONConfig;
import com.dna.jopt.rest.client.model.Node;
import com.dna.jopt.rest.client.model.OpeningHours;
import com.dna.jopt.rest.client.model.OptimizationKeySetting;
import com.dna.jopt.rest.client.model.OptimizationOptions;
import com.dna.jopt.rest.client.model.Position;
import com.dna.jopt.rest.client.model.Resource;
import com.dna.jopt.rest.client.model.RestOptimization;
import com.dna.jopt.rest.client.model.WorkingHours;

public final class TestInput {

    public static final String PUBLIC_JSON_LICENSE = "{\r\n" + "	\"version\": \"1.1\",\r\n"
	    + "	\"identifier\": \"PUBLIC-\",\r\n"
	    + "	\"description\": \"Key provided to for evaluation purpose from DNA evolutions GmbH.\",\r\n"
	    + "	\"contact\": \"www.dna-evolutions.com\",\r\n" + "	\"modules\": [{\r\n"
	    + "			\"Module:\": \"Elements\",\r\n" + "			\"max\": 15\r\n"
	    + "		}, {\r\n" + "			\"Module:\": \"Date\",\r\n"
	    + "			\"creation\": \"2021-05-25\",\r\n" + "			\"due\": \"2027-05-25\"\r\n"
	    + "		}\r\n" + "	],\r\n"
	    + "	\"key\": \"PUBLIC-bc799ef350fe9841c1354736d8f863cb85bac88cefd19960c1\"\r\n" + "}";

    private TestInput() {
	// Nothing to do
    }

    /*
     *
     * Rest Optimization
     *
     */

    public static RestOptimization defaultTouroptimizerTestInput() {

	/*
	 * Nodes
	 */
	List<Node> nodes = new ArrayList<>();
	nodes.add(defaultGeoNode(createPosition(50.9333, 6.95, "Koeln"), "Koeln"));
	nodes.add(defaultGeoNode(createPosition(51.448419, 7.017271, "Essen"), "Essen"));
	nodes.add(defaultGeoNode(createPosition(50.8, 6.48333, "Dueren"), "Dueren"));
	nodes.add(defaultGeoNode(createPosition(51.266287, 7.180998, "Wuppertal"), "Wuppertal"));
	nodes.add(defaultGeoNode(createPosition(50.772649, 6.077672, "Aachen"), "Aachen"));
	nodes.add(defaultGeoNode(createPosition(51.573276, 7.035723, "Gelsenkirchen"), "Gelsenkirchen"));
	nodes.add(defaultGeoNode(createPosition(51.512207, 7.460381, "Dortmund"), "Dortmund"));
	nodes.add(defaultGeoNode(createPosition(51.480579, 7.209159, "Bochum"), "Bochum"));
	nodes.add(defaultGeoNode(createPosition(50.742210, 7.094417, "Bonn"), "Bonn"));

	/*
	 * Resources
	 */
	List<Resource> ress = new ArrayList<>();
	ress.add(defaultCapacityResource(createPosition(50.747993, 6.167740, "Jack from Aachen"), "Jack from Aachen"));

	return defaultTouroptimizerTestInput(nodes, ress);
    }

    public static RestOptimization defaultTouroptimizerTestInput(List<Node> nodes, List<Resource> ress) {
	return defaultTouroptimizerTestInput(nodes, ress, Optional.empty());
    }

    public static RestOptimization defaultTouroptimizerTestInput(List<Node> nodes, List<Resource> ress,
	    Optional<String> jsonLicenseOpt) {

	RestOptimization myOpti = new RestOptimization();

	myOpti.setIdent("StandardTouroptimizerTestInput");

	// Nodes
	nodes.forEach(myOpti::addNodesItem);

	// Resources
	ress.forEach(myOpti::addResourcesItem);

	// Options
	OptimizationOptions optimizationOptions = new OptimizationOptions();
	optimizationOptions.setProperties(new HashMap<>());
	optimizationOptions.putPropertiesItem("JOpt.Algorithm.PreOptimization.SA.NumIterations", "1000000");
	optimizationOptions.putPropertiesItem("JOptExitCondition.JOptGenerationCount", "10000");

	myOpti.setOptimizationOptions(optimizationOptions);

	//
	JSONConfig extension = new JSONConfig();
	extension.setTimeOut("PT2H");
	OptimizationKeySetting keySetting = new OptimizationKeySetting();

	String jsonLicense = PUBLIC_JSON_LICENSE;
	if (jsonLicenseOpt.isPresent()) {
	    jsonLicense = jsonLicenseOpt.get();
	}

	keySetting.jsonLicense(jsonLicense);
	extension.setKeySetting(keySetting);
	myOpti.setExtension(extension);

	return myOpti;
    }

    /*
     *
     * GeoCoder
     *
     */

    public static List<GeoAddress> defaultVsAustraliaGoogleNodeAddresses() {

	List<GeoAddress> adds = new ArrayList<>();

	adds.add(new GeoAddress().city("Surry Hills").streetname("Albion St").housenumber("137-123").postalcode("2010")
		.locationId("Albion St"));

	adds.add(new GeoAddress().city("Concord West").streetname("Bangalla Road").housenumber("10").postalcode("2138")
		.locationId("Bangalla Rd"));

	adds.add(new GeoAddress().city("Haberfield").streetname("Kingston Street").housenumber("83-49")
		.postalcode("2045").locationId("Kingston St"));

	adds.add(new GeoAddress().city("Coorparoo").streetname("Lackey Ave").housenumber("4151").postalcode("2045")
		.locationId("Lackey Ave"));

	adds.add(new GeoAddress().city("Rose Bay").streetname("Beaumont St").housenumber("19").postalcode("2029")
		.locationId("Beaumont"));

	return adds;
    }

    public static List<GeoAddress> defaultSydneyResourceAddresses() {
	List<GeoAddress> adds = new ArrayList<>();
	adds.add(new GeoAddress().city("Grasmere").streetname("Werombi Road").housenumber("81").postalcode("2570")
		.locationId("GrasmereWerombi Road81"));

	adds.add(new GeoAddress().city("Camden").streetname("Edward Street").housenumber("36").postalcode("2570")
		.locationId("CamdenEdward Street36"));

	adds.add(new GeoAddress().city("Lucas Heights").streetname("New Illawarra Road").housenumber("178")
		.postalcode("2234").locationId("Lucas HeightsNew Illawarra Road178"));

	adds.add(new GeoAddress().city("Engadine").streetname("Sierra Road").housenumber("24").postalcode("2233")
		.locationId("EngadineSierra Road24"));

	adds = adds.stream().map(a -> {

	    a.setLocationId("RES-" + a.getLocationId());

	    return a;
	}).collect(Collectors.toList());

	return adds;
    }

    public static List<GeoAddress> defaultSydneyNodeAddresses() {
	List<GeoAddress> adds = new ArrayList<>();

	adds.add(new GeoAddress().city("Grasmere").streetname("Werombi Road").housenumber("81").postalcode("2570")
		.locationId("GrasmereWerombi Road81"));

	adds.add(new GeoAddress().city("Elderslie").streetname("Camden Valley Way").housenumber("24").postalcode("2570")
		.locationId("ElderslieCamden Valley Way24"));

	adds.add(new GeoAddress().city("Elderslie").streetname("Carpenter Street").housenumber("16").postalcode("2570")
		.locationId("ElderslieCarpenter Street16"));

	adds.add(new GeoAddress().city("Narellan").streetname("Camden Valley Way").housenumber("259").postalcode("2567")
		.locationId("NarellanCamden Valley Way259"));

	adds.add(new GeoAddress().city("Narellan").streetname("Somerset Avenue").housenumber("30").postalcode("2567")
		.locationId("NarellanSomerset Avenue30"));

	adds.add(new GeoAddress().city("Currans Hill").streetname("Caulfield Close").housenumber("8").postalcode("2567")
		.locationId("Currans HillCaulfield Close8"));

	adds.add(new GeoAddress().city("Eagle Vale").streetname("Namatjira Close").housenumber("35").postalcode("2558")
		.locationId("Eagle ValeNamatjira Close35"));

	adds.add(new GeoAddress().city("Minto").streetname("Airds Road").housenumber("157").postalcode("2566")
		.locationId("MintoAirds Road157"));

	adds.add(new GeoAddress().city("Minto").streetname("Magnum Place").housenumber("2").postalcode("2566")
		.locationId("MintoMagnum Place2"));

	adds.add(new GeoAddress().city("Minto").streetname("Ben Lomond Road").housenumber("44").postalcode("2566")
		.locationId("MintoBen Lomond Road44"));

	adds.add(new GeoAddress().city("Minto").streetname("Eagleview Road").housenumber("119").postalcode("2566")
		.locationId("MintoEagleview Road119"));

	adds.add(new GeoAddress().city("Minto Heights").streetname("Howard Road").housenumber("36").postalcode("2566")
		.locationId("Minto HeightsHoward Road36"));

	adds.add(new GeoAddress().city("Long Point").streetname("Kingdon Parade").housenumber("36").postalcode("2564")
		.locationId("Long PointKingdon Parade36"));

	adds.add(new GeoAddress().city("Barden Ridge").streetname("Sutcliffe Place").housenumber("2").postalcode("2234")
		.locationId("Barden RidgeSutcliffe Place2"));

	adds.add(new GeoAddress().city("Woronora Heights").streetname("Woronora River Frontage").housenumber("240")
		.postalcode("2233").locationId("Woronora HeightsWoronora River Frontage240"));

	adds.add(new GeoAddress().city("Sutherland").streetname("Linden Street").housenumber("121").postalcode("2232")
		.locationId("SutherlandLinden Street121"));

	adds.add(new GeoAddress().city("Sutherland").streetname("Merton Street").housenumber("9").postalcode("2232")
		.locationId("SutherlandMerton Street9"));

	adds.add(new GeoAddress().city("Menai").streetname("Bray Grove").housenumber("1").postalcode("2234")
		.locationId("MenaiBray Grove1"));

	adds.add(new GeoAddress().city("Bangor").streetname("Shackel Road").housenumber("14R").postalcode("2234")
		.locationId("BangorShackel Road14R"));

	adds.add(new GeoAddress().city("Sutherland").streetname("Carol Avenue").housenumber("8A").postalcode("2226")
		.locationId("SutherlandCarol Avenue8A"));

	adds.add(new GeoAddress().city("Kirrawee").streetname("Magnolia Street").housenumber("8").postalcode("2232")
		.locationId("KirraweeMagnolia Street8"));

	adds.add(new GeoAddress().city("Miranda").streetname("Box Road").housenumber("244").postalcode("2228")
		.locationId("MirandaBox Road244"));

	adds.add(new GeoAddress().city("Sylvania Waters").streetname("Belgrave Esplanade").housenumber("196")
		.postalcode("2224").locationId("Sylvania WatersBelgrave Esplanade196"));

	adds = adds.stream().map(a -> {

	    a.setLocationId("NODE-" + a.getLocationId());

	    return a;
	}).collect(Collectors.toList());

	return adds;
    }

    public static List<GeoAddress> defaultNodeAddresses() {

	List<GeoAddress> adds = new ArrayList<>();

	adds.add(new GeoAddress().city("Düsseldorf").streetname("Königsallee").housenumber("71").postalcode("40215")
		.locationId("Duessldorf Location"));

	adds.add(new GeoAddress().city("Köln").postalcode("50667").locationId("Cologne Location"));

	adds.add(new GeoAddress().city("Essen").streetname("Kreuzeskirchstraße").housenumber("25").postalcode("45127")
		.locationId("Essen Location"));

	adds.add(new GeoAddress().city("Düsseldorf").streetname("Rollandstraße").locationId("Duessldorf Location 2"));

	adds.add(new GeoAddress().city("Wuppertal").streetname("Herzogstraße").locationId("Wuppertal Location"));

	adds.add(new GeoAddress().city("Leverkusen").streetname("Hitdorfer Str.").housenumber("270").postalcode("51371")
		.locationId("Hitdorf Location"));

	adds.add(new GeoAddress().city("Aachen").streetname("Mauerstraße").locationId("Aachen Location"));

	return adds;
    }

    public static List<GeoAddress> defaultResourceAddresses() {
	List<GeoAddress> adds = new ArrayList<>();

	adds.add(new GeoAddress().city("Bonn").streetname("Dorotheenstraße").housenumber("135").postalcode("53111")
		.locationId("Bonn Location"));

	return adds;
    }

    /*
     *
     * GeoRouter - No extra methods for preparation
     *
     */

    /*
     *
     *
     *
     *
     *
     */

    /*
     *
     * Shared
     *
     */

    public static Position createPosition(Double latitude, Double longitude, String locationId) {
	Position pos = new Position();

	pos.setLatitude(latitude);
	pos.setLongitude(longitude);
	pos.setLocationId(locationId);

	return pos;
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
