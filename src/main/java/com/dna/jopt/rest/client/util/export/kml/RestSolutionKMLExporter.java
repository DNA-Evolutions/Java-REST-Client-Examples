package com.dna.jopt.rest.client.util.export.kml;

/*-
 * #%L
 * JOpt Java Core
 * %%
 * Copyright (C) 2017 - 2020 DNA Evolutions GmbH
 * %%
 * This file is subject to the terms and conditions defined in file 'LICENSE.txt',
 * which is part of this source code package.
 *
 * If not, see <https://www.dna-evolutions.com/agb-conditions-and-terms/>.
 * #L%
 */

import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.dna.jopt.rest.client.model.EncodedPolyline;
import com.dna.jopt.rest.client.model.Position;
import com.dna.jopt.rest.client.model.ResourceTrip;
import com.dna.jopt.rest.client.model.Route;
import com.dna.jopt.rest.client.model.RouteElementDetail;
import com.dna.jopt.rest.client.model.Solution;
import com.dna.jopt.rest.client.model.Violation;

/**
 * This class can be used to export the results in a .kml format which can be
 * used in Google Earth.
 *
 * <p>
 * Example:
 *
 * <pre>
 * {@code
 * public void onAsynchronousOptimizationResult(IOptimizationResult rapoptResult) {
 *   System.out.println(rapoptResult);
 *
 *   IEntityExporter kmlExporter = new EntityKMLExporter();
 *   kmlExporter.setTitle("" + this.getClass().getSimpleName());
 *
 *   try {
 *
 *     kmlExporter.export(
 *         rapoptResult.getContainer(),
 *         new FileOutputStream(new File("./" + this.getClass().getSimpleName() + ".kml")));
 *
 *   } catch (FileNotFoundException e) {
 *     //
 *     e.printStackTrace();
 *   }
 * }
 * }
 * </pre>
 *
 * @author DNA
 * @version 03/08/2019
 * @since 03/08/2019
 */
public class RestSolutionKMLExporter {

    private static final String BREAK_TAG = "<br/>";

    String exportTitle = "";

    private static final String[] COLORS = { "aa0000", "00aa00", "0000aa", "cc6600", "cc0066", "0099cc", "999900",
	    "003300", "003333", "660099" };

    private static final String URL_RES = "http://maps.google.com/mapfiles/kml/pushpin/blue-pushpin.png";
    private static final String URL_TWGN = "http://maps.google.com/mapfiles/kml/pushpin/ylw-pushpin.png";
    private static final String URL_EVENT = "http://maps.google.com/mapfiles/kml/pushpin/grn-pushpin.png";

    public boolean export(Solution sol, OutputStream fileOutputStream) {

	// Create printWriter
	PrintWriter targetWriter = new PrintWriter(fileOutputStream);

	// Write Header
	RestSolutionKMLExporter.writeKmlHeader(sol, targetWriter, COLORS);

	// Write solution
	List<String> placemarks = RestSolutionKMLExporter.writeSolution(sol, targetWriter, COLORS);

	// Write create placemarks
	RestSolutionKMLExporter.writePlaceMarks(placemarks, targetWriter);

	// Finish document
	this.writeEndDocument(targetWriter);

	return true;
    }

    /**
     * Write header.
     *
     * @param con          the con
     * @param targetWriter the target writer
     * @param colors       the colors
     */
    private static void writeKmlHeader(Solution sol, PrintWriter targetWriter, String[] colors) {

	targetWriter.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
	targetWriter.println("<kml xmlns=\"http://www.opengis.net/kml/2.2\">");
	targetWriter.println("<Document>");

	String title = "";

	Optional<String> identOpt = Optional.ofNullable(sol.getIdent());

	if (identOpt.isPresent()) {
	    title = identOpt.get();
	}

	if (title.isEmpty()) {
	    title = "Export_" + System.currentTimeMillis();
	}

	targetWriter.println("<name>" + title + "</name>");

	for (int i = 0; i < colors.length; i++) {
	    targetWriter.println("<Style id=\"" + "color" + i + "\"><LineStyle><color>ff" + colors[i]
		    + "</color><width>4</width></LineStyle><PolyStyle><color>7f00ff00</color></PolyStyle></Style>");
	}

	targetWriter.println("<Style id=\"DefaultStartTerminationStyle\"><IconStyle><Icon><href>"
		+ RestSolutionKMLExporter.URL_RES + "</href> </Icon></IconStyle> </Style>"
		+ "<Style id=\"DefaultNodeStyle\"><IconStyle><Icon><href>" + RestSolutionKMLExporter.URL_TWGN
		+ "</href></Icon></IconStyle></Style>" + "<Style id=\"DefaultEventNodeStyle\"><IconStyle><Icon><href>"
		+ RestSolutionKMLExporter.URL_EVENT + "</href></Icon></IconStyle></Style>");

	targetWriter.flush();
    }

