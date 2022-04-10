package com.dna.jopt.rest.client.util.testinputcreation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import com.dna.jopt.rest.client.model.GeoAddress;

public final class TestAddressInput {



    private TestAddressInput() {
	// Nothing to do
    }

    

    /*
     *
     * GeoCoder
     *
     */
    
    
    /*
     * Sydney
     */

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
    
    /*
     * Germany
     */

    public static List<GeoAddress> defaultGermanyNodeAddresses() {

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

    public static List<GeoAddress> defaultGermanyResourceAddresses() {
	List<GeoAddress> adds = new ArrayList<>();

	adds.add(new GeoAddress().city("Bonn").streetname("Dorotheenstraße").housenumber("135").postalcode("53111")
		.locationId("Bonn Location"));

	return adds;
    }


}
