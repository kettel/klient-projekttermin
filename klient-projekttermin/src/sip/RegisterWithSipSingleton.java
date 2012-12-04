package sip;

import java.text.ParseException;
import java.util.Timer;
import java.util.TimerTask;

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
	private Timer timer;
	private boolean isRegistred = false;
	private static Context context;

	private boolean isIntentsRegistred = false;
	private int intentsRegistred = 0;

	public SipManager manager = null;
	public SipProfile me = null;
	public String sipAddress = null;

	public ObservableCallStatus callStatus = new ObservableCallStatus();

	// Variabler som jag inte vet vart de ska vara. Main? Service? Hjälpklass?
	public IncomingCallReceiver callReceiver;
	public SipAudioCall call = null;

	// Bör hämtas från databas i framtiden
	public String username = "1001";
	public String domain = "94.254.72.38";
	public String password = "1001";

	private static RegisterWithSipSingleton instance = new RegisterWithSipSingleton();

	private RegisterWithSipSingleton(){}

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
        
        int delay = 5000; // delay for 5 sec.
		int period = 1000; // repeat every sec.

		// Försök återregistrera varje sekund om man inte är registrerad
		timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask()
		{
			public void run()
			{
				if(!isRegistred){
					Log.d("SIP/RegisterWithSipServer","Försöker mot SIP-server...");
					if(manager == null) {
						manager = SipManager.newInstance(context);
					}
					initializeLocalProfile();
				}
			}
		}, delay, period);
    }
	/**
	 * Registrera användaren hos SIP-servern
	 */
	public void initializeLocalProfile() {
		if (manager == null) {
            return;
        }

        if (me != null) {
        	closeLocalProfile();
        }
        Log.d("SIP","Ska skapa profil..");
        try {
        	
        	SipProfile.Builder builder = new SipProfile.Builder(username, domain);
        	builder.setPassword(password);
        	me = builder.build();

        	// Låt klienten kunna ta emot samtal (kryssrutan under Konton i samtalsinställningar)
        	Intent intent = new Intent();
        	intent.setAction("com.klient_projekttermin.INCOMING_CALL");
        	//intent.setAction("com.klient_projekttermin.OUTGOING_CALL");
        	PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, Intent.FILL_IN_DATA);
        	manager.open(me, pendingIntent, null);

        	// Sätt upp en lyssnare som lyssnar efter hur väl anslutningen har gått
        	manager.setRegistrationListener(me.getUriString(), new SipRegistrationListener() {
                public void onRegistering(String localProfileUri) {
                	Log.d("SIP","Registrerar mot SIP-server...");
                	//isRegistred = false;
                }

                public void onRegistrationDone(String localProfileUri, long expiryTime) {
                	Log.d("SIP","Redo");
                	isRegistred = true;
                	//initiateCall();
                }

                public void onRegistrationFailed(String localProfileUri, int errorCode,
                        String errorMessage) {
                	Log.d("SIP","Misslyckades att registrera mot SIP-server. Försöker igen...");
                	isRegistred = false;
                }
            });
        } 
        catch (ParseException e) {
        	Log.e("SIP","Parse error.. "+e);
        }
        catch (SipException e) {
        	Log.e("SIP","Sip exceptionerror.. "+e);
        }
		
	}
	/**
     * Closes out your local profile, freeing associated objects into memory
     * and unregistering your device from the server.
     */
    public void closeLocalProfile() {
    	if (timer != null){
        	timer.cancel();
        	timer.purge();
        }
    	if (manager == null) {
            return;
        }
        try {
            if (me != null) {
            	manager.close(me.getUriString());
            }
        } catch (Exception ee) {
            Log.d("SIP", "Failed to close local profile.", ee);
        }
    }
    
    /**
     * Make an outgoing call.
     */
    public void initiateCall() {
    	sipAddress = "sip:1003@94.254.72.38";

        try {
            SipAudioCall.Listener listener = new SipAudioCall.Listener() {
                // Much of the client's interaction with the SIP Stack will
                // happen via listeners.  Even making an outgoing call, don't
                // forget to set up a listener to set things up once the call is established.
                @Override
                public void onCallEstablished(SipAudioCall call) {
                	//isInCall= true;
                	callStatus.setStatus(true);
                    call.startAudio();
                    call.setSpeakerMode(true);
                    //call.toggleMute();
                }

                @Override
                public void onCallEnded(SipAudioCall call) {
                	//isInCall = false;
                	callStatus.setStatus(false);
                    //updateStatus("Ready.");
                }
            };

            call = manager.makeAudioCall(me.getUriString(), sipAddress, listener, 30);
            Intent startIncomingCallDialog = new Intent(context,IncomingCallDialog.class);
			startIncomingCallDialog.putExtra("caller", call.getPeerProfile().getDisplayName());
			startIncomingCallDialog.putExtra("outgoing",true);
			startIncomingCallDialog.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(startIncomingCallDialog);
        }
        catch (Exception e) {
            Log.i("RegisterWithSipServerService/InitiateCall", "Error when trying to close manager.", e);
            if (me != null) {
                try {
                	manager.close(me.getUriString());
                } catch (Exception ee) {
                    Log.i("RegisterWithSipServerService/InitiateCall",
                            "Error when trying to close manager.", ee);
                    ee.printStackTrace();
                }
            }
            if (call != null) {
            	call.close();
            }
        }
    }

}
