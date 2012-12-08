package sip;

import java.text.ParseException;

import login.User;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.sip.SipAudioCall;
import android.net.sip.SipException;
import android.net.sip.SipManager;
import android.net.sip.SipProfile;
import android.net.sip.SipRegistrationListener;
import android.util.Log;

public class RegisterWithSipSingleton {
	private static boolean isRegistred = false;
	private static Context context;

	private static boolean isIntentsRegistred = false;
	private static int intentsRegistred = 0;

	public static SipManager manager = null;
	public static SipProfile me = null;
	public static String sipAddress = null;

	public static ObservableCallStatus callStatus = new ObservableCallStatus();

	public boolean isCallAnswered = false;

	// Variabler som jag inte vet vart de ska vara. Main? Service? Hjälpklass?
	public IncomingCallReceiver callReceiver;

	private SipAudioCall call = null;

	// Hämta aktuell användare med lösenhash
	private User currentUser;
	private String username;
	private String domain = "94.254.72.38";
	private String password;

	private static int readyCounter = 0;
	private static int registeringCounter = 0;
	private static int failedCounter = 0;

	private static RegisterWithSipSingleton instance = new RegisterWithSipSingleton();

	private RegisterWithSipSingleton(){}

	public static RegisterWithSipSingleton getInstance(){
		return instance;
	}

	public static RegisterWithSipSingleton getInstance(Context c){

		// Det här borde bara köras när första instansen instansierar...
		if(context != c){
			Log.d("SIP/Singletonklassen","Kontexterna är inte samma...");
			context = c;
		}

		return instance;
	}

