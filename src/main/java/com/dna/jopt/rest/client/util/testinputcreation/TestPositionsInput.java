package com.dna.jopt.rest.client.util.testinputcreation;

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

import java.util.ArrayList;
import java.util.List;

import com.dna.jopt.rest.client.model.Position;

public class TestPositionsInput {

    private TestPositionsInput() {
	// Nothing to do
    }

    public static final String SYDNEY_NODE_POSITIONS__JSON = "[ {\r\n" + "  \"latitude\" : -34.052052,\r\n"
	    + "  \"longitude\" : 150.668724,\r\n" + "  \"locationId\" : \"Node_0\",\r\n" + "  \"geoAddress\" : {\r\n"
	    + "    \"locationId\" : \"Node_0\",\r\n" + "    \"housenumber\" : \"81\",\r\n"
	    + "    \"streetname\" : \"Werombi Road\",\r\n" + "    \"city\" : \"Grasmere\",\r\n"
	    + "    \"county\" : \"Camden\",\r\n" + "    \"state\" : \"New South Wales\",\r\n"
	    + "    \"statecode\" : \"NSW\",\r\n" + "    \"country\" : \"Australia\",\r\n"
	    + "    \"macrocountry\" : \"Sydney\",\r\n" + "    \"countrycode\" : \"AU\",\r\n"
	    + "    \"postalcode\" : \"2570\",\r\n" + "    \"layer\" : \"address\",\r\n"
	    + "    \"source\" : \"openaddresses\",\r\n" + "    \"accuracy\" : \"point\",\r\n"
	    + "    \"confidence\" : 1.0,\r\n" + "    \"label\" : \"81 Werombi Road, Grasmere, NSW, Australia\"\r\n"
	    + "  },\r\n" + "  \"locationParameters\" : null\r\n" + "}, {\r\n" + "  \"latitude\" : -34.052518,\r\n"
	    + "  \"longitude\" : 150.709943,\r\n" + "  \"locationId\" : \"Node_1\",\r\n" + "  \"geoAddress\" : {\r\n"
	    + "    \"locationId\" : \"Node_1\",\r\n" + "    \"housenumber\" : \"24\",\r\n"
	    + "    \"streetname\" : \"Camden Valley Way\",\r\n" + "    \"city\" : \"Elderslie\",\r\n"
	    + "    \"county\" : \"Camden\",\r\n" + "    \"state\" : \"New South Wales\",\r\n"
	    + "    \"statecode\" : \"NSW\",\r\n" + "    \"country\" : \"Australia\",\r\n"
	    + "    \"macrocountry\" : \"Sydney\",\r\n" + "    \"countrycode\" : \"AU\",\r\n"
	    + "    \"postalcode\" : \"2570\",\r\n" + "    \"layer\" : \"address\",\r\n"
	    + "    \"source\" : \"openaddresses\",\r\n" + "    \"accuracy\" : \"point\",\r\n"
	    + "    \"confidence\" : 1.0,\r\n"
	    + "    \"label\" : \"24 Camden Valley Way, Elderslie, NSW, Australia\"\r\n" + "  },\r\n"
	    + "  \"locationParameters\" : null\r\n" + "}, {\r\n" + "  \"latitude\" : -34.051988,\r\n"
	    + "  \"longitude\" : 150.71981,\r\n" + "  \"locationId\" : \"Node_2\",\r\n" + "  \"geoAddress\" : {\r\n"
	    + "    \"locationId\" : \"Node_2\",\r\n" + "    \"housenumber\" : \"16\",\r\n"
	    + "    \"streetname\" : \"Carpenter Street\",\r\n" + "    \"city\" : \"Elderslie\",\r\n"
	    + "    \"county\" : \"Camden\",\r\n" + "    \"state\" : \"New South Wales\",\r\n"
	    + "    \"statecode\" : \"NSW\",\r\n" + "    \"country\" : \"Australia\",\r\n"
	    + "    \"macrocountry\" : \"Sydney\",\r\n" + "    \"countrycode\" : \"AU\",\r\n"
	    + "    \"postalcode\" : \"2570\",\r\n" + "    \"layer\" : \"address\",\r\n"
	    + "    \"source\" : \"openaddresses\",\r\n" + "    \"accuracy\" : \"point\",\r\n"
	    + "    \"confidence\" : 1.0,\r\n" + "    \"label\" : \"16 Carpenter Street, Elderslie, NSW, Australia\"\r\n"
	    + "  },\r\n" + "  \"locationParameters\" : null\r\n" + "}, {\r\n" + "  \"latitude\" : -34.04213,\r\n"
	    + "  \"longitude\" : 150.729568,\r\n" + "  \"locationId\" : \"Node_3\",\r\n" + "  \"geoAddress\" : {\r\n"
	    + "    \"locationId\" : \"Node_3\",\r\n" + "    \"housenumber\" : \"259\",\r\n"
	    + "    \"streetname\" : \"Camden Valley Way\",\r\n" + "    \"city\" : \"Narellan\",\r\n"
	    + "    \"county\" : \"Camden\",\r\n" + "    \"state\" : \"New South Wales\",\r\n"
	    + "    \"statecode\" : \"NSW\",\r\n" + "    \"country\" : \"Australia\",\r\n"
	    + "    \"macrocountry\" : \"Sydney\",\r\n" + "    \"countrycode\" : \"AU\",\r\n"
	    + "    \"postalcode\" : \"2567\",\r\n" + "    \"layer\" : \"address\",\r\n"
	    + "    \"source\" : \"openaddresses\",\r\n" + "    \"accuracy\" : \"point\",\r\n"
	    + "    \"confidence\" : 1.0,\r\n"
	    + "    \"label\" : \"259 Camden Valley Way, Narellan, NSW, Australia\"\r\n" + "  },\r\n"
	    + "  \"locationParameters\" : null\r\n" + "}, {\r\n" + "  \"latitude\" : -34.042063,\r\n"
	    + "  \"longitude\" : 150.739632,\r\n" + "  \"locationId\" : \"Node_4\",\r\n" + "  \"geoAddress\" : {\r\n"
	    + "    \"locationId\" : \"Node_4\",\r\n" + "    \"housenumber\" : \"30\",\r\n"
	    + "    \"streetname\" : \"Somerset Avenue\",\r\n" + "    \"city\" : \"Narellan\",\r\n"
	    + "    \"county\" : \"Camden\",\r\n" + "    \"state\" : \"New South Wales\",\r\n"
	    + "    \"statecode\" : \"NSW\",\r\n" + "    \"country\" : \"Australia\",\r\n"
	    + "    \"macrocountry\" : \"Sydney\",\r\n" + "    \"countrycode\" : \"AU\",\r\n"
	    + "    \"postalcode\" : \"2567\",\r\n" + "    \"layer\" : \"address\",\r\n"
	    + "    \"source\" : \"openaddresses\",\r\n" + "    \"accuracy\" : \"point\",\r\n"
	    + "    \"confidence\" : 1.0,\r\n" + "    \"label\" : \"30 Somerset Avenue, Narellan, NSW, Australia\"\r\n"
	    + "  },\r\n" + "  \"locationParameters\" : null\r\n" + "} ]";

