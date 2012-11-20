package assignment;

import java.util.List;

import models.Assignment;
import models.AssignmentStatus;
import models.Contact;
import models.ModelInterface;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.example.klien_projekttermin.R;
import com.example.klien_projekttermin.database.Database;
import communicationModule.CommunicationService;
import communicationModule.CommunicationService.CommunicationBinder;

public class AssignmentDetails extends Activity {

	Database db;/* = Database.getInstance(getApplicationContext()); */
	private long assignmentID;
	private TextView textViewAssName;
	private TextView textViewDescription;
	private TextView textViewTime;
	private TextView textViewSpot;
	private TextView textViewStreetname;
	private TextView textViewCoord;
	private TextView agentCount;
	private CheckBox checkboxAssign;
	private ImageView image;
	private List<ModelInterface> listAssignments;
	private Assignment currentAssignment;
	private String currentUser;

	// -------ComService
	private CommunicationService communicationService;
	private boolean communicationBond = false;

	// -------End

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_uppdrag);

		// -------ComService---
		Intent intentServer = new Intent(this.getApplicationContext(),
				CommunicationService.class);
		bindService(intentServer, communicationServiceConnection,
				Context.BIND_AUTO_CREATE);
		// ----End----

		// Hämtar intent för att nå extras så som ID:t som clickades på i
		// assignmentoverview.
		Intent intent = getIntent();
		assignmentID = intent.getExtras().getLong("assignmentID");
		currentUser = intent.getExtras().getString("currentUser");
		
		// -----------TrashCode
				Toast toast = Toast.makeText(getApplicationContext(), "User: "
						+ currentUser, Toast.LENGTH_SHORT);
				toast.show();
				// ----End

		// Initierar databasen.
		db = Database.getInstance(this);

		listAssignments = db.getAllFromDB(new Assignment(),
				getContentResolver());

		// Hittar rätt assignment i databasen och sätter den tillgänglig i denna
		// klass.
		setCurrentAssignmentToReach();

		// Hämtar textvyerna som ska sättas.
		textViewAssName = (TextView) findViewById(R.id.assignment_name_set);
		textViewDescription = (TextView) findViewById(R.id.assignment_description_set);
		textViewTime = (TextView) findViewById(R.id.assignment_time_set);
		textViewSpot = (TextView) findViewById(R.id.assignment_spot_set);
		textViewStreetname = (TextView) findViewById(R.id.assignment_streetname_set);
		textViewCoord = (TextView) findViewById(R.id.assignment_coordinates_set);
		agentCount = (TextView) findViewById(R.id.textView_agentCount);
		checkboxAssign = (CheckBox) findViewById(R.id.checkBox_assign);
		image = (ImageView) findViewById(R.id.imageView1);

		// Lyssnar på om uppdraget är åtaget och checkar i checkrutan om den är
		// det.
		setCheckedIfAssigned();

		// Lyssnar den efter klick i checkrutan
		setCheckboxCheckedListener();

		// Sätter texten som ska visas i uppdragsvyn.
		setAssignmentToView();

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (communicationBond) {
			unbindService(communicationServiceConnection);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_uppdrag, menu);
		return true;
	}

	/**
	 * Returnerar true om uppdraget är åtaget eller false om det inte är det.
	 * 
	 * @return
	 */
	public void setCheckedIfAssigned() {

		List<Contact> assigned = currentAssignment.getAgents();
		for (Contact c : assigned) {

			// Kollar om den inloggade han antagit uppdraget och checkar i rutan
			// isf.
			if (c.getContactName().equals(currentUser)) {
				// Klicka i låådan checkboxchecked
				checkboxAssign.setChecked(true);
				checkboxAssign.setEnabled(false);
			}

		}

	}

	public void setCurrentAssignmentToReach() {

		for (ModelInterface m : listAssignments) {

			// Konverterar Modelinterfacet till ett Assignment.
			Assignment a = (Assignment) m;

			// Jämför ID:T som clickats på med befintliga Assignments från
			// databasen och sätter den texten.
			if (a.getId() == assignmentID) {
				currentAssignment = a;
			}

		}

	}

	/**
	 * Sätter texten som ska visas i uppdragsvyn.
	 */
	public void setAssignmentToView() {

		textViewAssName.setText(currentAssignment.getName());
		textViewDescription.setText(currentAssignment
				.getAssignmentDescription());
		textViewTime.setText(currentAssignment.getTimeSpan());
		textViewSpot.setText(currentAssignment.getSiteName());
		textViewStreetname.setText(currentAssignment.getStreetName());
		textViewCoord.setText("Latitud: " + currentAssignment.getLat()
				+ "  Longitud: " + currentAssignment.getLon() + "ID: "
				+ currentAssignment.getId());
		
		//Fyller en sträng med aktuella agenter.
		String temp = "";
		for (int i = 0; i < currentAssignment.getAgents().size(); i++) {
			temp = temp + currentAssignment.getAgents().get(i).getContactName() + ", ";
		}
		agentCount.setText(" Antal: " + currentAssignment.getAgents().size() + "(" + temp + ")");
		
	}

	/**
	 * Lyssnar på om checkboen checkas i gör gör den därefter ocheckbar och
	 * agenten läggs till till det uppdraget.
	 */
	public void setCheckboxCheckedListener() {

		checkboxAssign
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						// ---ComService
						communicationService
								.setContext(getApplicationContext());

						checkboxAssign.setEnabled(false); // disable checkbox

						currentAssignment.addAgents(new Contact(currentUser));
						
						// Sätter status för att uppdraget är påbörjat.
						currentAssignment
								.setAssignmentStatus(AssignmentStatus.STARTED);
						
						// Uppdaterar Uppdraget med den nya kontakten.
						db.updateModel((ModelInterface) currentAssignment,
								getContentResolver());
						communicationService.sendAssignment(currentAssignment);

						// Sätter texten som ska visas i uppdragsvyn.
						setAssignmentToView();
					}
				});

	}

	/**
	 * ComService för att skicka till server
	 */
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

}