    private static List<String> writeSolution(Solution sol, PrintWriter targetWriter, String[] colors) {
	List<Route> routes = sol.getRoutes();

	return routes.stream().map(r -> writeRoute(sol,
		// sol.trips(),
		r, targetWriter, colors)).filter(Optional::isPresent).map(Optional::get).flatMap(List::stream)
		.collect(Collectors.toList());
    }

    private static Optional<List<String>> writeRoute(Solution sol,
	    // Optional<PolylineTrips> trips,
	    Route r, PrintWriter targetWriter, String[] colors) {

	List<RouteElementDetail> details = r.getElementDetails();

	Optional<List<ResourceTrip>> resourceTripsOpt = Optional.ofNullable(r.getRouteTrip().getTrips());

	List<String> placeMarkPoints = new ArrayList<>();

	// Write Start
	targetWriter.println("<Placemark> <name>" + r.getStartElementId() + "</name><styleUrl>#color"
		+ (r.getId() % colors.length)
		+ "</styleUrl><LineString> <extrude>1</extrude><tessellate>1</tessellate><altitudeMode>clampToGround </altitudeMode><coordinates>");
	targetWriter.flush();

	Optional<Position> startPosOpt = Optional.ofNullable(r.getStartPosition());

	Position start;

	if (!startPosOpt.isPresent()) {
	    // TODO error
	    return Optional.empty();
	} else {
	    start = startPosOpt.get();
	}

	// In case of events we need to persist this
	Position lastKnowPos = start;

	Optional<String> locationIdOpt = Optional.ofNullable(lastKnowPos.getLocationId());

	String startLocation;

	if (!locationIdOpt.isPresent()) {
	    // TODO error
	    return Optional.empty();
	} else {
	    startLocation = locationIdOpt.get();
	}

	String lastKnowId = startLocation;

	// && ENALBE START!
	// boolean hasPresentTrip = myTripOpt.isPresent();
	//
	// if (!hasPresentTrip) {
	// writeLatLon(targetWriter, lastKnowPos, 10.0);
	// }

	// Create a placemarkPoint for the element
	placeMarkPoints.add(createStartEndPlacemarkPoint(r.getStartElementId(), r.getResourceId(), lastKnowPos,
		"DefaultStartTerminationStyle", true));

	// boolean isFirstElement = true;

	for (RouteElementDetail d : details) {

	    String curId = d.getElementId();

	    Optional<ResourceTrip> curTripOpt = extractResoureTrip(resourceTripsOpt, lastKnowId, curId);

	    Optional<Position> curPosOpt = Optional.ofNullable(d.getEffectivePosition());

	    if (curPosOpt.isPresent()) {

		Position curPos = curPosOpt.get();

		if (curTripOpt.isPresent()) {
		    writeTrip(targetWriter, curTripOpt.get().getLine(), 1000.0);
		} else {
		    writeLatLon(targetWriter, lastKnowPos, 1000.0);
		}

		// if (!hasPresentTrip) {
		// writeLatLon(targetWriter, curPos, 1000.0);
		// }

		placeMarkPoints.add(createPlacemarkPointNode(d, curPos, "DefaultNodeStyle", ""));

		lastKnowPos = curPos;
	    }

	    lastKnowId = curId;
	}

	// End Element
	Optional<Position> terminationPosOpt = Optional.ofNullable(r.getEndPosition());

	if (terminationPosOpt.isPresent()) {
	    String endElementId = r.getEndElementId();

	    Optional<ResourceTrip> curTripOpt = extractResoureTrip(resourceTripsOpt, lastKnowId, endElementId);

	    if (curTripOpt.isPresent()) {
		writeTrip(targetWriter, curTripOpt.get().getLine(), 1000.0);
	    } else {
		writeLatLon(targetWriter, terminationPosOpt.get(), 1000.0);
	    }

	    placeMarkPoints.add(createStartEndPlacemarkPoint(endElementId, r.getResourceId(), terminationPosOpt.get(),
		    "DefaultStartTerminationStyle", false));
	}

	targetWriter.println("</coordinates></LineString></Placemark>");
	targetWriter.flush();

	return Optional.of(placeMarkPoints);
    }

