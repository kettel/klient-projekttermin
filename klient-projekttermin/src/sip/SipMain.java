package sip;

import loginFunction.InactivityListener;

import java.text.ParseException;

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
	SipManager manager = null;
    private SipProfile me = null;
    SipAudioCall call = null;
    private String sipAddress = null;
    private IncomingCallReceiver callReceiver;
	private RegisterWithSipServer regSip = RegisterWithSipServer.getInstance();
	
	
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
		
		initiateCall("1003");
	}
	
	@Override
    public void onStart() {
        super.onStart();
        // When we get back from the preference setting Activity, assume
        // settings have changed, and re-login with new auth info.
        initializeManager();

		initiateCall("1003");
    }
	
	@Override
	public void onResume(){
		super.onResume();
		initializeManager();
		initiateCall("1003");
	}
	
	@Override
    public void onDestroy() {
        super.onDestroy();
        if (call != null) {
            call.close();
        }
        regSip.closeLocalProfile();

        if (callReceiver != null) {
            this.unregisterReceiver(callReceiver);
        }
    }
	
	public void initializeManager() {
		if(manager == null) {
          manager = SipManager.newInstance(this);
        }
        regSip.initializeLocalProfile();
    }
	
	
	
	private void initiateCall(String numberToCall) {
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
            
            sipAddress = "sip:"+numberToCall+"@"+regSip.staticdomain;
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
