package com.example.klien_projekttermin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import models.Assignment;
import models.Contact;
import models.MessageModel;

import database.Database;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SimpleAdapter;

public class MainActivity extends ListActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		

		testWriteReadToDB(this);
		
		String[] from = { "line1", "line2" };
		int[] to = { android.R.id.text1, android.R.id.text2 };

		setListAdapter(new SimpleAdapter(this, generateMenuContent(),
				android.R.layout.simple_list_item_2, from, to));
		getListView().setOnItemClickListener(new OnItemClickListener() {

			
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent myIntent;
				//Har man lagt till ett nytt menyval lägger man till en action för dessa här.
				switch (arg2) {
				case 0:
					// myIntent= new Intent(from.this,
					// to.class);
					break;
				case 1:
					// myIntent= new Intent(from.this,
					// to.class);
					break;

				default:
					// myIntent= new Intent(from.this,
					// to.class);
					break;
				}
				// SomeView.this.startActivity(myIntent);
			}

		});
	}
	/**
	 * Genererar de menyval som ska gå att göra.
	 * @return
	 * En List<HashMap<String, String>> där varje map bara har två värden. Ett för första raden och ett för andra.
	 */
	private List<HashMap<String, String>> generateMenuContent(){
		List<HashMap<String, String>>content=new ArrayList<HashMap<String,String>>();
		//Om menyn ska utökas ska man lägga till de nya valen i dessa arrayer. Notera att det krävs en subtitle till varje item.
		String[] menuItems={"Karta","Uppdragshanterare","Kontakter"};
		String[] menuSubtitle={"Visar en karta","Lägg till, ta bort eller ändra uppdrag","Visar kontaktlista"};
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
	
	 public void testWriteReadToDB(Context context){
	    	// Skapa en tom database för att skriva godtycklig datatyp (assignment, contact, message) 
	    	// till rätt databas
			Database db = new Database();

			// Testa contacts
			Contact testContact = new Contact("Nisse", Long.valueOf(12345), "nallecom","A","A","lirare");
			db.addToDB(testContact, context);
			Log.d("DB","Contacts DB size: "+db.getDBCount(testContact, context));

			// Testa assignments
			int w = 100, h = 100;
			Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
			Bitmap bmp = Bitmap.createBitmap(w, h, conf);
			Time time = new Time();
			time.setToNow();
			Assignment testAssignment = new Assignment("uppdrag", Long.valueOf(123456), Long.valueOf(654321), "Mott", "Sandare", "Katt i trad", time,"Status", bmp,"Allgatan 1","Ryd");
			db.addToDB(testAssignment,context);
			Log.d("DB","Assignment DB size: "+db.getDBCount(testAssignment, context));

			// Testa messages
			MessageModel testMessage = new MessageModel("Hej hej", "Kalle",time.toString());
			db.addToDB(testMessage, context);
			Log.d("DB","Message DB size: " + db.getDBCount(testMessage, context));
		}

}
