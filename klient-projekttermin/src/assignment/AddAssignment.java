package assignment;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import login.User;
import map.CustomAdapter;
import map.MapActivity;
import models.Assignment;
import models.AssignmentPriority;
import models.AssignmentStatus;
import models.Contact;
import models.ModelInterface;
import models.PictureModel;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.Toast;
import camera.Album;
import camera.Cam;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.klient_projekttermin.ActivityConstants;
import com.klient_projekttermin.R;
import com.klient_projekttermin.SecureActivity;
import com.nutiteq.components.WgsPoint;
import communicationModule.SocketConnection;

import contacts.ContactsBookActivity;
import database.Database;

public class AddAssignment extends SecureActivity implements Serializable,
		OnItemClickListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String jsonCoord = null;
	private ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();
	private String[] dataString = { "Uppdragsnamn", "Uppdragsbeskrivning",
			"Uppdragsplats", "Uppskattad tid", "Koordinater", "Prioritet",
			"Bild", "Lägg till agenter", "Inkludera utomstående aktörer" };
	private MenuItem saveItem;
	private String[] from = { "line1" };
	private int[] to = { R.id.text_item };
	private Database db;
	private SimpleEditTextItemAdapter adapter;
	private String currentUser;
	private ListView lv;
	private Bitmap bitmap;
	private int callingActivity;
	private boolean isExternalMission;
	private List<Contact> agents = new ArrayList<Contact>();
	private static String[] priorityAlts = { "Hög", "Normal", "Låg" };
	private static String[] pictureAlts = { "Bifoga bild", "Ta bild",
			"Ingen bild" };
	private static String[] coordsAlts = { "Bifoga koordinater från karta",
			"Använd GPS position", "Inga koordinater" };
	private int prioInteger;

	@Override
	@SuppressLint("UseSparseArrays")
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_assignment);
		lv = (ListView) findViewById(android.R.id.list);
		lv.setOnItemClickListener(this);
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
		} else if (resultCode == ActivityConstants.RESULT_FROM_CONTACTS) {
			fromContact(data);
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

	private void fromContact(Intent i) {
		Gson gson = new Gson();
		Type type = new TypeToken<List<Contact>>() {
		}.getType();
		agents = gson.fromJson(i.getStringExtra("agents"), type);
		setAgents(agents);
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

		HashMap<Integer, String> temp = ((SimpleEditTextItemAdapter) lv
				.getAdapter()).getItemStrings();

		if (temp.get(0) != null) {
			System.out.println("VAD E DENN" + temp.get(1));
			Assignment newAssignment = new Assignment(temp.get(0), temp.get(1),
					currentUser, isExternalMission, temp.get(2), temp.get(6),
					AssignmentStatus.NOT_STARTED, getByteArray(), temp.get(4),
					temp.get(4), checkPrioString(temp.get(3)), prioInteger);

			String tempUnseparated = temp.get(7);

			if (tempUnseparated == null) {
				tempUnseparated = "";
			}

			addAgentsFromList(tempUnseparated, newAssignment);

			tempUnseparated = ""; // Nolla strängen

			newAssignment.setGlobalID(currentUser);

			db.addToDB(newAssignment, getContentResolver());
			SocketConnection connection = new SocketConnection();
			connection.setContext(getApplicationContext());
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
		if (!agents.equals("")) {
			newAssignment.setAssignmentStatus(AssignmentStatus.STARTED);
		}

		List<String> items = new LinkedList<String>(Arrays.asList(agents
				.split("\\s*,\\s*"))); // reguljära uttryck haxx

		List<ModelInterface> list = db.getAllFromDB(new Contact(),
				getContentResolver());

		for (String agent : items) {
			for (ModelInterface modelInterface : list) {
				Contact contact = (Contact) modelInterface;
				if (contact.getContactName().equals(agent)) {
					newAssignment.addAgents(contact);
				}
			}
		}
		items.clear();
	}

	private AssignmentPriority checkPrioString(String prioString) {

		if (prioString == null) {
			prioInteger = 2;
			return AssignmentPriority.PRIO_NORMAL;
		} else if (prioString.equals("Hög prioritet")) {
			prioInteger = 1;
			return AssignmentPriority.PRIO_HIGH;
		} else if (prioString.equals("Normal prioritet")) {
			prioInteger = 2;
			return AssignmentPriority.PRIO_NORMAL;
		} else if (prioString.equals("Låg prioritet")) {
			prioInteger = 3;
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

	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		switch (arg2) {
		case 4:
			coordinateField();
			break;
		case 5:
			priorityAlternatives();
			break;
		case 6:
			pictureAlternatives();
			break;
		case 7:
			agentsAlternatives();
			break;
		case 8:
			CheckedTextView textview = (CheckedTextView) arg1;
			textview.setChecked(!textview.isChecked());
			isExternalMission = textview.isChecked();
		default:
			break;
		}
	}

	private void priorityAlternatives() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Välj uppdragets prioritet");
		ListView modeList = new ListView(this);
		CustomAdapter modeAdapter = new CustomAdapter(this,
				android.R.layout.simple_list_item_1, android.R.id.text1,
				priorityAlts);
		modeList.setAdapter(modeAdapter);
		builder.setView(modeList);
		final Dialog dialog = builder.create();
		modeList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				dialog.dismiss();
				adapter.getItemStrings().put(5, priorityAlts[arg2]);
				adapter.notifyDataSetChanged();
			}
		});
		dialog.show();
	}

	private void coordinateField() {
		LocationManager manager = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);
		String provider = manager.getBestProvider(new Criteria(), true);
		Location location = manager.getLastKnownLocation(provider);
		Gson gson = new Gson();
		final Type type = new TypeToken<WgsPoint[]>() {
		}.getType();
		WgsPoint[] wgs = new WgsPoint[1];
		wgs[0] = new WgsPoint(location.getLatitude(), location.getLongitude());

		final String pos = gson.toJson(wgs, type);

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Koordinater");
		ListView modeList = new ListView(this);
		CustomAdapter modeAdapter = new CustomAdapter(this,
				android.R.layout.simple_list_item_1, android.R.id.text1,
				coordsAlts);
		modeList.setAdapter(modeAdapter);
		builder.setView(modeList);
		final Dialog dialog = builder.create();
		modeList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				dialog.dismiss();
				switch (arg2) {
				case 0:
					dialog.dismiss();
					Intent intent = new Intent(AddAssignment.this,
							MapActivity.class);
					intent.putExtra("calling-activity",
							ActivityConstants.ADD_COORDINATES_TO_ASSIGNMENT);
					startActivityForResult(intent, 0);
					break;
				case 1:
					adapter.getItemStrings().put(4, pos);
					adapter.notifyDataSetChanged();
					break;
				default:
					break;
				}
			}
		});
		dialog.show();
	}

	private void pictureAlternatives() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Bild alternativ");
		ListView modeList = new ListView(this);
		CustomAdapter modeAdapter = new CustomAdapter(this,
				android.R.layout.simple_list_item_1, android.R.id.text1,
				pictureAlts);
		modeList.setAdapter(modeAdapter);
		builder.setView(modeList);

		final Dialog dialog = builder.create();
		modeList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				dialog.dismiss();

				switch (arg2) {
				case 0:
					Intent intent = new Intent(AddAssignment.this, Album.class);
					intent.putExtra("calling-activity",
							ActivityConstants.ADD_PICTURE_TO_ASSIGNMENT);
					startActivityForResult(intent, 1);
					break;
				case 1:
					Intent intent2 = new Intent(AddAssignment.this, Cam.class);
					intent2.putExtra("calling-activity",
							ActivityConstants.TAKE_PICTURE_FOR_ASSIGNMENT);
					startActivityForResult(intent2, 2);
					break;
				default:
					break;
				}
			}
		});
		dialog.show();
	}

	private void agentsAlternatives() {
		Intent intent = new Intent(AddAssignment.this,
				ContactsBookActivity.class);
		intent.putExtra("calling-activity", ActivityConstants.ADD_AGENTS);
		this.startActivityForResult(intent, 1);
	}

	public void setAgents(List<Contact> l) {
		StringBuilder sb = new StringBuilder();
		for (Contact contact : l) {
			sb.append(contact.getContactName() + ", ");
		}
		adapter.getItemStrings().put(7, sb.toString());
		adapter.notifyDataSetChanged();
	}
}
