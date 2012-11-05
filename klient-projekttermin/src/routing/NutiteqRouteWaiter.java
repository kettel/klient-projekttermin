package routing;

import android.graphics.Bitmap;
import com.nutiteq.BasicMapComponent;
import com.nutiteq.components.Line;
import com.nutiteq.components.LineStyle;
import com.nutiteq.components.Place;
import com.nutiteq.components.PlaceLabel;
import com.nutiteq.components.Route;
import com.nutiteq.components.WgsPoint;
import com.nutiteq.log.Log;
import com.nutiteq.services.DirectionsService;
import com.nutiteq.services.DirectionsWaiter;
import com.nutiteq.wrappers.Image;

public class NutiteqRouteWaiter implements DirectionsWaiter {
	public static NutiteqRouteWaiter instance;
	private WgsPoint startCoordinates;
	private WgsPoint endCoordinates;
	private Thread starter;
	private int routingService;
	private BasicMapComponent map;

	public NutiteqRouteWaiter(WgsPoint startCoordinates, WgsPoint endCoordinates, BasicMapComponent map, Bitmap start, Bitmap dest) {
		instance = this;
		this.startCoordinates = startCoordinates;
		this.endCoordinates = endCoordinates;
		this.map = map;
		Image st = Image.createImage(start);
		Image end = Image.createImage(dest);
		PlaceLabel poiLabel = new PlaceLabel("START");
		PlaceLabel poiLabel2 = new PlaceLabel("DESTINATION");
		Place startMarker = new Place(1, poiLabel, st, startCoordinates);
		Place destinationMarker = new Place(1, poiLabel2, end, endCoordinates);
		this.map.addPlace(startMarker);
		this.map.addPlace(destinationMarker);
		starter = new Thread(new Routingstarter(this));
		starter.start();
	}

	public void routeFound(Route route) {
		Line routeLine = route.getRouteLine();
		int[] lineColors = { 0xFF0000FF, 0xFF00FF00 };
		routeLine.setStyle(new LineStyle(lineColors[routingService], 5));
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
