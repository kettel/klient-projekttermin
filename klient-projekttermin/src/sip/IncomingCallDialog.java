package sip;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ToggleButton;

import com.klient_projekttermin.R;

public class IncomingCallDialog extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("SIP","Inne i incomingCallDialog-aktiviteten");
		setContentView(R.layout.activity_incoming_call_dialog);
		
		ToggleButton toggle = (ToggleButton) findViewById(R.id.toggleButton1);
		toggle.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// Svara på samtal
				if(buttonView.isChecked()){
					IncomingCallReceiver.answerCall = true;
					IncomingCallReceiver.answerCall(IncomingCallReceiver.incomingCall);
				}
				// Lägg på samtal
				if(!buttonView.isChecked() && IncomingCallReceiver.answerCall){
					IncomingCallReceiver.answerCall = false;
					IncomingCallReceiver.dropCall(IncomingCallReceiver.incomingCall);
					Log.d("SIP", "Samtal avslutat. Ska nu köra finish på aktivitet...");
					finish();
				}
			}
			
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_incoming_call_dialog, menu);
		return true;
	}
	

}
