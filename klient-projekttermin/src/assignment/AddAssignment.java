package assignment;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import loginFunction.InactivityListener;
import map.MapActivity;
import models.Assignment;
import models.AssignmentPriority;
import models.AssignmentStatus;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import camera.PhotoGallery;

import com.example.klien_projekttermin.ActivityConstants;
import com.example.klien_projekttermin.R;
import com.example.klien_projekttermin.database.Database;
import communicationModule.CommunicationService;
import communicationModule.CommunicationService.CommunicationBinder;

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
			"Uppdragsplats", "Bild", "Prioritet" };
	private MenuItem saveItem;
	private String[] from = { "line1" };
	private int[] to = { R.id.text_item };
	private Database db;
	private SimpleEditTextItemAdapter adapter;
	private String currentUser;
	private ListView lv;
	private Bitmap bitmap;

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
		System.out.println(jsonCoord);
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
		
		HashMap<Integer, String> temp = ((SimpleEditTextItemAdapter) lv
				.getAdapter()).getItemStrings();
		Assignment newAssignment = new Assignment(temp.get(0), temp.get(1),
				currentUser, false, temp.get(2), temp.get(3),
				AssignmentStatus.NOT_STARTED, getByteArray(),
				temp.get(4), temp.get(5), checkPrioString(temp.get(7)));
		
		db.addToDB(newAssignment, getContentResolver());
		communicationService.sendAssignment(newAssignment);
		finish();
	}

	
	private AssignmentPriority checkPrioString(String prioString) {

		if (prioString.equals("Hög prioritet")) {
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
