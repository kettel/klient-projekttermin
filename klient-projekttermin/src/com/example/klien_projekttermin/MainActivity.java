package com.example.klien_projekttermin;

import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;

import logger.LogViewer;
import logger.logger;
import models.Assignment;
import models.Contact;
import models.MessageModel;
import models.ModelInterface;

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

	public static final String LOGCONTENT = "com.exampel.klien_projekttermin";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		final logger testlogger = new logger((Context)this,"log.txt"); 
		String[] from = { "line1", "line2" };
		final Intent openLoggerIntent = new Intent(this, LogViewer.class);
		int[] to = { android.R.id.text1, android.R.id.text2 };
		
		// Testa DB
		testDB(this);
		
		setListAdapter(new SimpleAdapter(this, generateMenuContent(),
				android.R.layout.simple_list_item_2, from, to));
		getListView().setOnItemClickListener(new OnItemClickListener() {

			
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent myIntent;
				//Har man lagt till ett nytt menyval lägger man till en action för dessa här.
				switch (arg2) {
				case 0:
					try {
						testlogger.writeToLog("Nisse","testentry 1");
						testlogger.writeToLog(null,"testentry 2");
						testlogger.writeToLog("Nisse","testentry 3");
					} catch (Exception e) {
					}
					// myIntent= new Intent(from.this,
					// to.class);
					break;
				case 1:
					// myIntent= new Intent(from.this,
					// to.class);
					break;
				case 2:
					// myIntent= new Intent(from.this,
					// to.class);
					break;
				case 3:
					try {
						openLoggerIntent.putExtra(LOGCONTENT,testlogger.readFromLog());
						startActivity(openLoggerIntent);
					} catch (Exception e) {
					}
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
	
	public void testDB(Context context){
		Database db = new Database();
		db.addToDB(new Contact("Nise",Long.valueOf("0130123"),"nisse@gdsasdf","s","A","Skön lirare"),context);
		Bitmap fakeImage = null;
		db.addToDB(new Assignment("Katt i träd", Long.valueOf("12423423"),Long.valueOf("23423425"),"Kalle", "Nisse", "En katt i ett träd", "2 dagar", "Ej påbörjat", fakeImage, "Alstättersgata", "Lekplats"),context);
		db.addToDB(new MessageModel("Hejsan svejsan jättemycket!!!", "Kalle"),context);

		// Testa att hämta från databasen
		List<ModelInterface> testList = db.getAllFromDB(new MessageModel(),context);
		for (ModelInterface m : testList) {
			// Hämta gammalt meddelande
			MessageModel mess = (MessageModel) m;

			// Skapa ett uppdaterat meddelande
			MessageModel messUpdate = new MessageModel(mess.getId(), "mjuhu","höns",mess.getMessageTimeStamp(),mess.isRead());

			// Skriv det uppdaterade objektet till databasen
			db.updateModel(messUpdate,context);
		}

		testList = db.getAllFromDB(new Contact(),context);
		for (ModelInterface m : testList) {
			Contact cont = (Contact) m;

			Contact contUpd = new Contact(cont.getId(),"Nise",Long.valueOf("0130123"),"nisse@gdsasdf","s","A","Dålig lirare");
			db.updateModel(contUpd,context);
		}


		testList = db.getAllFromDB(new Assignment(),context);
		for (ModelInterface m : testList) {
			Assignment ass = (Assignment) m;

			Assignment assUpd = new Assignment(ass.getId(),"Katt i hav", Long.valueOf("12423423"),Long.valueOf("23423425"),"Kalle", "Nisse", "En katt i ett träd", "2 dagar", "Ej påbörjat", fakeImage, "Alstättersgata", "Lekplats");

			db.updateModel(assUpd,context);
		}

		Log.d("DB","Antal meddelanden: " + db.getDBCount(new MessageModel(),context));
		Log.d("DB","Antal kontakter: " + db.getDBCount(new Contact(),context));
		Log.d("DB","Antal uppdrag: " + db.getDBCount(new Assignment(),context));
	}
	

}
