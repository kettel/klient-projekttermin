package sip;

import com.klient_projekttermin.MainActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.sip.*;
import android.util.Log;

/*** Lyssnar efter inkommande SIP-samtal, fångar dem och ger dem till SipMain.
*/
public class IncomingCallReceiver extends BroadcastReceiver {
   /**
    * Processes the incoming call, answers it, and hands it over to the
    * WalkieTalkieActivity.
    * @param context The context under which the receiver is running.
    * @param intent The intent being received.
    */
   @Override
   public void onReceive(Context context, Intent intent) {
       SipAudioCall incomingCall = null;
       Log.d("SIP","Ett inkommande samtal...");
       try {
           SipAudioCall.Listener listener = new SipAudioCall.Listener() {
               @Override
               public void onRinging(SipAudioCall call, SipProfile caller) {
                   try {
                       call.answerCall(30);
                   } catch (Exception e) {
                       e.printStackTrace();
                   }
               }
           };
           MainActivity mActivity = (MainActivity) context;
           incomingCall = mActivity.manager.takeAudioCall(intent, listener);
           incomingCall.answerCall(30);
           incomingCall.startAudio();
           incomingCall.setSpeakerMode(true);
           if(incomingCall.isMuted()) {
               incomingCall.toggleMute();
           }
           mActivity.call = incomingCall;
       } catch (Exception e) {
           if (incomingCall != null) {
               incomingCall.close();
           }
       }
   }
}
