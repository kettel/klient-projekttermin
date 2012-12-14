package sip;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.sip.SipAudioCall;
import android.net.sip.SipException;
import android.net.sip.SipProfile;
import android.util.Log;

/*** Lyssnar efter inkommande SIP-samtal, fångar dem och ger dem till CallDialog.
 */
public class IncomingCallReceiver extends BroadcastReceiver {
	
	private int callCounter = 0;
	
	private SipRegistrator regSip;
	private CurrentCall currentCall;
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
		regSip = SipRegistrator.getInstance();
		
		try {
			SipAudioCall.Listener listener = new SipAudioCall.Listener() {
				// Vid inkommande samtal (när telefonen ringer)
				@Override
				public void onRinging(SipAudioCall call, SipProfile caller) {
					try {
//						regSip.setCall(call);
						// Om klienten redan är i samtal, hantera inte inkommande samtal
						if(regSip.callStatus.getStatus()){
							Log.d("SIP/IncomingCallReceiver","Upptaget... Är i samtal med annan. Tar emot samtal ifrån: " + call.getPeerProfile().getDisplayName() + " som kommer nekas.");
							return;
						}
						if(currentCall == null){
							currentCall = CurrentCall.getInstance();
						}
						currentCall.setCall(call);
						Intent startIncomingCallDialog = new Intent(context,CallDialogue.class);
						startIncomingCallDialog.putExtra("caller", call.getPeerProfile().getDisplayName());
						startIncomingCallDialog.putExtra("outgoing",false);
						startIncomingCallDialog.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						context.startActivity(startIncomingCallDialog);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				// När samtalet är avslutat/brutet
				@Override
				public void onCallEnded(SipAudioCall call){
					Log.d("SIP/IncomingCallRec/onCallEnded","Samtalet avslutades...");
					regSip.callStatus.setStatus(false);
					try {
						call.endCall();
					} catch (SipException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			};
			
			currentCall = CurrentCall.getInstance();
			// Om användaren inte är i samtal
			if(!currentCall.isBusy()){
				currentCall.setCall(regSip.manager.takeAudioCall(intent, listener));
				
				// Starta sedan CallDialog för ett inkommande samtal
				Intent startIncomingCallDialog = new Intent(context,CallDialogue.class);
				startIncomingCallDialog.putExtra("caller", currentCall.getCall().getPeerProfile().getDisplayName());
				startIncomingCallDialog.putExtra("outgoing",false);
				startIncomingCallDialog.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(startIncomingCallDialog);
			}
			// Om användaren är upptagen i ett annat samtal
			else{
				Log.d("SIP/IncomingCallReceiver","Upptaget... Är redan i samtal med "+currentCall.getCall().getPeerProfile().getDisplayName());
				return;
			}
		} catch (Exception e) {
			Log.e("SIP/IncomingCallReceiver","Något gick fel med det inkommande samtalet..", e);
		}
	}
}