    private static Optional<ResourceTrip> extractResoureTrip(Optional<List<ResourceTrip>> resourceTripsOpt,
	    String targetFromId, String targetToId) {
	Optional<ResourceTrip> curTripOpt = Optional.empty();

	if (resourceTripsOpt.isPresent()) {
	    // Try extract
	    curTripOpt = resourceTripsOpt.get().stream()
		    .filter(rt -> rt.getFromElementId().equals(targetFromId) && rt.getToElementId().equals(targetToId))
		    .findAny();
	}

	return curTripOpt;
    }

    private static void writeTrip(PrintWriter targetWriter, EncodedPolyline trip, double alt) {

	List<Position> poss = decodePolyline(trip.getEncodedPolyline(), 1E6);

	poss.forEach(p -> targetWriter.println(p.getLongitude() + "," + p.getLatitude() + "," + alt));

	targetWriter.flush();
    }

    private static void writeLatLon(PrintWriter targetWriter, Position lastKnowPos, double alt) {
	targetWriter.println(lastKnowPos.getLongitude() + "," + lastKnowPos.getLatitude() + "," + alt);
	targetWriter.flush();
    }

    private static String createStartEndPlacemarkPoint(String elementId, String visitorId, Position curPos,
	    String style, boolean isRouteStart) {

	String elementIdPrefix = "";

	if (isRouteStart) {
	    elementIdPrefix = "Start - ";
	}

	if (!isRouteStart) {
	    elementIdPrefix = "End - ";
	}

	String userStyle = "";
	String iconStyle = "<styleUrl>#" + style + "</styleUrl> ";

	String desc = "";

	if (!elementId.equals(visitorId)) {
	    desc = "<description>" + BREAK_TAG + "Visting Resource: " + forHTML(visitorId) + "</description>";
	}

	return userStyle + "<Placemark><name>" + forHTML(elementIdPrefix + elementId) + "</name> " + iconStyle + desc
		+ "<Point><coordinates>" + curPos.getLongitude() + "," + curPos.getLatitude() + ",10"
		+ "</coordinates></Point></Placemark>";
    }

    private static String createPlacemarkPointNode(RouteElementDetail detail, Position curPos, String style,
	    String elementIdPrefix) {

	// Create placepoint
	String xmlString = "";
	String desc = "";

	StringBuilder vioDescSB = new StringBuilder();

	Optional<List<Violation>> viosOpt = Optional.ofNullable(detail.getNodeViolations());

	if (viosOpt.isPresent()) {
	    List<Violation> vios = viosOpt.get();

	    if (vios.isEmpty()) {
		vios.forEach(v -> vioDescSB.append("NO VIOLATIONS"));
	    } else {
		vios.forEach(v -> vioDescSB.append("\n" + v));
	    }
	}

	// TODO DESC

	String userStyle = "";
	String iconStyle = "<styleUrl>#" + style + "</styleUrl> ";

	xmlString = userStyle + "<Placemark><name>" + forHTML(elementIdPrefix + detail.getElementId()) + "</name>"
		+ iconStyle + desc + "<Point><coordinates>" + curPos.getLongitude() + "," + curPos.getLatitude() + ",10"
		+ "</coordinates></Point></Placemark>";

	return xmlString;
    }

    private static void writePlaceMarks(List<String> points, PrintWriter targetWriter) {

	StringBuilder placeMarPointsSB = new StringBuilder();

	for (String curPlaceMarkPoint : points) {
	    placeMarPointsSB.append("\n" + curPlaceMarkPoint);
	}

	targetWriter.println(placeMarPointsSB.toString());
	targetWriter.flush();
    }

