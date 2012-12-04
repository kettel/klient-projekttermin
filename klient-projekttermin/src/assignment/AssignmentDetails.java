package assignment;

import java.lang.reflect.Type;
import java.util.List;

import login.User;
import map.CustomAdapter;
import map.MapActivity;
import models.Assignment;
import models.AssignmentStatus;
import models.Contact;
import models.ModelInterface;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.klient_projekttermin.ActivityConstants;
import com.klient_projekttermin.SecureActivity;
import com.klient_projekttermin.R;
import com.nutiteq.components.WgsPoint;
import communicationModule.SocketConnection;

import database.Database;

public class AssignmentDetails extends SecureActivity {

	private Database db;
	private long assignmentID;
	private TextView textViewAssName;
	private TextView textViewDescription;
	private TextView textViewPriority;
	private TextView textViewTime;
	private TextView textViewSpot;
	private TextView textViewCoord;
	private TextView agentCount;
	private CheckBox checkboxAssign;
	private ImageView image;
	private List<ModelInterface> listAssignments;
	private Assignment currentAssignment;
	private String currentUser;
	private boolean needToListen;
	public static String assignment;
	private String[] coordAlts = { "Gå till uppdraget på kartan" };

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_uppdrag);
		db = Database.getInstance(getApplicationContext());
		needToListen = true;
		


		User user = User.getInstance();
		currentUser = user.getAuthenticationModel().getUserName();
		
		// Hämtar intent för att nå extras så som ID:t som clickades på i
		// assignmentoverview.
		Intent intent = getIntent();
		int caller = intent.getIntExtra("calling-activity", 0);
		switch (caller) {
		case ActivityConstants.ASSIGNMENT_OVERVIEW:
			assignmentID = intent.getExtras().getLong("assignmentID");
			break;
		case ActivityConstants.ASSIGNMENT_NAME:
			assignmentID = intent.getExtras().getLong(MapActivity.assignmentName);
		default:
			break;
		}
		

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
		textViewPriority = (TextView) findViewById(R.id.assignment_prio_set);
		textViewTime = (TextView) findViewById(R.id.assignment_time_set);
		textViewSpot = (TextView) findViewById(R.id.assignment_spot_set);
		textViewCoord = (TextView) findViewById(R.id.assignment_coordinates_set);
		agentCount = (TextView) findViewById(R.id.textView_agentCount);
		checkboxAssign = (CheckBox) findViewById(R.id.checkBox_assign);
		image = (ImageView) findViewById(R.id.imageView1);

		// Lyssnar på om uppdraget är åtaget och checkar i checkrutan om den är
		// det.
		setCheckedIfAssigned();

		if (needToListen) {
			// Lyssnar den efter klick i checkrutan
			setCheckboxCheckedListener();
		}

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

			// Kollar om den inloggade han antagit uppdraget och checkar i rutan
			// isf.
			if (c.getContactName().equals(currentUser)) {
				// Klicka i låådan checkboxchecked
				checkboxAssign.setChecked(true);
				checkboxAssign.setEnabled(false);
				needToListen = false;
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
		Gson gson = new Gson();
		Type type = new TypeToken<WgsPoint[]>() {
		}.getType();
		StringBuilder sb = new StringBuilder();
		
		WgsPoint[] cords = gson.fromJson(currentAssignment.getRegion(), type);
		if (cords != null) {
			for (WgsPoint wgsPoint : cords) {
				sb.append(wgsPoint.getLat() + "," + wgsPoint.getLon());
			}
		}
		textViewAssName.setText(currentAssignment.getName());
		textViewDescription.setText(currentAssignment
				.getAssignmentDescription());
		textViewPriority.setText(currentAssignment.getAssignmentPriorityToString());
		textViewTime.setText(currentAssignment.getTimeSpan());
		textViewSpot.setText(currentAssignment.getSiteName());
		currentAssignment.getRegion();
		textViewCoord.setText(sb.toString());
		textViewCoord.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				centerMapOnLocation();
			}
		});
		Bitmap bitmap = BitmapFactory.decodeByteArray(
				currentAssignment.getCameraImage(), 0,
				currentAssignment.getCameraImage().length);
		image.setImageBitmap(bitmap);

		// Fyller en sträng med aktuella agenter.
		String temp = "";
		for (int i = 0; i < currentAssignment.getAgents().size(); i++) {
			temp = temp + currentAssignment.getAgents().get(i).getContactName()
					+ ", ";
		}
		agentCount.setText(" Antal: " + currentAssignment.getAgents().size()
				+ "(" + temp + ")\n");
	}

	/**
	 * Lyssnar på om checkboen checkas i gö den därefter ocheckbar och agenten
	 * läggs till till det uppdraget.
	 */
	public void setCheckboxCheckedListener() {

		checkboxAssign
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {

						checkboxAssign.setEnabled(false); // disable checkbox

						currentAssignment.addAgents(new Contact(currentUser));

						// Sätter status för att uppdraget är påbörjat.
						if (currentAssignment.getAssignmentStatus() == AssignmentStatus.NOT_STARTED) {
							currentAssignment
								.setAssignmentStatus(AssignmentStatus.STARTED);
						}
						

						// Uppdaterar Uppdraget med den nya kontakten.
						db.updateModel(currentAssignment,
								getContentResolver());
						SocketConnection connection=new SocketConnection();
						connection.sendModel(currentAssignment);

						// Sätter texten som ska visas i uppdragsvyn.
						setAssignmentToView();
					}
				});

	}
	
	private void centerMapOnLocation(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Koordinater för uppdraget");
		ListView modeList = new ListView(this);
		CustomAdapter modeAdapter = new CustomAdapter(this,
				android.R.layout.simple_list_item_1, android.R.id.text1,
				coordAlts);
		modeList.setAdapter(modeAdapter);
		builder.setView(modeList);
		
		final Dialog dialog = builder.create();
		dialog.setCancelable(false);
		modeList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				dialog.dismiss();
				switch (arg2) {
				case 0:
					Intent intent = new Intent(AssignmentDetails.this, MapActivity.class);
					intent.putExtra("calling-activity", ActivityConstants.ASSIGNMENT_DETAILS);
					intent.putExtra(assignment, currentAssignment.getRegion());
					AssignmentDetails.this.startActivity(intent);
					finish();
					break;
				default:
					break;
				}
			}
		});
		dialog.show();
	}

}
