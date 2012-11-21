package assignment;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

import map.MapActivity;
import models.Assignment;
import models.AssignmentStatus;
import android.app.ListActivity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import camera.PhotoGallery;

import com.example.klien_projekttermin.ActivityConstants;
import com.example.klien_projekttermin.R;
import com.example.klien_projekttermin.database.Database;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nutiteq.components.WgsPoint;
import communicationModule.CommunicationService;
import communicationModule.CommunicationService.CommunicationBinder;

public class AddAssignment extends ListActivity {

	// --------ComService
	private CommunicationService communicationService;
	private boolean communicationBond = false;
	// ----End

	double lat = 0;
	double lon = 0;
	private String json;
	private ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();
	private String[] dataString = { "Name", "coord", "Uppdragsbeskrivning",
			"uppskattadtid", "gatuadress", "uppdragsplats", "bild" };
	private MenuItem saveItem;
	private String[] from = { "line1" };
	private int[] to = { R.id.text_item };
	private Database db;
	private SimpleEditTextItemAdapter adapter;
	private String currentUser;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// ----ComService
		Intent intent = new Intent(this.getApplicationContext(),
				CommunicationService.class);
		bindService(intent, communicationServiceConnection,
				Context.BIND_AUTO_CREATE);
		// ---End

		setContentView(R.layout.activity_add_assignment);
		loadContent();
		adapter = new SimpleEditTextItemAdapter(this, data,
				R.layout.textfield_item, from, to);
		setListAdapter(adapter);

		int callingActivity = getIntent().getIntExtra("calling-activity", 0);
		currentUser = getIntent().getExtras().getString("currentUser");

		switch (callingActivity) {
		case ActivityConstants.MAP_ACTIVITY:
			adapter.textToItem(1, fromMap());
			break;
		case ActivityConstants.MAIN_ACTIVITY:
			break;
		case ActivityConstants.ADD_PICTURE_TO_ASSIGNMENT:
			adapter.textToItem(6, fromCamera());
			break;
		}
	}
	private void loadContent() {
		data.clear();
		for (String s : dataString) {
			HashMap<String, String> temp = new HashMap<String, String>();
			temp.put(from[0], s);
			data.add(temp);
		}
	}

	private String fromCamera() {
		Intent intent = getIntent();
		json = intent.getStringExtra(PhotoGallery.picture);
		Gson gson = new Gson();
		Type type = new TypeToken<Bitmap>() {
		}.getType();
		Bitmap bm = gson.fromJson(json, type);
		return json;
	}

	private String fromMap() {
		Intent intent = getIntent();
		json = intent.getStringExtra(MapActivity.coordinates);
		Gson gson = new Gson();
		Type type = new TypeToken<WgsPoint[]>() {
		}.getType();
		WgsPoint[] co = gson.fromJson(json, type);
		StringBuilder sb = new StringBuilder();
		for (WgsPoint wgsPoint : co) {
			sb.append(wgsPoint.getLat() + " , " + wgsPoint.getLon());
		}
		return sb.toString();
	}

	private ServiceConnection communicationServiceConnection = new ServiceConnection() {

		public void onServiceConnected(ComponentName className, IBinder service) {
			CommunicationBinder binder = (CommunicationBinder) service;
			communicationService = binder.getService();
			communicationBond = true;
		}

		public void onServiceDisconnected(ComponentName arg0) {
			communicationBond = false;

		}

	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_add_assignment, menu);
		this.saveItem = menu.findItem(R.id.save);
		communicationService.setContext(getApplicationContext()); // --ComService
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.equals(saveItem)) {
			saveToDB();
		}
		return super.onOptionsItemSelected(item);
	}

	private void saveToDB() {
		 db = Database.getInstance(getApplicationContext());
		HashMap<Integer, String> temp = ((SimpleEditTextItemAdapter) getListAdapter())
				.getItemStrings();
		Assignment newAssignment = new Assignment(temp.get(0), json,
				currentUser, false, temp.get(2), temp.get(3),
				AssignmentStatus.NOT_STARTED, temp.get(4), temp.get(5));

		db.addToDB(newAssignment, getContentResolver());
		communicationService.sendAssignment(newAssignment);
		finish();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(communicationBond)
		unbindService(communicationServiceConnection);
	}

}