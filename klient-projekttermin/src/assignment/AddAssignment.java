package assignment;

import java.text.DecimalFormat;

import map.MapActivity;
import models.Assignment;
import models.AssignmentStatus;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.klien_projekttermin.R;
import com.example.klien_projekttermin.database.Database;

import com.google.gson.Gson;
import communicationModule.CommunicationService;
import communicationModule.CommunicationService.CommunicationBinder;

public class AddAssignment extends Activity {

	private Database db;
	private CommunicationService communicationService;
	private boolean communicationBond = false;
	private Button addAssignmentButton;
	private EditText assignmentName;
	private EditText assignmentDescription;
	private EditText assignmentTime;
	private EditText assignmentStreetName;
	private EditText assignmentSpot;
	double lat = 0;
	double lon = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		DecimalFormat df = new DecimalFormat("#.00");
		Intent intent = getIntent();
		intent.getExtras();
		setContentView(R.layout.activity_add_assignment);

		

		Intent intentCom = new Intent(this.getApplicationContext(),
				CommunicationService.class);
		bindService(intentCom, communicationServiceConnection,
				Context.BIND_AUTO_CREATE);

		// Hämtar och sätter vyerna från .xml -filen.
		addAssignmentButton = (Button) findViewById(R.id.button_add_assignment);
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
				communicationService.setContext(getApplicationContext());
				
				if (!assignmentName.getText().toString().equals("")) {
					Assignment newAssignment = new Assignment(assignmentName
							.getText().toString(), "SELF", false,
							assignmentDescription.getText().toString(),
							assignmentTime.getText().toString(),
							AssignmentStatus.NOT_STARTED, assignmentStreetName
									.getText().toString());
					
					//Assignment poop =  new Assignment("Papegojja", 2365, 235, "Poopface", false, "Mörda kalle anka", "Tre timmar", AssignmentStatus.NEED_HELP, fakeImage, "I grerkland", "bajs");

					db.addToDB(newAssignment, getApplicationContext());
					communicationService.sendAssignment(newAssignment);
					
//					db.addToDB(newAssignment, getApplication());
//					communicationService.sendAssignment(newAssignment);
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
