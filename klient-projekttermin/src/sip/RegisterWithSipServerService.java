package sip;

import java.text.ParseException;
import java.util.Timer;
import java.util.TimerTask;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.sip.SipAudioCall;
import android.net.sip.SipException;
import android.net.sip.SipManager;
import android.net.sip.SipProfile;
import android.net.sip.SipRegistrationListener;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class RegisterWithSipServerService extends Service {
	private static Timer timer;
	private static boolean isRegistred = false;
	private static Context staticContext;
	
	private static boolean isIntentsRegistred = false;
	
	public static SipManager manager = null;
	public static SipProfile me = null;
	public static String sipAddress = null;
	
	public static boolean isInCall = false;
	
	// Variabler som jag inte vet vart de ska vara. Main? Service? Hjälpklass?
	public static IncomingCallReceiver callReceiver;
//    public static OutgoingCallReceiver callSender;
	public static SipAudioCall call = null;
	
	// Bör hämtas från databas i framtiden
	public static String username = "1001";
	public static String staticdomain = "94.254.72.38";
	public static String password = "1001";

	private final IBinder mBinder = new MyBinder();

	@Override
	public IBinder onBind(Intent arg0) {
		initializeManager(getApplicationContext());
		return mBinder;
	}

	public class MyBinder extends Binder {
		public RegisterWithSipServerService getService() {
			return RegisterWithSipServerService.this;
		}
	}
	
	public static void initializeManager(Context context) {
		staticContext = context;
		// Registrera intents för utgående och inkommande samtal
		if(!isIntentsRegistred){
			// SIP: Registrera Intent för att hantera inkommande SIP-samtal
	        IntentFilter filter = new IntentFilter();
	        filter.addAction("com.klient_projekttermin.INCOMING_CALL");
	        callReceiver = new IncomingCallReceiver();
	        context.registerReceiver(callReceiver, filter);
	        // SIP: Utgående samtal
//	        IntentFilter filterOut = new IntentFilter();
//	        filterOut.addAction("com.klient_projekttermin.OUTGOING_CALL");
//	        callSender = new OutgoingCallReceiver();
//	        context.registerReceiver(callSender, filter);
		}
		
		if(RegisterWithSipServerService.manager == null) {
			RegisterWithSipServerService.manager = SipManager.newInstance(context);
        }
        initializeLocalProfile(context);
        
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
					if(RegisterWithSipServerService.manager == null) {
						RegisterWithSipServerService.manager = SipManager.newInstance(staticContext);
					}
					initializeLocalProfile(staticContext);
				}
			}
		}, delay, period);
    }
	/**
	 * Registrera användaren hos SIP-servern
	 */
	public static void initializeLocalProfile(Context context) {
		if (RegisterWithSipServerService.manager == null) {
            return;
        }

        if (RegisterWithSipServerService.me != null) {
        	closeLocalProfile();
        }
        Log.d("SIP","Ska skapa profil..");
        try {
        	
        	SipProfile.Builder builder = new SipProfile.Builder(username, staticdomain);
        	builder.setPassword(password);
        	RegisterWithSipServerService.me = builder.build();

        	// Låt klienten kunna ta emot samtal (kryssrutan under Konton i samtalsinställningar)
        	Intent intent = new Intent();
        	intent.setAction("com.klient_projekttermin.INCOMING_CALL");
        	//intent.setAction("com.klient_projekttermin.OUTGOING_CALL");
        	PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, Intent.FILL_IN_DATA);
        	RegisterWithSipServerService.manager.open(RegisterWithSipServerService.me, pendingIntent, null);

        	// Sätt upp en lyssnare som lyssnar efter hur väl anslutningen har gått
        	RegisterWithSipServerService.manager.setRegistrationListener(RegisterWithSipServerService.me.getUriString(), new SipRegistrationListener() {
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
    public static void closeLocalProfile() {
        if (RegisterWithSipServerService.manager == null) {
            return;
        }
        try {
            if (RegisterWithSipServerService.me != null) {
            	RegisterWithSipServerService.manager.close(RegisterWithSipServerService.me.getUriString());
            }
        } catch (Exception ee) {
            Log.d("SIP", "Failed to close local profile.", ee);
        }
    }
    
    /**
     * Make an outgoing call.
     */
    public static void initiateCall() {
    	sipAddress = "sip:1002@94.254.72.38";

        try {
            SipAudioCall.Listener listener = new SipAudioCall.Listener() {
                // Much of the client's interaction with the SIP Stack will
                // happen via listeners.  Even making an outgoing call, don't
                // forget to set up a listener to set things up once the call is established.
                @Override
                public void onCallEstablished(SipAudioCall call) {
                	isInCall= true;
                    call.startAudio();
                    call.setSpeakerMode(true);
                    //call.toggleMute();
                }

                @Override
                public void onCallEnded(SipAudioCall call) {
                	isInCall = false;
                    //updateStatus("Ready.");
                }
            };

            call = RegisterWithSipServerService.manager.makeAudioCall(me.getUriString(), sipAddress, listener, 30);
            Intent startIncomingCallDialog = new Intent(staticContext,IncomingCallDialog.class);
			startIncomingCallDialog.putExtra("caller", call.getPeerProfile().getDisplayName());
			startIncomingCallDialog.putExtra("outgoing",true);
			startIncomingCallDialog.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			staticContext.startActivity(startIncomingCallDialog);
        }
        catch (Exception e) {
            Log.i("RegisterWithSipServerService/InitiateCall", "Error when trying to close manager.", e);
            if (RegisterWithSipServerService.me != null) {
                try {
                	RegisterWithSipServerService.manager.close(RegisterWithSipServerService.me.getUriString());
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