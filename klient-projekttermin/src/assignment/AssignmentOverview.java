package assignment;

import java.util.List;

import loginFunction.InactivityListener;
import models.Assignment;
import models.AssignmentStatus;
import models.ModelInterface;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

import com.klient_projekttermin.R;

import database.AssignmentTable;
import database.Database;
import communicationModule.CommunicationService;
import communicationModule.CommunicationService.CommunicationBinder;

public class AssignmentOverview extends InactivityListener {

	private long[] idInAdapter;
	private Database db;
	private List<ModelInterface> assList;
	private String currentUser;
	private ListView lv;

	//--------ComService
		private CommunicationService communicationService;
		private boolean communicationBond = false;
		//----End

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_assignment_overview);
		lv = (ListView)findViewById(android.R.id.list);
		
		Intent intent = new Intent(this.getApplicationContext(), CommunicationService.class);
		bindService(intent, communicationServiceConnection, Context.BIND_AUTO_CREATE);

		// Hämtar extras
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			currentUser = extras.getString("USER");
		}

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (communicationBond) {
			unbindService(communicationServiceConnection);
		}
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
		intent.putExtra("currentUser", currentUser);
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
		AssignmentCursorAdapter adapter = new AssignmentCursorAdapter(this,getContentResolver().query(AssignmentTable.Assignments.CONTENT_URI, null, null, null, null), false);
		this.lv.setAdapter(adapter);
	}

	/**
	 * Hämtar en stringarray med strängar som representerar uppdragen.
	 * 
	 * @return
	 */
	private String[] getAssHeadsFromDatabase() {
		db = Database.getInstance(getApplicationContext());
		assList = db.getAllFromDB(new Assignment(), getContentResolver());
		int i = 0;
		String[] tempHeadArr = new String[assList.size()];
		idInAdapter = new long[assList.size()];

		for (ModelInterface a : assList) {
			Assignment b = (Assignment) a;
			tempHeadArr[i] = b.getName();
			idInAdapter[i] = b.getId();
			i++;
		}
		return tempHeadArr;
	}

	/**
	 * Sätter en klicklyssnare på listvyn.
	 */
	public void setItemClickListner() {
		this.lv.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1,
					int itemClicked, long arg3) {

				Intent myIntent = new Intent(AssignmentOverview.this,
						AssignmentDetails.class);

				myIntent.putExtra("assignmentID", idInAdapter[itemClicked]);
				myIntent.putExtra("currentUser", currentUser);
				AssignmentOverview.this.startActivity(myIntent);
			}
		});
	}

	/**
	 * Tillsatt lyssnare i meddelandelistan som lyssnar efterz tryckningar p�
	 * listobjekt
	 */
	public void setLongItemClickListener() {
		// Skapar en lyssnare som lyssnar efter l�nga intryckningar
		this.lv.setOnItemLongClickListener(
		new OnItemLongClickListener() {

			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int eraseAtPos, long arg3) {
				showEraseOption(idInAdapter[eraseAtPos]);
				return true;
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
		alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "JA",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						eraseAssignment(eraseById);
					}
				});
		alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "AVBRYT",
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
		for (ModelInterface m : listAssignments) {
			Assignment a = (Assignment) m;
			if (a.getId() == assignmentId) {
				communicationService.setContext(getApplicationContext());
				db.deleteFromDB(a, getContentResolver());
				// Sätter status för att uppdraget har avslutats.
				a.setAssignmentStatus(AssignmentStatus.FINISHED);

				communicationService.sendAssignment(a);
			}

		}

		loadAssignmentList();
	}