    public static final String SYDNEY_RESOURCE_POSITIONS__JSON = "[ {\r\n" + "  \"latitude\" : -34.052052,\r\n"
	    + "  \"longitude\" : 150.668724,\r\n" + "  \"locationId\" : \"Resource_0\",\r\n"
	    + "  \"geoAddress\" : {\r\n" + "    \"locationId\" : \"Resource_0\",\r\n"
	    + "    \"housenumber\" : \"81\",\r\n" + "    \"streetname\" : \"Werombi Road\",\r\n"
	    + "    \"city\" : \"Grasmere\",\r\n" + "    \"county\" : \"Camden\",\r\n"
	    + "    \"state\" : \"New South Wales\",\r\n" + "    \"statecode\" : \"NSW\",\r\n"
	    + "    \"country\" : \"Australia\",\r\n" + "    \"macrocountry\" : \"Sydney\",\r\n"
	    + "    \"countrycode\" : \"AU\",\r\n" + "    \"postalcode\" : \"2570\",\r\n"
	    + "    \"layer\" : \"address\",\r\n" + "    \"source\" : \"openaddresses\",\r\n"
	    + "    \"accuracy\" : \"point\",\r\n" + "    \"confidence\" : 1.0,\r\n"
	    + "    \"label\" : \"81 Werombi Road, Grasmere, NSW, Australia\"\r\n" + "  },\r\n"
	    + "  \"locationParameters\" : null\r\n" + "} ]";

    /*
     * 
     * 
     * 
     * 
     */

    public static List<Position> defaultSydneyPositionsWithDuplicatesError() {
	List<Position> poss = new ArrayList<>();

	poss.add(new Position().latitude(-34.052052).longitude(150.668724).locationId("Position_0"));
	poss.add(new Position().latitude(-34.052052).longitude(150.668724).locationId("Position_0_AGAIN"));

	return poss;

    }

