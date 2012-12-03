package assignment;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import logger.logger;
import loginFunction.InactivityListener;
import map.MapActivity;
import models.Assignment;
import models.AssignmentPriority;
import models.AssignmentStatus;
import models.Contact;
import models.ModelInterface;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.ListView;
import camera.PhotoGallery;
import com.klient_projekttermin.ActivityConstants;
import com.klient_projekttermin.R;
import communicationModule.CommunicationService;
import communicationModule.CommunicationService.CommunicationBinder;
import database.Database;

public class AddAssignment extends InactivityListener implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// --------ComService
	private CommunicationService communicationService;
	private boolean communicationBond = false;
	// ----End
	private String jsonCoord = null;
	private String jsonPict = null;
	private ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();
	private String[] dataString = { "Uppdragsnamn", "Koordinater",
			"Uppdragsbeskrivning", "Uppskattad tid", "Gatuadress",
			"Uppdragsplats", "Bild", "Prioritet", "Lägg till agenter" };
	private MenuItem saveItem;
	private String[] from = { "line1" };
	private int[] to = { R.id.text_item };
	private Database db;
	private SimpleEditTextItemAdapter adapter;
	private String currentUser;
	private ListView lv;
	private Bitmap bitmap;
	private int callingActivity;
	private CheckBox toOutsiders;
	private boolean isExternalMission;

	@SuppressLint("UseSparseArrays")
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// ----ComService
		Intent intent = new Intent(this.getApplicationContext(),
				CommunicationService.class);
		bindService(intent, communicationServiceConnection,
				Context.BIND_AUTO_CREATE);
		// ---End
		setContentView(R.layout.activity_add_assignment);
		lv = (ListView) findViewById(android.R.id.list);
		loadContent();

		adapter = new SimpleEditTextItemAdapter(this, data,
				R.layout.textfield_item, from, to);
		this.lv.setAdapter(adapter);

		Intent i = getIntent();
		callingActivity = i.getIntExtra("calling-activity", 0);

		switch (callingActivity) {
		case ActivityConstants.MAP_ACTIVITY:
			fromMap(i);
			break;
		case ActivityConstants.ADD_PICTURE_TO_ASSIGNMENT:
			fromCamera(i);
			break;
		default:
			break;
		}

	}

	@Override
	protected void onNewIntent(Intent intent) {
		setIntent(intent);
		super.onNewIntent(intent);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == ActivityConstants.RESULT_FROM_MAP) {
			fromMap(data);
		} else if (resultCode == ActivityConstants.RESULT_FROM_CAMERA) {
			fromCamera(data);
		}
	}

	private void loadContent() {
		data.clear();
		for (String s : dataString) {
			HashMap<String, String> temp = new HashMap<String, String>();
			temp.put(from[0], s);
			data.add(temp);
		}
	}

	private void fromCamera(Intent intent) {
		bitmap = (Bitmap) intent.getExtras()
				.getParcelable(PhotoGallery.picture);
		jsonPict = "Bifogad bild";
		adapter.textToItem(6, jsonPict);
		runOnUiThread(new Runnable() {
			public void run() {
				adapter.notifyDataSetChanged();
			}
		});
	}

	private void fromMap(Intent intent) {
		jsonCoord = intent.getStringExtra(MapActivity.coordinates);
		adapter.textToItem(1, jsonCoord);
		runOnUiThread(new Runnable() {
			public void run() {
				adapter.notifyDataSetChanged();
			}
		});
	}

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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_add_assignment, menu);
		this.saveItem = menu.findItem(R.id.save);
		communicationService.setContext(getApplicationContext()); // --ComService
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.equals(saveItem)) {
			saveToDB();
		}
		return super.onOptionsItemSelected(item);
	}

	private void saveToDB() {
		db = Database.getInstance(getApplicationContext());
		
		//------Kollar om det är ett externt uppdrag;
		toOutsiders = (CheckBox) findViewById(R.id.checkBox_to_outsider);
		if (toOutsiders.isChecked()) {
			isExternalMission = true;
		}
		else
			isExternalMission = false;
		//-----End
		

		HashMap<Integer, String> temp = ((SimpleEditTextItemAdapter) lv
				.getAdapter()).getItemStrings();
		
		Assignment newAssignment = new Assignment(temp.get(0), temp.get(1),
				currentUser, isExternalMission, temp.get(2), temp.get(3),
				AssignmentStatus.NOT_STARTED, getByteArray(), temp.get(4),
				temp.get(5), checkPrioString(temp.get(7)));

		Log.e("FEL", "Det är ett externt uppdrag: " + newAssignment.isExternalMission());
		System.out.println("temp8: " + temp.get(8));
		String tempUnseparated = temp.get(8);

		if (tempUnseparated == null) {
			tempUnseparated = "";
		}

		addAgentsFromList(tempUnseparated, newAssignment); // temp(8) är en
															// sträng
															// med agenter som
															// ska
															// separeras med
															// ",".

		tempUnseparated = ""; // Nolla strängen

		Log.d("Assignment", "Ska nu lägga till ett uppdrag " + temp.get(0)
				+ temp.get(1) + currentUser + false + temp.get(2) + temp.get(3)
				+ AssignmentStatus.NOT_STARTED + "byteArray" + temp.get(4)
				+ temp.get(5));

		db.addToDB(newAssignment, getContentResolver());
		communicationService.sendAssignment(newAssignment);
		finish();
	}

	private void addAgentsFromList(String agents, Assignment newAssignment) {

		String newString = "";
		
		if (!agents.equals("")) {
			newString = agents.substring(9);
		}
		
		Log.e("FEL", "Rätt split? ->" + newString);

		List<String> items = new LinkedList<String>(Arrays.asList(newString
				.split("\\s*,\\s*"))); // reguljära uttryck haxx

		for (String string : items) {
			Log.e("FEL", "Regexplittade agents: " + string);
		}

		List<ModelInterface> list = db.getAllFromDB(new Contact(),
				getContentResolver());

		for (String agent : items) {
			for (ModelInterface modelInterface : list) {
				Contact contact = (Contact) modelInterface;
				if (contact.getContactName().equals(agent)) {
					newAssignment.addAgents(new Contact(agent));
					Log.e("FEL",
							"Lägger till agenter i add assignment från cpadaptern, ska va 2: ");
				}
			}
		}
		items.clear();
	}

	private AssignmentPriority checkPrioString(String prioString) {

		if (prioString == null) {
			return AssignmentPriority.PRIO_NORMAL;
		} else if (prioString.equals("Hög prioritet")) {
			return AssignmentPriority.PRIO_HIGH;
		} else if (prioString.equals("Normal prioritet")) {
			return AssignmentPriority.PRIO_NORMAL;
		} else if (prioString.equals("Låg prioritet")) {
			return AssignmentPriority.PRIO_LOW;
		} else
			return AssignmentPriority.PRIO_NORMAL;

	}

	private byte[] getByteArray() {
		if (bitmap != null) {
			ByteArrayOutputStream byteArrayBitmapStream = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.PNG, 100,
					byteArrayBitmapStream);
			byte[] b = byteArrayBitmapStream.toByteArray();
			return b;
		} else {
			return new byte[2];
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (communicationBond)
			unbindService(communicationServiceConnection);
	}

}
