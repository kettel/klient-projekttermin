package assignment;

import java.util.List;

import login.User;
import models.Assignment;
import models.AssignmentStatus;
import models.ModelInterface;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

import com.klient_projekttermin.ActivityConstants;
import com.klient_projekttermin.R;
import com.klient_projekttermin.SecureActivity;
import communicationModule.SocketConnection;

import database.AssignmentTable;
import database.Database;

public class AssignmentOverview extends SecureActivity {

	private long[] idInAdapter;
	private Database db;
	private List<ModelInterface> assList;
	private String currentUser;
	private ListView lv;
	private Cursor c;
	private AssignmentCursorAdapter adapter;
	private List<ModelInterface> listAssignments;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_assignment_overview);
		lv = (ListView) findViewById(android.R.id.list);
		db = Database.getInstance(getApplicationContext());

		User user = User.getInstance();
		currentUser = user.getAuthenticationModel().getUserName();

	}

	// Gör en custom topmeny.
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_assignment_overview, menu);

		return true;
	}

	/**
	 * Startar aktiviteten som skapar uppdrag
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent = new Intent(AssignmentOverview.this, AddAssignment.class);
		AssignmentOverview.this.startActivity(intent);

		return true;

	}

	@Override
	public void onResume() {
		super.onResume();
		loadAssignmentList();
		setItemClickListner();
		setLongItemClickListener();
	}

	public void loadAssignmentList() {
		 getAssHeadsFromDatabase();
		/**
		 * MÅSTE FIXA EN BÄTTRE CURSOR
		 */
		c = getContentResolver().query(AssignmentTable.Assignments.CONTENT_URI,
				null, null, null, AssignmentTable.Assignments.PRIORITY_INT + " ASC ");
		adapter = new AssignmentCursorAdapter(this, c, false);
		this.lv.setAdapter(adapter);
	}

	/**
	 * Hämtar en stringarray med strängar som representerar uppdragen.
	 * 
	 * @return
	 */
	private String[] getAssHeadsFromDatabase() {
		assList = db.getAllFromDB(new Assignment(), getContentResolver());
		int i = 0;
		String[] tempHeadArr = new String[assList.size()];
		idInAdapter = new long[assList.size()];

		for (ModelInterface a : assList) {
			Assignment b = (Assignment) a;
			tempHeadArr[i] = b.getName() + "   Prio: "
					+ b.getAssignmentPriorityToString();
			idInAdapter[i] = b.getId();
			i++;
			db = Database.getInstance(getApplicationContext());
			System.out.println("PRIO INTS :" + b.getPrio_int());
		}
		return tempHeadArr;
	}

	private long getID(int id) {
		listAssignments = db.getAllFromDB(new Assignment(),
				getContentResolver());
		long a = adapter.getItemId(id);
		for (ModelInterface modelInterface : listAssignments) {
			Assignment s = (Assignment) modelInterface;
			if (s.getAssignmentStatus() == AssignmentStatus.NEED_HELP
					&& s.getId() == a) {
				if (s.getAgents().isEmpty()) {
					s.setAssignmentStatus(AssignmentStatus.NOT_STARTED);
					db.updateModel(s, getContentResolver());
				} else {
					s.setAssignmentStatus(AssignmentStatus.STARTED);
					db.updateModel(s, getContentResolver());
				}
			}
			adapter.notifyDataSetChanged();
			if (s.getId() == a) {
				return s.getId();
			}
		}
		return 0;
	}

	private boolean checkIfFinished(int id) {
		List<ModelInterface> ass = db.getAllFromDB(new Assignment(),
				getContentResolver());
		long a = adapter.getItemId(id);
		for (ModelInterface modelInterface : ass) {
			Assignment s = (Assignment) modelInterface;
			if (s.getAssignmentStatus() == AssignmentStatus.FINISHED
					&& s.getId() == a) {
				db.deleteFromDB(s, getContentResolver());
				return true;
			}
		}
		return false;
	}

	/**
	 * Sätter en klicklyssnare på listvyn.
	 */
	public void setItemClickListner() {

		this.lv.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1,
					int itemClicked, long arg3) {

				if (checkIfFinished(itemClicked)) {
					Toast toast = Toast.makeText(getApplicationContext(),
							"Uppdraget är avslutat", Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.TOP, 0, 300);
					toast.show();
					loadAssignmentList();
				} else {
					Intent myIntent = new Intent(AssignmentOverview.this,
							AssignmentDetails.class);
					myIntent.putExtra("assignmentID", getID(itemClicked));
					myIntent.putExtra("calling-activity",
							ActivityConstants.ASSIGNMENT_OVERVIEW);
					AssignmentOverview.this.startActivity(myIntent);
				}
			}
		});
	}

	/**
	 * Tillsatt lyssnare i meddelandelistan som lyssnar efterz tryckningar på
	 * listobjekt
	 */
	public void setLongItemClickListener() {
		// Skapar en lyssnare som lyssnar efter långa intryckningar
		this.lv.setOnItemLongClickListener(new OnItemLongClickListener() {

			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int eraseAtPos, long arg3) {
				showEraseOption(eraseAtPos);
				return true;
			}
		});
	}

	/**
	 * Metoden skapar en dialogruta som frågar användaren om denne vill ta bort
	 * en konversation Metoden ger också användaren två valmöjligheter, JA eller
	 * Avbryt
	 */
	public void showEraseOption(final long eraseById) {
		// final long eraseAssingmentWithId = eraseById;

		AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.setTitle("Radera uppdrag.");
		alertDialog.setMessage("Vill du ta bort uppdraget?");
		alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "JA",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						eraseAssignment(eraseById);
					}
				});
		alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "AVBRYT",
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
					}
				});

		alertDialog.show();

	}

	/**
	 * Metoden tar bort ett uppdrag.
	 */
	public void eraseAssignment(long assignmentId) {
		List<ModelInterface> listAssignments = db.getAllFromDB(
				new Assignment(), getContentResolver());
		long a = adapter.getItemId((int) assignmentId);
		for (ModelInterface modelInterface : listAssignments) {
			Assignment s = (Assignment) modelInterface;
			if (s.getId() == a) {
				db.deleteFromDB(s, getContentResolver());
				s.setAssignmentStatus(AssignmentStatus.FINISHED);
				SocketConnection connection = new SocketConnection();
				connection.setContext(getApplicationContext());
				connection.sendModel(s);
			}
		}
		loadAssignmentList();
	}
}
