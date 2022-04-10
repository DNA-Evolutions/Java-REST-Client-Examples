package com.dna.jopt.rest.client.util.io.export.kml;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.List;

import com.dna.jopt.rest.client.model.EncodedPolyline;
import com.dna.jopt.rest.client.model.Position;


public class KMLPolyLineExporter {

    private static final String URL_RES = "http://maps.google.com/mapfiles/kml/pushpin/blue-pushpin.png";
    private static final String URL_TWGN = "http://maps.google.com/mapfiles/kml/pushpin/ylw-pushpin.png";
    private static final String URL_EVENT = "http://maps.google.com/mapfiles/kml/pushpin/grn-pushpin.png";

    private static final double KML_ALTITUDE = 1000.0;

    private static final String BREAK_TAG = "<br/>";

    private static final String[] COLORS = { "aa0000", "00aa00", "0000aa", "cc6600", "cc0066", "0099cc", "999900",
	    "003300", "003333", "660099" };

    private KMLPolyLineExporter() {
	// Nothing to do
    }

    public static void exportKML(OutputStream fileOutputStream, String title, List<String> shapes,
	    List<String> shapeTitles) {

	if (shapes.size() != shapeTitles.size()) {
	    throw new IllegalStateException("Each shape must have exactly one title");
	}

	// Create printWriter
	PrintWriter targetWriter = new PrintWriter(fileOutputStream);

	// Write Header
	writeKmlHeader(targetWriter, title, COLORS);

	List<String> placeMarkPoints = new ArrayList<>();
	for (int ii = 0; ii < shapes.size(); ii++) {
	    placeMarkPoints.addAll(
		    writeTrip(targetWriter, toEncodedPolyline(shapes.get(ii)), shapeTitles.get(ii), KML_ALTITUDE, ii));
	}

	writePlaceMarks(placeMarkPoints, targetWriter);

	writeEndDocument(targetWriter);

    }

    private static void writePlaceMarks(List<String> points, PrintWriter targetWriter) {

	StringBuilder placeMarPointsSB = new StringBuilder();

	for (String curPlaceMarkPoint : points) {
	    placeMarPointsSB.append("\n" + curPlaceMarkPoint);
	}

	targetWriter.println(placeMarPointsSB.toString());
	targetWriter.flush();
    }

    private static EncodedPolyline toEncodedPolyline(String shape) {
	EncodedPolyline polyline = new EncodedPolyline();
	polyline.setEncodedPolyline(shape);
	polyline.setPrecision(1E6);

	return polyline;
    }

    private static void writeKmlHeader(PrintWriter targetWriter, String ident, String[] colors) {

	targetWriter.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
	targetWriter.println("<kml xmlns=\"http://www.opengis.net/kml/2.2\">");
	targetWriter.println("<Document>");

	String title = "Export_" + ident + "_" + System.currentTimeMillis();

	targetWriter.println("<name>" + title + "</name>");

	for (int i = 0; i < colors.length; i++) {
	    targetWriter.println("<Style id=\"" + "color" + i + "\"><LineStyle><color>ff" + colors[i]
		    + "</color><width>4</width></LineStyle><PolyStyle><color>7f00ff00</color></PolyStyle></Style>");
	}

	targetWriter.println("<Style id=\"DefaultStartTerminationStyle\"><IconStyle><Icon><href>" + URL_RES
		+ "</href> </Icon></IconStyle> </Style>" + "<Style id=\"DefaultNodeStyle\"><IconStyle><Icon><href>"
		+ URL_TWGN + "</href></Icon></IconStyle></Style>"
		+ "<Style id=\"DefaultEventNodeStyle\"><IconStyle><Icon><href>" + URL_EVENT
		+ "</href></Icon></IconStyle></Style>");

	targetWriter.flush();
    }

    private static List<String> writeTrip(PrintWriter targetWriter, EncodedPolyline trip, String tripTitle, double alt,
	    int index) {

	List<String> placeMarkPoints = new ArrayList<>();

	List<Position> poss = decodePolyline(trip.getEncodedPolyline(), 1E6);

	if (poss.isEmpty()) {
	    return placeMarkPoints;
	}

	Position start = poss.get(0);

	// Write Start
	targetWriter.println("<Placemark> <name>" + tripTitle + "</name><styleUrl>#color" + (index % COLORS.length)
		+ "</styleUrl><LineString> <extrude>1</extrude><tessellate>1</tessellate><altitudeMode>clampToGround </altitudeMode><coordinates>");
	targetWriter.flush();

	placeMarkPoints.add(createStartEndPlacemarkPoint(tripTitle, "Member of " + tripTitle, start,
		"DefaultStartTerminationStyle", true));

	if (poss.size() > 1) {
	    Position end = poss.get(poss.size() - 1);

	    placeMarkPoints.add(createStartEndPlacemarkPoint(tripTitle, "Member of " + tripTitle, end,
		    "DefaultStartTerminationStyle", false));
	}

	poss.forEach(p -> targetWriter.println(p.getLongitude() + "," + p.getLatitude() + "," + alt));

	targetWriter.println("</coordinates></LineString></Placemark>");
	targetWriter.flush();

	return placeMarkPoints;
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
	    desc = "<description>" + BREAK_TAG + forHTML(visitorId) + "</description>";
	}

	return userStyle + "<Placemark><name>" + forHTML(elementIdPrefix + elementId) + "</name> " + iconStyle + desc
		+ "<Point><coordinates>" + curPos.getLongitude() + "," + curPos.getLatitude() + ",10"
		+ "</coordinates></Point></Placemark>";
    }

    private static void writeEndDocument(PrintWriter targetWriter) {

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
