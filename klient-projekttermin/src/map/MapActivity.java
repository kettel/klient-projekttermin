package map;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import routing.NutiteqRouteWaiter;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.ZoomControls;

import com.example.klien_projekttermin.R;
import com.nutiteq.BasicMapComponent;
import com.nutiteq.android.MapView;
import com.nutiteq.components.Place;
import com.nutiteq.components.PlaceIcon;
import com.nutiteq.components.PlaceLabel;
import com.nutiteq.components.Polygon;
import com.nutiteq.components.WgsPoint;
import com.nutiteq.listeners.MapListener;
import com.nutiteq.location.LocationMarker;
import com.nutiteq.location.LocationSource;
import com.nutiteq.location.NutiteqLocationMarker;
import com.nutiteq.location.providers.AndroidGPSProvider;
import com.nutiteq.maps.OpenStreetMap;
import com.nutiteq.ui.ThreadDrivenPanning;
import com.nutiteq.wrappers.AppContext;
import com.nutiteq.wrappers.Image;

public class MapActivity extends Activity implements Observer,
		SearchView.OnCloseListener, SearchView.OnQueryTextListener, MapListener {

	private BasicMapComponent mapComponent;
	private String[] from = { "line1", "line2" };
	private int[] to = { android.R.id.text1, android.R.id.text2 };
	private ListView lv;
	private SearchSuggestions searchSuggestions = new SearchSuggestions();
	private SimpleAdapter sm;
	private MapView mapView;
	private ZoomControls zoomControls;
	private SearchView searchView;
	private Bitmap icon;
	private Bitmap icon2;

	private final WgsPoint LINKÖPING = new WgsPoint(15.5826, 58.427);
	private final WgsPoint STHLM = new WgsPoint(18.07, 59.33);
	private boolean isInAddMode = false;
	private boolean gpsOnOff = true;
	private ArrayList<WgsPoint> points = new ArrayList<WgsPoint>();
	private ArrayList<Place> regionCorners = new ArrayList<Place>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		icon = BitmapFactory.decodeResource(getResources(), R.drawable.pin);
		icon2 = BitmapFactory.decodeResource(getResources(),
				R.drawable.pin_green);

		setContentView(R.layout.activity_map);
		this.mapComponent = new BasicMapComponent("tutorial", new AppContext(
				this), 1, 1, LINKÖPING, 10);
		navigateToLocation(LINKÖPING, mapComponent);
		this.mapComponent.setMap(OpenStreetMap.MAPNIK);
		this.mapComponent.setPanningStrategy(new ThreadDrivenPanning());
		this.mapComponent.startMapping();
		this.mapComponent.setMapListener(this);

		// get the mapview that was defined in main.xml
		// mapview requires a mapcomponent
		this.lv = (ListView) findViewById(R.id.mylist);
		this.sm = new SimpleAdapter(this, searchSuggestions.getList(),
				android.R.layout.simple_list_item_2, from, to);
		this.lv.setAdapter(sm);
		this.mapView = (MapView) findViewById(R.id.mapview);
		this.mapView.setMapComponent(mapComponent);
		this.searchSuggestions.addObserver(this);

		this.zoomControls = (ZoomControls) findViewById(R.id.zoomcontrols);
		this.zoomControls.setOnZoomInClickListener(new View.OnClickListener() {
			public void onClick(final View v) {
				mapComponent.zoomIn();
			}
		});
		this.zoomControls.setOnZoomOutClickListener(new View.OnClickListener() {
			public void onClick(final View v) {
				mapComponent.zoomOut();
			}
		});
		System.out.println(gpsOnOff);
		activateGPS(gpsOnOff);
	}

	/**
	 * Aktiverar GPS:en
	 */
	private void activateGPS(boolean on) {
		if (on) {
			final LocationSource locationSource = new AndroidGPSProvider(
					(LocationManager) getSystemService(Context.LOCATION_SERVICE),
					1000L);
			final LocationMarker marker = new NutiteqLocationMarker(
					new PlaceIcon(Image.createImage(icon), icon.getWidth() / 2,
							icon.getHeight()), 3000, true);
			locationSource.setLocationMarker(marker);
			mapComponent.setLocationSource(locationSource);
		} else {
			mapComponent.setLocationSource(null);
		}
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		View v=(View)menu.findItem(R.id.menu_search);
		AutoCompleteTextView actv=(AutoCompleteTextView)v.findViewById(R.id.ab_Search);

		return super.onCreateOptionsMenu(menu);
	}

	public boolean onQueryTextChange(String newText) {
		final String str = newText;
		new Thread(new Runnable() {

			public void run() {
				// TODO Auto-generated method stub
				searchSuggestions.updateSearch(str);
			}
		}).start();
		return true;
	}

	public boolean onQueryTextSubmit(String query) {
		//searchView.setVisibility(SearchView.GONE);
		final String str = query;
		new Thread(new Runnable() {

			public void run() {
				// TODO Auto-generated method stub
				searchSuggestions.updateSearch(str);
			}
		}).start();
		return true;
	}

	public void navigateToLocation(WgsPoint destination, BasicMapComponent map) {
		new NutiteqRouteWaiter(destination, STHLM, map, icon, icon2);
	}

	public void addInterestPoint(WgsPoint pointLocation, String label) {

		PlaceLabel poiLabel = new PlaceLabel(label);
		Image poiImage = Image.createImage(icon);
		Place p = new Place(1, poiLabel, poiImage, pointLocation);
		mapComponent.addPlace(p);
	}

	public void changeAddRegionMode(MenuItem m) {
		isInAddMode = !isInAddMode;
		if (isInAddMode) {
			m.setTitle("active");
			points.clear();
		} else {
			m.setTitle("mode");
			if (!points.isEmpty()) {
				WgsPoint[] p = (WgsPoint[]) points.toArray(new WgsPoint[points
						.size()]);
				mapComponent.addPolygon(new Polygon(p));
			}
			if (!regionCorners.isEmpty()) {
				Place[] corners = (Place[]) regionCorners
						.toArray(new Place[regionCorners.size()]);
				mapComponent.removePlaces(corners);
			}
		}
	}

	public void gpsStatus(MenuItem m) {
		System.out.println("GPS");
		if (gpsOnOff) {
			gpsOnOff = false;
			m.setTitle("GPS/off");
			activateGPS(gpsOnOff);
		} else {
			gpsOnOff = true;
			m.setTitle("GPS/on");
			activateGPS(gpsOnOff);
		}
	}

	public void update(Observable observable, Object data) {
		if (lv.getVisibility()==ListView.GONE) {
			this.lv.setVisibility(ListView.VISIBLE);
			this.mapView.setVisibility(MapView.GONE);
			this.zoomControls.setVisibility(ZoomControls.GONE);
			this.searchView.setVisibility(SearchView.VISIBLE);
		}
		System.out.println("observed");
		this.sm.notifyDataSetChanged();
	}

	public boolean onClose() {
		System.out.println("ONCLOSE");
		lv.setVisibility(ListView.GONE);
		mapView.setVisibility(MapView.VISIBLE);
		zoomControls.setVisibility(ZoomControls.VISIBLE);
		return false;
	}

	public void mapClicked(WgsPoint arg0) {
		// TODO Auto-generated method stub
		if (isInAddMode) {
			points.add(arg0);
			Image poiImage = Image.createImage(icon);
			Place temp = new Place(1, "dummy", poiImage, arg0);
			mapComponent.addPlace(temp);
			regionCorners.add(temp);
		}
	}

	public void mapMoved() {
		// TODO Auto-generated method stub
	}

	public void needRepaint(boolean arg0) {
		// TODO Auto-generated method stub
	}

}
