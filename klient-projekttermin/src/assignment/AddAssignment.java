package assignment;

import java.text.DecimalFormat;

import map.MapActivity;
import models.Assignment;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.klien_projekttermin.R;

import database.Database;

public class AddAssignment extends Activity {

	private Database db;
	private Button addAssignmentButton;
	private EditText assignmentName;
//	private EditText assignmentCoord;
	private EditText assignmentDescription;
	private EditText assignmentTime;
	private EditText assignmentStreetName;
	private EditText assignmentSpot;
//	private double[] coordinates;
//	private String coords="";
//	private StringBuilder sb = new StringBuilder();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		DecimalFormat df = new DecimalFormat("#.00");
//		Intent intent = getIntent();
//		coordinates = (double[]) intent.getDoubleArrayExtra(MapActivity.coordinates);
//		for (int i = 0; i < coordinates.length; i++) {
//			double temp = Double.valueOf(df.format(coordinates[i]));
//			coords = sb.append(temp+ " , ").toString();
//		}
//		coords = coords.substring(0, coords.length()-2);
		setContentView(R.layout.activity_add_assignment);

		// Hämtar och sätter vyerna från .xml -filen.
		addAssignmentButton = (Button) findViewById(R.id.button_add_assignment);
		assignmentName = (EditText) findViewById(R.id.assignment_name);
//		assignmentCoord = (EditText) findViewById(R.id.assignment_coord);
//		assignmentCoord.setText(coords);
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
					Assignment newAssignment = new Assignment(45,assignmentName
							.getText().toString(), (double)333, (double)12,
							"Den här ska bort.",
							"Ska automatiskt hämtas från den inloggade",
							assignmentDescription.getText().toString(),
							assignmentTime.getText().toString(),
							"Status", fakeImage, assignmentStreetName
									.getText().toString(), assignmentSpot
									.getText().toString());
					db.addToDB(newAssignment, getApplication());
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
