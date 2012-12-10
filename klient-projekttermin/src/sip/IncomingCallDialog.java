package sip;

import java.util.Observable;
import java.util.Observer;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.net.sip.SipException;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.Vibrator;
import android.util.Log;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.klient_projekttermin.R;

/**
 * Inkommande 
 * TODO: Lös buggar med ringsignalen.. [DONE]
 * TODO: Vibrera vid inkommande samtal [DONE]
 * TODO: Tänd displayen vid inkommande samtal [DONE]
 * TODO: Lås skärmen när telefonen förs mot örat [DONE]
 * TODO: Lås upp skärmen när telefonen tas bort från örat [DONE]
 * TODO: Timern [DONE]
 * 
 * Utgående: 
 * Fånga/hantera att mottagaren: 
 * TODO: - svarar [PARTIALLY DONE]
 * TODO: - inte svarar [DONE]
 * TODO: - Lägger på [DONE]
 * TODO: - Är upptagen [DONE]
 * TODO: Visa kontaktnamn [PARTIALLY DONE]
 * TODO: Timern [DONE]
 * 
 * Samtalslogg: 
 * TODO: Notera missat samtal 
 * TODO: Notera mottaget samtal 
 * TODO: Notera uppringt samtal 
 * TODO: Visa samtalstid för ringda/mottagna samtal (om tid finns)
 * 
 * Databas: 
 * TODO: Hämta SIP-profil från servern [DONE]
 * TODO: Hämta och lagra kontakters SIP-nummer (för samtal inom FM) [DONE] 
 * TODO: Om behövs, lägg till SIP-nummer i kontaktmodellen  [DONE]
 * 
 * Buggar:
 * TODO: Vid inkommande samtal ska inte "PÅ" stå efter att man har tryckt på "Svara". [DONE]
 * TODO: Vid inkommande samtal när man tryckt på "Lägg på" ska inte "AV" synas innan aktiviteten stängs. [DONE]
 * TODO: Fixa så även andra telefoner än Acro S kan registrera sig på Servern. [DONE]
 * TODO: Andra telefoner än Acro S ska kunna utnyttja närhetssensorn för Skärm På/Av. [DONE]
 * TODO: Vid utgående samtal ska texten "TextView" i fält för samtalstid inte synas. [DONE]
 * TODO: Samtal ska överleva en skärmrotation [DONE]
 * TODO: Blinkande gul lampa efter att Skärmlås PÅ använts, ska inte blinka. Stängs av efter att man låst upp skärmen. (Acro S?) (GCM)
 * TODO: Återregistrera enheten när den har blivit "Lagged" hos SIP-servern. (sköts nu när man ska ringa en kontakt och inte är registrerad..)
 * TODO: Tuta upptaget och neka nya inkommande samtal när man är i samtal.
 * TODO: Registrera att mottagaren av samtalet har svarat vid utgående samtal. Väldigt märkligt då kopplingssignaler
 * 		 spelas tills andra änden svarar...
 * 
 * @author kettel
 * 
 */
public class IncomingCallDialog extends Activity {

	private long timeWhenCallStarted = 0;
	private String timeInCall = new String("");
	private String caller = new String();
	private Handler handler;
	private Runnable runnable;

	// Boolean för att hålla reda på om telefonen ringer
	// True om ringsignal spelas, false annars.
	private boolean isRinging = false;

	// Variabler för ringsignal
	private Uri notification;
	private Ringtone ringtone;
	private RingtoneManager mRingtoneManager;
	
	// Vibrator
	private Vibrator vibrator;
	
	// Närhetssensor
	private SensorManager mSensorManager;
	private Sensor mProximitySensor;
	private SensorEventListener proximitySensorEventListener;
	
	// Hämta engergihanterare för att hämta status på skärm
	private PowerManager pm;
	
	// DevicePolicyManager för att kunna låsa skärmen
	protected static final int REQUEST_ENABLE = 0;
    private DevicePolicyManager devicePolicyManager;
    private ComponentName adminComponent;
	
	// Om skärmen är tänd
	private boolean isScreenOn;

