package assignment;

import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import map.MapActivity;
import models.Assignment;
import models.AssignmentStatus;
import android.app.ListActivity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.klien_projekttermin.R;
import com.example.klien_projekttermin.database.Database;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nutiteq.components.WgsPoint;
import communicationModule.CommunicationService;
import communicationModule.CommunicationService.CommunicationBinder;

public class AddAssignment extends ListActivity {

	private Database db;
	private CommunicationService communicationService;
	private boolean communicationBond = false;
	private Button addAssignmentButton;
	private EditText assignmentName;
	private EditText assignmentDescription;
	private EditText assignmentTime;
	private EditText assignmentStreetName;
	private EditText assignmentSpot;
	private EditText assignmnetCoords;

	double lat = 0;
	double lon = 0;
	private EditText assignmentCoord;
	private String json;
	private String coordinates;
	private String[] from={"line1","line2"};
	private int[] to={R.id.editText1,R.id.textView1};
	private List<HashMap<String, String>> data=new ArrayList<HashMap<String,String>>();
	private String[] dataString={"Name","coord","Uppdragsbeskrivning","uppskattadtid","gatuadress","uppdragsplats"};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_add_assignment);
		addContent();
		SimpleEditTextItemAdapter adapter = new SimpleEditTextItemAdapter(this,
				data, R.layout.textfield_item, from, to);
		setListAdapter(adapter);
		Intent intent = getIntent();
		json = intent.getStringExtra(MapActivity.coordinates);
		Gson gson = new Gson();
		Type type = new TypeToken<WgsPoint[]>() {}.getType();
		WgsPoint[] co = gson.fromJson(json, type);
		setContentView(R.layout.activity_add_assignment);

		/**
		 * Lägg till detta i koordinat fältet
		 */
		StringBuilder sb = new StringBuilder();
		for (WgsPoint wgsPoint : co) {
			sb.append(wgsPoint.getLat() + " , " + wgsPoint.getLon());
		}


		db = new Database();
	}

	private void addContent() {
		data.clear();
		for (String s : dataString) {
			HashMap<String, String> temp = new HashMap<String, String>();
			temp.put("line1", "hint");
			temp.put("line2", s);
			data.add(temp);
		}
	}

	/**
	 * L�gger till lyssnare till "add-uppdrag-knappen".
	 * 
	 * @param button
	 */
	public void setButtonClickListnerAddAssignment(Button button) {

		// Skapar en humbug-bitmap.
		Bitmap.Config conf = Bitmap.Config.ARGB_8888;
		final Bitmap fakeImage = Bitmap.createBitmap(100, 100, conf);

		// Lyssnar efter klick.
		button.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				communicationService.setContext(getApplicationContext());

				if (!assignmentName.getText().toString().equals("")) {
					Assignment newAssignment = new Assignment("niko", json, "self", false, "HEJ", "12", AssignmentStatus.NEED_HELP, null, "HEJ", "HEJ"); 
					db.addToDB(newAssignment, getApplicationContext());
					communicationService.sendAssignment(newAssignment);
				}

				// Stänger aktiviteten.
				finish();
			}
		});
	}

	private ServiceConnection communicationServiceConnection = new ServiceConnection() {

		public void onServiceDisconnected(ComponentName arg0) {
			communicationBond = false;
		}

		public void onServiceConnected(ComponentName name, IBinder service) {
			CommunicationBinder binder = (CommunicationBinder) service;
			communicationService = binder.getService();
			communicationBond = true;

		}

	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_add_assignment, menu);
		return true;
	}
}

