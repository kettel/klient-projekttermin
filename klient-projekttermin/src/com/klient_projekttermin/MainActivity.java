package com.klient_projekttermin;

import static com.klient_projekttermin.CommonUtilities.DISPLAY_MESSAGE_ACTION;
import static com.klient_projekttermin.CommonUtilities.EXTRA_MESSAGE;
import static com.klient_projekttermin.CommonUtilities.SENDER_ID;
import static com.klient_projekttermin.CommonUtilities.SERVER_URL;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import loginFunction.InactivityListener;
import loginFunction.LogInFunction;
import loginFunction.User;
import map.MapActivity;
import messageFunction.Inbox;
import models.Contact;
import qosManager.QoSManager;
import sip.IncomingCallReceiver;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.sip.SipAudioCall;
import android.net.sip.SipException;
import android.net.sip.SipManager;
import android.net.sip.SipProfile;
import android.net.sip.SipRegistrationListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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
import communicationModule.SocketConnection;

import contacts.ContactsBookActivity;
import database.Database;

public class MainActivity extends InactivityListener {

	AsyncTask<Void, Void, Void> mRegisterTask;

	private QoSManager qosManager;
	private Database database;
	private SocketConnection socketConnection;

	// SIP-variabler
	public String sipAddress = null;
    public SipManager manager = null;
    public SipProfile me = null;
    public SipAudioCall call = null;
    public IncomingCallReceiver callReceiver;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initiateDB(this);
		qosManager = QoSManager.getInstance();

		setContentView(R.layout.activity_main);
		
		// SIP: Registrera Intent för att hantera inkommande SIP-samtal
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.klient_projekttermin.INCOMING_CALL");
        callReceiver = new IncomingCallReceiver();
        this.registerReceiver(callReceiver, filter);
        
        // SIP: Registrera klienten hos SIP-servern 
        // TODO: Skriv om till service..
        initializeManager();
        
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
					break;
				case 5:
					if (qosManager.allowedToStartSip()) {
						//myIntent = new Intent(MainActivity.this, SipMain.class);
					} else {
						unallowedStart.show();
					}
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
	protected void onStart() {
		super.onStart();
		
        // SIP: Registrera klienten hos SIP-servern 
        // TODO: Skriv om till service..
        initializeManager();
	}
	
	@Override
	public void onResume(){
		super.onResume();
		
        // SIP: Registrera klienten hos SIP-servern 
        // TODO: Skriv om till service..
        initializeManager();
	}
	
	/**
	 * Registrera med SIP-servern.
	 */
	public void initializeManager() {
        if(manager == null) {
          manager = SipManager.newInstance(this);
        }

        initializeLocalProfile();
    }

    /**
     * Logs you into your SIP provider, registering this device as the location to
     * send SIP calls to for your SIP address.
     */
    public void initializeLocalProfile() {
        if (manager == null) {
            return;
        }

        if (me != null) {
            closeLocalProfile();
        }

        String username = "1001";
        String domain = "94.254.72.38";
        String password = "1001";

        try {
            SipProfile.Builder builder = new SipProfile.Builder(username, domain);
            builder.setPassword(password);
            me = builder.build();

            Intent i = new Intent();
            i.setAction("com.klient_projekttermin.INCOMING_CALL");
            PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, Intent.FILL_IN_DATA);
            manager.open(me, pi, null);

            // This listener must be added AFTER manager.open is called,
            // Otherwise the methods aren't guaranteed to fire.

            manager.setRegistrationListener(me.getUriString(), new SipRegistrationListener() {
                    public void onRegistering(String localProfileUri) {
                    	Log.d("SIP","Registering with SIP Server...");
                    }

                    public void onRegistrationDone(String localProfileUri, long expiryTime) {
                    	Log.d("SIP","Ready");
                    }

                    public void onRegistrationFailed(String localProfileUri, int errorCode,
                            String errorMessage) {
                    	Log.d("SIP","Registration failed.  Please check settings.");
                    }
                });
        } catch (ParseException pe) {
            //updateStatus("Connection Error.");
        } catch (SipException se) {
            //updateStatus("Connection error.");
        }
    }
    
    /**
     * Closes out your local profile, freeing associated objects into memory
     * and unregistering your device from the server.
     */
    public void closeLocalProfile() {
        if (manager == null) {
            return;
        }
        try {
            if (me != null) {
                manager.close(me.getUriString());
            }
        } catch (Exception ee) {
            Log.d("MainActivity/closeLocalProfile", "Failed to close local profile.", ee);
        }
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
				"Kamera", "Kontakter" ,"Samtal"};
		String[] menuSubtitle = { "Visar en karta", "Visar Inkorgen",
				"Visar tillgängliga uppdrag", "Ta bilder", "Visa kontakter" ,"Ring ett samtal"};
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
				System.out.println("New gcm-message: "+newMessage);
				User user=User.getInstance();
				user.getAuthenticationModel().setGCMID(GCMRegistrar.getRegistrationId(getApplicationContext()));
				socketConnection = new SocketConnection();
				socketConnection.addObserver(new PullRequestHandler(getApplicationContext()));
				socketConnection.pullFromServer();
				checkContactDatabase();
			}
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
