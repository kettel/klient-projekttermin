package sip;

import android.net.sip.SipAudioCall;
import android.net.sip.SipException;
import android.util.Log;

public class CurrentCall {

	private SipAudioCall call = null;
	
	private CurrentCall(){}
	
	private static CurrentCall instance = new CurrentCall();
	
	public static CurrentCall getInstance(){
		return instance;
	}
	
    public void answerCall(){
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

	public void dropCall(){
		try {
			if(call != null){
				call.endCall();
			}
		} catch (SipException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public SipAudioCall getCall() {
		return call;
	}

	public void setCall(SipAudioCall call) {
		this.call = call;
	}

	/**
	 * Se efter om användaren är i samtal.
	 * @return True om sant, false annars.
	 */
	public boolean isBusy(){
		boolean busy = false;
		if(call == null){
			busy = false;
		}else{
			busy = call.isInCall();
		}
		return busy;
	}
}
