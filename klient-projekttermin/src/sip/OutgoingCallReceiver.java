package sip;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.sip.SipAudioCall;
import android.os.Bundle;
import android.util.Log;

public class OutgoingCallReceiver extends BroadcastReceiver {
	public static final String OUTGOING_CALL = "com.klient_projekttermin.action.OUTGOING_CALL";

	
	static boolean answerCall = false;
	static SipAudioCall outgoingCall = null;
	static int callCounter = 0;
	static String contactToCall = new String();
	/**
     * Make an outgoing call.
     */
	@Override
	public void onReceive(final Context context, Intent intent) {
		boolean isRegistred = false;
		try{
			isRegistred = RegisterWithSipServerService.manager.isRegistered(RegisterWithSipServerService.me.getUriString());
		}catch(Exception e){
			
		}
		if(!isRegistred){
			Log.d("SIP/OutgoingCallReceiver","Är ej registrerad, registrerar...");
			RegisterWithSipServerService.initializeManager(context);
		}

		// Hämta vem det ska ringas till
		Bundle extras = intent.getExtras();
		if (extras != null) {
			contactToCall = extras.getString("contactToCall");
			Log.d("SIP/OutgoingCallReceiver","Kontakt att ringa: " + contactToCall);
		}
		try {
            SipAudioCall.Listener listener = new SipAudioCall.Listener() {
                // Much of the client's interaction with the SIP Stack will
                // happen via listeners.  Even making an outgoing call, don't
                // forget to set up a listener to set things up once the call is established.
            	@Override
            	public void onCalling(SipAudioCall call){
            		// TODO: Vad ska hända när man ringer.. Spela upp ljud kanske?
            	}
                @Override
                public void onCallEstablished(SipAudioCall call) {
                	answerCall=true;
                    call.startAudio();
                    call.setSpeakerMode(true);
                    //call.toggleMute();
                    //updateStatus(call);
                }

                @Override
                public void onCallEnded(SipAudioCall call) {
                    //updateStatus("Ready.");
                	answerCall = false;
                }
            };
            
            
            String sipAddress = "sip:"+contactToCall + "@" + RegisterWithSipServerService.staticdomain;
            Log.d("SIP/OutgoingCallReceiver","Registrerad? " +RegisterWithSipServerService.manager.isRegistered(RegisterWithSipServerService.me.getUriString()));
            outgoingCall = RegisterWithSipServerService.manager.makeAudioCall(RegisterWithSipServerService.me.getUriString(), sipAddress, listener, 30);
			Intent startIncomingCallDialog = new Intent(context,IncomingCallDialog.class);
			startIncomingCallDialog.putExtra("caller", outgoingCall.getPeerProfile().getDisplayName());
			context.startActivity(startIncomingCallDialog);
			RegisterWithSipServerService.call = outgoingCall;

        }
        catch (Exception e) {
            Log.i("SIP/OutgoingCallReceiver", "Error when trying to close manager.", e);
            if (RegisterWithSipServerService.me != null) {
                try {
                	RegisterWithSipServerService.manager.close(RegisterWithSipServerService.me.getUriString());
                } catch (Exception ee) {
                    Log.i("SIP/OutgoingCallReceiver",
                            "Error when trying to close manager.", ee);
                    ee.printStackTrace();
                }
            }
            if (RegisterWithSipServerService.call != null) {
            	RegisterWithSipServerService.call.close();
            }
        }
    }
}
