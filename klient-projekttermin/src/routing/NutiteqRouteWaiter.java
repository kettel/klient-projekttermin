package routing;

import com.nutiteq.BasicMapComponent;
import com.nutiteq.components.Line;
import com.nutiteq.components.LineStyle;
import com.nutiteq.components.Place;
import com.nutiteq.components.Route;
import com.nutiteq.components.WgsPoint;
import com.nutiteq.log.Log;
import com.nutiteq.services.DirectionsService;
import com.nutiteq.services.DirectionsWaiter;
import com.nutiteq.wrappers.Image;

/**
 * Förser Routingstarter med nödvändig information för att starta en ruttberäkning.
 * 
 * @author nicklas
 *
 */
public class NutiteqRouteWaiter implements DirectionsWaiter {
	public static NutiteqRouteWaiter instance;
	private WgsPoint startCoordinates;
	private WgsPoint endCoordinates;
	private Thread starter;
	private int routingService;
	private BasicMapComponent map;

	/**
	 * Läser in start- och slutkoordinater för rutten. Markerar dessa punkter på kartan.
	 * Säger åt Routingstarter att starta en ruttberäkning i en ny tråd.
	 * 
	 * @param startCoordinates
	 * @param endCoordinates
	 * @param map
	 * @param start
	 * @param dest
	 */
	public NutiteqRouteWaiter(WgsPoint startCoordinates, WgsPoint endCoordinates, BasicMapComponent map, Image start, Image dest) {
		instance = this;
		this.startCoordinates = startCoordinates;
		this.endCoordinates = endCoordinates;
		this.map = map;
		Place startMarker = new Place(1, "START", start, startCoordinates);
		Place destinationMarker = new Place(1, "END", dest, endCoordinates);
		this.map.addPlace(startMarker);
		this.map.addPlace(destinationMarker);
		starter = new Thread(new Routingstarter(this));
		starter.start();
	}

	/**
	 * När en rutt är funnen rita ut den på kartan. 
	 */
	public void routeFound(Route route) {
		Line routeLine = route.getRouteLine();
		int[] lineColors = { 0xFF0000FF, 0xFF00FF00 };
		routeLine.setStyle(new LineStyle(lineColors[routingService], 5));
		map.addLine(routeLine);
	}

	/**
	 * Skriver ut vilket fel som uppstått
	 */
	public void routingErrors(final int errorCodes) {
		final StringBuffer errors = new StringBuffer("Errors: ");
		if ((errorCodes & DirectionsService.ERROR_DESTINATION_ADDRESS_NOT_FOUND) != 0) {
			errors.append("destination not found,");
		}
		if ((errorCodes & DirectionsService.ERROR_FROM_ADDRESS_NOT_FOUND) != 0) {
			errors.append("from not found,");
		}
		if ((errorCodes & DirectionsService.ERROR_FROM_AND_DESTINATION_ADDRESS_SAME) != 0) {
			errors.append("from and destination same,");
		}
		if ((errorCodes & DirectionsService.ERROR_ROUTE_NOT_FOUND) != 0) {
			errors.append("route not found,");
		}
		Log.error(errors.toString());
	}

	public void routingParsingError(String arg0) {
	}

	public void setDirectionsService(final DirectionsService directions) {
		directions.execute();
	}

	public void initialize() {
	}

	public WgsPoint getStartCoordinates() {
		return startCoordinates;
	}

	public WgsPoint getEndCoordinates() {
		return endCoordinates;
	}

	public void networkError() {
	}

}
