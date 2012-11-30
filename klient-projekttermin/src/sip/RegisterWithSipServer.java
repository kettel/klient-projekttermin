package sip;

import java.text.ParseException;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.sip.SipAudioCall;
import android.net.sip.SipException;
import android.net.sip.SipManager;
import android.net.sip.SipProfile;
import android.net.sip.SipRegistrationListener;
import android.util.Log;

public class RegisterWithSipServer {
	public static SipManager manager = null;
    public static SipProfile me = null;
    public static SipAudioCall call = null;
    public static String sipAddress = null;
    public static IncomingCallReceiver callReceiver;
    
    public static Context context = null;
	
    // Bör hämtas från databas i framtiden
    public static String username = "1001";
    public static String staticdomain = "94.254.72.38";
    public static String password = "1001";
	
	
	private static RegisterWithSipServer instance = new RegisterWithSipServer();
	private RegisterWithSipServer(){}
	
	public static RegisterWithSipServer getInstance(Context context){
		RegisterWithSipServer.context = context;
		return instance;
	}
	public static RegisterWithSipServer getInstance(){
		return instance;
	}
	
	public void initializeManager() {
		if(manager == null) {
          manager = SipManager.newInstance(context);
        }
        initializeLocalProfile();
    }
	/**
	 * Registrera användaren hos SIP-servern
	 */
	public void initializeLocalProfile() {
		Log.d("SIP","Ska initiera SIP...@RegisterWithSip/46");
		if (manager == null) {
            return;
        }

        if (me != null) {
            closeLocalProfile();
        }
        try {
        	
        	SipProfile.Builder builder = new SipProfile.Builder(username, staticdomain);
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
                	Log.d("SIP","Registering with SIP Server...");
                }

                public void onRegistrationDone(String localProfileUri, long expiryTime) {
                	Log.d("SIP","Ready");
                }

                public void onRegistrationFailed(String localProfileUri, int errorCode,
                        String errorMessage) {
                	Log.d("SIP","Registration with SIP-server failed.  Please check settings.");
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
}
