package com.klient_projekttermin;

import static com.klient_projekttermin.CommonUtilities.DISPLAY_MESSAGE_ACTION;
import static com.klient_projekttermin.CommonUtilities.EXTRA_MESSAGE;
import login.LogInActivity;
import login.User;
import models.Contact;
import qosManager.QoSManager;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.google.android.gcm.GCMRegistrar;

import communicationModule.PullResponseHandler;
import communicationModule.SocketConnection;

import database.Database;

/**
 * Bas aktivitet för alla aktiviteter. Alla aktiviteter extendar denna.
 * 
 * @author nicklas
 * 
 */
@SuppressLint("HandlerLeak")
public class SecureActivity extends Activity {
	private QoSManager qosManager;
	public static String inactivity;
	public static int LOGIN_REQUEST = 1;
	private User user = User.getInstance();
	
	private SocketConnection socketConnection = new SocketConnection();
	private Database database;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		database = Database.getInstance(getApplicationContext());
		registerReceiver(mHandleMessageReceiver, new IntentFilter(
				DISPLAY_MESSAGE_ACTION));
		qosManager = QoSManager.getInstance();
		qosManager.setContext(this);
		qosManager.startBatteryCheckingThread(this);
		socketConnection.setContext(getApplicationContext());
		socketConnection.addObserver(new PullResponseHandler(
				getApplicationContext()));
		if (!user.isLoggedIn()) {
			setResult(LogInActivity.STAY_ALIVE);
			finish();
		}
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(mHandleMessageReceiver);
		super.onDestroy();
	}

	private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String newMessage = intent.getExtras().getString(EXTRA_MESSAGE);
			if (newMessage.contains("registered")) {
				System.out.println("Registrerad, pullar från server");
				user.getAuthenticationModel()
						.setGCMID(
								GCMRegistrar
										.getRegistrationId(getApplicationContext()));
				/**
				 * Viktigt att checkContactDatabase körs först, annars finns
				 * risken att inga kontakter hämtas. Detta då det kan ligga en
				 * kontakt i kön när en pullrequest körs.
				 */
				checkContactDatabase();
				socketConnection.pullFromServer();
			} else if (newMessage.equals("logout")) {
				logout();
			}
		}
	};
	public void logout(){
		socketConnection.logout();
		setResult(LogInActivity.STAY_ALIVE);
		finish();
	}

	public void checkContactDatabase() {
		if (database.getDBCount(new Contact(), getContentResolver()) == 0) {
			socketConnection.getAllContactsReq();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == LOGIN_REQUEST) {
			if (resultCode == Activity.RESULT_CANCELED) {
				System.out.println("result canceled");
				finish();
			}
		}
	}

	/**
	 * Sätter hur lång tid timeouten är på
	 */
	public static final long DISCONNECT_TIMEOUT = 600000;

	private Handler disconnectHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
		}
	};

	/**
	 * Är detta som körs då timeouten har gått ut
	 */
	private Runnable disconnectCallback = new Runnable() {
		public void run() {
			Intent intent = new Intent(SecureActivity.this, LogInActivity.class);
			intent.putExtra("calling-activity", ActivityConstants.INACTIVITY);
			user.setLoggedIn(false);
			SecureActivity.this.startActivity(intent);
		}
	};

	public void resetDisconnectTimer() {
		disconnectHandler.removeCallbacks(disconnectCallback);
		disconnectHandler.postDelayed(disconnectCallback, DISCONNECT_TIMEOUT);
	}

	public void stopDisconnectTimer() {
		disconnectHandler.removeCallbacks(disconnectCallback);
	}

	@Override
	public void onUserInteraction() {
		resetDisconnectTimer();
	}

	@Override
	protected void onResume() {		
		super.onResume();
		qosManager.adjustToCurrentBatteryMode(this);
		resetDisconnectTimer();
	}

	@Override
	public void onStop() {
		super.onStop();
		stopDisconnectTimer();
	}
}