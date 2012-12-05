package com.klient_projekttermin;

import static com.klient_projekttermin.CommonUtilities.SENDER_ID;
import static com.klient_projekttermin.CommonUtilities.SERVER_URL;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import login.LogInActivity;
import login.User;
import map.MapActivity;
import messageFunction.Inbox;
import qosManager.QoSInterface;
import qosManager.QoSManager;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import camera.CameraMenu;

import com.google.android.gcm.GCMRegistrar;
import com.klient_projekttermin.R.id;
import communicationModule.PullResponseHandler;
import communicationModule.SocketConnection;

import contacts.ContactsBookActivity;
import database.Database;

public class MainActivity extends SecureActivity {

	AsyncTask<Void, Void, Void> mRegisterTask;

	private QoSManager qosManager;
	private MenuItem onlineMarker;
	private MenuItem offlineMarker;
	private Database database;
	private SocketConnection socketConnection = new SocketConnection();
	private User user;
	private Boolean haveConnectedWifi= true;
	private Boolean haveConnectedMobile = true;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initiateDB(this);
		qosManager = QoSManager.getInstance();
		setContentView(R.layout.activity_main);
		user = User.getInstance();

		socketConnection.addObserver(new PullResponseHandler(
				getApplicationContext()));

		// used to replace listview functionality
		ListView lv = (ListView) findViewById(android.R.id.list);

		checkNotNull(SERVER_URL, "SERVER_URL");
		checkNotNull(SENDER_ID, "SENDER_ID");
		// Make sure the device has the proper dependencies.
		GCMRegistrar.checkDevice(this);
		// Make sure the manifest was properly set - comment out this line
		// while developing the app, then uncomment it when it's ready.
		GCMRegistrar.checkManifest(this);
		final String regId = GCMRegistrar.getRegistrationId(this);
		if (regId.equals("")) {
			// Automatically registers application on startup.
			GCMRegistrar.register(getApplicationContext(), SENDER_ID);
		} else {
			// Device is already registered on GCM, check server.
			if (GCMRegistrar.isRegisteredOnServer(this) && user.isLoggedIn()) {
				// Skips registration.
				user.getAuthenticationModel()
				.setGCMID(
						GCMRegistrar
						.getRegistrationId(getApplicationContext()));
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
					if (qosManager.isAllowedToStartMap()) {
						myIntent = new Intent(MainActivity.this,
								MapActivity.class);
						myIntent.putExtra("calling-activity",
								ActivityConstants.MAIN_ACTIVITY);
					} else {
						unallowedStart.show();
					}
					break;
				case 1:
					if (qosManager.isAllowedToStartMessages()) {
						myIntent = new Intent(MainActivity.this, Inbox.class);
					} else {
						unallowedStart.show();
					}
					break;
				case 2:
					if (qosManager.isAllowedToStartAssignment()) {
						myIntent = new Intent(MainActivity.this,
								AssignmentOverview.class);
					} else {
						unallowedStart.show();
					}
					break;
				case 3:
					if (qosManager.isAllowedToStartCamera()) {
						myIntent = new Intent(MainActivity.this,
								CameraMenu.class);
					} else {
						unallowedStart.show();
					}
					break;
				case 4:
					myIntent = new Intent(MainActivity.this,
							ContactsBookActivity.class);
					break;
				default:
					break;
				}
				if (myIntent != null) {
					MainActivity.this.startActivity(myIntent);
				}
			}
		});
	}

	@Override
	public void onBackPressed() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Avsluta");
		builder.setMessage("Vill du avsluta?");
		builder.setPositiveButton("Ja", new OnClickListener() {
			public void onClick(DialogInterface dialog, int arg1) {
				dialog.dismiss();
				SocketConnection socketConnection = new SocketConnection();
				socketConnection.logout();
				setResult(RESULT_CANCELED);
				finish();
			}
		});
		builder.setNegativeButton("Nej", new OnClickListener() {
			public void onClick(DialogInterface dialog, int arg1) {
				dialog.dismiss();
			}
		});
		builder.setCancelable(false);
		builder.create().show();
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
	
		onlineMarker = menu.findItem(R.id.OnlineMarker);
		offlineMarker = menu.findItem(R.id.OfflineMarker);

		registerReceiver(new BroadcastReceiver() {      
		        public void onReceive(Context context, Intent intent) {
		        	  ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		        	    NetworkInfo[] netInfo = cm.getAllNetworkInfo();
		        	    for (NetworkInfo ni : netInfo) {
		        	        if (ni.getTypeName().equalsIgnoreCase("WIFI"))
		        	            if (ni.isConnected()){
		        	                haveConnectedWifi = true;
		        	            }else{
		        	            	haveConnectedWifi = false;
		        	            }
		        	        if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
		        	            if (ni.isConnected()){
		        	                haveConnectedMobile = true;
		        	            }else{
		        	            	haveConnectedMobile = false;
		        	            }
		        	    }
		        	qosManager.checkConnectivity(onlineMarker, offlineMarker, haveConnectedWifi, haveConnectedMobile);
		        }}, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
		
		return true;
	}

	@Override
	protected void onDestroy() {

		if (mRegisterTask != null) {
			mRegisterTask.cancel(true);
		}
		GCMRegistrar.onDestroy(getApplicationContext());
		super.onDestroy();
	}

	private void checkNotNull(Object reference, String name) {
		if (reference == null) {
			// throw new NullPointerException(
			// getString(R.string.error_config, name));
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int logOutId = findViewById(id.logout).getId();
		int qosId = findViewById(id.QoSManager).getId();

		if (item.getItemId() == logOutId) {
			logout();
			return false;
		} else if (item.getItemId() == qosId) {
			startQoSManager();
			return false;
		} else {
			return false;
		}
	}

	public void startQoSManager() {
		Intent intent = new Intent(MainActivity.this, QoSInterface.class);
		this.startActivity(intent);
	}

	public void logout() {
		finish();
		Intent intent = new Intent(MainActivity.this, LogInActivity.class);
		user.setLoggedIn(false);
		this.startActivity(intent);
	}
}
