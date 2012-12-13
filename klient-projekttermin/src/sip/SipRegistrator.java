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

/**
 * Registrera SIP-användare hos en SIP-server
 * @author kettel
 *
 */
public class SipRegistrator {
	private boolean isRegistred = false;
	private Context context;

	private boolean isIntentsRegistred = false;
	private int intentsRegistred = 0;

	public SipManager manager = null;
	public SipProfile me = null;
	public String sipAddress = null;

	// Observer för samtalsstatus
	public ObservableCallStatus callStatus = new ObservableCallStatus();

	// Variabler som jag inte vet vart de ska vara. Main? Service? Hjälpklass?
	private IncomingCallReceiver callReceiver;
	
	private IntentFilter filter;
	
	private CurrentCall currentCall;

	// Hämta aktuell användare med lösenhash
	private User currentUser;
	private String username;
	private String domain;
	private String password;

	private int readyCounter = 0;
	private int registeringCounter = 0;
	private int failedCounter = 0;
	
	private SipRegistrationListener sipRegListener;

	private static SipRegistrator instance = new SipRegistrator();

	private SipRegistrator(){}

	/**
	 * Används för att hämta instans när context inte behöver sättas. Används för alla
	 * instanser förutom första gången SIP registreras. (görs i MainActivity)
	 * @return
	 */
	public static SipRegistrator getInstance(){
		return instance;
	}

