package sip;

import android.net.sip.SipAudioCall;
import android.net.sip.SipException;
import android.util.Log;

public class StaticCall {

	public static SipAudioCall call = null;
	
    public static void answerCall(SipAudioCall call){
		try {
			Log.d("SIP/StaticCall/AnswerCall","Ska svara på samtal..");
			call.answerCall(30);
			call.startAudio();
			call.setSpeakerMode(true);
			if(call.isMuted()) {
				call.toggleMute();
			}
			Log.d("SIP/StaticCall/AnswerCall","Är samtalet öppet? " + call.isInCall());
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
