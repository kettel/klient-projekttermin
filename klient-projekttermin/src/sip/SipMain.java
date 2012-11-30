package sip;

import java.text.ParseException;

import loginFunction.InactivityListener;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.sip.SipAudioCall;
import android.net.sip.SipException;
import android.net.sip.SipManager;
import android.net.sip.SipProfile;
import android.net.sip.SipRegistrationListener;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

import com.klient_projekttermin.R;


public class SipMain extends InactivityListener{
	private String currentUser;
	public SipManager manager = null;
    public SipProfile me = null;
    public SipAudioCall call = null;
    public String sipAddress = null;
    public IncomingCallReceiver callReceiver;
    
    // Bör hämtas från databas i framtiden
    String username = "1001";
    String domain = "94.254.72.38";
    String password = "1001";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sip_main);
		
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			currentUser = extras.getString("USER");
		}
		
		// Set up the intent filter.  This will be used to fire an
        // IncomingCallReceiver when someone calls the SIP address used by this
        // application.
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.klient_projekttermin.INCOMING_CALL");
        callReceiver = new IncomingCallReceiver();
        this.registerReceiver(callReceiver, filter);
		
		initializeManager();
		
		//makeOutgoingCall("1002");
	}
	
	@Override
    public void onStart() {
        super.onStart();
        // When we get back from the preference setting Activity, assume
        // settings have changed, and re-login with new auth info.
        initializeManager();
    }
	
	@Override
    public void onDestroy() {
        super.onDestroy();
        if (call != null) {
            call.close();
        }

        closeLocalProfile();

        if (callReceiver != null) {
            this.unregisterReceiver(callReceiver);
        }
    }
	
	public void initializeManager() {
		if(manager == null) {
          manager = SipManager.newInstance(this);
        }
        initializeLocalProfile();
    }
	
	/**
	 * Registrera användaren hos SIP-servern
	 */
	private void initializeLocalProfile() {
		if (manager == null) {
            return;
        }

        if (me != null) {
            closeLocalProfile();
        }
        try {
        	
        	SipProfile.Builder builder = new SipProfile.Builder(username, domain);
        	builder.setPassword(password);
        	me = builder.build();

        	// Låt klienten kunna ta emot samtal (kryssrutan under Konton i samtalsinställningar)
        	Intent intent = new Intent();
        	intent.setAction("com.klient_projekttermin.INCOMING_CALL");
        	PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, Intent.FILL_IN_DATA);
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
                	Log.d("SIP","Registration failed.  Please check settings.");
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
	
	private void initiateCall() {
		try {
            SipAudioCall.Listener listener = new SipAudioCall.Listener() {
                // Much of the client's interaction with the SIP Stack will
                // happen via listeners.  Even making an outgoing call, don't
                // forget to set up a listener to set things up once the call is established.
                @Override
                public void onCallEstablished(SipAudioCall call) {
                    call.startAudio();
                    call.setSpeakerMode(true);
                    // Om ljudet är av, toggla ljudet
                    if(call.isMuted()){
                    	call.toggleMute();
                    }
                    
                }

                @Override
                public void onCallEnded(SipAudioCall call) {
                    Log.d("SIP","Samtal avslutat");
                }
            };
            String sipNumber = "1002";
            sipAddress = "sip:"+sipNumber+"@"+domain;
            call = manager.makeAudioCall(me.getUriString(), sipAddress, listener, 30);

        }
        catch (Exception e) {
            Log.i("SIP", "Error when trying to close manager.", e);
            if (me != null) {
                try {
                    manager.close(me.getUriString());
                } catch (Exception ee) {
                    Log.i("SIP",
                            "Error when trying to close manager.", ee);
                    ee.printStackTrace();
                }
            }
            if (call != null) {
                call.close();
            }
        }
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_sip_main, menu);
		return true;
	}
	
	
	
	
}
