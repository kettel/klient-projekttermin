package com.example.klien_projekttermin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.example.klien_projekttermin.databaseNewProviders.Contact.Contacts;
import com.example.klien_projekttermin.databaseNewProviders.ContactsDB;
import com.example.klien_projekttermin.databaseProvider.Database;

import map.MapActivity;
import messageFunction.Inbox;
import models.Contact;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SimpleAdapter;
import assignment.AssignmentOverview;

public class MainActivity extends ListActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		String[] from = { "line1", "line2" };
		int[] to = { android.R.id.text1, android.R.id.text2 };
		testDB();
		setListAdapter(new SimpleAdapter(this, generateMenuContent(),
				android.R.layout.simple_list_item_2, from, to));
		getListView().setOnItemClickListener(new OnItemClickListener() {
			Intent myIntent = null;
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				//Har man lagt till ett nytt menyval lägger man till en action för dessa här.
				switch (arg2) {
				case 0:
					myIntent = new Intent(MainActivity.this,MapActivity.class);
					break;
				case 1:
					myIntent = new Intent(MainActivity.this,Inbox.class);
					break;
				case 2:
					myIntent = new Intent(MainActivity.this,AssignmentOverview.class);
					break;
				default:
					break;
				}
				MainActivity.this.startActivity(myIntent);
			}

		});
	}
	private void testDB() {
    	ContactsDB db = ContactsDB.getInstance(getApplicationContext());
    	db.addContact(getContentResolver(), "Titeluran");
    	Log.d("DB","Hur många notes i db: " + Integer.toString(db.getCount(getContentResolver())));
    	//Log.d("DB", "Alla namn: " + db.getAll(getContentResolver()));
    	Cursor cursor = getContentResolver().query(
    			Contacts.CONTENT_URI, null,Contacts.CONTACT_ID + " IS NOT null", null, null);
    	Log.d("DB","Cursorstorlek: " + cursor.getCount());
    	String ret = new String();
    	if (cursor.moveToFirst()) {
			do {
				Log.d("DB","ID: " + Integer.toString(cursor.getInt(0)));
				Log.d("DB","Namn: " + cursor.getString(1));
				ret += cursor.getString(1);
			} while (cursor.moveToNext());
    	}
    	Log.d("DB","Alla namn: " + ret);
    	cursor.close();
	}
	/**
	 * Genererar de menyval som ska gå att göra.
	 * @return
	 * En List<HashMap<String, String>> där varje map bara har två värden. Ett för första raden och ett för andra.
	 */
	private List<HashMap<String, String>> generateMenuContent(){
		List<HashMap<String, String>>content=new ArrayList<HashMap<String,String>>();
		//Om menyn ska utökas ska man lägga till de nya valen i dessa arrayer. Notera att det krävs en subtitle till varje item.
		String[] menuItems={"Karta","Meddelanden", "Uppdragshanteraren"};
		String[] menuSubtitle={"Visar en karta","Visar Inkorgen", ""};
		//Ändra inget här under
		for (int i = 0; i < menuItems.length; i++) {
			HashMap<String, String> hashMap = new HashMap<String, String>();
			hashMap.put("line1",menuItems[i] );
			hashMap.put("line2",menuSubtitle[i]);
			content.add(hashMap);
		}
		return content;
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
}
