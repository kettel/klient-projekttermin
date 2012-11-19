package assignment;

import java.util.List;

import models.Assignment;
import models.AssignmentStatus;
import models.Contact;
import models.ModelInterface;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.klien_projekttermin.R;
import com.example.klien_projekttermin.database.Database;


public class AssignmentDetails extends Activity {

	Database db = Database.getInstance(getApplicationContext());
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

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_uppdrag);

		// H�mtar intent f�r att n� extras s� som ID:t som clickades p� i
		// assignmentoverview.
		Intent intent = getIntent();
		assignmentID = intent.getExtras().getLong("assignmentID");
		// currentUser = intent.getExtras().getString("currentUser");

		// Initierar databasen.
		db = Database.getInstance(this);

		listAssignments = db.getAllFromDB(new Assignment(),
				getContentResolver());

		// Hittar rätt assignment i databasen och sätter den tillgänglig i denna
		// klass.
		setCurrentAssignmentToReach();


		// H�mtar textvyerna som ska s�ttas.
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

			// TODO: Läg till användaren som är inloggad så man vet om mans ka
			// klicka i lådan eller inte.
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

		// agentCount.setText(currentAssignment.getAgents().size());
		textViewAssName.setText(currentAssignment.getName());
		textViewDescription.setText(currentAssignment
				.getAssignmentDescription());
		textViewTime.setText(currentAssignment.getTimeSpan());
		textViewSpot.setText(currentAssignment.getSiteName());
		textViewStreetname.setText(currentAssignment.getStreetName());
		textViewCoord.setText("Latitud: " + currentAssignment.getLat()
				+ "  Longitud: " + currentAssignment.getLon() + "ID: "
				+ currentAssignment.getId());

	}

	/**
	 * Lyssnar på om checkboen checkas i gör gör den därefter ocheckbar och
	 * agenten lägs till till det uppdraget.
	 */
	public void setCheckboxCheckedListener() {

		checkboxAssign
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {

						checkboxAssign.setEnabled(false); // disable checkbox

						// TODO: byt ut new Contact till den kontakten man är.!
						currentAssignment.addAgents(new Contact(currentUser));
						// Sätter status för att uppdraget är påbörjat.
						currentAssignment
								.setAssignmentStatus(AssignmentStatus.STARTED);


						// Uppdaterar Uppdraget med den nya kontakten.
						db.updateModel((ModelInterface) currentAssignment,
								getContentResolver());

					}
				});

	}

}