    private void writeEndDocument(PrintWriter targetWriter) {

	targetWriter.println("</Document></kml>");
	targetWriter.flush();
    }

    /*
     * Helpers for right html
     */
    private static String forHTML(String aText) {
	final StringBuilder result = new StringBuilder();
	final StringCharacterIterator iterator = new StringCharacterIterator(aText);
	char character = iterator.current();

	while (character != CharacterIterator.DONE) {
	    if (character == '<') {
		result.append("&lt;");
	    } else if (character == '>') {
		result.append("&gt;");
	    } else if (character == '&') {
		result.append("&amp;");
	    } else if (character == '\"') {
		result.append("&quot;");
	    } else if (character == '\t') {
		addCharEntity(9, result);
	    } else if (character == '!') {
		addCharEntity(33, result);
	    } else if (character == '#') {
		addCharEntity(35, result);
	    } else if (character == '$') {
		addCharEntity(36, result);
	    } else if (character == '%') {
		addCharEntity(37, result);
	    } else if (character == '\'') {
		addCharEntity(39, result);
	    } else if (character == '(') {
		addCharEntity(40, result);
	    } else if (character == ')') {
		addCharEntity(41, result);
	    } else if (character == '*') {
		addCharEntity(42, result);
	    } else if (character == '+') {
		addCharEntity(43, result);
	    } else if (character == ',') {
		addCharEntity(44, result);
	    } else if (character == '-') {
		addCharEntity(45, result);
	    } else if (character == '.') {
		addCharEntity(46, result);
	    } else if (character == '/') {
		addCharEntity(47, result);
	    } else if (character == ':') {
		addCharEntity(58, result);
	    } else if (character == ';') {
		addCharEntity(59, result);
	    } else if (character == '=') {
		addCharEntity(61, result);
	    } else if (character == '?') {
		addCharEntity(63, result);
	    } else if (character == '@') {
		addCharEntity(64, result);
	    } else if (character == '[') {
		addCharEntity(91, result);
	    } else if (character == '\\') {
		addCharEntity(92, result);
	    } else if (character == ']') {
		addCharEntity(93, result);
	    } else if (character == '^') {
		addCharEntity(94, result);
	    } else if (character == '_') {
		addCharEntity(95, result);
	    } else if (character == '`') {
		addCharEntity(96, result);
	    } else if (character == '{') {
		addCharEntity(123, result);
	    } else if (character == '|') {
		addCharEntity(124, result);
	    } else if (character == '}') {
		addCharEntity(125, result);
	    } else if (character == '~') {
		addCharEntity(126, result);
	    } else {
		// the char is not a special one
		// add it to the result as is
		result.append(character);
	    }
	    character = iterator.next();
	}
	return result.toString();
    }

    private static void addCharEntity(Integer aIdx, StringBuilder aBuilder) {
	String padding = "";
	if (aIdx <= 9) {
	    padding = "00";
	} else if (aIdx <= 99) {
	    padding = "0";
	} else {
	    // no prefix
	}
	String number = padding + aIdx.toString();
	aBuilder.append("&#" + number + ";");
    }

    private static List<Position> decodePolyline(String encoded, double prec) {

	List<Position> poly = new ArrayList<>();
	int index = 0;
	int len = encoded.length();
	int lat = 0;
	int lng = 0;

	while (index < len) {

	    int b;
	    int shift = 0;
	    int result = 0;

	    do {
		b = encoded.charAt(index++) - 63;
		result |= (b & 0x1f) << shift;
		shift += 5;
	    } while (b >= 0x20);
	    int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
	    lat += dlat;

	    shift = 0;
	    result = 0;
	    do {
		b = encoded.charAt(index++) - 63;
		result |= (b & 0x1f) << shift;
		shift += 5;
	    } while (b >= 0x20);

	    int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
	    lng += dlng;

	    Position pos = new Position();
	    pos.setLatitude((double) lat / prec);
	    pos.setLongitude((double) lng / prec);

	    poly.add(pos);
	}

	return poly;
    }
}
