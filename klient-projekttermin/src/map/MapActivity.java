package map;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import models.Assignment;
import models.ModelInterface;
import routing.NutiteqRouteWaiter;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ZoomControls;

import com.example.klien_projekttermin.R;
import com.nutiteq.BasicMapComponent;
import com.nutiteq.android.MapView;
import com.nutiteq.components.KmlPlace;
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

import database.Database;

/**
 * En aktivitet som skapar en karta med en meny där de olika alternativen för
 * kartan finns
 * 
 * @author nicklas
 * 
 */
public class MapActivity extends Activity implements Observer, MapListener,
		Runnable, OnItemClickListener {

	private BasicMapComponent mapComponent;
	private SearchSuggestions searchSuggestions = new SearchSuggestions();
	private ArrayAdapter<String> sm;
	private MapView mapView;
	private ZoomControls zoomControls;
	private final WgsPoint LINKÖPING = new WgsPoint(15.5826, 58.427);
	private boolean isInAddMode = false;
	private boolean gpsOnOff = true;
	private ArrayList<WgsPoint> points = new ArrayList<WgsPoint>();
	private ArrayList<Place> regionCorners = new ArrayList<Place>();
	private static Image[] icons = {
			Utils.createImage("/res/drawable-hdpi/pin.png"),
			Utils.createImage("/res/drawable-hdpi/pin_green.png"),
			Utils.createImage("/res/drawable-hdpi/pos_arrow_liten.png") };
	private EditText actv;
	private ListView lv;
	private static String[] searchAlts = { "Navigera till plats", "Visa plats" };
	LocationSource locationSource;
	private MenuItem searchItem;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/**
		 * Sätter inställningar för kartan, samt lägger till en lyssnare.
		 */
		locationSource = new AndroidGPSProvider(
				(LocationManager) getSystemService(Context.LOCATION_SERVICE),
				1000L);
		this.setContentView(R.layout.activity_map);
		this.mapComponent = new BasicMapComponent("tutorial", new AppContext(
				this), 1, 1, LINKÖPING, 10);
		this.mapComponent.setMap(OpenStreetMap.MAPNIK);
		this.mapComponent.setPanningStrategy(new ThreadDrivenPanning());
		this.mapComponent.startMapping();
		this.mapComponent.setMapListener(this);

		/**
		 * Hämtar listview till sökförslagen samt lägger till en adapter. Lägger
		 * till en observer på searchSuggestions
		 */
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
		 * Hämta information från databasen om aktuella uppdrag
		 */
		getDatabaseInformation();
		/**
		 * GPS:en ska vara påslagen vid start
		 */
		activateGPS(gpsOnOff);
	}

	/**
	 * Hämtar alla uppdrag från databasen och markerar ut dessa på kartan
	 */
	public void getDatabaseInformation() {
		Assignment a = new Assignment();
		Database db = new Database();
		List<ModelInterface> list = db.getAllFromDB(a, getBaseContext());
		for (int i = 0; i < db.getDBCount(a, getBaseContext()); i++) {
			a = (Assignment) list.get(i);
			addInterestPoint(new WgsPoint(a.getLat(), a.getLon()), a.getName());
		}
	}

	/**
	 * Aktiverar GPS:en
	 */
	private void activateGPS(boolean on) {
		/**
		 * Om GPS är på centrera kartan vid gps positionen
		 */
		if (on) {
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
		final Menu m = menu;
		runOnUiThread(new Runnable() {
			public void run() {
				MenuInflater inflater = getMenuInflater();
				inflater.inflate(R.menu.menu, m);
				MenuItem item = m.findItem(R.id.menu_deactivate_gps);
				item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
					public boolean onMenuItemClick(MenuItem item) {
						return gpsStatus(item);
					}
				});
				item = m.findItem(R.id.menu_add_region);
				item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
					public boolean onMenuItemClick(MenuItem item) {
						return changeAddRegionMode(item);
					}
				});
			}
		});
		View v = (View) menu.findItem(R.id.menu_search).getActionView();
		searchItem = (MenuItem) menu.findItem(R.id.menu_search);
		this.actv = (EditText) v.findViewById(R.id.ab_Search);
		this.lv = (ListView) findViewById(R.id.mylist);
		this.lv.setOnItemClickListener(this);
		this.actv.addTextChangedListener(new TextWatcher() {

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				final String temp = s.toString();
				if (!temp.isEmpty()) {
					new Thread(new Runnable() {

						public void run() {
							searchSuggestions.updateSearch(temp);
						}
					}).start();
				}else {
					sm.clear();
				}

			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			public void afterTextChanged(Editable s) {

			}
		});
		sm = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
		lv.setAdapter(sm);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.equals(this.searchItem)) {
			actv.requestFocus();
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
		}

		return true;
	}

	/**
	 * Starta navigation till destination
	 * 
	 * @param destination
	 *            WgsPoint
	 * @param map
	 *            Kart objektet
	 */
	public void navigateToLocation(int arg) {
		activateGPS(true);
		mapComponent.setMiddlePoint(searchSuggestions.getList().get(arg)
				.getPlace().getWgs());
		new NutiteqRouteWaiter(locationSource.getLocation(), searchSuggestions
				.getList().get(arg).getPlace().getWgs(), mapComponent,
				icons[2], icons[2]);
	}

	public void centerMapOnLocation(int arg) {
		activateGPS(false);
		addInterestPoint(searchSuggestions.getList().get(arg).getPlace()
				.getWgs(), searchSuggestions.getList().get(arg).getName());
		mapComponent.setMiddlePoint(searchSuggestions.getList().get(arg)
				.getPlace().getWgs());
	}

	/**
	 * Markera en punkt på kartan
	 * 
	 * @param pointLocation
	 *            Position för punkten WgsPoint
	 * @param label
	 *            Namn som syns om man klickar på punkten
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
	 *            klickbart menuItem
	 */
	public boolean changeAddRegionMode(MenuItem m) {
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
				WgsPoint[] p = (WgsPoint[]) (points)
						.toArray(new WgsPoint[points.size()]);
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
		return true;
	}

	/**
	 * Ändrar tillstånd för GPS:en
	 * 
	 * @param m
	 *            klickbart menuItem
	 */

	public boolean gpsStatus(MenuItem m) {
		gpsOnOff = !gpsOnOff;
		if (!gpsOnOff) {
			m.setTitle("GPS/off");
			activateGPS(gpsOnOff);
		} else {
			m.setTitle("GPS/on");
			activateGPS(gpsOnOff);
		}
		return true;
	}

	/**
	 * Notifierar sökresultatens listviews adapter om att sökresultaten har
	 * ändrats
	 */
	public void update(Observable observable, Object data) {
		runOnUiThread(this);
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

	public void run() {
		if (this.lv.getVisibility() == ListView.GONE) {
			this.lv.setVisibility(ListView.VISIBLE);
			this.zoomControls.setVisibility(ZoomControls.GONE);
		}
		sm.clear();
		for (KmlPlace temp : searchSuggestions.getList()) {
			sm.addAll(temp.getName());
		}
		sm.notifyDataSetChanged();
	}

	public void showMapView() {
		runOnUiThread(new Runnable() {

			public void run() {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
				lv.setVisibility(ListView.GONE);
				zoomControls.setVisibility(ZoomControls.VISIBLE);
			}
		});
	}

	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Meny");
		ListView modeList = new ListView(this);
		ArrayAdapter<String> modeAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, android.R.id.text1,
				searchAlts);
		modeList.setAdapter(modeAdapter);
		builder.setView(modeList);
		final Dialog dialog = builder.create();
		modeList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				dialog.dismiss();
				showMapView();
				searchItem.collapseActionView();
				switch (arg2) {
				case 0:
					navigateToLocation(arg2);
					break;
				case 1:
					centerMapOnLocation(arg2);
					break;
				default:
					break;
				}
			}
		});
		dialog.show();
	}
}
