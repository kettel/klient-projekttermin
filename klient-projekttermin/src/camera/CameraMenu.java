package camera;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.klient_projekttermin.ActivityConstants;
import com.klient_projekttermin.R;
import com.klient_projekttermin.SecureActivity;

public class CameraMenu extends SecureActivity {

	private String[] from = { "line1", "line2" };
	private int[] to = { android.R.id.text1, android.R.id.text2 };

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_camera_menu);
		ListView lv = (ListView) findViewById(android.R.id.list);
		lv.setAdapter(new SimpleAdapter(this, generateMenuContent(),
				android.R.layout.simple_list_item_2, from, to));
		lv.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				switch (arg2) {
				case 0:
					Intent myIntent2 = new Intent(CameraMenu.this, Cam.class);
					CameraMenu.this.startActivity(myIntent2);
					break;
				case 1:
					Intent i = new Intent(CameraMenu.this, Album.class);
					i.putExtra("calling-activity", ActivityConstants.CAMERA);
					CameraMenu.this.startActivity(i);
					break;
				default:
					break;
				}

			}
		});
	}

	private List<HashMap<String, String>> generateMenuContent() {
		List<HashMap<String, String>> content = new ArrayList<HashMap<String, String>>();
		String[] menuItems = { "Kamera", "Album" };
		String[] menuSubtitle = { "Ta bilder", "Visa upp bilder" };
		for (int i = 0; i < menuItems.length; i++) {
			HashMap<String, String> hashMap = new HashMap<String, String>();
			hashMap.put("line1", menuItems[i]);
			hashMap.put("line2", menuSubtitle[i]);
			content.add(hashMap);
		}
		return content;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_camera_menu, menu);
		return true;
	}

}
