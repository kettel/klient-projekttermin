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
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleAdapter;

import com.example.klien_projekttermin.R;
import com.example.klien_projekttermin.database.Database;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nutiteq.components.WgsPoint;

public class AddAssignment extends ListActivity {

	private Database db;
	private Button addAssignmentButton;
	private EditText assignmentName;
	private EditText assignmentDescription;
	private EditText assignmentTime;
	private EditText assignmentStreetName;
	private EditText assignmentSpot;
	double lat = 0;
	double lon = 0;
	private EditText assignmentCoord;
	private String json;
	private String coordinates;
	private String[] from={"line1","line2"};
	private int[] to={R.id.editText1,R.id.textView1};
	private List<HashMap<String, String>> data=new ArrayList<HashMap<String,String>>();
	private String[] dataString={"Name","coord","Uppdragsbeskrivning","uppskattadtid","gatuadress","uppdragsplats"};


	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_add_assignment);
		addContent();
		SimpleAdapter adapter=new SimpleAdapter(this, data, R.layout.textfield_item, from, to);
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
	private void addContent(){
		data.clear();
		for (String s : dataString) {
			HashMap<String, String> temp=new HashMap<String, String>();
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
				if (!assignmentName.getText().toString().equals("")) {
					Assignment newAssignment = new Assignment("niko", json, "self", false, "HEJ", "12", AssignmentStatus.NEED_HELP, null, "HEJ", "HEJ"); 
					db.addToDB(newAssignment, getBaseContext());
				}

				// Stänger aktiviteten.
				finish();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_add_assignment, menu);
		return true;
	}
}

