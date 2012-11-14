package assignment;

import java.text.DecimalFormat;

import map.MapActivity;
import models.Assignment;
import models.AssignmentStatus;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.klien_projekttermin.R;
import com.example.klien_projekttermin.database.Database;

import com.google.gson.Gson;
import com.nutiteq.components.WgsPoint;

public class AddAssignment extends Activity {

	private Database db;
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

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		DecimalFormat df = new DecimalFormat("#.00");
		Intent intent = getIntent();
		coordinates = intent.getStringExtra(MapActivity.coordinates);
		Gson gson = new Gson();
		WgsPoint[] coords = gson.fromJson(
				intent.getStringExtra(MapActivity.coordinates),
				WgsPoint[].class);
		setContentView(R.layout.activity_add_assignment);

		// Hämtar och sätter vyerna från .xml -filen.
		addAssignmentButton = (Button) findViewById(R.id.button_add_assignment);
		assignmentName = (EditText) findViewById(R.id.assignment_name);
		assignmnetCoords = (EditText) findViewById(R.id.assignment_coord);
		for (int i = 0; i < coords.length; i++) {
			lat = coords[i].getLat();
			lon = coords[i].getLon();
			assignmnetCoords.setText(lat + " , " + lon);
		}
		assignmentDescription = (EditText) findViewById(R.id.assignment_description);
		assignmentTime = (EditText) findViewById(R.id.assignment_time);
		assignmentStreetName = (EditText) findViewById(R.id.assignment_street_name);
		assignmentSpot = (EditText) findViewById(R.id.assignment_spot);

		db = new Database();
		setButtonClickListnerAddAssignment(addAssignmentButton);

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
				if (!assignmentName.getText().toString().equals("")) {
					Assignment newAssignment = new Assignment(assignmentName
							.getText().toString(), coordinates, false,
							assignmentDescription.getText().toString(),
							assignmentTime.getText().toString(),
							AssignmentStatus.NOT_STARTED, assignmentStreetName
									.getText().toString());
					db.addToDB(newAssignment, getBaseContext());
				}

				// Stänger aktiviteten.
				finish();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_add_assignment, menu);
		return true;
	}
}
