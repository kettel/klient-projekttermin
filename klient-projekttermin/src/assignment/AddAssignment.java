package assignment;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

import map.MapActivity;
import models.Assignment;
import models.AssignmentStatus;
import android.app.ListActivity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.example.klien_projekttermin.R;
import com.example.klien_projekttermin.database.Database;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nutiteq.components.WgsPoint;
import communicationModule.CommunicationService;
import communicationModule.CommunicationService.CommunicationBinder;

public class AddAssignment extends ListActivity {

	private CommunicationService communicationService;
	private boolean communicationBond = false;

	private double lat = 0;
	private double lon = 0;
	private EditText assignmentCoord;
	private String json;
	private String coordinates;
	private ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();
	private String[] dataString = { "Name", "coord", "Uppdragsbeskrivning",
			"uppskattadtid", "gatuadress", "uppdragsplats" };
	private MenuItem saveItem;
	private MenuItem cancelItem;
	private String[] from = { "line1" };
	private int[] to = { R.id.editText1 };

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_add_assignment);
		loadContent();
		SimpleEditTextItemAdapter adapter = new SimpleEditTextItemAdapter(this,
				data, R.layout.textfield_item, from, to);
		setListAdapter(adapter);
		int callingActivity = getIntent().getIntExtra("calling-activity", 0);

		switch (callingActivity) {
		case ActivityConstants.MAP_ACTIVITY:
			fromMap();
			break;
		case ActivityConstants.MAIN_ACTIVITY:
			// Activity2 is started from Activity3
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

	private void fromMap() {
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

	}

	private ServiceConnection communicationServiceConnection = new ServiceConnection() {

		public void onServiceDisconnected(ComponentName arg0) {
			communicationBond = false;
		}

		public void onServiceConnected(ComponentName name, IBinder service) {
			CommunicationBinder binder = (CommunicationBinder) service;
			communicationService = binder.getService();
			communicationBond = true;

		}

	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_add_assignment, menu);
		this.saveItem = menu.findItem(R.id.save);
		this.cancelItem = menu.findItem(R.id.cancel);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		if (item.equals(saveItem)) {
			saveToDB();
		} else if (item.equals(cancelItem)) {
			finish();
		}
		return super.onOptionsItemSelected(item);
	}

	private void saveToDB() {
		// Skapar en humbug-bitmap.
		Database db = new Database();
		HashMap<Integer, String>temp=((SimpleEditTextItemAdapter)getListAdapter()).getItemStrings();
//		communicationService.setContext(getApplicationContext());
		Assignment newAssignment = new Assignment(temp.get(0), 0, 0, "eric", false, temp.get(2),temp.get(3) , AssignmentStatus.NOT_STARTED, temp.get(4), temp.get(5));
		db.addToDB(newAssignment, getApplicationContext());
		//communicationService.sendAssignment(newAssignment);

		// Stänger aktiviteten.
		finish();

	}
}
