package map;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import routing.NutiteqRouteWaiter;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
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
import com.nutiteq.utils.Utils;
import com.nutiteq.wrappers.AppContext;
import com.nutiteq.wrappers.Image;

/**
 * En aktivitet som skapar en karta med en meny där de olika alternativen för
 * kartan finns
 * 
 * @author nicklas
 * 
 */
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
	private final WgsPoint LINKÖPING = new WgsPoint(15.5826, 58.427);
	private final WgsPoint STHLM = new WgsPoint(18.07, 59.33);
	private boolean isInAddMode = false;
	private boolean gpsOnOff = true;
	private ArrayList<WgsPoint> points = new ArrayList<WgsPoint>();
	private ArrayList<Place> regionCorners = new ArrayList<Place>();
	private static Image[] icons = { Utils.createImage("/res/drawable-hdpi/pin.png"),
			Utils.createImage("/res/drawable-hdpi/pin_green.png"),
			Utils.createImage("/res/drawable-hdpi/pos_arrow_liten.png") };

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/**
		 * Sätter inställningar för kartan, samt lägger till en lyssnare.
		 */
		this.setContentView(R.layout.activity_map);
		this.mapComponent = new BasicMapComponent("tutorial", new AppContext(
				this), 1, 1, LINKÖPING, 10);
		navigateToLocation(LINKÖPING, mapComponent);
		this.mapComponent.setMap(OpenStreetMap.MAPNIK);
		this.mapComponent.setPanningStrategy(new ThreadDrivenPanning());
		this.mapComponent.startMapping();
		this.mapComponent.setMapListener(this);

		/**
		 * Hämtar listview till sökförslagen samt lägger till en adapter. Lägger
		 * till en observer på searchSuggestions
		 */
		this.lv = (ListView) findViewById(R.id.mylist);
		this.sm = new SimpleAdapter(this, searchSuggestions.getList(),
				android.R.layout.simple_list_item_2, from, to);
		this.lv.setAdapter(sm);
		this.searchSuggestions.addObserver(this);
		/**
		 * Hämtar mapview och lägger till kartan i den
		 */
		this.mapView = (MapView) findViewById(R.id.mapview);
		this.mapView.setMapComponent(mapComponent);

		/**
		 * Lägger till zoom funktionalitet till kartan
		 */
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
		/**
		 * GPS:en ska vara påslagen vid start
		 */
		activateGPS(gpsOnOff);
	}

	/**
	 * Aktiverar GPS:en
	 */
	private void activateGPS(boolean on) {
		/**
		 * Om GPS är på, hämta position sen placera en ikon på positionen
		 */
		if (on) {
			final LocationSource locationSource = new AndroidGPSProvider(
					(LocationManager) getSystemService(Context.LOCATION_SERVICE),
					1000L);
			final LocationMarker marker = new NutiteqLocationMarker(
					new PlaceIcon(icons[0], icons[0].getWidth() / 2,
							icons[0].getHeight()), 3000, true);
			locationSource.setLocationMarker(marker);
			mapComponent.setLocationSource(locationSource);
		} else {
			mapComponent.setLocationSource(null);
		}
	}

	/**
	 * Skapa meny i actionbar
	 */
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);

		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		searchView = (SearchView) menu.findItem(R.id.menu_search)
				.getActionView();
		searchView.setSearchableInfo(searchManager
				.getSearchableInfo(getComponentName()));
		/**
		 * Lägg till lyssnare som lyssnar på sökfältet.
		 */
		searchView.setOnCloseListener(this);
		searchView.setOnQueryTextListener(this);

		return super.onCreateOptionsMenu(menu);
	}

	/**
	 * Uppdaterar sökförslagen
	 */
	public boolean onQueryTextChange(String newText) {
		searchSuggestions.updateSearch(newText);
		return true;
	}

	/**
	 * Vid sökning dölj allt förutom listview:n med sökresultat.
	 */
	public boolean onQueryTextSubmit(String query) {
		searchView.setVisibility(SearchView.GONE);
		searchSuggestions.updateSearch(query);
		lv.setVisibility(ListView.VISIBLE);
		mapView.setVisibility(MapView.GONE);
		zoomControls.setVisibility(ZoomControls.GONE);
		searchView.setVisibility(SearchView.VISIBLE);
		return true;
	}

	/**
	 * Starta navigation till destination
	 * 
	 * @param destination
	 * @param map
	 */
	public void navigateToLocation(WgsPoint destination, BasicMapComponent map) {
		new NutiteqRouteWaiter(destination, STHLM, map, icons[2], icons[2]);
	}

	/**
	 * Markera en punkt på kartan
	 * 
	 * @param pointLocation
	 * @param label
	 */
	public void addInterestPoint(WgsPoint pointLocation, String label) {
		PlaceLabel poiLabel = new PlaceLabel(label);
		Place p = new Place(1, poiLabel, icons[2], pointLocation);
		mapComponent.addPlace(p);
	}

	/**
	 * Ändrar tillståndet för att markera regioner på kartan
	 * 
	 * @param m
	 */
	public void changeAddRegionMode(MenuItem m) {
		isInAddMode = !isInAddMode;
		/**
		 * När klar med markering nollställ listan med punkter
		 */
		if (isInAddMode) {
			m.setTitle("Klar med markering");
			points.clear();
		}
		/**
		 * Skapa punkter på kartan som användaren vill markera
		 */
		else {
			m.setTitle("Markera region");
			if (!points.isEmpty()) {
				WgsPoint[] p = (WgsPoint[]) points.toArray(new WgsPoint[points
						.size()]);
				mapComponent.addPolygon(new Polygon(p));
			}
			/**
			 * Tar bort punkterna från kartan
			 */
			if (!regionCorners.isEmpty()) {
				Place[] corners = (Place[]) regionCorners
						.toArray(new Place[regionCorners.size()]);
				mapComponent.removePlaces(corners);
			}
		}
	}

	/**
	 * Ändrar tillstånd för GPS:en
	 * 
	 * @param m
	 */
	public void gpsStatus(MenuItem m) {
		if (gpsOnOff) {
			m.setTitle("GPS/off");
			activateGPS(gpsOnOff);
		} else {
			m.setTitle("GPS/on");
			activateGPS(gpsOnOff);
		}
		gpsOnOff = !gpsOnOff;
	}

	/**
	 * Notifierar sökresultatens listviews adapter om att sökresultaten har
	 * ändrats
	 */
	public void update(Observable observable, Object data) {
		sm.notifyDataSetChanged();
		sm.notifyDataSetInvalidated();
	}

	/**
	 * När sökningsview stängs dölj listview och visa kartan med zoom
	 * kontrollerna
	 */
	public boolean onClose() {
		lv.setVisibility(ListView.GONE);
		mapView.setVisibility(MapView.VISIBLE);
		zoomControls.setVisibility(ZoomControls.VISIBLE);
		return false;
	}

	/**
	 * Om i markeringsläge lägg till de markerade punkterna i points array, samt
	 * på kartan och även som hörn i regionerna
	 */
	public void mapClicked(WgsPoint arg0) {
		if (isInAddMode) {
			points.add(arg0);
			Place temp = new Place(1, "dummy", icons[2], arg0);
			mapComponent.addPlace(temp);
			regionCorners.add(temp);
		}
	}

	public void mapMoved() {
	}

	public void needRepaint(boolean arg0) {
	}
}
