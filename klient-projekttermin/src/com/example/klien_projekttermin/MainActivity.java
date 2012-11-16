package com.example.klien_projekttermin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import communicationModule.CommunicationService;
import communicationModule.CommunicationService.CommunicationBinder;

import models.MessageModel;
import android.app.ListActivity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SimpleAdapter;

public class MainActivity extends ListActivity {

	public static final String LOGCONTENT = "com.exampel.klien_projekttermin";
	private CommunicationService communicationService;
	private boolean communicationBond = false;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		String[] from = { "line1", "line2" };
		int[] to = { android.R.id.text1, android.R.id.text2 };
		
		// start services
		Intent intent = new Intent(this.getApplicationContext(), CommunicationService.class);
		bindService(intent, communicationServiceConnection, Context.BIND_AUTO_CREATE);
		// end service
		
		setListAdapter(new SimpleAdapter(this, generateMenuContent(),
				android.R.layout.simple_list_item_2, from, to));
		getListView().setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				//Har man lagt till ett nytt menyval lägger man till en action för dessa här.
				switch (arg2) {
				case 0:
					if(communicationBond){
						communicationService.setContext(getApplicationContext());
						System.out.println("hej hej");
						communicationService.sendMessage(new MessageModel("Hello, this is dog.","test"));
					}
					break;
					
				case 1:
					
					break;
				case 2:
					
					break;
				case 3:
					
					try {
					} catch (Exception e) {
					}
					break;
				default:
					// myIntent= new Intent(from.this,
					// to.class);
					break;
				}
//				MainActivity.this.startActivity(myIntent);
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