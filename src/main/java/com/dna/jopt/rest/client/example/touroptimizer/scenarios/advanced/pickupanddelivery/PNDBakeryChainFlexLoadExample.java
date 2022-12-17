package com.dna.jopt.rest.client.example.touroptimizer.scenarios.advanced.pickupanddelivery;

import java.io.File;
import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.dna.jopt.rest.client.example.touroptimizer.helper.TourOptimizerRestCaller;
import com.dna.jopt.rest.client.model.CapacityResource;
import com.dna.jopt.rest.client.model.ElementConnection;
import com.dna.jopt.rest.client.model.GeoNode;
import com.dna.jopt.rest.client.model.Position;
import com.dna.jopt.rest.client.model.Resource;
import com.dna.jopt.rest.client.model.RestOptimization;
import com.dna.jopt.rest.client.model.SimpleLoad;
import com.dna.jopt.rest.client.model.SimpleLoadCapacity;
import com.dna.jopt.rest.client.model.SimpleNodeDepot;
import com.dna.jopt.rest.client.model.SimpleResourceDepot;
import com.dna.jopt.rest.client.model.SupplyFlexLoad;
import com.dna.jopt.rest.client.model.TextSolution;
import com.dna.jopt.rest.client.model.WorkingHours;
import com.dna.jopt.rest.client.util.endpoints.Endpoints;
import com.dna.jopt.rest.client.util.io.json.RestJSONParser;
import com.dna.jopt.rest.client.util.secretsmanager.SecretsManager;
import com.dna.jopt.rest.client.util.secretsmanager.caughtexception.NoSecretFileFoundException;
import com.dna.jopt.rest.client.util.secretsmanager.caughtexception.SecretNotFoundException;
import com.dna.jopt.rest.client.model.Node;
import com.dna.jopt.rest.client.model.OpeningHours;
import com.dna.jopt.rest.client.model.OptimizationOptions;
import com.dna.jopt.rest.client.model.OptimizationOptionsProperties;

public class PNDBakeryChainFlexLoadExample {

    public static void main(String[] args) throws IOException, NoSecretFileFoundException, SecretNotFoundException {

	/*
	 * 
	 * Modify me
	 * 
	 */
	boolean isAzureCall = !true;
	boolean isSave2JSON = true;

	// Define the data to be used

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
	 * Optimize
	 *
	 */
	Optional<TextSolution> textSolutionOpt;

	// We simply provide empty connections => This will trigger automatic haversine
	// distance calculations

	List<ElementConnection> emptyConnections = new ArrayList<>();

	
	OptimizationOptions optimizationOptions = new OptimizationOptions();
	
	OptimizationOptionsProperties props = new OptimizationOptionsProperties();
	props.put("JOpt.Algorithm.PreOptimization.SA.NumIterations", "10000");
	props.put("JOptExitCondition.JOptGenerationCount", "1000");
	props.put("JOptWeight.Capacity", "200");
	
	optimizationOptions.setProperties(props);

	RestOptimization result = tourOptimizerCaller.optimize(createExampleNodes(), createExampleResources(),
		emptyConnections, Optional.of(m.get("joptlic")), Optional.of(optimizationOptions));

	textSolutionOpt = Optional.ofNullable(result.getExtension().getTextSolution());

	// Show result
	textSolutionOpt.ifPresent(ts -> {
	    System.out.println("Printing text solution");
	    System.out.println(ts);
	});

	/*
	 * Save to JSON snapshot
	 */
	if (isSave2JSON) {

	    String jsonFile = "OptimizationTest" + ".json";
	    
	    System.out.println(RestJSONParser.toJSONString(result, tourOptimizerCaller.getMapper()));

	    RestJSONParser.toJsonFile(result, new File(jsonFile), tourOptimizerCaller.getMapper());
	}

    }

    /*
     * 
     * Creation
     * 
     * 
     */
    public static List<Resource> createExampleResources() {
	List<Resource> ress = new ArrayList<>();

	ress.add(createResource("JackTruckCologne", 50.9333, 6.95, "JackTruckCologneDepot"));
	ress.add(createResource("JohnTruckAachen", 50.775346, 6.083887, "JohnTruckAachenDepot"));
	return ress;
    }

    /*
     * Res
     */

    /*
     * Node
     */

    public static List<Node> createExampleNodes() {
	List<Node> nodes = new ArrayList<>();

	nodes.add(createNode("StoreEssen", 51.45, 7.01667, "StoreEssenDepot", 5));
	nodes.add(createNode("StoreDueren", 50.8, 6.48333, "StoreDuerenDepot", 2));
	nodes.add(createNode("StoreWuppertal", 51.2667, 7.18333, "StoreWuppertalDepot", 4));
	nodes.add(createNode("StoreDortmund", 51.51728, 7.48446, "StoreDortmundDepot", 8));
	nodes.add(createNode("StoreMoenchengladbach", 51.19541, 6.41172, "StoreMoenchengladbachDepot", 1));
	nodes.add(createNode("StoreLeverkusen", 51.04154, 6.99944, "StoreLeverkusenDepot", 7));

	nodes.add(createExampleSupplyNodes("SupplyFlexNodeAachen", 50.77577, 6.08177, "SupplyFlexNodeAachenDepot", 20));
	nodes.add(createExampleSupplyNodes("SupplyFlexNodeCologne", 50.9333, 6.95, "SupplyFlexNodeCologneDepot", 20));

	return nodes;
    }

    /*
     * 
     * Helper
     * 
     */

