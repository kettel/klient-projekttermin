package sip;

import java.util.Observable;
import java.util.Observer;

import android.util.Log;

public class ObserverCallStatus implements Observer {

	public void update(Observable arg0, Object arg1) {
		Log.d("SIP/ObserverCallStatus","Nu har visst användaren svarat..");
		
	}

}
