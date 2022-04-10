package com.dna.jopt.rest.client.util.testinputcreation;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import com.dna.jopt.rest.client.model.JSONConfig;
import com.dna.jopt.rest.client.model.Node;
import com.dna.jopt.rest.client.model.OptimizationKeySetting;
import com.dna.jopt.rest.client.model.OptimizationOptions;
import com.dna.jopt.rest.client.model.Resource;
import com.dna.jopt.rest.client.model.RestOptimization;

public class TestRestOptimizationCreator {
    
    
    private TestRestOptimizationCreator() {
	// Nothing to do
    }

    public static final String PUBLIC_JSON_LICENSE = "{\r\n" + "	\"version\": \"1.1\",\r\n"
	    + "	\"identifier\": \"PUBLIC-\",\r\n"
	    + "	\"description\": \"Key provided to for evaluation purpose from DNA evolutions GmbH.\",\r\n"
	    + "	\"contact\": \"www.dna-evolutions.com\",\r\n" + "	\"modules\": [{\r\n"
	    + "			\"Module:\": \"Elements\",\r\n" + "			\"max\": 15\r\n"
	    + "		}, {\r\n" + "			\"Module:\": \"Date\",\r\n"
	    + "			\"creation\": \"2021-05-25\",\r\n" + "			\"due\": \"2027-05-25\"\r\n"
	    + "		}\r\n" + "	],\r\n"
	    + "	\"key\": \"PUBLIC-bc799ef350fe9841c1354736d8f863cb85bac88cefd19960c1\"\r\n" + "}";
    
    
    public static final String NON_VALID_JSON_LICENSE = "{\r\n" + "	\"version\": \"1.1\",\r\n"
	    + "	\"identifier\": \"PUBLIC-\",\r\n"
	    + "	\"description\": \"Key provided to for evaluation purpose from DNA evolutions GmbH.\",\r\n"
	    + "	\"contact\": \"www.dna-evolutions.com\",\r\n" + "	\"modules\": [{\r\n"
	    + "			\"Module:\": \"Elements\",\r\n" + "			\"max\": 15\r\n"
	    + "		}, {\r\n" + "			\"Module:\": \"Date\",\r\n"
	    + "			\"creation\": \"2021-05-25\",\r\n" + "			\"due\": \"2027-05-25\"\r\n"
	    + "		}\r\n" + "	],\r\n"
	    + "	\"key\": \"NON_VALID\"\r\n" + "}";
    
    /*
     *
     * Rest Optimization
     *
     */

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
}
