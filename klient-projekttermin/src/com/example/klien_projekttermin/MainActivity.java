package com.example.klien_projekttermin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.example.klien_projekttermin.database.Database;
import communicationModule.CommunicationService;
import communicationModule.IncomeingDataListners;
import communicationModule.CommunicationService.CommunicationBinder;

import camera.Camera;

import loginFunction.InactivityListener;
import map.MapActivity;
import messageFunction.Inbox;
import models.Assignment;
import models.AssignmentStatus;
import models.Contact;
import models.MessageModel;
import models.ModelInterface;
import android.app.ListActivity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import assignment.AssignmentOverview;
import camera.Camera;


//import com.google.android.gcm.GCMRegistrar;

public class MainActivity extends InactivityListener implements IncomeingDataListners{
	
	private String userName;

	public static final String LOGCONTENT = "com.exampel.klien_projekttermin";
	private CommunicationService communicationService;
	private boolean communicationBond = false;
	private static final String SENDER_ID = "943011390551";
	IncomeingDataListners test = this;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		testDB(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		ListView lv = (ListView) findViewById(android.R.id.list);
//		GCMRegistrar.checkDevice(this);
//		GCMRegistrar.checkManifest(this);
//		final String regId = GCMRegistrar.getRegistrationId(this);
//		if (regId.equals("")) {
//		  GCMRegistrar.register(this, SENDER_ID);
//		} else {
//		  System.out.println("Already registerd");
//		}
		
		Intent intent = new Intent(this.getApplicationContext(), CommunicationService.class);
		bindService(intent, communicationServiceConnection, Context.BIND_AUTO_CREATE);
		
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			userName = extras.getString("USER");
		}
		
