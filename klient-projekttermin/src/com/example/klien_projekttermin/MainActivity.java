package com.example.klien_projekttermin;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import com.example.klien_projekttermin.databaseEncrypted.Database;
import com.example.klien_projekttermin.databaseProvider.NotesDB;
import com.example.klien_projekttermin.logger.LogViewer;
import com.example.klien_projekttermin.logger.logger;
import com.example.klien_projekttermin.models.Assignment;
import com.example.klien_projekttermin.models.Contact;
import com.example.klien_projekttermin.models.MessageModel;
import com.example.klien_projekttermin.models.ModelInterface;


import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
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
		long timer = Calendar.getInstance().getTimeInMillis();
		testDBProvider(this);
		timer = Calendar.getInstance().getTimeInMillis() - timer;
		Log.d("DB", "Exekveringstid: " + Long.toString(timer));
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

	public void testDBFull(Context context){
		Database db = new Database();
		db.addToDB(new Contact("Nise",Long.valueOf("0130123"),"nisse@gdsasdf","s","A","Skön lirare"),context);
		int w = 600, h = 600;
		Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
		Bitmap fakeImage = Bitmap.createBitmap(w, h, conf);
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

			db.deleteFromDB(messUpdate, context);
		}

		testList = db.getAllFromDB(new Contact(),context);
		for (ModelInterface m : testList) {
			Contact cont = (Contact) m;

			Contact contUpd = new Contact(cont.getId(),"Nise",Long.valueOf("0130123"),"nisse@gdsasdf","s","A","Dålig lirare");
			db.updateModel(contUpd,context);

			db.deleteFromDB(contUpd, context);
		}


		testList = db.getAllFromDB(new Assignment(),context);
		for (ModelInterface m : testList) {
			Assignment ass = (Assignment) m;

			Assignment assUpd = new Assignment(ass.getId(),"Katt i hav", Long.valueOf("12423423"),Long.valueOf("23423425"),"Kalle", "Nisse", "En katt i ett träd", "2 dagar", "Ej påbörjat", fakeImage, "Alstättersgata", "Lekplats");

			db.updateModel(assUpd,context);

			db.deleteFromDB(assUpd, context);
		}

		Log.d("DB","Antal meddelanden: " + db.getDBCount(new MessageModel(),context));
		Log.d("DB","Antal kontakter: " + db.getDBCount(new Contact(),context));
		Log.d("DB","Antal uppdrag: " + db.getDBCount(new Assignment(),context));
	}
	
	public void testDBFullSingleton(Context context){
		com.example.klien_projekttermin.databaseEncryptedSingleton.Database db = com.example.klien_projekttermin.databaseEncryptedSingleton.Database.getInstance(context);
		db.addToDB(new Contact("Nise",Long.valueOf("0130123"),"nisse@gdsasdf","s","A","Skön lirare"));
		int w = 600, h = 600;
		Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
		Bitmap fakeImage = Bitmap.createBitmap(w, h, conf);
		db.addToDB(new Assignment("Katt i träd", Long.valueOf("12423423"),Long.valueOf("23423425"),"Kalle", "Nisse", "En katt i ett träd", "2 dagar", "Ej påbörjat", fakeImage, "Alstättersgata", "Lekplats"));
		db.addToDB(new MessageModel("Hejsan svejsan jättemycket!!!", "Kalle"));

		// Testa att hämta från databasen
		List<ModelInterface> testList = db.getAllFromDB(new MessageModel());
		for (ModelInterface m : testList) {
			// Hämta gammalt meddelande
			MessageModel mess = (MessageModel) m;

			// Skapa ett uppdaterat meddelande
			MessageModel messUpdate = new MessageModel(mess.getId(), "mjuhu","höns",mess.getMessageTimeStamp(),mess.isRead());

			// Skriv det uppdaterade objektet till databasen
			db.updateModel(messUpdate);

			db.deleteFromDB(messUpdate);
		}

		testList = db.getAllFromDB(new Contact());
		for (ModelInterface m : testList) {
			Contact cont = (Contact) m;

			Contact contUpd = new Contact(cont.getId(),"Nise",Long.valueOf("0130123"),"nisse@gdsasdf","s","A","Dålig lirare");
			db.updateModel(contUpd);

			db.deleteFromDB(contUpd);
		}


		testList = db.getAllFromDB(new Assignment());
		for (ModelInterface m : testList) {
			Assignment ass = (Assignment) m;

			Assignment assUpd = new Assignment(ass.getId(),"Katt i hav", Long.valueOf("12423423"),Long.valueOf("23423425"),"Kalle", "Nisse", "En katt i ett träd", "2 dagar", "Ej påbörjat", fakeImage, "Alstättersgata", "Lekplats");

			db.updateModel(assUpd);

			db.deleteFromDB(assUpd);
		}

		Log.d("DB","Antal meddelanden: " + db.getDBCount(new MessageModel()));
		Log.d("DB","Antal kontakter: " + db.getDBCount(new Contact()));
		Log.d("DB","Antal uppdrag: " + db.getDBCount(new Assignment()));
	}

	public void testDBPartial(Context context){
		Database db = new Database();
		/*
		int w = 600, h = 600;
		Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
		Bitmap fakeImage = Bitmap.createBitmap(w, h, conf);
		db.addToDB(new Assignment("Katt i träd", Long.valueOf("12423423"),Long.valueOf("23423425"),"Kalle", "Nisse", "En katt i ett träd", "2 dagar", "Ej påbörjat", fakeImage, "Alstättersgata", "Lekplats"),context);
		*/
		db.addToDB(new MessageModel("Hej svehjs","Kalle"), context);

		// Testa att hämta från databasen samt uppdatera databasen
		List<ModelInterface> testList = db.getAllFromDB(new MessageModel(),context);
		for (ModelInterface m : testList) {
			// Hämta gammalt meddelande
			MessageModel mess= (MessageModel) m;
			Log.d("DB",mess.getId() + "-> " + mess.getReciever() + "; " + mess.getMessageContent() + "\n");
			// Skapa nytt meddelande
			MessageModel messUpd = new MessageModel(mess.getId(),"Hej dååå",mess.getReciever().toString(),mess.getMessageTimeStamp(),mess.isRead());
			// Lägg in det uppdaterade meddelandet
			db.updateModel(messUpd, context);
		}
		testList = db.getAllFromDB(new MessageModel(),context);
		Log.d("DB","*********** UPPDATERAD LISTA ************");

		// Testa att skriva ut den uppdaterade listan samt töm hela databasen
		for (ModelInterface m : testList) {
			// Hämta gammalt meddelande
			MessageModel mess = (MessageModel) m;
			Log.d("DB",mess.getId() + "-> " + mess.getReciever() + "; " + mess.getMessageContent() + "\n");
			// Ta bort aktuell post från databasen
			db.deleteFromDB(mess, context);
		}
		// Testa så databasen är tom
		testList = db.getAllFromDB(new MessageModel(),context);
		Log.d("DB","Storlek: " + testList.size());
	}

	
	public void testDBProvider(Context context){
		// Ladda in bibliotek. Fungerar för subklasser.
		//SQLiteDatabase.loadLibs(context);
		com.example.klien_projekttermin.databaseProvider.Database db = com.example.klien_projekttermin.databaseProvider.Database.getInstance(context);
		db.addToDB(new MessageModel("Hej svehjs","Kalle"), context);
		Log.d("DB","Lagt till en post i databasen");
		Log.d("DB","Antal meddelanden: " + db.getDBCount(new MessageModel(), context));
	}


}