package assignment;

import java.util.List;

import models.Assignment;
import models.ModelInterface;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;

import com.example.klien_projekttermin.R;

import database.Database;

public class Uppdrag extends Activity {
	
	Database db;
	private long assignmentID;
	private TextView textViewAssName;
	private TextView textViewDescription;
	private TextView textViewTime;
	private TextView textViewSpot;
	private TextView textViewStreetname;
	private TextView textViewCoord;
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_uppdrag);
		
		//H�mtar intent f�r att n� extras s� som ID:t som clickades p� i assignmentoverview.
		Intent intent = getIntent();
		assignmentID = intent.getExtras().getLong("assignmentID");
		
		//Initierar databasen.
		db = new Database();
		//H�mtar textvyerna som ska s�ttas.
		textViewAssName = (TextView) findViewById(R.id.assignment_name_set);
		textViewDescription = (TextView) findViewById(R.id.assignment_description_set);
		textViewTime = (TextView) findViewById(R.id.assignment_time_set);
		textViewSpot = (TextView) findViewById(R.id.assignment_spot_set);
		textViewStreetname = (TextView) findViewById(R.id.assignment_streetname_set);
		textViewCoord = (TextView) findViewById(R.id.assignment_coordinates_set);
		
		//S�tter texten som ska visas i uppdragsvyn.
		setAssignmentToView();

	}
	
	/**
	 * S�tter texten som ska visas i uppdragsvyn.
	 */
	public void setAssignmentToView(){
		
		List<ModelInterface> listAssignments = db.getAllFromDB(
				new Assignment(), getApplicationContext());
		

		for (ModelInterface m : listAssignments) {
			
			//Konverterar Modelinterfacet till ett Assignment.
			Assignment a = (Assignment)m;
			
			//J�mf�r ID:T som clickats p� med befindliga Assignments fr�n databasen och s�tter den texten.
			if (a.getId() == assignmentID) {
				textViewAssName.setText(a.getName());
				textViewDescription.setText(a.getAssignmentDescription());
				textViewTime.setText(a.getTimeSpan());
				textViewSpot.setText(a.getSiteName());
				textViewStreetname.setText(a.getStreetName());
				textViewCoord.setText("Latitud: " + a.getLat() + "  Longitud: " + a.getLon());
			}
			
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_uppdrag, menu);
		return true;
	}

}
