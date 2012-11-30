package map;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import loginFunction.InactivityListener;
import loginFunction.User;
import models.Assignment;
import models.ModelInterface;
import routing.MapManager;
import routing.NutiteqRouteWaiter;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ZoomControls;
import assignment.AddAssignment;
import assignment.AssignmentDetails;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.klient_projekttermin.ActivityConstants;
import com.klient_projekttermin.R;
import com.nutiteq.BasicMapComponent;
import com.nutiteq.android.MapView;
import com.nutiteq.components.KmlPlace;
import com.nutiteq.components.OnMapElement;
import com.nutiteq.components.Place;
import com.nutiteq.components.PlaceIcon;
import com.nutiteq.components.PlaceLabel;
import com.nutiteq.components.PolyStyle;
import com.nutiteq.components.Polygon;
import com.nutiteq.components.WgsPoint;
import com.nutiteq.listeners.MapListener;
import com.nutiteq.listeners.OnLongClickListener;
import com.nutiteq.listeners.OnMapElementListener;
import com.nutiteq.location.LocationMarker;
import com.nutiteq.location.LocationSource;
import com.nutiteq.location.NutiteqLocationMarker;
import com.nutiteq.location.providers.AndroidGPSProvider;
import com.nutiteq.maps.OpenStreetMap;
import com.nutiteq.maps.StoredMap;
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
public class MapActivity extends InactivityListener implements Observer,
		MapListener, Runnable, OnItemClickListener, OnMapElementListener {

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
			Utils.createImage("/res/drawable-hdpi/blobredsmall.png"),
			Utils.createImage("/res/drawable-hdpi/blobgreensmall.png"),
			Utils.createImage("/res/drawable-hdpi/blobbluesmall.png") };
	private EditText actv;
	private ListView lv;
	private static String[] searchAlts = { "Navigera till plats", "Visa plats",
			"Lägg till uppdrag på position" };
	public static String coordinates;
	public static String longitud;
	private LocationSource locationSource;
	private MenuItem searchItem;
	private ProgressBar sp;
	private Button clearSearch;
	private LocationManager manager;
	private MenuItem gpsFollowItem;
	private MapManager mm = new MapManager();
	private static String[] regionAlts = { "Ta bort region",
			"Skapa uppdrag med region" };
	private static String[] placeAlts = { "Visa detaljer för uppdrag" };
	private static String[] clickAlts = { "lägg till uppdrag" };
	private boolean onRetainCalled;
	private int callingActivity;
	private HashMap<Integer, String> content;
	public static String contents;
	public static String activityId;
	public static String assignmentName;
	private String currentUser;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		User user = User.getInstance();
		currentUser = user.getAuthenticationModel().getUserName();

		/**
		 * Sätter inställningar för kartan, samt lägger till en lyssnare.
		 */
		System.out.println("ON CREATE MAP");
		this.manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		this.locationSource = new AndroidGPSProvider(manager, 1000L);
		// this.setContentView(R.layout.activity_map);
		/**
		 * Kollar om gps är aktiverat
		 */
		if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			buildAlertMessageNoGps();

		}

		/**
		 * Hämtar listview till sökförslagen samt lägger till en adapter. Lägger
		 * till en observer på searchSuggestions
		 */
		this.searchSuggestions.addObserver(this);

		this.haveNetworkConnection();
		/**
		 * Hämta information från databasen om aktuella uppdrag
		 */
		// this.getDatabaseInformation();
		/**
		 * GPS:en ska vara påslagen vid start
		 */

	}

	public void createMap() {
		this.setContentView(R.layout.activity_map);
		this.mapComponent = new BasicMapComponent("tutorial", new AppContext(
				this), 1, 1, LINKÖPING, 10);
		// final StoredMap sm = new StoredMap("OurAwsomeMap", "/map", true);
		// mapComponent.setMap(sm);
		this.mapComponent.setMap(OpenStreetMap.MAPNIK);
		this.mapComponent.setPanningStrategy(new ThreadDrivenPanning());
		this.mapComponent.startMapping();
		this.mapComponent.setMapListener(this);
		this.mapComponent.setOnMapElementListener(this);
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
		mapView.setVisibility(0);
		activateGPS(gpsOnOff);
		onRetainCalled = false;

	}

	private void buildAlertMessageNoGps() {
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(
				"Your GPS seems to be disabled, do you want to enable it?")
				.setCancelable(false)
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(final DialogInterface dialog,
									final int id) {
								startActivity(new Intent(
										Settings.ACTION_LOCATION_SOURCE_SETTINGS));
							}
						})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(final DialogInterface dialog,
							final int id) {
						dialog.cancel();
					}
				});
		final AlertDialog alert = builder.create();
		alert.show();
	}

	private void haveNetworkConnection() {
		boolean haveConnectedWifi = false;
		boolean haveConnectedMobile = false;

		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo[] netInfo = cm.getAllNetworkInfo();
		for (NetworkInfo ni : netInfo) {
			if (ni.getTypeName().equalsIgnoreCase("WIFI"))
				if (ni.isConnected())
					haveConnectedWifi = true;
			if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
				if (ni.isConnected())
					haveConnectedMobile = true;
		}
		if (!haveConnectedWifi && !haveConnectedMobile) {
			final AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(
					"No network connection enabled, do you want to enable it?")
					.setCancelable(false)
					.setPositiveButton("Yes",
							new DialogInterface.OnClickListener() {
								public void onClick(
										final DialogInterface dialog,
										final int id) {
									startActivity(new Intent(
											Settings.ACTION_NETWORK_OPERATOR_SETTINGS));
								}
							})
					.setNegativeButton("No",
							new DialogInterface.OnClickListener() {
								public void onClick(
										final DialogInterface dialog,
										final int id) {
									dialog.cancel();
								}
							});
			final AlertDialog alert = builder.create();
			alert.show();
		}
	}

	/**
	 * Hämtar alla uppdrag från databasen och markerar ut dessa på kartan
	 */
	public void getDatabaseInformation() {
		Assignment a = new Assignment();
		Database db = Database.getInstance(getApplicationContext());
		List<ModelInterface> list = db.getAllFromDB(a, getContentResolver());
		System.out
				.println("database " + db.getDBCount(a, getContentResolver()));
		for (int i = 0; i < db.getDBCount(a, getContentResolver()); i++) {
			a = (Assignment) list.get(i);
			// addInterestPoint(a.getRegion());
		}
	}

	public void getDatabaseRegionInformation() {
		Assignment a = new Assignment();
		Database db = Database.getInstance(getApplicationContext());
		Gson gson = new Gson();
		List<ModelInterface> list = db.getAllFromDB(a, getContentResolver());
		for (int i = 0; i < db.getDBCount(a, getContentResolver()); i++) {
			a = (Assignment) list.get(i);
			Type type = new TypeToken<WgsPoint[]>() {
			}.getType();
			if (!a.getRegion().equals("")) {
				WgsPoint[] co = gson.fromJson(a.getRegion(), type);
				if (co.length > 1) {
					PlaceLabel pl = new PlaceLabel(a.getName());
					Polygon p = new Polygon(co, new PolyStyle(Color.BLUE), pl);
					mapComponent.addPolygon(p);
				} else {
					for (WgsPoint wgsPoint : co) {
						addInterestPoint(wgsPoint, a.getName());
					}
				}
			}
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
				gpsFollowItem = m.findItem(R.id.menu_deactivate_gps);
				gpsFollowItem.setEnabled(manager
						.isProviderEnabled(LocationManager.GPS_PROVIDER));
				gpsFollowItem
						.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
							public boolean onMenuItemClick(MenuItem item) {
								return gpsStatus(item);
							}
						});
				MenuItem item = m.findItem(R.id.menu_add_region);
				item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
					public boolean onMenuItemClick(MenuItem item) {
						return changeAddRegionMode(item);
					}
				});
			}
		});
		View v = (View) menu.findItem(R.id.menu_search).getActionView();
		this.sp = (ProgressBar) v.findViewById(R.id.spinner);
		this.searchItem = (MenuItem) menu.findItem(R.id.menu_search);
		this.clearSearch = (Button) v.findViewById(R.id.clearSearch);
		this.clearSearch.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				showMapView();
				searchItem.collapseActionView();
			}
		});
		this.actv = (EditText) v.findViewById(R.id.ab_Search);
		this.lv = (ListView) findViewById(R.id.mylist);
		this.lv.setOnItemClickListener(this);
		this.actv.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				final String temp = s.toString();
				if (!temp.isEmpty()) {
					runOnUiThread(new Runnable() {
						public void run() {
							sp.setVisibility(ProgressBar.VISIBLE);
						}
					});
					new Thread(new Runnable() {
						public void run() {
							searchSuggestions.updateSearch(temp,
									locationSource.getLocation());
						}
					}).start();
				} else {
					runOnUiThread(new Runnable() {
						public void run() {
							sp.setVisibility(ProgressBar.GONE);
						}
					});
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
	protected void onResume() {
		super.onResume();
		callingActivity = getIntent().getIntExtra("calling-activity", 0);
		Bundle extras = getIntent().getExtras();
		createMap();
		switch (callingActivity) {
		case ActivityConstants.ADD_ASSIGNMENT_ACTIVITY:

			break;
		case ActivityConstants.MAIN_ACTIVITY:
			getDatabaseRegionInformation();
			break;
		case ActivityConstants.ASSIGNMENT_DETAILS:
			getDatabaseRegionInformation();
			centerMapOnAssignment(extras
					.getString(AssignmentDetails.assignment));
			break;
		default:
			getDatabaseRegionInformation();
			break;
		}

		if (gpsFollowItem != null) {
			runOnUiThread(new Runnable() {

				public void run() {
					gpsFollowItem.setEnabled(manager
							.isProviderEnabled(LocationManager.GPS_PROVIDER));
				}
			});

		}
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
		activateGPS(false);
		mapComponent.setMiddlePoint(locationSource.getLocation());
		new NutiteqRouteWaiter(locationSource.getLocation(), searchSuggestions
				.getList().get(arg).getPlace().getWgs(), mapComponent,
				icons[2], icons[2], mm);
	}

	public void centerMapOnLocation(int arg) {
		activateGPS(false);
		mapComponent.setMiddlePoint(searchSuggestions.getList().get(arg)
				.getPlace().getWgs());
	}

	public void centerMapOnAssignment(String region) {
		activateGPS(false);
		Gson gson = new Gson();
		Type type = new TypeToken<WgsPoint[]>() {
		}.getType();
		WgsPoint[] co = gson.fromJson(region, type);
		mapComponent.setMiddlePoint(co[0]);
	}

	/**
	 * Markera en punkt på kartan
	 * 
	 * @param pointLocation
	 *            Position för punkten WgsPoint
	 * @param label
	 *            Namn som syns om man klickar på punkten
	 */
	public void addInterestPoint(WgsPoint region, String name) {
		Place p = new Place(1, name, icons[2], region);
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
			m.setTitle("Sluta följ");
			activateGPS(gpsOnOff);
		} else {
			m.setTitle("Följ position");
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
		} else {
			createInterestPoint(arg0);
		}
	}

	public void mapMoved() {
	}

	public void needRepaint(boolean arg0) {
	}

	private void createInterestPoint(WgsPoint w) {
		final WgsPoint[] wgs = new WgsPoint[1];
		wgs[0] = w;
		final Gson gson = new Gson();
		final Type type = new TypeToken<WgsPoint[]>() {
		}.getType();
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("VAl");
		ListView modeList = new ListView(this);
		CustomAdapter modeAdapter = new CustomAdapter(this,
				android.R.layout.simple_list_item_1, android.R.id.text1,
				clickAlts);
		modeList.setAdapter(modeAdapter);
		builder.setView(modeList);
		final Dialog dialog = builder.create();
		modeList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				dialog.dismiss();
				switch (arg2) {
				case 0:
					addInterestPoint(wgs[0], "Uppdrag");
					Intent intent = new Intent(MapActivity.this,
							AddAssignment.class);
					intent.putExtra("calling-activity",
							ActivityConstants.MAP_ACTIVITY);
					intent.putExtra(coordinates, gson.toJson(wgs, type));
					if (callingActivity == ActivityConstants.MAIN_ACTIVITY) {
						MapActivity.this.startActivity(intent);
					} else {
						setResult(ActivityConstants.RESULT_FROM_MAP, intent);
					}
					finish();
					break;
				default:
					break;
				}
			}
		});
		dialog.show();
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
		sp.setVisibility(ProgressBar.GONE);
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
		final int choice = arg2;
		switch (callingActivity) {
		case ActivityConstants.ADD_COORDINATES_TO_ASSIGNMENT:
			displayAddCoordinatesToAssignment(choice);
			break;
		default:
			displaySearchAlts(choice);
			break;
		}
	}

	public void displayAddCoordinatesToAssignment(int ch) {
		final int choice = ch;
		final Gson gson = new Gson();
		final Type type = new TypeToken<WgsPoint[]>() {
		}.getType();
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Koordinater");
		builder.setMessage("Använd koordinater ?");
		builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				Intent intent = new Intent(MapActivity.this,
						AddAssignment.class);
				WgsPoint[] coords = { searchSuggestions.getList().get(choice)
						.getPlace().getWgs() };
				intent.putExtra("calling-activity",
						ActivityConstants.MAP_ACTIVITY);
				intent.putExtra(contents, content);
				intent.putExtra(coordinates, gson.toJson(coords, type));
				System.out.println("JSON I MAP " + gson.toJson(coords, type));
				setResult(ActivityConstants.RESULT_FROM_MAP, intent);
				finish();
			}
		});
		builder.setNegativeButton("cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		builder.setCancelable(false);
		builder.create().show();
	}

	public void displaySearchAlts(int ch) {
		final int choice = ch;
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Meny");
		ListView modeList = new ListView(this);
		CustomAdapter modeAdapter = new CustomAdapter(this,
				android.R.layout.simple_list_item_1, android.R.id.text1,
				searchAlts);
		modeList.setAdapter(modeAdapter);
		if (this.locationSource.getLocation() == null) {
			modeAdapter.navigationToggle();
		}
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
					navigateToLocation(choice);
					break;
				case 1:
					centerMapOnLocation(choice);
					break;
				case 2:
					WgsPoint[] coords = { searchSuggestions.getList()
							.get(choice).getPlace().getWgs() };
					createAssignment(coords);
					break;
				default:
					break;
				}
			}
		});
		dialog.show();
	}

	public void createAssignment(WgsPoint[] coords) {
		Gson gson = new Gson();
		Intent intent = new Intent(MapActivity.this, AddAssignment.class);
		Type type = new TypeToken<WgsPoint[]>() {
		}.getType();
		intent.putExtra(coordinates, gson.toJson(coords, type));
		intent.putExtra("calling-activity", ActivityConstants.MAP_ACTIVITY);
		MapActivity.this.startActivity(intent);
	}

	public void regionChoice(OnMapElement arg) {
		final OnMapElement argument = arg;
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Regions val");
		ListView modeList = new ListView(this);
		CustomAdapter modeAdapter = new CustomAdapter(this,
				android.R.layout.simple_list_item_1, android.R.id.text1,
				regionAlts);
		modeList.setAdapter(modeAdapter);
		builder.setView(modeList);
		final Dialog dialog = builder.create();
		modeList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				dialog.dismiss();
				switch (arg2) {
				case 0:
					mapComponent.removePolygon(((Polygon) argument));
					break;
				case 1:
					if (callingActivity == ActivityConstants.ADD_COORDINATES_TO_ASSIGNMENT) {
						addRegionToAssignment(argument);
					} else {
						createAssignmentFromRegion(argument);
					}
					break;
				default:
					break;
				}
			}
		});
		dialog.show();
	}

	public void addRegionToAssignment(OnMapElement arg) {
		Intent intent = new Intent(MapActivity.this, AddAssignment.class);
		Gson gson = new Gson();
		WgsPoint[] ar = arg.getPoints();
		Type type = new TypeToken<WgsPoint[]>() {
		}.getType();
		intent.putExtra(coordinates, gson.toJson(ar, type));
		intent.putExtra("calling-activity", ActivityConstants.MAP_ACTIVITY);
		setResult(ActivityConstants.RESULT_FROM_MAP, intent);
		finish();
	}

	public void createAssignmentFromRegion(OnMapElement arg) {
		Intent intent = new Intent(MapActivity.this, AddAssignment.class);
		Gson gson = new Gson();
		WgsPoint[] ar = arg.getPoints();
		Type type = new TypeToken<WgsPoint[]>() {
		}.getType();
		intent.putExtra(coordinates, gson.toJson(ar, type));
		intent.putExtra("calling-activity", ActivityConstants.MAP_ACTIVITY);
		MapActivity.this.startActivity(intent);
	}

	public void elementClicked(OnMapElement arg0) {
		System.out.println("onclicked");
	}

	/**
	 * Kollar om det är en man har
	 */
	public void elementEntered(OnMapElement arg0) {
		if (arg0 instanceof Polygon) {
			regionChoice(arg0);
		}
		if (arg0 instanceof Place) {
			getAssignmentFromLabel(arg0);
		}
	}

	public void elementLeft(OnMapElement arg0) {
	}

	private void getAssignmentFromLabel(OnMapElement l) {
		final OnMapElement label = l;
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(label.getLabel().toString());
		ListView modeList = new ListView(this);
		CustomAdapter modeAdapter = new CustomAdapter(this,
				android.R.layout.simple_list_item_1, android.R.id.text1,
				placeAlts);
		modeList.setAdapter(modeAdapter);
		builder.setView(modeList);
		final Dialog dialog = builder.create();
		modeList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				dialog.dismiss();
				switch (arg2) {
				case 0:
					Assignment a = new Assignment();
					Database db = Database.getInstance(getApplicationContext());
					List<ModelInterface> list = db.getAllFromDB(a,
							getContentResolver());
					for (int i = 0; i < db.getDBCount(a, getContentResolver()); i++) {
						a = (Assignment) list.get(i);
						System.out.println("ASS NAME" + a.getName());
						System.out.println("label name "
								+ label.getLabel().toString());
						if (a.getName().equals(label.getLabel().toString())) {
							Intent intent = new Intent(MapActivity.this,
									AssignmentDetails.class);
							intent.putExtra("calling-activity",
									ActivityConstants.ASSIGNMENT_NAME);
							intent.putExtra(assignmentName, a.getId());
							intent.putExtra("currentUser", currentUser);
							MapActivity.this.startActivity(intent);
							break;
						}
					}
					break;
				default:
					break;
				}
			}
		});
		dialog.show();
	}

	@Override
	public Object onRetainNonConfigurationInstance() {
		onRetainCalled = true;
		return mapComponent;
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if (!onRetainCalled && mapComponent != null) {
			mapComponent.stopMapping();
			mapComponent = null;
		}
	}
}
