package assignment;

import java.lang.reflect.Type;
import java.util.ArrayList;

import map.MapActivity;
import models.Assignment;
import models.AssignmentStatus;
import android.app.ListActivity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

import com.example.klien_projekttermin.R;
import com.example.klien_projekttermin.database.Database;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nutiteq.components.WgsPoint;
import communicationModule.CommunicationService;
import communicationModule.CommunicationService.CommunicationBinder;

public class AddAssignment extends ListActivity {

	private Database db;
	private CommunicationService communicationService;

	double lat = 0;
	double lon = 0;
	private String json;
	private ArrayList<String> data = new ArrayList<String>();
	private MenuItem saveItem;
	private MenuItem cancelItem;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_add_assignment);
		ArrayAdapter<String> adapter = new SimpleEditTextItemAdapter(this,
				R.layout.textfield_item);
		adapter.addAll(data);
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
		db = new Database();
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
		}

		public void onServiceConnected(ComponentName name, IBinder service) {
			CommunicationBinder binder = (CommunicationBinder) service;
			communicationService = binder.getService();

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
		if (item.equals(saveItem)) {
			saveToDB();
		} else if (item.equals(cancelItem)) {
			finish();
		}
		return super.onOptionsItemSelected(item);
	}

	private void saveToDB() {
		communicationService.setContext(getApplicationContext());

			Assignment newAssignment = new Assignment("niko", json, "self",
					false, "HEJ", "12", AssignmentStatus.NEED_HELP, null,
					"HEJ", "HEJ");
			db.addToDB(newAssignment, getApplicationContext());
			communicationService.sendAssignment(newAssignment);
		finish();
	}
}
