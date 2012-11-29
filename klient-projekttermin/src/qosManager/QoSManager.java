package qosManager;

import java.util.Observable;
import java.util.Observer;

import com.klient_projekttermin.MainActivity;

import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.content.Context;
import android.content.Intent;
import android.view.WindowManager;
import android.widget.Toast;

public class QoSManager implements Observer {
	private float screenBrightnesslevel = (float) 0.3;
	private Boolean turnOnNetwork = false;
	private Boolean turnOnGPS = false;
	private Boolean permissionToStartMap = true;
	private Boolean permissionToStartCamera = true;
	private Boolean permissionToStartMessages = true;
	private Boolean permissionToStartAssignment = true;
	private BatteryCheckingFunction batteryCheckingFunction;
	private Context applicationContext;
	
	private QoSManager(){}
	
	private static QoSManager instance = new QoSManager();
	
	public static QoSManager getInstance(){
		return instance;
	}

	public void startBatteryCheckingThread(Context context){
		applicationContext = context;
		batteryCheckingFunction = new BatteryCheckingFunction(context);
		batteryCheckingFunction.addObserver(this);
	}
	
	/**
	 * Denna metod anropas om batterinivån har ändrats då den undersöks i checkBattryStatus. 
	 */
	public void update(Observable observable, Object data) {
		int batteryLevel = (Integer) data;
		
		adjustNetworkStatus(false);
		
		if(batteryLevel < 30){
			System.out.println("Batterinivån är låg: "+batteryLevel+"%");
			adjustToLowBatteryLevel(turnOnNetwork, turnOnGPS, screenBrightnesslevel);
		}

		else if(batteryLevel< 15){
			System.out.println("Batterinivån är kritisk: "+batteryLevel+"%");
			adjustToOkayBatteryLevel(turnOnNetwork, turnOnGPS, screenBrightnesslevel);
		}
		Toast.makeText(applicationContext, "Batteri status: "+data+"%", Toast.LENGTH_SHORT).show();
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
		WindowManager.LayoutParams layout = ((MainActivity)applicationContext).getWindow().getAttributes();
		layout.screenBrightness = brightnessValue;
		((MainActivity)applicationContext).getWindow().setAttributes(layout);
	}

	/**
	 * Metoden stänger eller sätter igågng av WiFi-anslutning i enheten
	 */
	public void adjustNetworkStatus(Boolean wantToTurnOn){
		System.out.println("Nätverksanslutningar är avstängda/startade i enheten");
				WifiManager wifiManager = (WifiManager) applicationContext.getSystemService(applicationContext.WIFI_SERVICE);
				wifiManager.setWifiEnabled(wantToTurnOn);
	}

	/**
	 * Metoden stänger av eller sätter igång GPSen i enheten
	 */
	public void adjustGPSStatus(Boolean wantToTurnOn){
		System.out.println("GPSen är avstängd/startad");
		((MainActivity)applicationContext).startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
	}
	
	public void setPermissionToStartMap(Boolean b){
		permissionToStartMap = b;
	}
	public void setPermissionToStartAssignment(Boolean b){
		permissionToStartAssignment = b;
	}
	public void setPermissionToStartMessages(Boolean b){
		permissionToStartMessages = b;
	}
	public void setPermissionToStartCamera(Boolean b){
		permissionToStartCamera = b;
	}
	
	public boolean allowedToStartMap(){
		return permissionToStartMap; 
	}
	public boolean allowedToStartAssignment(){
		return permissionToStartAssignment; 
	}
	public boolean allowedToStartMessages(){
		return permissionToStartMessages; 
	}
	public boolean allowedToStartCamera(){
		return permissionToStartCamera; 
	}
}
