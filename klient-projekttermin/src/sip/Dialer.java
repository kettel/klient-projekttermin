package sip;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class Dialer extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, final Intent intent) {     

		if (intent.getAction().equals("android.intent.action.NEW_OUTGOING_CALL")) {

			String phoneNumber = intent.getExtras().getString( "android.intent.extra.PHONE_NUMBER");
			Log.d("SIP","Ringer ett utgående samtal. Borde starta native dial!");
			// Call some function from here to make SIP Call using this phoneNumber.
			// Use this "phoneNumber" to your sip application & setResultData null.

			setResultData(null);

		} 

	}
}