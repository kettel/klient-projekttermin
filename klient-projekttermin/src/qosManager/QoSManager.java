package qosManager;

import java.util.Observable;
import java.util.Observer;

import com.klient_projekttermin.R;

import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

public class QoSManager extends Activity implements Observer {

	BatteryCheckingFunction batteryCheckingFunction;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_battery_checker);
		batteryCheckingFunction = new BatteryCheckingFunction();
		batteryCheckingFunction.addObserver(this);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_battery_checker, menu);
		return true;
	}

	/**
	 * Metoden undersöker enhetens nuvarande batterinivå och skickar en notify om den ändrats mot förra gången den testats
	 * @param v
	 */
	public void checkBatteryStatus(View v){   
		batteryCheckingFunction.startCheckThread(getApplicationContext());
	}

	/**
	 * Denna metod anropas om batterinivån har ändrats då den undersöks i checkBattryStatus. 
	 */
	public void update(Observable observable, Object data) {
		float screenBrightnesslevel = (float) 0.3;
		Boolean turnOnNetwork = false;
		Boolean turnOnGPS = false;

		int batteryLevel = (Integer) data;

		adjustGPSStatus(true);
		if(batteryLevel < 30){
			System.out.println("Batterinivån är låg: "+batteryLevel+"%");
			adjustToLowBatteryLevel(turnOnNetwork, turnOnGPS, screenBrightnesslevel);
		}
		else if(batteryLevel< 15){
			System.out.println("Batterinivån är kritisk: "+batteryLevel+"%");
			adjustToOkayBatteryLevel(turnOnNetwork, turnOnGPS, screenBrightnesslevel);
		}
		Toast.makeText(getApplicationContext(), "Batteri status: "+data+"%", Toast.LENGTH_SHORT).show();

	}

	/**
	 * Metoden ändrar enhetens inställningar om batteriet når en låg laddningsnivå
	 */
	public void adjustToLowBatteryLevel(Boolean turnOnNetwork,Boolean turnOnGPS, float screenBrightnesslevel ) {
		adjustScreenBrightness(screenBrightnesslevel);
		adjustNetworkStatus(turnOnNetwork);
		adjustGPSStatus(turnOnGPS);
	}

	/**
	 * Metoden ändrar enhetens inställningar om batteriet når en kritisk laddningsnivå
	 */
	public void adjustToOkayBatteryLevel(Boolean turnOnNetwork,Boolean turnOnGPS, float screenBrightnesslevel) {
		adjustScreenBrightness(screenBrightnesslevel);
		adjustNetworkStatus(turnOnNetwork);
		adjustGPSStatus(turnOnGPS);
	}

	/**
	 * Metoden justerar skärmljusstyrkan med hjälp av ett inkommande floatvärde
	 * @param value kan vara ett valfritt float-värde mellan 0.0-1.0;
	 */
	public void adjustScreenBrightness(float brightnessValue){
		WindowManager.LayoutParams layout = getWindow().getAttributes();
		layout.screenBrightness = brightnessValue;
		getWindow().setAttributes(layout);
	}

	/**
	 * Metoden stänger eller sätter igågng av WiFi-anslutning i enheten
	 */
	public void adjustNetworkStatus(Boolean wantToTurnOn){
		System.out.println("Nätverksanslutningar är avstängda/startade i enheten");
		WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(getApplicationContext().WIFI_SERVICE);
		wifiManager.setWifiEnabled(wantToTurnOn);
	}

	/**
	 * Metoden stänger av eller sätter igång GPSen i enheten
	 */
	public void adjustGPSStatus(Boolean wantToTurnOn){
		System.out.println("GPSen är avstängd/startad");
		startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
	}
}