	/**
	 * Initiera SIP-manager så SIP-profilen senare kan skapas så användaren kan registreras 
	 * hos SIP-servern.
	 */
	public void initializeManager() {

		// Registrera intents för utgående och inkommande samtal. Gör detta bara en gång.
		if(!isIntentsRegistred){
			intentsRegistred++;
			Log.d("SIP/Singletonklassen","Registrerar intents för gång nummer: " + intentsRegistred);
			// SIP: Registrera Intent för att hantera inkommande SIP-samtal
			filter = new IntentFilter();
			filter.addAction("com.klient_projekttermin.INCOMING_CALL");
			callReceiver = new IncomingCallReceiver();
			context.registerReceiver(callReceiver, filter);
			isIntentsRegistred = true;
		}

		// Om manager inte finns, skapa den
		if(manager == null) {
			manager = SipManager.newInstance(context);
		}
		
		// Fortsätt sedan med att initiera själva SIP-profilen
		initializeLocalProfile();

	}
	/**
	 * Initiera SIP-profilen (alltså gör själva registreringen av användaren hos servern)
	 */
	private void initializeLocalProfile() {
		// Om manager inte finns är något galet. Gör då inget.
		if (manager == null) {
			return;
		}

		// Om den lokala profilen redan är initierad, stäng den och hämta på nytt.
		if (me != null) {
			closeLocalProfile();
		}

		// Hämta aktuell användare med tillhörande inloggningsuppgifter
		currentUser = User.getInstance();
		username = currentUser.getAuthenticationModel().getUserName();
		password = currentUser.getAuthenticationModel().getPasswordHash();

		// Om användaren inte är registrerad redan, försök registrera hos SIP-servern
		if(!isRegistred){
			Log.d("SIP/SipRegistrator/InitializeLocalProfile","Ska skapa profil..");
			Log.d("SIP/SipRegistrator/InitializeLocalProfile","Användarnamn: " + username);
			try {
				// Skapa en SIP-profil
				SipProfile.Builder builder = new SipProfile.Builder(username, domain);
				builder.setPassword(password);
				me = builder.build();

				// Låt klienten kunna ta emot samtal (kryssrutan under Konton i samtalsinställningar, tar massa batteri)
				Intent intent = new Intent();
				intent.setAction("com.klient_projekttermin.INCOMING_CALL");
				PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, Intent.FILL_IN_DATA);
				
				// Öppna sedan SIP-profilen mha SIP-manager
				manager.open(me, pendingIntent, null);

				// Skapa en lyssnare som lyssnar efter hur väl SIP-registreringen har gått
				sipRegListener = new SipRegistrationListener() {
					public void onRegistering(String localProfileUri) {
						registeringCounter++;
						Log.d("SIP/SipRegistrator/InitializeLocalProfile","Registrerar mot SIP-server för "+registeringCounter +" gången...");
					}

					public void onRegistrationDone(String localProfileUri, long expiryTime) {
						readyCounter++;
						Log.d("SIP/SipRegistrator/InitializeLocalProfile","Redo för "+readyCounter+" gången.");
						isRegistred = true;
					}

					public void onRegistrationFailed(String localProfileUri, int errorCode,
							String errorMessage) {
						failedCounter++;
						Log.d("SIP/SipRegistrator/InitializeLocalProfile","Misslyckades att registrera mot SIP-server för "+failedCounter+ " gången.");
						isRegistred = false;
					}
				};
				
				// Slutligen, koppla lyssnaren till SIP-managern så vi vet hur väl det gick.
				manager.setRegistrationListener(me.getUriString(), sipRegListener);
			} 
			catch (ParseException e) {
				Log.e("SIP/SipRegistrator/InitializeLocalProfile","Parse error.. "+e);
				e.printStackTrace();
			}
			catch (SipException e) {
				Log.e("SIP/SipRegistrator/InitializeLocalProfile","Sip exceptionerror.. "+e);
				e.printStackTrace();
			}
		}
	}
	/**
	 * Stäng SIP-profilen, avallokera minnesresurser i telefonen och avregistrera 
	 * klienten hos SIP-servern.
	 */
	public void closeLocalProfile() {
		
		// Om manager inte finns behöver man inte göra mer än att avsluta här
		if (manager == null) {
			// Är användaren registrerad, sätt isRegistred till false
			if(isRegistred){
				isRegistred = false;
			}
			return;
		}
		// Annars, försök avregistrera klienten samt stänga SIP-manager
		try {
			if (me != null) {
				manager.unregister(me, sipRegListener);
				manager.close(me.getUriString());
				if(isRegistred()){
					isRegistred = false;
				}
			}
		} catch (Exception ee) {
			Log.d("SIP/SipRegistrator/CloseLocalProfile", "Failed to close local profile.", ee);
			ee.printStackTrace();
		}
	}

	public void endLocalProfile(){
		// Om Intent för att öppna samtalsdialogen vid inkommande samtal finns, avregistrera det
		if(isIntentsRegistred){
			context.unregisterReceiver(callReceiver);
			isIntentsRegistred = false;
		}
		closeLocalProfile();
	}
	
	/**
	 * Ring ett utgående samtal.
	 * @param nameToCall	Namn på person att ringa. Måste existera på SIP-servern för att det ska fungera.
	 */
	public void initiateCall(String nameToCall) {
		Log.d("SipRegistrator/initiateCall","Kontakt att ringa: "+nameToCall);
		sipAddress = "sip:"+nameToCall+"@"+domain;
		
		try {
			// I princip alla SIP-händelser sker med lyssnare. Här en lyssnare för hur väl det ringda samtalet har gått
			SipAudioCall.Listener listener = new SipAudioCall.Listener() {
				// Om den andra änden är upptaget
				@Override
				public void onCallBusy(SipAudioCall call) {
					Log.d("SIP/Singleton/InitCall","Den du ringt till är upptagen");
				}
				// När man ringer
				@Override
				public void onCalling(SipAudioCall call){
					Log.d("SIP/Singleton/InitCall","onCalling");
				}
				// När ett samtal är kopplat. Dock ej samma som att den andra änden har svarat.
				@Override
				public void onCallEstablished(SipAudioCall call) {
					Log.d("SIP/Singleton/InitCall","onCallEstablished");
					callStatus.setStatus(true);
					call.startAudio();
					call.setSpeakerMode(true);
				}
				// När man hör ringsignalerna i luren och vet att samtalet har kopplats
				// och det ringer hos den andra änden. Slutar när andra änden svarat.
				@Override
				public void onRingingBack (SipAudioCall call){
					Log.d("SIP/Singleton/InitCall","onRingingBack");
				}
				
				// När samtalet har avslutats/brutits.
				@Override
				public void onCallEnded(SipAudioCall call) {
					Log.d("SIP/Singleton/InitCall","onCallEnded");
					
					// Underrätta ev lyssnare
					callStatus.setStatus(false);
					
					// Försök att avsluta samtalet
					try {
						call.endCall();
					} catch (SipException e) {
						e.printStackTrace();
					}
				}
			};
			if(manager == null){
				Log.d("SIP/SipRegistrator/InitiateCall","Manager är null...");
			}
			// Hämta en instans av CurrentCall
			currentCall = CurrentCall.getInstance();
			
			// Sätt utgående samtal till currentCall
			currentCall.setCall(manager.makeAudioCall(me.getUriString(), sipAddress, listener, 30));
			
			// Starta dialogrutan för samtal och sätt det som ett utgående samtal
			Intent startIncoming = new Intent(context,CallDialogue.class);
			startIncoming.putExtra("outgoing",true);
			startIncoming.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(startIncoming);
		}
		catch (Exception e) {
			Log.i("SIP/SipRegistrator/InitiateCall", "Fel när SIP-manager försökte stängas..", e);
			if (me != null) {
				try {
					manager.close(me.getUriString());
				} catch (Exception ee) {
					Log.i("SIP/SipRegistrator/InitiateCall",
							"Fel när SIP-manager försökte stängas..", ee);
					ee.printStackTrace();
				}
			}
			// ..gammalt
//			if (call != null) {
//				call.close();
//			}
		}
	}

	/**
	 * Returnerar true om SIP-profilen är registrerad, false annars
	 * @return
	 */
	public boolean isRegistred() {
		Log.d("SIP/SipRegistrator/isRegistred","Ska returnera isRegistred. Är jag registrerad? " +((isRegistred)?"Ja":"Nej"));
		return isRegistred;
	}
	
	/**
	 * Sätt context för SIP-profilen. Krävs innan registrering.
	 * @param context	ApplicationContext
	 */
	public void setContext(Context context) {
		this.context = context;
	}

	public Context getContext() {
		return context;
	}

	/**
	 * Sätt domän att ansluta till. Krävs innan registrering.
	 * @param domain IP-nummer till SIP-server
	 */
	public void setDomain(String domain) {
		this.domain = domain;
	}
	
}