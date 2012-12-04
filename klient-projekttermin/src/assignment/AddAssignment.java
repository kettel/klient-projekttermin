package assignment;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import login.User;
import map.MapActivity;
import models.Assignment;
import models.AssignmentPriority;
import models.AssignmentStatus;
import models.Contact;
import models.ModelInterface;
import models.PictureModel;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;

import com.klient_projekttermin.ActivityConstants;
import com.klient_projekttermin.R;
import com.klient_projekttermin.SecureActivity;
import communicationModule.SocketConnection;

import database.Database;

public class AddAssignment extends SecureActivity implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String jsonCoord = null;
	private ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();
	private String[] dataString = { "Uppdragsnamn", "Koordinater",
			"Uppdragsbeskrivning", "Uppskattad tid", "Uppdragsplats", "Bild",
			"Prioritet", "Lägg till agenter" };
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

	@Override
	@SuppressLint("UseSparseArrays")
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_assignment);
		lv = (ListView) findViewById(android.R.id.list);
		loadContent();

		adapter = new SimpleEditTextItemAdapter(this, data,
				R.layout.textfield_item, from, to);
		this.lv.setAdapter(adapter);

		Intent i = getIntent();
		callingActivity = i.getIntExtra("calling-activity", 0);

		User user = User.getInstance();
		currentUser = user.getAuthenticationModel().getUserName();

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
		int id = intent.getIntExtra("pic", 0);
		bitmap = getPic(id);
		adapter.textToItem(5, "Bifogad bild");
		runOnUiThread(new Runnable() {
			public void run() {
				adapter.notifyDataSetChanged();
			}
		});
	}

	private void fromMap(Intent intent) {
		jsonCoord = intent.getStringExtra(MapActivity.coordinates);
		String name = intent.getStringExtra("name");
		System.out.println(name);
		System.out.println(jsonCoord);
		adapter.textToItem(4, name);
		adapter.textToItem(1, jsonCoord);
		runOnUiThread(new Runnable() {
			public void run() {
				adapter.notifyDataSetChanged();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_add_assignment, menu);
		this.saveItem = menu.findItem(R.id.save);
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

		// ------Kollar om det är ett externt uppdrag;
		toOutsiders = (CheckBox) findViewById(R.id.checkBox_to_outsider);
		if (toOutsiders.isChecked()) {
			isExternalMission = true;
		} else
			isExternalMission = false;
		// -----End

		HashMap<Integer, String> temp = ((SimpleEditTextItemAdapter) lv
				.getAdapter()).getItemStrings();

		if (temp.get(0) != null) {
			Assignment newAssignment = new Assignment(temp.get(0), temp.get(1),
					currentUser, isExternalMission, temp.get(2), temp.get(3),
					AssignmentStatus.NOT_STARTED, getByteArray(), temp.get(4),
					temp.get(4), checkPrioString(temp.get(6)));

			String tempUnseparated = temp.get(7);

			if (tempUnseparated == null) {
				tempUnseparated = "";
			}

			addAgentsFromList(tempUnseparated, newAssignment); // temp(8) är en
																// sträng
																// med agenter
																// som
																// ska
																// separeras med
																// ",".

			tempUnseparated = ""; // Nolla strängen

			Log.d("Assignment",
					"Ska nu lägga till ett uppdrag " + temp.get(0)
							+ temp.get(1) + currentUser + false + temp.get(2)
							+ temp.get(3) + AssignmentStatus.NOT_STARTED
							+ "byteArray" + temp.get(4) + temp.get(5));

			db.addToDB(newAssignment, getContentResolver());
			SocketConnection connection = new SocketConnection();
			connection.sendModel(newAssignment);
			finish();
		} else {
			Toast toast = Toast.makeText(getApplicationContext(),
					"Kan inte skapa uppdrag utan namn", Toast.LENGTH_LONG);
			toast.setGravity(Gravity.CENTER_VERTICAL, 0, 50);
			toast.show();
		}

	}

	private void addAgentsFromList(String agents, Assignment newAssignment) {

		String newString = "";

		if (!agents.equals("")) {
			newString = agents.substring(9);
			newAssignment.setAssignmentStatus(AssignmentStatus.STARTED);
		}

		List<String> items = new LinkedList<String>(Arrays.asList(newString
				.split("\\s*,\\s*"))); // reguljära uttryck haxx

		for (String string : items) {
		}
		List<ModelInterface> list = db.getAllFromDB(new Contact(),
				getContentResolver());
		Set<String> noDoublicatesSet = new HashSet<String>(items);

		for (String agent : noDoublicatesSet) {
			for (ModelInterface modelInterface : list) {
				Contact contact = (Contact) modelInterface;
				if (contact.getContactName().equals(agent)) {
					newAssignment.addAgents(new Contact(agent));
				}
			}
		}
		items.clear();
		noDoublicatesSet.clear();
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

	private Bitmap getPic(int id) {
		System.out.println(id);
		db = Database.getInstance(getApplicationContext());
		List<ModelInterface> pics = db.getAllFromDB(new PictureModel(),
				getContentResolver());
		PictureModel p = (PictureModel) pics.get(id);
		BitmapFactory.Options ops = new BitmapFactory.Options();
		ops.inSampleSize = 2;
		Bitmap bitmap = BitmapFactory.decodeByteArray(p.getPicture(), 0,
				p.getPicture().length, ops);
		return bitmap;
	}
}
