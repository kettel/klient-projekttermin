package assignment;

import java.lang.reflect.Type;

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
import com.google.gson.reflect.TypeToken;
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
	private EditText assignmentCoord;
	double lat=0;
	double lon = 0;
	String json;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		json = intent.getStringExtra(MapActivity.coordinates);
		Gson gson = new Gson();
		Type type = new TypeToken<WgsPoint[]>() {}.getType();
		WgsPoint[] co = gson.fromJson(json, type);
		setContentView(R.layout.activity_add_assignment);

		// Hämtar och sätter vyerna från .xml -filen.
		addAssignmentButton = (Button) findViewById(R.id.button_add_assignment);
		assignmentCoord = (EditText) findViewById(R.id.assignment_coord);
		StringBuilder sb = new StringBuilder();
		for (WgsPoint wgsPoint : co) {
			sb.append(wgsPoint.getLat() + " , " + wgsPoint.getLon());
		}
		assignmentCoord.setText(sb.toString());
		assignmentName = (EditText) findViewById(R.id.assignment_name);
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
					Assignment newAssignment = new Assignment("niko", json, "self", false, "HEJ", "12", AssignmentStatus.NEED_HELP, null, "HEJ", "HEJ"); 
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

