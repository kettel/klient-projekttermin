package qosManager;

import java.util.Observable;
import java.util.Observer;

import com.klient_projekttermin.SecureActivity;

import communicationModule.PullResponseHandler;
import communicationModule.SocketConnection;

import login.User;
import android.app.Activity;
import android.content.Context;

import android.net.wifi.WifiManager;
import android.view.MenuItem;
import android.view.WindowManager;

public class QoSManager extends SecureActivity implements Observer {
	private int lowBatteryLevel=20;
	private Boolean permissionToUseWiFi = false;
	private Boolean permissionToStartMap = false;
	private Boolean permissionToStartCamera = false;
	private Boolean permissionToStartMessages = true;
	private Boolean permissionToStartAssignment = false;
	private float screenBrightnesslevel = (float) 0.2;

	private float screenBrightnesslevelOkay = (float) 0.5;
	private Boolean permissionToStartMapOkay = true;
	private Boolean permissionToUseWiFiOkay = true;
	private Boolean permissionToStartCameraOkay = true;
	private Boolean permissionToStartMessagesOkay = true;
	private Boolean permissionToStartAssignmentOkay = true;

	private Boolean BatterySaveModeIsActivated=false;
	private Boolean okayBatterylevel = true;

	private BatteryCheckingFunction batteryCheckingFunction;
	private Context applicationContext;
	private User user;
	private MenuItem connectivityMarker;
	private Boolean readyToAdjustCM = false;

	private QoSManager() {
		user = User.getInstance();
	}

	private static QoSManager instance = new QoSManager();

	public static QoSManager getInstance() {
		return instance;
	}

	public void setContext(Context context){
		applicationContext = context;
	}

	public void setConnectivityMarker(MenuItem menuItem){
		connectivityMarker = menuItem;
	}

	public MenuItem getConnectivityMarker(){
		return connectivityMarker;
	}

	public void startBatteryCheckingThread(Context context) {
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
			//			adjustToOkayBatteryLevel();
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
		adjustNetworkStatus(permissionToUseWiFiOkay);
	}

	/**
	 * Metoden ändrar enhetens inställningar om batteriet når en låg
	 * laddningsnivå
	 */
	public void adjustToLowBatteryLevel() {
		BatterySaveModeIsActivated=true;
		// Acro S-specifikt?
		//adjustScreenBrightness(screenBrightnesslevel);
		adjustNetworkStatus(permissionToUseWiFi);
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

	public void checkServerConnection(){
//		SocketConnection connection = new SocketConnection();	
//		connection.addObserver(new PullResponseHandler(applicationContext));
//		connection.pullFromServer();
	}

	public void changeConnectivityMarkerStatus(final Boolean serverConnection){

		runOnUiThread(new Runnable() {
			
			public void run() {
				if(serverConnection){
					connectivityMarker.setIcon(android.R.drawable.presence_online);
				}
				else{
					connectivityMarker.setIcon(android.R.drawable.presence_offline);
				}				
			}
		});
	}

	public Boolean batterySaveModeIsActivated(){
		return BatterySaveModeIsActivated;
	}

	public boolean isAllowedToStartMap() {
		if(BatterySaveModeIsActivated){
			return permissionToStartMap;
		}
		else{
			return permissionToStartMapOkay;
		}
	}

	public boolean isAllowedToStartAssignment() {
		if(BatterySaveModeIsActivated){
			return permissionToStartAssignment;
		}
		else{
			return permissionToStartAssignmentOkay;
		}
	}

	public boolean isAllowedToStartMessages() {
		if(BatterySaveModeIsActivated){
			return permissionToStartMessages;
		}
		else{
			return permissionToStartMessagesOkay;
		}
	}

	public boolean isAllowedToStartCamera() {
		if(BatterySaveModeIsActivated){
			return permissionToStartCamera;
		}
		else{
			return permissionToStartCameraOkay;
		}
	}
	public boolean isAllowedToUseWiFi(){
		if(BatterySaveModeIsActivated){
			return permissionToUseWiFi;
		}
		else{
			return permissionToUseWiFiOkay;
		}
	}
	
	public Boolean readyToAdjustCM() {
		return readyToAdjustCM;
	}

	public void setReadyToAdjustCM(Boolean readyToAdjustCM) {
		this.readyToAdjustCM = readyToAdjustCM;
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
	public void setScreenBrightnessValueLow(float newScreenBrightnessLevel){
		screenBrightnesslevel = newScreenBrightnessLevel;

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
		permissionToUseWiFi = permissionNetworkLow;
	}

	public void setPermissionToStartCamera(Boolean permissionStartCameraLow) {
		permissionToStartCamera = permissionStartCameraLow;
	}

	public void setPermissionToStartAssignment(Boolean permissionStartAssignmentLow) {
		permissionToStartAssignment = permissionStartAssignmentLow;
	}

	public void setLowBatteryLevel(int batterylevel) {
		lowBatteryLevel = batterylevel;
		if(batterySaveModeIsActivated()){
			adjustToLowBatteryLevel();
		}
	}
	public Boolean getPermissionToStartMap() {
		return permissionToStartMap;
	}

	public Boolean getPermissionToUseWiFi() {
		return permissionToUseWiFi;
	}

	public Boolean getPermissionToStartCamera() {
		return permissionToStartCamera;
	}

	public Boolean getPermissionToStartMessages() {
		return permissionToStartMessages;
	}

	public Boolean getPermissionToStartAssignment() {
		return permissionToStartAssignment;
	}
}
