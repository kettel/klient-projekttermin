package sip;

import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.klient_projekttermin.MainActivity;
import com.klient_projekttermin.R;

/**
 * Inkommande 
 * TODO: Lös buggar med ringsignalen.. [DONE]
 * TODO: Vibrera vid inkommande samtal [DONE]
 * TODO: Tänd displayen vid inkommande samtal [DONE]
 * TODO: Timern
 * 
 * Utgående: 
 * Fånga/hantera att mottagaren: 
 * TODO: - svarar 
 * TODO: - inte svarar
 * TODO: - Lägger på 
 * TODO: - Är upptagen 
 * TODO: Visa kontaktnamn 
 * TODO: Timern
 * 
 * Samtalslogg: 
 * TODO: Notera missat samtal 
 * TODO: Notera mottaget samtal 
 * TODO: Notera uppringt samtal 
 * TODO: Visa samtalstid för ringda/mottagna samtal (om tid finns)
 * 
 * Databas: 
 * TODO: Hämta SIP-profil från servern 
 * TODO: Hämta och lagra kontakters SIP-nummer (för samtal inom FM) 
 * TODO: Om behövs, lägg till SIP-nummer i kontaktmodellen
 * 
 * @author kettel
 * 
 */
public class IncomingCallDialog extends Activity {

	private long timeWhenCallStarted = 0;
	private String timeInCall = new String();
	private String caller = new String();
	private Timer timer;

	// Boolean för att hålla reda på om telefonen ringer
	// True om ringsignal spelas, false annars.
	private boolean isRinging = false;

	// Hämta förvald ringsignal
	private Uri notification;
	private Ringtone ringtone;
	private RingtoneManager mRingtoneManager;
	private Vibrator vibrator;

	RegisterWithSipSingleton regSip;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("SIP/IncomingCallDialog/OnCreate",
				"Startar incomingcalldialog...");
		setContentView(R.layout.activity_incoming_call_dialog);
		Bundle extras = getIntent().getExtras();
		boolean isOutgoing = extras.getBoolean("outgoing");

		// Sätt text i dialogfönstret
		if (extras.getString("caller") != null) {
			caller = extras.getString("caller");
		} else {
			caller = "";
		}
		
		// Hämta standardringsignal (-1 ger dock ett fel, men ger rätt ringsignal..)
		// Skiter ringtone är typ null på ett lagom märkligt sätt (går att starta men ej
		// stoppa om det ligger i onStart().
		mRingtoneManager = new RingtoneManager(this);
		ringtone = mRingtoneManager.getRingtone(-1);
		vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		
		// Tänd skärmen
		unlockScreen();
		
