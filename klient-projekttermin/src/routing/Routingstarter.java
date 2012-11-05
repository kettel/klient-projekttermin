package routing;

import map.MapApplication;

import com.nutiteq.services.YourNavigationDirections;

public class Routingstarter implements Runnable {
	private final NutiteqRouteWaiter nutiteqRouteWaiter;
//	private MapApplication app;

	public Routingstarter(NutiteqRouteWaiter nutiteqRouteWaiter, MapApplication app) {
		this.nutiteqRouteWaiter =nutiteqRouteWaiter;
//		this.app = app;
	}

	public void run() {

		new YourNavigationDirections(nutiteqRouteWaiter,
				nutiteqRouteWaiter.getStartCoordinates(),
				nutiteqRouteWaiter.getEndCoordinates(),
				YourNavigationDirections.MOVE_METHOD_CAR,
				YourNavigationDirections.ROUTE_TYPE_FASTEST).execute();

	}
}