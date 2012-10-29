package com.example.klien_projekttermin;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ZoomControls;

import com.nutiteq.BasicMapComponent;
import com.nutiteq.android.MapView;
import com.nutiteq.components.PlaceIcon;
import com.nutiteq.components.WgsPoint;
import com.nutiteq.location.LocationMarker;
import com.nutiteq.location.LocationSource;
import com.nutiteq.location.NutiteqLocationMarker;
import com.nutiteq.location.providers.AndroidGPSProvider;
import com.nutiteq.maps.OpenStreetMap;
import com.nutiteq.ui.ThreadDrivenPanning;
import com.nutiteq.wrappers.AppContext;
import com.nutiteq.wrappers.Image;

public class MapActivity extends Activity {

	private BasicMapComponent mapComponent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        mapComponent = new BasicMapComponent("tutorial", new AppContext(this), 1, 1, new WgsPoint(24.764580,
        		59.437420), 10);
        		mapComponent.setMap(OpenStreetMap.MAPNIK);
        		mapComponent.setPanningStrategy(new ThreadDrivenPanning());
        		mapComponent.startMapping();
        		// get the mapview that was defined in main.xml
        		MapView mapView = (MapView)findViewById(R.id.mapview);
        		// mapview requires a mapcomponent
        		mapView.setMapComponent(mapComponent);

        		ZoomControls zoomControls = (ZoomControls)findViewById(R.id.zoomcontrols);
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
        		(LocationManager) getSystemService(Context.LOCATION_SERVICE), 1000L);
        		Bitmap icon = BitmapFactory.decodeResource(getResources(),
        		R.drawable.icon);
        		final LocationMarker marker = new NutiteqLocationMarker(new PlaceIcon(Image
        		.createImage(icon), icon.getWidth()/2, icon.getHeight()), 3000, true);
        		locationSource.setLocationMarker(marker);
        		mapComponent.setLocationSource(locationSource);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_map, menu);
        return true;

    }
}
