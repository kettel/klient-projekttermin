package routing;

import map.MapApplication;

import com.nutiteq.BasicMapComponent;
import com.nutiteq.components.Line;
import com.nutiteq.components.LineStyle;
import com.nutiteq.components.Route;
import com.nutiteq.components.WgsPoint;
import com.nutiteq.log.Log;
import com.nutiteq.services.DirectionsService;
import com.nutiteq.services.DirectionsWaiter;

public class NutiteqRouteWaiter implements DirectionsWaiter {
	public static NutiteqRouteWaiter instance;
	private WgsPoint startCoordinates;
	private WgsPoint endCoordinates;
	private Thread starter;
	private int routingService;
//	private MapApplication app;
	private BasicMapComponent map;

	public NutiteqRouteWaiter(WgsPoint startCoordinates, WgsPoint endCoordinates, MapApplication app, BasicMapComponent map) {
		instance = this;
		this.startCoordinates = startCoordinates;
		this.endCoordinates = endCoordinates;
//		this.app = app;
		this.map = map;
		starter = new Thread(new Routingstarter(this, app));
		starter.start();
	}

	public void routeFound(Route route) {
//		app.setRoute(route);
		// pass route to Application

		// add route as line to map
		Line routeLine = route.getRouteLine();
		int[] lineColors = { 0xFF0000FF, 0xFF00FF00 };

		routeLine.setStyle(new LineStyle(lineColors[routingService], 2));

		map.addLine(routeLine);
	}

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
