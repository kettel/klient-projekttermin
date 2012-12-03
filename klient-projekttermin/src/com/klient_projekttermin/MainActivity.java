package com.klient_projekttermin;

import static com.klient_projekttermin.CommonUtilities.DISPLAY_MESSAGE_ACTION;
import static com.klient_projekttermin.CommonUtilities.EXTRA_MESSAGE;
import static com.klient_projekttermin.CommonUtilities.SENDER_ID;
import static com.klient_projekttermin.CommonUtilities.SERVER_URL;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import login.LogInActivity;
import login.User;
import map.MapActivity;
import messageFunction.Inbox;
import models.AuthenticationModel;
import models.Contact;
import qosManager.QoSManager;
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
import android.widget.Toast;
import assignment.AssignmentOverview;
import camera.Camera;

import com.google.android.gcm.GCMRegistrar;

import communicationModule.PullResponseHandler;
import communicationModule.SocketConnection;

import contacts.ContactsBookActivity;
import database.Database;

public class MainActivity extends SecureActivity {

	AsyncTask<Void, Void, Void> mRegisterTask;

	private QoSManager qosManager;
	private Database database;
	private String userName;
	private SocketConnection socketConnection=new SocketConnection();
	private User user = User.getInstance();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initiateDB(this);
		Database database=Database.getInstance(getApplicationContext());
//		database.addToDB(new Contact("eric"), getContentResolver());
//		database.addToDB(new Contact("erica"), getContentResolver());
		
		
//		// Communication model
//		Intent intent = new Intent(this.getApplicationContext(),
//				CommunicationService.class);
//		bindService(intent, communicationServiceConnection,
//				Context.BIND_AUTO_CREATE);

			
			userName = user.getAuthenticationModel().getUserName();

		
		qosManager = QoSManager.getInstance();

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
			GCMRegistrar.register(getApplicationContext(), SENDER_ID);
		} else {
			// Device is already registered on GCM, check server.
			if (GCMRegistrar.isRegisteredOnServer(this)) {
				// Skips registration.
				user.getAuthenticationModel().setGCMID(GCMRegistrar.getRegistrationId(getApplicationContext()));
				socketConnection.pullFromServer();
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
			Toast unallowedStart = Toast.makeText(getApplicationContext(),
					"Du har inte tillåtelse att starta denna funktion",
					Toast.LENGTH_SHORT);

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent myIntent = null;
				// Har man lagt till ett nytt menyval lägger man till en action
				// för dessa här.
				switch (arg2) {
				case 0:
					if (qosManager.allowedToStartMap()) {
						myIntent = new Intent(MainActivity.this,
								MapActivity.class);
						myIntent.putExtra("calling-activity", ActivityConstants.MAIN_ACTIVITY);
					} else {
						unallowedStart.show();
					}
					break;
				case 1:
					if (qosManager.allowedToStartMessages()) {
						System.out.println("Startar meddelanden");
						myIntent = new Intent(MainActivity.this, Inbox.class);
					} else {
						unallowedStart.show();
					}
					break;
				case 2:
					if (qosManager.allowedToStartAssignment()) {
						myIntent = new Intent(MainActivity.this,
								AssignmentOverview.class);
					} else {
						unallowedStart.show();
					}
					break;
				case 3:
					if (qosManager.allowedToStartCamera()) {
						myIntent = new Intent(MainActivity.this, Camera.class);
					} else {
						unallowedStart.show();
					}
					break;
				case 4:
					myIntent = new Intent(MainActivity.this,
							ContactsBookActivity.class);
				default:
					break;
				}
				if (myIntent != null) {
					MainActivity.this.startActivity(myIntent);
				}
			}
		});
	}

	public void checkContactDatabase() {
		System.out.println(database.getDBCount(new Contact(), getContentResolver()));
		if (database.getDBCount(new Contact(), getContentResolver()) == 0) {
			socketConnection.getAllContactsReq();
		}
	}

	private void initiateDB(Context context) {
		// Tvinga in SQLCipher-biblioteken. För säkerhetsskull...
		database = Database.getInstance(context);
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
				"Visar tillgängliga uppdrag", "Ta bilder", "Visa kontakter" };
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
		GCMRegistrar.onDestroy(getApplicationContext());
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
			if (newMessage.contains("registered")) {
				user.getAuthenticationModel().setGCMID(GCMRegistrar.getRegistrationId(getApplicationContext()));
				socketConnection.addObserver(new PullResponseHandler(getApplicationContext()));
				socketConnection.pullFromServer();
				checkContactDatabase();
			}
		}
	};

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		logout();
		return false;
	}
	public void logout(){
		finish();
		Intent intent = new Intent(MainActivity.this, LogInActivity.class);
		user.setLoggedIn(false);
		this.startActivity(intent);
	}
}
