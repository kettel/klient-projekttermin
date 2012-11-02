package map;

import java.util.Observable;
import java.util.Observer;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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
import com.nutiteq.location.LocationMarker;
import com.nutiteq.location.LocationSource;
import com.nutiteq.location.NutiteqLocationMarker;
import com.nutiteq.location.providers.AndroidGPSProvider;
import com.nutiteq.maps.OpenStreetMap;
import com.nutiteq.ui.ThreadDrivenPanning;
import com.nutiteq.wrappers.AppContext;
import com.nutiteq.wrappers.Image;

public class MapActivity extends Activity implements Observer,
		SearchView.OnCloseListener, SearchView.OnQueryTextListener {

	private BasicMapComponent mapComponent;
	private String[] from = { "line1", "line2" };
	private int[] to = { android.R.id.text1, android.R.id.text2 };
	private ListView lv;
	private SearchSuggestions searchSuggestions = new SearchSuggestions();
	private SimpleAdapter sm;
	private MapView mapView;
	private ZoomControls zoomControls;
	private SearchView searchView;
	private final WgsPoint LINKÖPING=new WgsPoint(15.5826,58.427);
	
	private InputMethodManager mgr;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		mapComponent = new BasicMapComponent("tutorial", new AppContext(this),
				1, 1, LINKÖPING, 10);
		mapComponent.setMap(OpenStreetMap.MAPNIK);
		mapComponent.setPanningStrategy(new ThreadDrivenPanning());
		mapComponent.startMapping();
		lv=(ListView) findViewById(R.id.mylist);
		sm=new SimpleAdapter(this, searchSuggestions.getList(),
				android.R.layout.simple_list_item_2, from, to);
		lv.setAdapter(sm);
		 mapView = (MapView) findViewById(R.id.mapview);
		mapView.setMapComponent(mapComponent);

		searchSuggestions.addObserver(this);
		mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		
		 zoomControls = (ZoomControls) findViewById(R.id.zoomcontrols);
		// set zoomcontrols listeners to enable zooming
		zoomControls.setOnZoomInClickListener(new View.OnClickListener() {
			public void onClick(final View v) {
				mapComponent.zoomIn();
			}
		});
		zoomControls.setOnZoomOutClickListener(new View.OnClickListener() {
			public void onClick(final View v) {
				mapComponent.zoomOut();
			}
		});

		// GPS Location
		final LocationSource locationSource = new AndroidGPSProvider(
				(LocationManager) getSystemService(Context.LOCATION_SERVICE),
				1000L);
		Bitmap icon = BitmapFactory.decodeResource(getResources(),
				R.drawable.ic_launcher);
		final LocationMarker marker = new NutiteqLocationMarker(
				new PlaceIcon(Image.createImage(icon), icon.getWidth() / 2,
						icon.getHeight()), 3000, true);
		locationSource.setLocationMarker(marker);
		mapComponent.setLocationSource(locationSource);
		WgsPoint[] region={new WgsPoint(16.1938481,58.563669),new WgsPoint(16.105957,59.143262),new WgsPoint(15.710449,58.853826)};
		addRegion(region);
		addInterestPoint(LINKÖPING, "Linkan");
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		searchView = (SearchView) menu.findItem(R.id.menu_search)
				.getActionView();
		searchView.setSearchableInfo(searchManager
				.getSearchableInfo(getComponentName()));
		searchView.setOnCloseListener(this);
		searchView.setOnQueryTextListener(this);

		return super.onCreateOptionsMenu(menu);
		}

	public boolean onQueryTextChange(String newText) {
		searchSuggestions.updateSearch(newText);
		
//		System.out.println("change");
//		lv.setVisibility(ListView.VISIBLE);
//		mgr.showSoftInput(searchView, InputMethodManager.SHOW_FORCED);
//		mapView.setVisibility(MapView.GONE);
//		zoomControls.setVisibility(ZoomControls.GONE);
		
		return true;
	}

	public boolean onQueryTextSubmit(String query) {
		searchView.setVisibility(SearchView.GONE);
		searchSuggestions.updateSearch(query);
		System.out.println("change");
		lv.setVisibility(ListView.VISIBLE);
		mapView.setVisibility(MapView.GONE);
		zoomControls.setVisibility(ZoomControls.GONE);
		searchView.setVisibility(SearchView.VISIBLE);
		System.out.println("query");
		return true;
	}

	public void addInterestPoint(WgsPoint pointLocation, String label) {
		Bitmap icon = BitmapFactory.decodeResource(getResources(),
				R.drawable.ic_launcher);

		Image poiImage = Image.createImage(icon);
		PlaceLabel poiLabel = new PlaceLabel(label);

		Place p = new Place(1, poiLabel, poiImage, pointLocation);
		mapComponent.addPlace(p);
	}
	public void addRegion(WgsPoint[] region){
		mapComponent.addPolygon(new Polygon(region));
	}

	public void update(Observable observable, Object data) {
		System.out.println("observed");
//		searchView.setVisibility(SearchView.GONE);
//		searchView.onActionViewCollapsed();
		sm.notifyDataSetChanged();
		sm.notifyDataSetInvalidated();
//		searchView.onActionViewExpanded();	
//		searchView.setSelected(true);	
//		searchView.setVisibility(SearchView.VISIBLE);
//		searchView.bringToFront();
	}

	public boolean onClose() {
		System.out.println("ONCLOSE");
		lv.setVisibility(ListView.GONE);
		mapView.setVisibility(MapView.VISIBLE);
		zoomControls.setVisibility(ZoomControls.VISIBLE);
		return false;
	}
}
