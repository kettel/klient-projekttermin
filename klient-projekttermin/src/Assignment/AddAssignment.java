package assignment;

import models.Assignment;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import database.Database;
import com.example.klien_projekttermin.*;

public class AddAssignment extends Activity {

	private Database db;
	private Button addAssignmentButton;
	private EditText assignmentName;
	//private EditText assignmentCoord;
	private EditText assignmentDescription;
	private EditText assignmentTime;
	private EditText assignmentStreetName;
	private EditText assignmentSpot;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_assignment);

		// H�mtar och s�tter vyerna fr�n .xml -filen.
		addAssignmentButton = (Button) findViewById(R.id.button_add_assignment);
		assignmentName = (EditText) findViewById(R.id.assignment_name);
		//assignmentCoord = (EditText) findViewById(R.id.assignment_coord);
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
							.getText().toString(), (double)66666, (double)77777,
							"Den här ska bort.",
							"Ska automatiskt hämtas fr�n den innloggade",
							assignmentDescription.getText().toString(),
							assignmentTime.getText().toString(),
							"Status", fakeImage, assignmentStreetName
									.getText().toString(), assignmentSpot
									.getText().toString());
					db.addToDB(newAssignment, getApplication());
				}

				// St�nger aktiviteten.
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
