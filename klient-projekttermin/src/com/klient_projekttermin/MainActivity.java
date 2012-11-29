package com.klient_projekttermin;

import static com.klient_projekttermin.CommonUtilities.DISPLAY_MESSAGE_ACTION;
import static com.klient_projekttermin.CommonUtilities.EXTRA_MESSAGE;
import static com.klient_projekttermin.CommonUtilities.SENDER_ID;
import static com.klient_projekttermin.CommonUtilities.SERVER_URL;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import loginFunction.InactivityListener;
import loginFunction.LogInFunction;
import map.MapActivity;
import messageFunction.Inbox;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import assignment.AssignmentOverview;
import camera.Camera;

import com.google.android.gcm.GCMRegistrar;
import communicationModule.SocketConnection;

import contacts.ContactsBookActivity;
import database.Database;

public class MainActivity extends InactivityListener {

	private String userName;
	AsyncTask<Void, Void, Void> mRegisterTask;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initiateDB(this);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			userName = extras.getString("USER");
		}
		setContentView(R.layout.activity_main);

		// used to replace listview functionality
		ListView lv = (ListView) findViewById(android.R.id.list);

		checkNotNull(SERVER_URL, "SERVER_URL");
		checkNotNull(SENDER_ID, "SENDER_ID");
		// Make sure the device has the proper dependencies.
		GCMRegistrar.checkDevice(this);
		// Make sure the manifest was properly set - comment out this line
		// while developing the app, then uncomment it when it's ready.
		GCMRegistrar.checkManifest(this);
		registerReceiver(mHandleMessageReceiver, new IntentFilter(
				DISPLAY_MESSAGE_ACTION));
		final String regId = GCMRegistrar.getRegistrationId(this);
		if (regId.equals("")) {
			// Automatically registers application on startup.
			GCMRegistrar.register(this, SENDER_ID);
		} else {
			// Device is already registered on GCM, check server.
			if (GCMRegistrar.isRegisteredOnServer(this)) {
				// Skips registration.
			} else {
				// Try to register again, but not in the UI thread.
				// It's also necessary to cancel the thread onDestroy(),
				// hence the use of AsyncTask instead of a raw thread.
				final Context context = this;
				mRegisterTask = new AsyncTask<Void, Void, Void>() {

					@Override
					protected Void doInBackground(Void... params) {
						ServerUtilities.register(context, regId);
						return null;
					}

					@Override
					protected void onPostExecute(Void result) {
						mRegisterTask = null;
					}

				};
				mRegisterTask.execute(null, null, null);
			}

		}
		String[] from = { "line1", "line2" };
		int[] to = { android.R.id.text1, android.R.id.text2 };
		lv.setAdapter(new SimpleAdapter(this, generateMenuContent(),
				android.R.layout.simple_list_item_2, from, to));
		lv.setOnItemClickListener(new OnItemClickListener() {
			Intent myIntent = null;

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// Har man lagt till ett nytt menyval lägger man till en action
				// för dessa här.
				switch (arg2) {
				case 0:
					myIntent = new Intent(MainActivity.this, MapActivity.class);
					myIntent.putExtra("USER", userName);

					break;
				case 1:
					myIntent = new Intent(MainActivity.this, Inbox.class);
					myIntent.putExtra("USER", userName);
					break;
				case 2:
					myIntent = new Intent(MainActivity.this,
							AssignmentOverview.class);
					myIntent.putExtra("USER", userName);
					break;
				case 3:
					myIntent = new Intent(MainActivity.this, Camera.class);
					myIntent.putExtra("USER", userName);
					break;
				case 4:
					myIntent = new Intent(MainActivity.this, ContactsBookActivity.class);
					myIntent.putExtra("USER", userName);
				default:
					break;
				}
				MainActivity.this.startActivity(myIntent);
			}

		});
		SocketConnection socketConnection=new SocketConnection();
		socketConnection.addObserver(new PullRequestHandler(this));
		socketConnection.pullFromServer();
		
	}

	private void initiateDB(Context context) {
		// Tvinga in SQLCipher-biblioteken. För säkerhetsskull...
		Database.getInstance(context);
	}

	/**
	 * Genererar de menyval som ska gå att göra.
	 * 
	 * @return En List<HashMap<String, String>> där varje map bara har två
	 *         värden. Ett för första raden och ett för andra.
	 */
	private List<HashMap<String, String>> generateMenuContent() {
		List<HashMap<String, String>> content = new ArrayList<HashMap<String, String>>();
		// Om menyn ska utökas ska man lägga till de nya valen i dessa arrayer.
		// Notera att det krävs en subtitle till varje item.
		String[] menuItems = { "Karta", "Meddelanden", "Uppdragshanteraren",
				"Kamera", "Kontakter" };
		String[] menuSubtitle = { "Visar en karta", "Visar Inkorgen",
				"Visar tillgängliga uppdrag", "Ta bilder", "Visa kontakter"};
		// Ändra inget här under
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
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	protected void onDestroy() {
		if (mRegisterTask != null) {
			mRegisterTask.cancel(true);
		}
		unregisterReceiver(mHandleMessageReceiver);
		GCMRegistrar.onDestroy(this);
		super.onDestroy();
	}

	private void checkNotNull(Object reference, String name) {
		if (reference == null) {
			// throw new NullPointerException(
			// getString(R.string.error_config, name));
		}
	}

	private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String newMessage = intent.getExtras().getString(EXTRA_MESSAGE);
			System.out.println(newMessage);
			// mDisplay.append(newMessage + "\n");
		}
	};

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		finish();
		Intent intent = new Intent(MainActivity.this, LogInFunction.class);
		this.startActivity(intent);
		return false;
	}
}