		// Utgående samtal
		if (isOutgoing) {
			outgoingCall();
		}
		// Inkommande samtal
		else {
			incomingCall();
		}

	}

	@Override
	protected void onStart() {
		// Hämta regSip från MainActivity
		// .. är det här som nyttan med en service börjar uppenbara sig?
		regSip = MainActivity.regSip;
		// Hmm.. nedan verkar av någon konstig anledning lösa FATAL: Timer - X
		// för timern.
		Log.d("SIP/IncomingCallDialog/onStart",
				"kör en onStart och hämtat regSip. Är isCallAnswered? "
						+ ((regSip.isCallAnswered) ? "sant" : "falskt"));
		super.onStart();
	}

	@Override
	public void onBackPressed() {
		killEssentials();
		finish();
	}

	@Override
	protected void onDestroy() {
		Log.d("SIP/IncomingDialer/onDestroy", "Ska köra onDestroy()...");
		killEssentials();
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_incoming_call_dialog, menu);
		return true;
	}

	/**
	 * Hantera ett utgående samtal
	 */
	private void outgoingCall() {
		Log.d("SIP/IncomingCallDialog/outgoingCall",
				"Startar ett utgående samtal..");

		updateCaller("Ringer " + caller + "...");

		// Starta timern
		// startCallTimer();

		// Sätt toggle-knappen till nedtryckt (det är ju vi som ringer...)
		setToggleButtonChecked();

		// Lyssna efter om personen har svarat på påringningen
		final class ObserverCallStatus implements Observer {
			public void update(Observable arg0, Object arg1) {
				Log.d("SIP/SipSingleton/Outgoingcall/ObserverCallStatus",
						"Nu har visst användaren gjort något..");
				// Om något har avbrutit samtalet så callStatus är false
				if (!RegisterWithSipSingleton.callStatus.getStatus()) {
					Log.d("SIP/SipSingleton/Outgoingcall/ObserverCallStatus",
							"Andra änden la visst på...");
					finish();
				}
			}
		}
		ObserverCallStatus observer = new ObserverCallStatus();
		RegisterWithSipSingleton.callStatus.addObserver(observer);

		// Lyssna på toggle-knappen
		ToggleButton toggle = (ToggleButton) findViewById(R.id.toggleButton1);

		toggle.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// Svara på samtal
				if (buttonView.isChecked()) {
					// Samtal är besvarat
					regSip.isCallAnswered = true;
					// Sätt aktuell tid till initialtid för samtalsstart
					timeWhenCallStarted = System.currentTimeMillis();
					// Besvara samtalet
					// IncomingCallReceiver.answerCall(IncomingCallReceiver.incomingCall);
					// Uppdatera tiden i textView
					updateCallTime();
				}
				// Lägg på samtal
				if (!buttonView.isChecked()) {
					regSip.isCallAnswered = false;
					StaticCall.dropCall(StaticCall.call);
					Log.d("SIP",
							"Samtal avslutat. Ska nu köra finish på aktivitet...");
					finish();
				}
			}
		});
	}

	/**
	 * Hantera ett inkommande samtal
	 */
	public void incomingCall() {

		Log.d("SIP/IncomingCallDialog/incomingCall", "Tagit emot samtal från "
				+ caller);

		// startCallTimer();
		updateCaller(caller + " ringer...");
		updateCallTime();

		// Dra igång ringsignalen
		if (ringtone == null) {
			notification = RingtoneManager
					.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
			ringtone = RingtoneManager.getRingtone(getApplicationContext(),
					notification);
		}
		startRingTone();

		// Lyssna efter om personen har svarat på påringningen
		final class ObserverCallStatus implements Observer {
			public void update(Observable arg0, Object arg1) {
				Log.d("SIP/SipSingleton/incomingCall/ObserverCallStatus",
						"Nu har visst användaren gjort något..");
				// Om något har avbrutit samtalet så callStatus är false
				if (!RegisterWithSipSingleton.callStatus.getStatus()) {
					Log.d("SIP/SipSingleton/incomingCall/ObserverCallStatus",
							"Andra änden la visst på...");
					killEssentials();
					finish();
				}
			}
		}
		// Lyssna efter om personen har svarat på påringningen
		ObserverCallStatus observer = new ObserverCallStatus();
		RegisterWithSipSingleton.callStatus.addObserver(observer);

		// Lyssna på toggle-knappen
		ToggleButton toggle = (ToggleButton) findViewById(R.id.toggleButton1);
		toggle.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// Svara på samtal
				if (buttonView.isChecked()) {
					// Stoppa ringsignalen
					stopRingTone();
					// Sätt true då samtalet är öppet
					RegisterWithSipSingleton.callStatus.setStatus(true);
					// Uppdatera samtalstexten
					updateCaller("I samtal med " + caller);
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
				if (!buttonView.isChecked() && regSip.isCallAnswered) {
					regSip.isCallAnswered = false;
					RegisterWithSipSingleton.callStatus.setStatus(false);
					StaticCall.dropCall(StaticCall.call);
					Log.d("SIP/IncomingCallDialog/incomingCall/onCheckedListener",
							"Samtal avslutat. Ska nu köra finish på aktivitet...");
					killEssentials();
					finish();
				}
			}
		});
	}

	

	private void setToggleButtonChecked() {
		this.runOnUiThread(new Runnable() {
			public void run() {
				// Hämta toggleknappen och "tryck" på den
				ToggleButton toggle = (ToggleButton) findViewById(R.id.toggleButton1);
				toggle.setChecked(true);
			}
		});
	}

	private void startCallTimer() {
		int delay = 0; // Vänta inte innan timern ska starta
		int period = 1000; // Upprepa varje sekund

		timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				if (regSip.isCallAnswered) {
					long millis = System.currentTimeMillis()
							- timeWhenCallStarted;
					int seconds = (int) (millis / 1000);
					int minutes = seconds / 60;
					seconds = seconds % 60;
					if (seconds < 10) {
						String time = "" + minutes + ":0" + seconds;
						Log.d("SIP", time);
						timeInCall = time;
					} else {
						String time = "" + minutes + ":" + seconds;
						Log.d("SIP", time);
						timeInCall = time;
					}
					// Uppdatera samtalstiden
					updateCallTime();
				}
			}
		}, delay, period);
	}

	private void updateCallTime() {
		this.runOnUiThread(new Runnable() {
			public void run() {
				// Sätt namnet på den som ringer
				TextView timeView = (TextView) findViewById(R.id.textViewTimeInCall);
				timeView.setText(timeInCall);
			}
		});
	}

	private void updateCaller(final String text) {
		// Sätt namn på vem som ringer i UI-tråden (Be a good citizen..)
		this.runOnUiThread(new Runnable() {
			public void run() {
				// Sätt namnet på den som ringer
				TextView callerView = (TextView) findViewById(R.id.textViewCaller);
				callerView.setText(text);
			}
		});
	}

	public static void endCall() {
		RegisterWithSipSingleton.callStatus.setStatus(false);
	}

	/**
	 * Då onFinish inte riktigt är pålitlig för att tillräckligt snabbt döda
	 * vare sig ringsignal eller timer
	 */
	private void killEssentials() {
		// Döda låset på att tända skärmen så den kan släckas
		//wl.release();
		
		// Stoppa ringsignalen (om på)
		stopRingTone();
		// Döda timern när samtalsdialogen stängs
		if (timer != null) {
			Log.d("SIP/IncomingDialer/killEssentials", "Ska döda timern...");
			timer.cancel();
			timer.purge();
			Log.d("SIP/IncomingDialer/killEssentials", "Timern dödad.");
		}
		timeInCall = "";
	}
	private void unlockScreen() {
        Window window = this.getWindow();
        window.addFlags(LayoutParams.FLAG_DISMISS_KEYGUARD);
        window.addFlags(LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        window.addFlags(LayoutParams.FLAG_TURN_SCREEN_ON);
    }
	private void startRingTone() {
		if (ringtone != null) {
			Log.d("SIP/IncomingCallDialog/startRingTone", "Ska starta ringsignal...");
			ringtone.play();
			
			// Start immediately
			// Vibrate for 200 milliseconds
			// Sleep for 500 milliseconds
			long[] pattern = { 0, 200, 500 };
			vibrator.vibrate(pattern, 0);
			
			isRinging = true;
			Log.d("SIP/IncomingCallDialog/startRingTone", "Ringsignal startad...");
		}
	}
	
	private void stopRingTone() {
		// Om ringsignalen spelas, stoppa den
		if (isRinging) {
			//if (ringtone != null && ringtone.isPlaying()) {
				Log.d("SIP/IncomingDialer/stopRingTone", "Ska stoppa ringsignalen ringtone...");
				ringtone.stop();
				vibrator.cancel();
				Log.d("SIP/IncomingDialer/stopRingTone", "Stoppade ringsignal ringtone");
			//}

			if (mRingtoneManager != null) {
				Log.d("SIP/IncomingDialer/stopRingTone", "Ska stoppa ringsignal RingToneManager.");
				mRingtoneManager.stopPreviousRingtone();
				Log.d("SIP/IncomingDialer/stopRingTone", "Stoppade ringsignal RingToneManager.");
			}
			
			isRinging = false;
		}
	}
}
