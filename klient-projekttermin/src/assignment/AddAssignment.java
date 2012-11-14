package assignment;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.klien_projekttermin.R;
import com.example.klien_projekttermin.database.Database;
import com.google.gson.Gson;
import com.nutiteq.components.WgsPoint;
import communicationModule.CommunicationService;
import communicationModule.CommunicationService.CommunicationBinder;

public class AddAssignment extends ListActivity {

	private Database db;
	private CommunicationService communicationService;
	private boolean communicationBond = false;
	private Button addAssignmentButton;
	private EditText assignmentName;
	private EditText assignmentDescription;
	private EditText assignmentTime;
	private EditText assignmentStreetName;
	private EditText assignmentSpot;
	private EditText assignmnetCoords;

	double lat = 0;
	double lon = 0;
	private String coordinates;
	private String[] from = { "line1", "line2" };
	private int[] to = { R.id.editText1, R.id.textView1 };
	private List<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();
	private String[] dataString = { "Name", "coord", "Uppdragsbeskrivning",
			"uppskattadtid", "gatuadress", "uppdragsplats" };

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_assignment);
		addContent();
		SimpleEditTextItemAdapter adapter = new SimpleEditTextItemAdapter(this,
				data, R.layout.textfield_item, from, to);
		setListAdapter(adapter);
		DecimalFormat df = new DecimalFormat("#.00");
		Intent intent = getIntent();
		coordinates = intent.getStringExtra(MapActivity.coordinates);
		Gson gson = new Gson();
		WgsPoint[] coords = gson.fromJson(
				intent.getStringExtra(MapActivity.coordinates),
				WgsPoint[].class);

		db = new Database();
	}

	private void addContent() {
		data.clear();
		for (String s : dataString) {
			HashMap<String, String> temp = new HashMap<String, String>();
			temp.put("line1", "hint");
			temp.put("line2", s);
			data.add(temp);
		}
	}

	/**
	 * L�gger till lyssnare till "add-uppdrag-knappen".
	 * 
	 * @param button
	 */
	public void setButtonClickListnerAddAssignment(Button button) {

		// Skapar en humbug-bitmap.
		Bitmap.Config conf = Bitmap.Config.ARGB_8888;
		final Bitmap fakeImage = Bitmap.createBitmap(100, 100, conf);

		// Lyssnar efter klick.
		button.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				communicationService.setContext(getApplicationContext());

				if (!assignmentName.getText().toString().equals("")) {
					Assignment newAssignment = new Assignment(assignmentName
							.getText().toString(), coordinates, false,
							assignmentDescription.getText().toString(),
							assignmentTime.getText().toString(),
							AssignmentStatus.STARTED, assignmentStreetName
									.getText().toString());

					db.addToDB(newAssignment, getApplicationContext());
					communicationService.sendAssignment(newAssignment);
				}

				// Stänger aktiviteten.
				finish();
			}
		});
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
		return true;
	}
}
