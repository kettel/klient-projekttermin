package sip;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.klient_projekttermin.MainActivity;
import com.klient_projekttermin.R;

public class IncomingCallDialog extends Activity {

	private long timeWhenCallStarted = 0;
	private String timeInCall = new String();
	private String caller = new String();
	private Timer timer;
	
	RegisterWithSipSingleton regSip;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("SIP/IncomingCallDialog/OnCreate","Startar incomingcalldialog...");
		setContentView(R.layout.activity_incoming_call_dialog);
		Bundle extras = getIntent().getExtras();
		boolean isOutgoing = extras.getBoolean("outgoing");
		
		if(isOutgoing){
			Log.d("SIP/IncomingCallDialog/OnCreate","Startar ett utgående samtal..");
			if(extras.getString("caller") != null){
				caller = "ringer " + extras.getString("caller") + "...";
			}else{
				caller = "ringer ...";
			}
			
			startCallTimer();
			updateCaller();
			setToggleButtonChecked();
			
			// Lyssna efter om personen har svarat på påringningen
			ObserverCallStatus observer = new ObserverCallStatus();
			RegisterWithSipSingleton.callStatus.addObserver(observer);
			
			// Lyssna på toggle-knappen
			ToggleButton toggle = (ToggleButton) findViewById(R.id.toggleButton1);
			
			toggle.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
					// Svara på samtal
					if(buttonView.isChecked()){
						// Samtal är besvarat
						regSip.isCallAnswered = true;
						// Sätt aktuell tid till initialtid för samtalsstart
						timeWhenCallStarted = System.currentTimeMillis();
						// Besvara samtalet
						//IncomingCallReceiver.answerCall(IncomingCallReceiver.incomingCall);
						// Uppdatera tiden i textView
						updateCallTime();
					}
					// Lägg på samtal
					if(!buttonView.isChecked()){
						regSip.isCallAnswered = false;
						StaticCall.dropCall(StaticCall.call);
						Log.d("SIP", "Samtal avslutat. Ska nu köra finish på aktivitet...");
						finish();
					}
				}
			});
			
		}else{
			
			caller = extras.getString("caller") + " ringer...";
			Log.d("SIP/IncomingCallDialog","Tagit emot samtal från " + caller);

			startCallTimer();
			updateCaller();
			

			// Lyssna på toggle-knappen
			ToggleButton toggle = (ToggleButton) findViewById(R.id.toggleButton1);
			toggle.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
					// Svara på samtal
					if(buttonView.isChecked()){
						// Samtal är besvarat
						regSip.isCallAnswered = true;
						// Sätt aktuell tid till initialtid för samtalsstart
						timeWhenCallStarted = System.currentTimeMillis();
						// Besvara samtalet
						StaticCall.answerCall(StaticCall.call);
						// Uppdatera tiden i textView
						updateCallTime();
					}
					// Lägg på samtal
					if(!buttonView.isChecked() && regSip.isCallAnswered){
						regSip.isCallAnswered = false;
						StaticCall.dropCall(StaticCall.call);
						Log.d("SIP", "Samtal avslutat. Ska nu köra finish på aktivitet...");
						finish();
					}
				}
			});
		}

	}
	@Override
	protected void onStart(){
		super.onStart();
		
		// Hämta regSip från MainActivity
		// .. är det här som nyttan med en service börjar uppenbara sig?
		regSip = MainActivity.regSip;
	}
	
	@Override
	protected void onDestroy(){
		super.onDestroy();
		// Döda timern när samtalsdialogen stängs
		timer.cancel();
		timer.purge();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_incoming_call_dialog, menu);
		return true;
	}

	private void setToggleButtonChecked(){
		this.runOnUiThread(new Runnable() {
			public void run() {
				// Hämta toggleknappen och "tryck" på den
				ToggleButton toggle = (ToggleButton) findViewById(R.id.toggleButton1);
				toggle.setChecked(true);
			}
		});
	}
	
	private void startCallTimer(){
		int delay = 5000; // delay for 5 sec.
		int period = 1000; // repeat every sec.

		timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask()
		{
			public void run()
			{
				if(regSip.isCallAnswered){
					long millis = System.currentTimeMillis() - timeWhenCallStarted;
					int seconds = (int) (millis / 1000);
					int minutes = seconds / 60;

					if (seconds < 10) {
						String time = "" + minutes + ":0" + seconds;
						Log.d("SIP",time);
						timeInCall = time;
					} else {
						String time = "" + minutes + ":" + seconds;
						Log.d("SIP",time);
						timeInCall = time;
					}
					// Uppdatera samtalstiden
					updateCallTime();
				}
			}
		}, delay, period);
	}
	
	private void updateCallTime(){
		this.runOnUiThread(new Runnable() {
			public void run() {
				// Sätt namnet på den som ringer
				TextView timeView = (TextView) findViewById(R.id.textViewTimeInCall);
				timeView.setText(timeInCall);
			}
		});
	}
	private void updateCaller(){
		// Sätt namn på vem som ringer i UI-tråden (Be a good citizen..)
		this.runOnUiThread(new Runnable() {
			public void run() {
				// Sätt namnet på den som ringer
				TextView callerView = (TextView) findViewById(R.id.textViewCaller);
				callerView.setText(caller);
				TextView timeView = (TextView) findViewById(R.id.textViewTimeInCall);
				timeView.setText(timeInCall);
			}
		});
	}

}
