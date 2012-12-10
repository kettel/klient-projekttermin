package sip;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.sip.SipAudioCall;
import android.net.sip.SipException;
import android.net.sip.SipProfile;
import android.util.Log;

/*** Lyssnar efter inkommande SIP-samtal, fångar dem och ger dem till SipMain.
 */
public class IncomingCallReceiver extends BroadcastReceiver {
	
	static SipAudioCall incomingCall = null;
	static int callCounter = 0;
	
	private RegisterWithSipSingleton regSip;
	
	/**
	 * Processes the incoming call, answers it, and hands it over to the
	 * WalkieTalkieActivity.
	 * @param context The context under which the receiver is running.
	 * @param intent The intent being received.
	 */
	@Override
	public void onReceive(final Context context, Intent intent) {
		callCounter++;
		Log.d("SIP/IncomingCallReceiver/onReceive","Ett inkommande samtal... Samtal nummer: "+callCounter);
		regSip = RegisterWithSipSingleton.getInstance(context);
		
		try {
			SipAudioCall.Listener listener = new SipAudioCall.Listener() {
				@Override
				public void onRinging(SipAudioCall call, SipProfile caller) {
					try {
//						regSip.setCall(call);
						if(regSip.isCallAnswered()){
							Log.d("SIP/IncomingCallReceiver","Upptaget...");
						}
						Intent startIncomingCallDialog = new Intent(context,IncomingCallDialog.class);
						startIncomingCallDialog.putExtra("caller", call.getPeerProfile().getDisplayName());
						startIncomingCallDialog.putExtra("outgoing",false);
						startIncomingCallDialog.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						context.startActivity(startIncomingCallDialog);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				@Override
				public void onCallEnded(SipAudioCall call){
					Log.d("SIP/IncomingCallRec/onCallEnded","Samtalet avslutades...");
					RegisterWithSipSingleton.callStatus.setStatus(false);
					try {
						call.endCall();
					} catch (SipException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			};
			// TODO: Problem i Static-land här då manager antagligen inte riktigt finns..
			// TODO: Se till så att inställningar inte dras ned i hastighet så kopiöst...
			incomingCall = regSip.manager.takeAudioCall(intent, listener);
			Intent startIncomingCallDialog = new Intent(context,IncomingCallDialog.class);
			startIncomingCallDialog.putExtra("caller", incomingCall.getPeerProfile().getDisplayName());
			startIncomingCallDialog.putExtra("outgoing",false);
			startIncomingCallDialog.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			regSip.setCall(incomingCall);
			StaticCall.call = incomingCall;
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
}