	public void initializeManager() {


		// Registrera intents för utgående och inkommande samtal
		if(!isIntentsRegistred){
			intentsRegistred++;
			Log.d("SIP/Singletonklassen","Registrerar intents för gång nummer: " + intentsRegistred);
			// SIP: Registrera Intent för att hantera inkommande SIP-samtal
			IntentFilter filter = new IntentFilter();
			filter.addAction("com.klient_projekttermin.INCOMING_CALL");
			callReceiver = new IncomingCallReceiver();
			context.registerReceiver(callReceiver, filter);
			isIntentsRegistred = true;
		}

		if(manager == null) {
			manager = SipManager.newInstance(context);
		}
		initializeLocalProfile();

	}
	/**
	 * Registrera användaren hos SIP-servern
	 */
	public void initializeLocalProfile() {
		if (manager == null) {
			return;
		}

		// Om den lokala profilen redan är initierad, stäng den och hämta på nytt.
		if (me != null) {
			closeLocalProfile();
		}

		// Hämta aktuell användare
		currentUser = User.getInstance();
		username = currentUser.getAuthenticationModel().getUserName();
		password = currentUser.getAuthenticationModel().getPasswordHash();

		// Om användaren inte är registrerad, försök registrera
		if(!isRegistred){
			Log.d("SIP/RegisterWithSipSingleton/InitializeLocalProfile","Ska skapa profil..");
			Log.d("SIP/RegisterWithSipSingleton/InitializeLocalProfile","Användarnamn: " + username);
			try {

				SipProfile.Builder builder = new SipProfile.Builder(username, domain);
				builder.setPassword(password);
				me = builder.build();

				// Låt klienten kunna ta emot samtal (kryssrutan under Konton i samtalsinställningar)
				Intent intent = new Intent();
				intent.setAction("com.klient_projekttermin.INCOMING_CALL");
				PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, Intent.FILL_IN_DATA);
				manager.open(me, pendingIntent, null);

				// Sätt upp en lyssnare som lyssnar efter hur väl anslutningen har gått
				manager.setRegistrationListener(me.getUriString(), new SipRegistrationListener() {
					public void onRegistering(String localProfileUri) {
						registeringCounter++;
						Log.d("SIP/RegisterWithSipSingleton/InitializeLocalProfile","Registrerar mot SIP-server för "+registeringCounter +" gången...");
					}

					public void onRegistrationDone(String localProfileUri, long expiryTime) {
						readyCounter++;
						Log.d("SIP/RegisterWithSipSingleton/InitializeLocalProfile","Redo för "+readyCounter+" gången.");
						isRegistred = true;
					}

					public void onRegistrationFailed(String localProfileUri, int errorCode,
							String errorMessage) {
						failedCounter++;
						Log.d("SIP/RegisterWithSipSingleton/InitializeLocalProfile","Misslyckades att registrera mot SIP-server för "+failedCounter+ " gången. Försöker igen...");
						isRegistred = false;
					}
				});
			} 
			catch (ParseException e) {
				Log.e("SIP/RegisterWithSipSingleton/InitializeLocalProfile","Parse error.. "+e);
				e.printStackTrace();
			}
			catch (SipException e) {
				Log.e("SIP/RegisterWithSipSingleton/InitializeLocalProfile","Sip exceptionerror.. "+e);
				e.printStackTrace();
			}
		}
	}
	/**
	 * Closes out your local profile, freeing associated objects into memory
	 * and unregistering your device from the server.
	 */
	public void closeLocalProfile() {

		if (manager == null) {
			if(isRegistred){
				isRegistred = false;
			}
			return;
		}
		try {
			if (me != null) {
				manager.close(me.getUriString());
				if(isRegistred){
					isRegistred = false;
				}
			}
		} catch (Exception ee) {
			Log.d("SIP/RegisterWithSipSingleton/CloseLocalProfile", "Failed to close local profile.", ee);
			ee.printStackTrace();
		}
	}

	/**
	 * Make an outgoing call.
	 */
	public void initiateCall(String nameToCall) {
		Log.d("RegisterWithSipSingleton/initiateCall","Kontakt att ringa: "+nameToCall);
		sipAddress = "sip:"+nameToCall+"@94.254.72.38";

		try {
			SipAudioCall.Listener listener = new SipAudioCall.Listener() {
				// Much of the client's interaction with the SIP Stack will
				// happen via listeners.  Even making an outgoing call, don't
				// forget to set up a listener to set things up once the call is established.
				@Override
				public void onCallBusy(SipAudioCall call) {
					Log.d("SIP/Singleton/InitCall","Den du ringt till är upptagen");
				}
				@Override
				public void onCalling(SipAudioCall call){
					Log.d("SIP/Singleton/InitCall","onCalling");
				}
				@Override
				public void onCallEstablished(SipAudioCall call) {
					Log.d("SIP/Singleton/InitCall","onCallEstablished");
					callStatus.setStatus(true);
					call.startAudio();
					call.setSpeakerMode(true);
				}

				@Override
				public void onRingingBack (SipAudioCall call){
					Log.d("SIP/Singleton/InitCall","onRingingBack");
				}


				@Override
				public void onCallEnded(SipAudioCall call) {
					//isInCall = false;
					Log.d("SIP/Singleton/InitCall","onCallEnded");
					callStatus.setStatus(false);
					try {
						call.endCall();
					} catch (SipException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					//updateStatus("Ready.");
				}
			};
			if(manager == null){
				Log.d("SIP/RegisterWithSipSingleton/InitiateCall","Manager är null...");
			}
			call = manager.makeAudioCall(me.getUriString(), sipAddress, listener, 30);
			Intent startIncoming = new Intent(context,IncomingCallDialog.class);
			startIncoming.putExtra("outgoing",true);
			startIncoming.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			StaticCall.call = call;
			context.startActivity(startIncoming);
		}
		catch (Exception e) {
			Log.i("SIP/RegisterWithSipSingleton/InitiateCall", "Error when trying to close manager.", e);
			if (me != null) {
				try {
					manager.close(me.getUriString());
				} catch (Exception ee) {
					Log.i("SIP/RegisterWithSipSingleton/InitiateCall",
							"Error when trying to close manager.", ee);
					ee.printStackTrace();
				}
			}
			if (call != null) {
				call.close();
			}
		}
	}

	public void dropCall(){
		try {
			call.endCall();
		} catch (SipException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	public SipAudioCall getCall() {
		return call;
	}

	public void setCall(SipAudioCall call) {
		this.call = call;
	}
}