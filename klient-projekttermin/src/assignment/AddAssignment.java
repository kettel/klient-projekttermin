package assignment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import loginFunction.InactivityListener;
import map.MapActivity;
import models.Assignment;
import models.AssignmentPriority;
import models.AssignmentStatus;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;
import camera.Album;

import com.klient_projekttermin.ActivityConstants;
import com.klient_projekttermin.R;
import communicationModule.SocketConnection;

import database.Database;

public class AddAssignment extends InactivityListener implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String jsonCoord = null;
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
	private int callingActivity;

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
		int id = intent.getIntExtra(Album.pic, 0);
		bitmap = getPic(id);
		adapter.textToItem(6, "Bifogad bild");
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

		HashMap<Integer, String> temp = ((SimpleEditTextItemAdapter) lv
				.getAdapter()).getItemStrings();
		if(temp.get(0) != null){
		Assignment newAssignment = new Assignment(temp.get(0), temp.get(1),
				currentUser, false, temp.get(2), temp.get(3),
				AssignmentStatus.NOT_STARTED, getByteArray(), temp.get(4),
				temp.get(5), checkPrioString(temp.get(7)));

		Log.d("Assignment", "Ska nu lägga till ett uppdrag " + temp.get(0)
				+ temp.get(1) + currentUser + false + temp.get(2) + temp.get(3)
				+ AssignmentStatus.NOT_STARTED + "byteArray" + temp.get(4)
				+ temp.get(5));

		db.addToDB(newAssignment, getContentResolver());
		
		SocketConnection connection=new SocketConnection();
		connection.sendModel(newAssignment);
		} else {
			Toast toast = Toast.makeText(getApplicationContext(),
					"Kan inte skapa uppdrag utan namn",
					Toast.LENGTH_LONG);
			toast.setGravity(Gravity.TOP, 0, 50);
			toast.show();
		}
		finish();
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
		System.out.println(bitmap);
		if (bitmap != null) {
			ByteArrayOutputStream byteArrayBitmapStream = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.PNG, 100,
					byteArrayBitmapStream);
			System.out.println("BITMAP TO BYTE[]");
			byte[] b = byteArrayBitmapStream.toByteArray();
			System.out.println(b);
			return b;
		} else {
			return new byte[2];
		}
	}
	
	private Bitmap getPic(int id){
		File file = new File(Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/Pictures/Album/");
		File imageList[] = file.listFiles();
		ArrayList<Bitmap> images = new ArrayList<Bitmap>();
		BitmapFactory.Options bitop = new BitmapFactory.Options();
		bitop.inSampleSize = 16;
		for (int i = 0; i < imageList.length; i++) {
			Bitmap b = BitmapFactory.decodeFile(imageList[i].getAbsolutePath(),
					bitop);
			images.add(b);
		}
		return images.get(id);
	}
}
