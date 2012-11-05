package routing;

import com.nutiteq.services.YourNavigationDirections;


public class Routingstarter implements Runnable {
	private final NutiteqRouteWaiter nutiteqRouteWaiter;
//	private MapApplication app;

	public Routingstarter(NutiteqRouteWaiter nutiteqRouteWaiter) {
		this.nutiteqRouteWaiter =nutiteqRouteWaiter;
	}

	public void run() {

		System.out.println("RUN IN ROUTIGN STARTER");
		new YourNavigationDirections(nutiteqRouteWaiter,
				nutiteqRouteWaiter.getStartCoordinates(),
				nutiteqRouteWaiter.getEndCoordinates(),
				YourNavigationDirections.MOVE_METHOD_CAR,
				YourNavigationDirections.ROUTE_TYPE_FASTEST).execute();

	}
}