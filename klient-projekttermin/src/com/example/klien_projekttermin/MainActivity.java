package com.example.klien_projekttermin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.example.klien_projekttermin.database.Database;
import communicationModule.CommunicationService;
import communicationModule.IncomeingDataListners;
import communicationModule.CommunicationService.CommunicationBinder;

import communicationModule.CommunicationService;
import communicationModule.CommunicationService.CommunicationBinder;

import camera.Camera;

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
import android.widget.SimpleAdapter;
import assignment.AssignmentOverview;


//import com.google.android.gcm.GCMRegistrar;

public class MainActivity extends ListActivity{
	
	private String userName;


	public static final String LOGCONTENT = "com.exampel.klien_projekttermin";
	private CommunicationService communicationService;
	private boolean communicationBond = false;
	private static final String SENDER_ID = "943011390551";
	private Database database;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
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
		
//		Contact contact = new Contact("strighild");
//		
//		dataBase.addToDB(contact, getContentResolver());
		
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			userName = extras.getString("USER");
		}
		
		String[] from = { "line1", "line2" };
		int[] to = { android.R.id.text1, android.R.id.text2 };
		setListAdapter(new SimpleAdapter(this, generateMenuContent(),
				android.R.layout.simple_list_item_2, from, to));
		getListView().setOnItemClickListener(new OnItemClickListener() {
			Intent myIntent = null;
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				communicationService.setContext(getApplicationContext());
				database = Database.getInstance(getApplicationContext());
				//Har man lagt till ett nytt menyval lägger man till en action för dessa här.
				switch (arg2) {
				case 0:
					myIntent = new Intent(MainActivity.this,MapActivity.class);
					myIntent.putExtra("USER", userName);
					break;
				case 1:
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
}
