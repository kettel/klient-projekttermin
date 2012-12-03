package qosManager;

import java.util.Observable;
import java.util.Observer;

import android.app.Activity;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.view.WindowManager;

public class QoSManager implements Observer {
	private Boolean permissionToStartMap = true;
	private Boolean permissionToStartCamera = true;
	private Boolean permissionToStartMessages = true;
	private Boolean permissionToStartAssignment = true;

	private float screenBrightnesslevelDefault = (float) 0.3;
	private Boolean permissionToStartMapDefault = false;
	private Boolean permissionToUseNetworkDefault = false;
	private Boolean permissionToStartCameraDefault = false;
	private Boolean permissionToStartMessagesDefault = true;
	private Boolean permissionToStartAssignmentDefault = true;

	private Boolean BatterySaveModeIsActivated=false;
	private Boolean okayBatterylevel = true;

	private float screenBrightnesslevelLow = (float) 0.2;
	private int lowBatteryLevel=20;
	private Boolean permissionToStartMapLow = true;
	private Boolean permissionToUseNetworkLow = true;
	private Boolean permissionToStartCameraLow = true;
	private Boolean permissionToStartMessagesLow = true;
	private Boolean permissionToStartAssignmentLow = true;

	private BatteryCheckingFunction batteryCheckingFunction;
	private Context applicationContext;

	private QoSManager() {
	}

	private static QoSManager instance = new QoSManager();

	public static QoSManager getInstance() {
		return instance;
	}
	
	public void setContext(Context context){
		applicationContext = context;
	}

	public void startBatteryCheckingThread(Context context) {
		applicationContext = context;
		batteryCheckingFunction = new BatteryCheckingFunction(context);
		batteryCheckingFunction.addObserver(this);
	}
	
	public Boolean isBatteryCheckThreadStarted(){
		return batteryCheckingFunction.isBatteryBeingChecked();
	}
	
	public void stopBatteryCheckThread(){
		batteryCheckingFunction.stopBatteryCheckFunction();
	}

	/**
	 * Denna metod anropas om batterinivån har ändrats då den undersöks i
	 * checkBattryStatus.
	 */
	public void update(Observable observable, Object data) {
		int batteryLevel = (Integer) data;

		if(batteryLevel<=lowBatteryLevel&&okayBatterylevel){
			okayBatterylevel=false;
			adjustToLowBatteryLevel();
		}
		else if(batteryLevel>lowBatteryLevel&&!okayBatterylevel){
			okayBatterylevel = true;
			adjustToOkayBatteryLevel();
		}
	}

	/**
	 * Metoden ändrar enhetens inställningar om batteriet når en bra
	 * laddningsnivå
	 */
	public void adjustToOkayBatteryLevel() {
		BatterySaveModeIsActivated = false;
		adjustScreenBrightness(screenBrightnesslevelDefault);
		adjustNetworkStatus(permissionToUseNetworkDefault);
		permissionToStartAssignment = permissionToStartAssignmentDefault;
		permissionToStartCamera = permissionToStartCameraDefault;
		permissionToStartMap = permissionToStartMapDefault;
		permissionToStartMessages = permissionToStartMessagesDefault;
	}

	/**
	 * Metoden ändrar enhetens inställningar om batteriet når en låg
	 * laddningsnivå
	 */
	public void adjustToLowBatteryLevel() {
		BatterySaveModeIsActivated=true;
		adjustScreenBrightness(screenBrightnesslevelLow);
		adjustNetworkStatus(permissionToUseNetworkLow);
		permissionToStartAssignment = permissionToStartAssignmentLow;
		permissionToStartCamera = permissionToStartCameraLow;
		permissionToStartMap = permissionToStartMapLow;
		permissionToStartMessages = permissionToStartMessagesLow;
	}

	/**
	 * Metoden sätter ett värde på skärmljusstyrkan
	 */
	public void setScreenBrightnessValue(float screenBrightnessLevel){
		screenBrightnesslevelLow = screenBrightnessLevel;
		if(BatterySaveModeIsActivated){
			adjustScreenBrightness(screenBrightnesslevelLow);
		}
		
	}

	/**
	 * Metoden justerar skärmljusstyrkan med hjälp av ett inkommande floatvärde
	 * 
	 * @param value
	 *            kan vara ett valfritt float-värde mellan 0.0-1.0;
	 */
	public void adjustScreenBrightness(float brightnessValue) {
		WindowManager.LayoutParams layout = ((Activity) applicationContext).getWindow().getAttributes();
		layout.screenBrightness = brightnessValue;
		((Activity) applicationContext).getWindow().setAttributes(layout);
	}

	/**
	 * Metoden stänger eller sätter igågng av WiFi-anslutning i enheten
	 */
	public void adjustNetworkStatus(Boolean wantToTurnOn) {
		System.out
		.println("Nätverksanslutningar är avstängda/startade i enheten");
		WifiManager wifiManager = (WifiManager) applicationContext
				.getSystemService(Context.WIFI_SERVICE);
		wifiManager.setWifiEnabled(wantToTurnOn);
	}

	public boolean isAllowedToStartMap() {
		return permissionToStartMap;
	}

	public boolean isAllowedToStartAssignment() {
		return permissionToStartAssignment;
	}

	public boolean isAllowedToStartMessages() {
		return permissionToStartMessages;
	}

	public boolean isAllowedToStartCamera() {
		return permissionToStartCamera;
	}

	public void setPermissionToStartMessagesLow(Boolean permissionToStartMessagesLow) {
		this.permissionToStartMessagesLow = permissionToStartMessagesLow;
	}

	public void setPermissionToStartMapLow(Boolean permissionToStartMapLow) {
		this.permissionToStartMapLow = permissionToStartMapLow;
	}

	public void setPermissionToUseNetworkLow(Boolean permissionToUseNetworkLow) {
		this.permissionToUseNetworkLow = permissionToUseNetworkLow;
	}

	public void setPermissionToStartCameraLow(Boolean permissionToStartCameraLow) {
		this.permissionToStartCameraLow = permissionToStartCameraLow;
	}

	public void setPermissionToStartAssignmentLow(
			Boolean permissionToStartAssignmentLow) {
		this.permissionToStartAssignmentLow = permissionToStartAssignmentLow;
	}

	public void setLowBatteryLevel(int batterylevel) {
		lowBatteryLevel = batterylevel;
	}
}
