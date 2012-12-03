package sip;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.sip.SipAudioCall;
import android.net.sip.SipException;
import android.net.sip.SipProfile;
import android.util.Log;

import com.klient_projekttermin.MainActivity;

/*** Lyssnar efter inkommande SIP-samtal, fångar dem och ger dem till SipMain.
 */
public class IncomingCallReceiver extends BroadcastReceiver {
	static boolean answerCall = false;
	static SipAudioCall incomingCall = null;
	static int callCounter = 0;
	/**
	 * Processes the incoming call, answers it, and hands it over to the
	 * WalkieTalkieActivity.
	 * @param context The context under which the receiver is running.
	 * @param intent The intent being received.
	 */
	@Override
	public void onReceive(final Context context, Intent intent) {
		callCounter++;
		Log.d("SIP","Ett inkommande samtal... Samtal nummer: "+callCounter);
		
		try {
			SipAudioCall.Listener listener = new SipAudioCall.Listener() {
				@Override
				public void onRinging(SipAudioCall call, SipProfile caller) {
					try {
						Intent startIncomingCallDialog = new Intent(context,IncomingCallDialog.class);
						startIncomingCallDialog.putExtra("caller", call.getPeerProfile().getDisplayName());
						context.startActivity(startIncomingCallDialog);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			};
			MainActivity mActivity = (MainActivity) context;
			incomingCall = mActivity.manager.takeAudioCall(intent, listener);
			// Fuling för att hindra att fler än 1 callDialog startas..
			Intent startIncomingCallDialog = new Intent(context,IncomingCallDialog.class);
			startIncomingCallDialog.putExtra("caller", incomingCall.getPeerProfile().getDisplayName());
			context.startActivity(startIncomingCallDialog);
			mActivity.call = incomingCall;
		} catch (Exception e) {
			if (incomingCall != null) {
				incomingCall.close();
			}
		}
	}
	public static void answerCall(SipAudioCall call){
		try {
			call.answerCall(30);
			call.startAudio();
			call.setSpeakerMode(true);
			if(call.isMuted()) {
				call.toggleMute();
			}
		} catch (SipException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void dropCall(SipAudioCall call){
		try {
			call.endCall();
		} catch (SipException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
