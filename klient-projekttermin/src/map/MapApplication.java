package map;

import com.nutiteq.BasicMapComponent;
import com.nutiteq.components.Place;
import com.nutiteq.components.Route;
import com.nutiteq.components.WgsPoint;
import com.nutiteq.location.NutiteqLocationMarker;
import com.nutiteq.ui.SimpleScaleBar;
import com.nutiteq.ui.ThreadDrivenPanning;
import com.nutiteq.wrappers.AppContext;

public class MapApplication {

	private Route route;
	private NutiteqLocationMarker gpsMarker;
	public Place gridLabelPlace;
//	private static final WgsPoint DEFAULT_MIDDLE_POINT = new WgsPoint(4.890935,
//			52.373801); // amsterdam 4.890935, 52.373801 // Tallinn 24.634772,
//						// 59.367196 // London -0.62, 51.6
//	private static final int DEFAULT_ZOOM = 7;

	public MapApplication() {
//		mapComponent = new BasicMapComponent("Map", new AppContext(this), 1, 1,
//				DEFAULT_MIDDLE_POINT, DEFAULT_ZOOM);
//		mapComponent.setPanningStrategy(new ThreadDrivenPanning());
//		mapComponent.setTouchClickTolerance(80);
//		mapComponent.setSmoothZoom(true);
//		mapComponent.setDoubleClickZoomIn(true);
//		mapComponent.setDualClickZoomOut(true);
//		mapComponent.setShowOverlaysWhileZooming(false);
//		
//		SimpleScaleBar scaleBar = new SimpleScaleBar();
//		scaleBar.setAlignment(SimpleScaleBar.BOTTOM_LEFT);
//		scaleBar.setOffset(20, 40);
//		mapComponent.setScaleBar(scaleBar);
//		mapComponent.startMapping();

	}

	public MapApplication getMapApplication() {
		return this;
	}

	public Route getRoute() {
		return route;
	}

	public void setRoute(Route route) {
		this.route = route;
	}

	public NutiteqLocationMarker getGpsMarker() {
		return gpsMarker;
	}

	public void setGpsMarker(NutiteqLocationMarker gpsMarker) {
		this.gpsMarker = gpsMarker;
	}

}