    private static Resource createResource(String resId, double lat, double lon, String depotId) {
	Resource res = new Resource();

	res.setWorkingHours(createWorkinghours());
	res.setId(resId);
	res.setPosition(new Position().latitude(lat).longitude(lon));
	res.setMaxTime("PT10H");
	res.setMaxDistance("2800.0 km");
	res.setLocationId(resId);

	CapacityResource type = new CapacityResource();
	type.setTypeName("Capacity");

	res.setType(type);

	res.setResourceDepot(createResourceDepot(depotId));

	return res;
    }

    public static SimpleResourceDepot createResourceDepot(String depotId) {
	SimpleLoadCapacity breadCap = new SimpleLoadCapacity();
	breadCap.setLoadCapacityId("Bread");
	breadCap.setCurrentLoad(0.0);
	breadCap.setMaxCapacityValue(20);

	SimpleResourceDepot depot = new SimpleResourceDepot();
	depot.setDepotId(depotId);
	depot.setMaximalUnitMatchedCapacity(20.0);

	depot.addItemsItem(breadCap);

	return depot;

    }

    private static Node createExampleSupplyNodes(String storeId, double lat, double lon, String depotId,
	    double initialValue) {

	String visitDuration = "PT80M";

	Node supplyNode = new Node();

	supplyNode.setOpeningHours(createOpeninghours());
	supplyNode.setId(storeId);
	supplyNode.setVisitDuration(visitDuration);
	supplyNode.setPriority(1);
	supplyNode.setLocationId(storeId);

	GeoNode geoPart = new GeoNode();
	geoPart.setPosition(new Position().latitude(lat).longitude(lon));
	geoPart.setTypeName("Geo");
	supplyNode.setType(geoPart);

	supplyNode.setNodeDepot(createSupplyFlexDepot(depotId, initialValue));

	return supplyNode;
    }

    private static Node createNode(String storeId, double lat, double lon, String depotId, double loadValue) {

	String visitDuration = "PT80M";

	Node store = new Node();

	store.setOpeningHours(createOpeninghours());
	store.setId(storeId);
	store.setLocationId(storeId);
	store.setVisitDuration(visitDuration);
	store.setPriority(1);
	GeoNode geoPart = new GeoNode();
	geoPart.setPosition(new Position().latitude(lat).longitude(lon));
	geoPart.setTypeName("Geo");
	store.setType(geoPart);

	store.setNodeDepot(createNodeDepot(depotId, loadValue));

	return store;

    }

    public static SimpleNodeDepot createNodeDepot(String depotId, double breadCount) {

	SimpleLoad breadLoad = new SimpleLoad();
	breadLoad.setLoadId("Bread");
	breadLoad.setIsRequest(true);
	breadLoad.setIsFuzzyVisit(true);
	breadLoad.setLoadValue(breadCount);
	breadLoad.setPriority(1);

	SimpleNodeDepot depot = new SimpleNodeDepot();
	depot.setDepotId(depotId);

	depot.addItemsItem(breadLoad);

	return depot;

    }

    public static SimpleNodeDepot createSupplyFlexDepot(String depotId, double intialBreadCount) {
	SupplyFlexLoad supplyFreadLoad = new SupplyFlexLoad();
	supplyFreadLoad.setLoadId("Bread");
	supplyFreadLoad.setLoadValue(intialBreadCount);

	SimpleNodeDepot depot = new SimpleNodeDepot();
	depot.setDepotId(depotId);

	depot.addItemsItem(supplyFreadLoad);

	return depot;

    }

    public static List<OpeningHours> createOpeninghours() {

	String zone = "Europe/Berlin";
	ZoneId zoneId = ZoneId.of(zone);

	int numDays = 1;

	/*
	 *
	 */
	return IntStream.range(0, numDays).boxed().map(ii -> {
	    ZonedDateTime start = ZonedDateTime.of(2023, 1, 1 + ii, 8, 0, 0, 0, zoneId);
	    ZonedDateTime end = ZonedDateTime.of(2023, 1, 1 + ii, 20, 0, 0, 0, zoneId);

	    return createOpeninghours(start, end, zoneId);
	}).collect(Collectors.toList());
    }

    public static OpeningHours createOpeninghours(ZonedDateTime begin, ZonedDateTime end, ZoneId zoneId) {

	OpeningHours hr = new OpeningHours();

	hr.setBegin(begin.toInstant());
	hr.setEnd(end.toInstant());
	hr.setZoneId(zoneId.getId());

	return hr;
    }

    public static List<WorkingHours> createWorkinghours() {

	String zone = "Europe/Berlin";
	ZoneId zoneId = ZoneId.of(zone);

	int numDays = 1;

	/*
	 *
	 */
	return IntStream.range(0, numDays).boxed().map(ii -> {
	    ZonedDateTime start = ZonedDateTime.of(2023, 1, 1 + ii, 8, 0, 0, 0, zoneId);
	    ZonedDateTime end = ZonedDateTime.of(2023, 1, 1 + ii, 20, 0, 0, 0, zoneId);

	    return createWorkinghours(start, end, zoneId);
	}).collect(Collectors.toList());
    }

    public static WorkingHours createWorkinghours(ZonedDateTime begin, ZonedDateTime end, ZoneId zoneId) {

	WorkingHours hr = new WorkingHours();

	hr.setBegin(begin.toInstant());
	hr.setEnd(end.toInstant());
	hr.setZoneId(zoneId.getId());

	return hr;
    }

}
