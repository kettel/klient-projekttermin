package routing;

import com.nutiteq.services.YourNavigationDirections;

/**
 * Startar en ruttberäkning utifrån nutiteqwatier:ns egenskaper
 * 
 * @author nicklas
 *
 */
public class Routingstarter implements Runnable {
	private final NutiteqRouteWaiter nutiteqRouteWaiter;

	public Routingstarter(NutiteqRouteWaiter nutiteqRouteWaiter) {
		this.nutiteqRouteWaiter =nutiteqRouteWaiter;
	}

	/**
	 * Kör en ruttberäkning
	 */
	public void run() {
		new YourNavigationDirections(nutiteqRouteWaiter,
				nutiteqRouteWaiter.getStartCoordinates(),
				nutiteqRouteWaiter.getEndCoordinates(),
				YourNavigationDirections.MOVE_METHOD_CAR,
				YourNavigationDirections.ROUTE_TYPE_FASTEST).execute();

	}
}