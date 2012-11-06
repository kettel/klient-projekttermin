package com.example.klien_projekttermin;

import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;

import communicationModule.CommunicationModule;

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
						CommunicationModule testCom = new CommunicationModule(new logger(getApplicationContext(), "comlog"),new MessageModel("Arne har gått vilse i skogen. Arne Är en äldre man i 80 års oldern och trors ha på sig en röd jacka","vilse i skogen gruppen"));
//						testCom.sendMessage(new MessageModel("Arne har gått vilse i skogen. Arne Är en äldre man i 80 års oldern och trors ha på sig en röd jacka","vilse i skogen gruppen"));

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


}