    public static List<Position> defaultSydneyNodePositions() {

	List<Position> poss = new ArrayList<>();

	poss.add(new Position().latitude(-34.052052).longitude(150.668724).locationId("Node_0"));
	poss.add(new Position().latitude(-34.052518).longitude(150.709943).locationId("Node_1"));
	poss.add(new Position().latitude(-34.051988).longitude(150.71981).locationId("Node_2"));
	poss.add(new Position().latitude(-34.04213).longitude(150.729568).locationId("Node_3"));
	poss.add(new Position().latitude(-34.042063).longitude(150.739632).locationId("Node_4"));
	poss.add(new Position().latitude(-34.041006).longitude(150.779042).locationId("Node_5"));
	poss.add(new Position().latitude(-34.042611).longitude(150.800852).locationId("Node_6"));
	poss.add(new Position().latitude(-34.042334).longitude(150.830416).locationId("Node_7"));
	poss.add(new Position().latitude(-34.041776).longitude(150.839277).locationId("Node_8"));
	poss.add(new Position().latitude(-34.032093).longitude(150.849402).locationId("Node_9"));
	poss.add(new Position().latitude(-34.032283).longitude(150.860021).locationId("Node_10"));
	poss.add(new Position().latitude(-34.033504).longitude(150.885173).locationId("Node_11"));
	poss.add(new Position().latitude(-34.016844).longitude(150.901184).locationId("Node_12"));
	poss.add(new Position().latitude(-34.032085).longitude(151.009819).locationId("Node_13"));
	poss.add(new Position().latitude(-34.03345).longitude(151.019328).locationId("Node_14"));
	poss.add(new Position().latitude(-34.032983).longitude(151.050504).locationId("Node_15"));
	poss.add(new Position().latitude(-34.031779).longitude(151.059578).locationId("Node_16"));
	poss.add(new Position().latitude(-34.021961).longitude(151.019686).locationId("Node_17"));
	poss.add(new Position().latitude(-34.02273).longitude(151.030557).locationId("Node_18"));
	poss.add(new Position().latitude(-34.08002).longitude(150.999438).locationId("Node_19"));
	poss.add(new Position().latitude(-34.022009).longitude(151.069768).locationId("Node_20"));
	poss.add(new Position().latitude(-34.02241).longitude(151.098778).locationId("Node_21"));
	poss.add(new Position().latitude(-34.022038).longitude(151.109346).locationId("Node_22"));
	poss.add(new Position().latitude(-34.052077).longitude(150.69951).locationId("Node_23"));
	poss.add(new Position().latitude(-34.051068).longitude(150.976722).locationId("Node_24"));
	poss.add(new Position().latitude(-34.052015).longitude(150.999808).locationId("Node_25"));

	return poss;

    }

    public static List<Position> defaultSydneyResourcePositions() {

	List<Position> poss = new ArrayList<>();

	poss.add(new Position().latitude(-34.052052).longitude(150.668724).locationId("Resource_0"));
	poss.add(new Position().latitude(-34.052518).longitude(150.709943).locationId("Resource_1"));
	poss.add(new Position().latitude(-34.051988).longitude(150.71981).locationId("Resource_2"));
	poss.add(new Position().latitude(-34.052015).longitude(150.999808).locationId("Resource_3"));

	return poss;

    }

    /*
     * 
     * 
     */

    public static List<Position> defaultSmallSydneyNodePositions() {

	List<Position> poss = new ArrayList<>();

	poss.add(new Position().latitude(-34.052052).longitude(150.668724).locationId("Node_0"));
	poss.add(new Position().latitude(-34.052518).longitude(150.709943).locationId("Node_1"));
	poss.add(new Position().latitude(-34.051988).longitude(150.71981).locationId("Node_2"));
	poss.add(new Position().latitude(-34.04213).longitude(150.729568).locationId("Node_3"));
	poss.add(new Position().latitude(-34.042063).longitude(150.739632).locationId("Node_4"));
	poss.add(new Position().latitude(-34.041006).longitude(150.779042).locationId("Node_5"));

	return poss;

    }

    public static List<Position> defaultSmallSydneyResourcePositions() {

	List<Position> poss = new ArrayList<>();

	poss.add(new Position().latitude(-34.052052).longitude(150.668724).locationId("Resource_0"));

	return poss;

    }

}
