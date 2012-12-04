package qosManager;

import java.util.Observable;
import java.util.Observer;

import android.app.Activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.view.WindowManager;

public class QoSManager implements Observer {
	private Boolean permissionToStartMap = true;
	private Boolean permissionToUseNetwork = true;
	private Boolean permissionToStartCamera = true;
	private Boolean permissionToStartMessages = true;
	private Boolean permissionToStartAssignment = true;
	private float screenBrightnesslevel = (float) 0.2;
	private int lowBatteryLevel=20;
	
	private float screenBrightnesslevelOkay = (float) 0.3;
	private Boolean permissionToStartMapOkay = true;
	private Boolean permissionToUseNetworkOkay = true;
	private Boolean permissionToStartCameraOkay = true;
	private Boolean permissionToStartMessagesOkay = true;
	private Boolean permissionToStartAssignmentOkay = true;

	private Boolean BatterySaveModeIsActivated=false;
	private Boolean okayBatterylevel = true;

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
	
	public void adjustToCurrentBatteryMode(){
		if(BatterySaveModeIsActivated){
			adjustToLowBatteryLevel();
		}
		else{
			adjustToOkayBatteryLevel();
		}
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
		adjustScreenBrightness(screenBrightnesslevelOkay);
		adjustNetworkStatus(permissionToUseNetworkOkay);
		permissionToStartAssignment = permissionToStartAssignmentOkay;
		permissionToStartCamera = permissionToStartCameraOkay;
		permissionToStartMap = permissionToStartMapOkay;
		permissionToStartMessages = permissionToStartMessagesOkay;
	}

	/**
	 * Metoden ändrar enhetens inställningar om batteriet når en låg
	 * laddningsnivå
	 */
	public void adjustToLowBatteryLevel() {
		BatterySaveModeIsActivated=true;
		adjustScreenBrightness(screenBrightnesslevel);
		adjustNetworkStatus(permissionToUseNetwork);
	}

	/**
	 * Metoden justerar skärmljusstyrkan med hjälp av ett inkommande floatvärde
	 * 
	 * @param value kan vara ett valfritt float-värde mellan 0.0-1.0;
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
		WifiManager wifiManager = (WifiManager) applicationContext
				.getSystemService(Context.WIFI_SERVICE);
		wifiManager.setWifiEnabled(wantToTurnOn);
	}
	
	public Boolean isBatterySaveModeActivated(){
		return BatterySaveModeIsActivated;
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
	public boolean isAllowedToUseWiFi(){
		return permissionToUseNetwork;
	}
	
	public float getScreenBrightnessValue(){
		return screenBrightnesslevel;
	}
	
	public int getLowBatteryLevel(){
		return lowBatteryLevel;
	}
	
	/**
	 * Metoden sätter ett värde på skärmljusstyrkan
	 */
	public void setScreenBrightnessValueLow(float screenBrightnessLevel){
		screenBrightnesslevel = screenBrightnessLevel;
		if(BatterySaveModeIsActivated){
			adjustScreenBrightness(screenBrightnesslevel);
		}
	}

	public void setPermissionToStartMessages(Boolean permissionStartMessagesLow) {
		permissionToStartMessages = permissionStartMessagesLow;
	}

	public void setPermissionToStartMap(Boolean permissionStartMapLow) {
		permissionToStartMap = permissionStartMapLow;
	}

	public void setPermissionToUseNetwork(Boolean permissionNetworkLow) {
		permissionToUseNetwork = permissionNetworkLow;
	}

	public void setPermissionToStartCamera(Boolean permissionStartCameraLow) {
		permissionToStartCamera = permissionStartCameraLow;
	}

	public void setPermissionToStartAssignment(
			Boolean permissionStartAssignmentLow) {
		permissionToStartAssignment = permissionStartAssignmentLow;
	}

	public void setLowBatteryLevel(int batterylevel) {
		lowBatteryLevel = batterylevel;
	}
}
