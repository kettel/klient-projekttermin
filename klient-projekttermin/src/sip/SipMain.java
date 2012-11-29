package sip;

import java.text.ParseException;

import loginFunction.InactivityListener;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
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
		registerSipAccount();
		
		makeOutgoingCall("1002");
	}
	
	private void makeOutgoingCall(String sipNumber) {
		try {
            SipAudioCall.Listener listener = new SipAudioCall.Listener() {
                // Much of the client's interaction with the SIP Stack will
                // happen via listeners.  Even making an outgoing call, don't
                // forget to set up a listener to set things up once the call is established.
                @Override
                public void onCallEstablished(SipAudioCall call) {
                    call.startAudio();
                    call.setSpeakerMode(true);
                    call.toggleMute();
                }

                @Override
                public void onCallEnded(SipAudioCall call) {
                    Log.d("SIP","Samtal avslutat");
                }
            };
            sipAddress = "sip:"+sipNumber+"@"+domain;
            call = manager.makeAudioCall(me.getUriString(), sipAddress, listener, 30);

        }
        catch (Exception e) {
            Log.i("WalkieTalkieActivity/InitiateCall", "Error when trying to close manager.", e);
            if (me != null) {
                try {
                    manager.close(me.getUriString());
                } catch (Exception ee) {
                    Log.i("WalkieTalkieActivity/InitiateCall",
                            "Error when trying to close manager.", ee);
                    ee.printStackTrace();
                }
            }
            if (call != null) {
                call.close();
            }
        }
	}

	private void makeOutgoingCallNative(String sipNumber) {
		// Ring ett samtal till 1002 med native dial
		String uri = "sip:" + sipNumber + "@94.254.72.38";
		Intent intent = new Intent(Intent.ACTION_CALL);
		intent.setData(Uri.parse(uri));
		startActivity(intent);
	}

	private void registerSipAccount() {
		if (SipManager.isVoipSupported(this) && SipManager.isApiSupported(this)){
			// SIP is supported, let's go!
			try {
				if(manager == null) {
					manager = SipManager.newInstance(this);
				}
				
				SipProfile.Builder builder = new SipProfile.Builder(username, domain);
				builder.setPassword(password);
	            me = builder.build();
	            
	            // Låt klienten kunna ta emot samtal (kryssrutan under Konton i samtalsinställningar)
	            Intent intent = new Intent();
	            intent.setAction("android.klient-projekttermin.INCOMING_CALL");
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
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_sip_main, menu);
		return true;
	}
}
