package sip;

import contacts.ContactsBookActivity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.sip.SipAudioCall;
import android.net.sip.SipException;
import android.net.sip.SipProfile;
import android.os.IBinder;
import android.util.Log;

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
						startIncomingCallDialog.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						context.startActivity(startIncomingCallDialog);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			};
			// TODO: Problem i Static-land här då manager antagligen inte riktigt finns..
			// TODO: Döda SipService när appen dödas.
			// TODO: Se till så att inställningar inte dras ned i hastighet så kopiöst...
			incomingCall = RegisterWithSipServerService.manager.takeAudioCall(intent, listener);
			Intent startIncomingCallDialog = new Intent(context,IncomingCallDialog.class);
			startIncomingCallDialog.putExtra("caller", incomingCall.getPeerProfile().getDisplayName());
			startIncomingCallDialog.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			RegisterWithSipServerService.call = incomingCall;
			context.startActivity(startIncomingCallDialog);
		} catch (Exception e) {
			if (incomingCall != null) {
				Log.d("SIP/IncomingCallReceiver","IncomingCall är inte null men något gick fel. Stänger...");
				incomingCall.close();
				Log.d("SIP/IncomingCallReceiver","Fel: " + e.toString());
				e.printStackTrace();
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