	//RegisterWithSipSingleton regSip;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("SIP/IncomingCallDialog/OnCreate",
				"Startar incomingcalldialog...");
		setContentView(R.layout.activity_incoming_call_dialog);
		Bundle extras = getIntent().getExtras();
		boolean isOutgoing = extras.getBoolean("outgoing");
		
		// Nollställ callTime
		timeInCall = "";
		
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
		
		// Initiera vibratorn
		vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		
		// Se efter om skärmen är tänd
		pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		isScreenOn = pm.isScreenOn();
		
		// Initiera policyManager så skärmen kan låsas
		
		Log.d("SIP/IncomingCallDialog/onCreate","Skärmen är nu: " + (isScreenOn?"på":"av"));
		
		// Tänd skärmen om släckt
		if(!isScreenOn){
			unlockScreen();
		}
		
		// Initiera närhetssensorn
		mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
		mProximitySensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
		
		// Skapa en lyssnare för närhetssensorn
		proximitySensorEventListener = new SensorEventListener(){

			public void onAccuracyChanged(Sensor sensor, int accuracy) {
				// TODO Auto-generated method stub
			}

			public void onSensorChanged(SensorEvent event) {
				if(event.sensor.getType()==Sensor.TYPE_PROXIMITY){
					// Telefonen förs mot örat
					if(event.values[0] == 0.0){
						// Om skärmen är tänd, släck skärmen
						if(isScreenOn){
							Log.d("SIP/IncomingCallDialog/onCreate","Användaren har telefonen mot örat. Släcker skärmen...");
							lockScreen();
						}
					}
					// Telefonen tas bort från örat
					// Sony: 1.0 när telefonen inte är nära något
					// Samsung: 5.0
					// Andra: ?
					else if(event.values[0] > 0.0){
						Log.d("SIP/IncomingCallDialog/onCreate","Proximity: "+event.values[0]);
						Log.d("SIP/IncomingCallDialog/onCreate","Skärmen är nu: " + (isScreenOn?"på":"av"));
						// Om skärmen är släckt, lås upp skärmen
						if(!isScreenOn){
							Log.d("SIP/IncomingCallDialog/onCreate","Användaren har inte telefonen vid örat. Tänder skärmen...");
							unlockScreen();
						}
					}
				}
			}
		};
		
		// Registrera närhetssensorlyssnaren
		if (mProximitySensor == null){
			Log.d("SIP/IncomingCallDialog/onCreate","Det finns ingen närhetssensor!");
		}
		else{
			mSensorManager.registerListener(proximitySensorEventListener,
					mProximitySensor,
					SensorManager.SENSOR_DELAY_NORMAL);
		}
		
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
//		regSip = MainActivity.regSip;
		// Hmm.. nedan verkar av någon konstig anledning lösa FATAL: Timer - X
		// för timern.
		Log.d("SIP/IncomingCallDialog/onStart",
				"kör en onStart och hämtat regSip. Är isCallAnswered? "
						+ ((RegisterWithSipSingleton.isCallAnswered()) ? "sant" : "falskt"));
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
		if(RegisterWithSipSingleton.isRegistred()){
			Log.d("SIP/IncomingCallDialog/outgoingCall",
					"Startar ett utgående samtal..");

			updateCaller("Ringer " + caller + "...");

			// Starta timern
			startCallTimer();

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
					// Om den andra änden svarar
					else if(RegisterWithSipSingleton.callStatus.getStatus()){
						Log.d("SIP/SipSingleton/Outgoingcall/ObserverCallStatus",
								"Andra änden svarade...");
						RegisterWithSipSingleton.setCallAnswered(true);
						// Sätt tiden till när samtalet besvaras
						timeWhenCallStarted = System.currentTimeMillis();
						
						// Uppdatera texten för vem du pratar med
						updateCaller("I samtal...");
						//RegisterWithSipSingleton.callStatus.setStatus(true);
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
						// Standardläge, väntar på att andra änden ska svara
						
					}
					// Lägg på samtal
					if (!buttonView.isChecked()) {
						RegisterWithSipSingleton.setCallAnswered(false);
						RegisterWithSipSingleton.callStatus.setStatus(false);
						Log.d("SIP/IncomingCallDialog/OutgoingCall",
								"Samtal avslutat. Ska nu köra finish på aktivitet...");
//						finish();
					}
				}
			});
		}
		else{
			Log.d("SIP/IncomingCallDialog/OutgoingCall","Användaren är inte registrerad mot SIP-servern. Ska registrera...");
			RegisterWithSipSingleton.initializeManager();
		}
		
	}

	/**
	 * Hantera ett inkommande samtal
	 */
	public void incomingCall() {

		Log.d("SIP/IncomingCallDialog/incomingCall", "Tagit emot samtal från "
				+ caller);

		startCallTimer();
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
				// Om något har avbrutit samtalet så callStatus är false
				if (!RegisterWithSipSingleton.callStatus.getStatus()) {
					Log.d("SIP/SipSingleton/incomingCall/ObserverCallStatus",
							"Någon la visst på...");
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
					RegisterWithSipSingleton.setCallAnswered(true);
					// Sätt aktuell tid till initialtid för samtalsstart
					timeWhenCallStarted = System.currentTimeMillis();
					// Besvara samtalet
					StaticCall.answerCall(StaticCall.call);
					// Uppdatera tiden i textView
					updateCallTime();
					
				}
				// Lägg på samtal
				if (!buttonView.isChecked() && RegisterWithSipSingleton.isCallAnswered()) {
					RegisterWithSipSingleton.setCallAnswered(false);
					try {
						IncomingCallReceiver.incomingCall.endCall();
					} catch (SipException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					// Underrätta lyssnare om att samtalet är slut
					RegisterWithSipSingleton.callStatus.setStatus(false);
					Log.d("SIP/IncomingCallDialog/incomingCall/onCheckedListener",
							"Samtal avslutat. Ska nu köra finish på aktivitet...");
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
		final int period = 1000; // Upprepa varje sekund

		handler = new Handler();
		
		runnable = new Runnable(){
			public void run() {
				if (RegisterWithSipSingleton.isCallAnswered()) {
					long millis = System.currentTimeMillis()
							- timeWhenCallStarted;
					int seconds = (int) (millis / 1000);
					int minutes = seconds / 60;
					seconds = seconds % 60;
					if (seconds < 10) {
						String time = "" + minutes + ":0" + seconds;
						timeInCall = time;
					} else {
						String time = "" + minutes + ":" + seconds;
						timeInCall = time;
					}
					// Uppdatera samtalstiden
					updateCallTime();
				}
				// Kör runnable igen
				handler.postDelayed(this, period);
			}
		};
		
		handler.postDelayed(runnable, delay);
		
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
		StaticCall.dropCall(StaticCall.call);
	}

	/**
	 * Då onFinish inte riktigt är pålitlig för att tillräckligt snabbt döda
	 * vare sig ringsignal eller timer
	 */
	private void killEssentials() {
		// Avsluta ev pågående samtal
		RegisterWithSipSingleton.setCallAnswered(false);
		RegisterWithSipSingleton.dropCall();
		endCall();
		
		// Avregistrera närhetssensorlyssnaren
		if(mSensorManager != null){
			mSensorManager.unregisterListener(proximitySensorEventListener,mProximitySensor);
			mSensorManager = null;
		}
		
		// Stoppa ringsignalen (om på)
		stopRingTone();
		
		// Döda timern när samtalsdialogen stängs
		if (handler != null) {
			Log.d("SIP/IncomingDialer/killEssentials", "Ska döda timern...");
			handler.removeCallbacks(runnable);
			Log.d("SIP/IncomingDialer/killEssentials", "Timern dödad.");
		}
		// Töm samtalstidslabeln när samtalet stängs
		timeInCall = "";
		updateCallTime();
	}
	private void unlockScreen() {
		Log.d("SIP/IncomingCallDialog/unLockScreen","Ska tända skärmen...");
		
        Window window = this.getWindow();
        window.addFlags(LayoutParams.FLAG_DISMISS_KEYGUARD);
        window.addFlags(LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        window.addFlags(LayoutParams.FLAG_TURN_SCREEN_ON);
        isScreenOn = true;
		Log.d("SIP/IncomingCallDialog/unLockScreen","Skärmen är nu: " + (isScreenOn?"på":"av"));
    }
	
	private void lockScreen() {
		// Börja med att rensa alla flaggor så skärmen får låsas
		Window window = this.getWindow();
		window.clearFlags(LayoutParams.FLAG_DISMISS_KEYGUARD);
        window.clearFlags(LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        window.clearFlags(LayoutParams.FLAG_TURN_SCREEN_ON);
        
        // Släcker skärmen men tänder den inte igen sen...
//        WindowManager.LayoutParams layoutParam = getWindow().getAttributes();
//        layoutParam.screenBrightness = 0; 
//        layoutParam.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
//        getWindow().setAttributes(layoutParam);
//        
        // Hämta sedan adminrättigheter för att få låsa skärmen
        adminComponent = new ComponentName(IncomingCallDialog.this, Darclass.class);
        devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        
        // Om device-rättigheter inte är instansierade, hämta dem genom en JÄTTEIRRITERANDE ruta..
        // TODO: Autoacceptera rutan..
        if (!devicePolicyManager.isAdminActive(adminComponent)) {
            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, adminComponent);
            startActivityForResult(intent, REQUEST_ENABLE);
        } 
        // Med instansierade device-rättigheter, trigga skärmlås
        else {
            devicePolicyManager.lockNow();
        }
        
        // Sätt sedan skärmflaggan till falsk
        isScreenOn = false;
        
		Log.d("SIP/IncomingCallDialog/lockScreen","Skärmen är nu: " + (isScreenOn?"på":"av"));
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