		String[] from = { "line1", "line2" };
		int[] to = { android.R.id.text1, android.R.id.text2 };
		lv.setAdapter(new SimpleAdapter(this, generateMenuContent(),
				android.R.layout.simple_list_item_2, from, to));
		lv.setOnItemClickListener(new OnItemClickListener() {
			Intent myIntent = null;
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				//test
				communicationService.setContext(getApplicationContext());
				//Har man lagt till ett nytt menyval lägger man till en action för dessa här.
				switch (arg2) {
				case 0:
					myIntent = new Intent(MainActivity.this,MapActivity.class);
					myIntent.putExtra("USER", userName);
					break;
				case 1:
					communicationService.registerIncomeingMessagelistener(test);
					myIntent = new Intent(MainActivity.this,Inbox.class);
					myIntent.putExtra("USER", userName);
					break;
				case 2:
					myIntent = new Intent(MainActivity.this,AssignmentOverview.class);
					myIntent.putExtra("USER", userName);
					break;
				case 3:
					myIntent = new Intent(MainActivity.this,Camera.class);
					myIntent.putExtra("USER", userName);
					break;
				default:
					break;
				}
				MainActivity.this.startActivity(myIntent);
			}

		});
	}
	private void testDB(Context context) {
		Database db = Database.getInstance(context);
		db.addToDB(new Assignment("Namn", "Sändare", false, "Beskrivning", "Tidsspann 2 veckor", AssignmentStatus.NOT_STARTED,"Gatunamn", "Platsnamn"),getContentResolver());
		db.addToDB(new Contact("Kontaktnamn"), getContentResolver());
		db.addToDB(new MessageModel("Meddelandeinnehåll", "Mottagera", "Sändare"), getContentResolver());
		Log.d("DB","** Uppdrag **");
		List <ModelInterface> assignments = db.getAllFromDB(new Assignment(), getContentResolver());
		for (ModelInterface modelInterface : assignments) {
			Assignment assignment = (Assignment) modelInterface;
			Log.d("DB", "Id: " + assignment.getId() + " Namn: " + assignment.getName());
			Assignment assignmentUpdate = new Assignment("Uppdaterat namn", "Uppdaterad sändare", false, "Uppdaterad beskrivning", "Uppdaterat tidsspann", AssignmentStatus.NEED_HELP, "Uppdaterad gatunamn", "Uppdaterad plats");
			assignmentUpdate.setId(assignment.getId());
			db.updateModel(assignmentUpdate, getContentResolver());
		}
		Log.d("DB", "** Uppdaterade uppdrag **");
		assignments = db.getAllFromDB(new Assignment(), getContentResolver());
		for (ModelInterface modelInterface : assignments) {
			Assignment assignment = (Assignment) modelInterface;
			Log.d("DB", "Id: " + assignment.getId() + " Namn: " + assignment.getName());
			db.deleteFromDB(assignment, getContentResolver());
		}
		Log.d("DB","** Kontakter **");
		List <ModelInterface> contacts = db.getAllFromDB(new Contact(), getContentResolver());
		for (ModelInterface modelInterface : contacts) {
			Contact contact = (Contact) modelInterface;
			Log.d("DB","Id: " + contact.getId() + " -> Namn: " + contact.getContactName());
			Contact updatedContact = new Contact(contact.getId(),"Uppdaterat kontaktnamn..");
			db.updateModel(updatedContact, getContentResolver());
		}
		Log.d("DB","** Uppdaterade kontakter **");
		contacts = db.getAllFromDB(new Contact(), getContentResolver());
		for (ModelInterface modelInterface : contacts) {
			Contact contact = (Contact) modelInterface;
			Log.d("DB","Id: " + contact.getId() + " -> Namn: " + contact.getContactName());
			db.deleteFromDB(contact, getContentResolver());
		}
		Log.d("DB","** Meddelanden **");
		List <ModelInterface> messages = db.getAllFromDB(new MessageModel(), getContentResolver());
		for (ModelInterface modelInterface : messages) {
			MessageModel mess = (MessageModel) modelInterface;
			Log.d("DB", "Id: " + mess.getId() + " -> Sändare: " + mess.getSender() + " Innehåll: " + mess.getMessageContent().toString() + " Mottagare: " + mess.getReciever());
			MessageModel updatedMess = new MessageModel(mess.getId(),"Updated content", "Updated receiver", "Updated sender", mess.getMessageTimeStamp(), true);
			db.updateModel(updatedMess, getContentResolver());
		}
		Log.d("DB","** Uppdaterade meddelanden **");
		messages = db.getAllFromDB(new MessageModel(), getContentResolver());
		for (ModelInterface modelInterface : messages) {
			MessageModel mess = (MessageModel) modelInterface;
			Log.d("DB", "Id: " + mess.getId() + " -> Sändare: " + mess.getSender() + " Innehåll: " + mess.getMessageContent().toString() + " Mottagare: " + mess.getReciever());
			db.deleteFromDB(mess, getContentResolver());
		}
		assignments = db.getAllFromDB(new Assignment(), getContentResolver());
		contacts = db.getAllFromDB(new Contact(), getContentResolver());
		messages = db.getAllFromDB(new MessageModel(), getContentResolver());
		Log.d("DB","Antal uppdrag: " + assignments.size());
		Log.d("DB","Antal kontakter: " + contacts.size());
		Log.d("DB","Antal meddelanden: " + messages.size());
	}
	/**
	 * Genererar de menyval som ska gå att göra.
	 * @return
	 * En List<HashMap<String, String>> där varje map bara har två värden. Ett för första raden och ett för andra.
	 */
	private List<HashMap<String, String>> generateMenuContent(){
		List<HashMap<String, String>>content=new ArrayList<HashMap<String,String>>();
		//Om menyn ska utökas ska man lägga till de nya valen i dessa arrayer. Notera att det krävs en subtitle till varje item.
		String[] menuItems={"Karta","Meddelanden", "Uppdragshanteraren", "Kamera"};
		String[] menuSubtitle={"Visar en karta","Visar Inkorgen", "Visar tillgängliga uppdrag", "Ta bilder"};
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

	@Override
	protected void onDestroy (){
		super.onDestroy();
		unbindService(communicationServiceConnection);
	}
	
	private ServiceConnection communicationServiceConnection = new ServiceConnection() {
		
		public void onServiceConnected(ComponentName className,IBinder service) {
		        CommunicationBinder binder = (CommunicationBinder) service;
	            communicationService = binder.getService();
	            communicationBond = true;
		}
		
		public void onServiceDisconnected(ComponentName arg0) {
		      	communicationBond = false;
		}

	   };
	public void handelIncomeingMessage() {
		// här ska man göra grejer
	}
	public void handelIncomeingAssignment() {
		// här ska man göra grejer
		
	}
	


}
