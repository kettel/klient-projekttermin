package assignment;

import java.util.List;

import models.Assignment;
import models.ModelInterface;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import database.Database;
import com.example.klien_projekttermin.*;
public class AssignmentOverview extends Activity {

	ListView listView;
	String[] assignmentHeadlineArray;
	long[] idInAdapter;
	Database db;
	List<ModelInterface> assList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_assignment_overview);

		db = new Database();
		listView = (ListView) findViewById(R.id.listView1);
		loadAssignmentList();
		setItemClickListner();
		setLongItemClickListener();

	}

	// G�r en custom topmeny.
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// getMenuInflater().inflate(R.menu.activity_uppdragslista, menu);

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.top_menu_bar, menu);
		return true;
	}

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
	}

	public void loadAssignmentList() {

		// H�mtar en stringarray med str�ngar som representerar uppdragen.
		assignmentHeadlineArray = getAssHeadsFromDatabase();

		// // First paramenter - Context
		// // Second parameter - Layout for the row
		// // Third parameter - ID of the TextView to which the data is written
		// // Forth - the Array of data
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, android.R.id.text1,
				assignmentHeadlineArray);

		// // Assign adapter to ListView
		listView.setAdapter(adapter);
	}

	/**
	 * H�mtar en stringarray med str�ngar som representerar uppdragen.
	 * 
	 * @return
	 */
	private String[] getAssHeadsFromDatabase() {

		assList = db.getAllFromDB(new Assignment(), getApplicationContext());
		int i = 0;
		String[] tempHeadArr = new String[assList.size()];

		// H�ller koll p� ID varje position i adaptern erh�ller.
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
	 * S�tter en klicklyssnare p� listvyn.
	 */
	public void setItemClickListner() {
		listView = (ListView) findViewById(R.id.listView1);
		listView.setClickable(true);
		listView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1,
					int itemClicked, long arg3) {

				Intent myIntent = new Intent(AssignmentOverview.this,
						Uppdrag.class);

				// Skickar med ID:t p� det klickade uppdtaget.
				myIntent.putExtra("assignmentID", idInAdapter[itemClicked]);
				AssignmentOverview.this.startActivity(myIntent);
			}
		});
	}

	/*
	 * Tills�tt lyssnare i meddelandelistan som lyssnar efter l�nga tryckningar
	 * p� listobjekt
	 */
	public void setLongItemClickListener() {
		// Skapar en lyssnare som lyssnar efter l�nga intryckningar
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {

			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int eraseAtPos, long arg3) {
				showEraseOption(idInAdapter[eraseAtPos]);
				return true;
			}
		});
	}

	/*
	 * Metoden skapar en dialogruta som fr�gar anv�ndaren om denne vill ta bort
	 * en konversation Metoden ger ocks� anv�ndaren tv� valm�jligheter, JA eller
	 * Avbryt
	 */
	public void showEraseOption(final long eraseById) {
		// final long eraseAssingmentWithId = eraseById;

		AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.setTitle("Radera uppdrag.");
		alertDialog.setMessage("Vill du ta bort uppdraget?");
		alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "JA",
				new DialogInterface.OnClickListener() {

					// Om anv�ndaren trycker p� ja s� k�rs metoden
					// eraseAssignment()
					public void onClick(DialogInterface dialog, int which) {
						eraseAssignment(eraseById);
					}
				});
		alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "AVBRYT",
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						// G�r inget
					}
				});

		alertDialog.show();

	}

	/**
	 * Metoden tar bort ett uppdrag.
	 */
	public void eraseAssignment(long assignmentId) {

		List<ModelInterface> listAssignments = db.getAllFromDB(
				new Assignment(), getApplicationContext());

		for (ModelInterface m : listAssignments) {

			// Konverterar Modelinterfacet till ett Assignment.
			Assignment a = (Assignment) m;

			// J�mf�r ID:T som clickhold:ats p� med befindliga Assignments fr�n
			// databasen och tar bort det uppdraget.
			if (a.getId() == assignmentId) {
				db.deleteFromDB(a, getApplicationContext());
			}

		}

		loadAssignmentList();
	}

}
