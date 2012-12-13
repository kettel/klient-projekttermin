package qosManager;

import java.util.Observable;
import java.util.Observer;

import com.klient_projekttermin.SecureActivity;

import communicationModule.PullResponseHandler;
import communicationModule.SocketConnection;

import android.app.Activity;
import android.content.Context;

import android.net.wifi.WifiManager;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ToggleButton;

public class QoSManager extends SecureActivity implements Observer {
	private int lowBatteryLevel=20;
	private Boolean permissionToUseWiFi = false;
	private Boolean permissionToStartMap = false;
	private Boolean permissionToStartCamera = false;
	private Boolean permissionToStartMessages = true;
	private Boolean permissionToStartAssignment = false;
	private Boolean permissionToStartContactBook = false;
	private Boolean permissionToStartCallLog = false;
	private float screenBrightnesslevel = (float) 0.1;

	private float screenBrightnesslevelOkay = (float) 0.5;
	private Boolean permissionToStartMapOkay = true;
	private Boolean permissionToUseWiFiOkay = true;
	private Boolean permissionToStartCameraOkay = true;
	private Boolean permissionToStartMessagesOkay = true;
	private Boolean permissionToStartAssignmentOkay = true;
	private Boolean permissionToStartContactBookOkay = true;
	private Boolean permissionToStartCallLogOkay = true;

	private Boolean BatterySaveModeIsActivated=false;
	private Boolean okayBatterylevel = true;

	private BatteryCheckingFunction batteryCheckingFunction;
	private Context applicationContext;
	private MenuItem connectivityMarker;
	private ToggleButton batterySaveModeToggle;
	private Boolean toggleIsSet = false;
	private Boolean readyToAdjustCM = false;

	private QoSManager() {
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

	public void setBatterySaveModeToggle(ToggleButton toggleButton){
		batterySaveModeToggle = toggleButton;
		toggleIsSet = true;
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

	public void adjustToCurrentBatteryMode(Context context){
		if(BatterySaveModeIsActivated){
			adjustToLowBatteryLevel(context);
		}else{
			adjustToOkayBatteryLevel(context);	
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
			adjustToLowBatteryLevel(applicationContext);
		}
		else if(batteryLevel>lowBatteryLevel&&!okayBatterylevel){
			okayBatterylevel = true;
			adjustToOkayBatteryLevel(applicationContext);
		}
	}

	/**
	 * Metoden ändrar enhetens inställningar om batteriet når en bra
	 * laddningsnivå
	 */
	public void adjustToOkayBatteryLevel(Context context) {
		System.out.println("Anpassar till okej batterinivå");
		BatterySaveModeIsActivated = false;

		if(toggleIsSet){
			runOnUiThread(new Runnable() {

				public void run() {
					if(batterySaveModeToggle.isChecked()){
						batterySaveModeToggle.setChecked(false);
					}
				}
			});
		}

		adjustScreenBrightness(context, screenBrightnesslevelOkay);
		adjustNetworkStatus(permissionToUseWiFiOkay);
	}

	/**
	 * Metoden ändrar enhetens inställningar om batteriet når en låg
	 * laddningsnivå
	 */
	public void adjustToLowBatteryLevel(Context context) {
		System.out.println("Anpassar till lågt batteri");
		BatterySaveModeIsActivated=true;

		if(toggleIsSet){
			runOnUiThread(new Runnable() {

				public void run() {
					if(!batterySaveModeToggle.isChecked()){
						batterySaveModeToggle.setChecked(true);
					}
				}
			});
		}

		adjustScreenBrightness(context, screenBrightnesslevel);
		adjustNetworkStatus(permissionToUseWiFi);
	}

	/**
	 * Metoden justerar skärmljusstyrkan med hjälp av ett inkommande floatvärde
	 * 
	 * @param value kan vara ett valfritt float-värde mellan 0.0-1.0;
	 */
	public void adjustScreenBrightness(final Context context, float brightnessValue) {
		System.out.println("Korrigerar ljusstyrkan till: "+brightnessValue);
		final WindowManager.LayoutParams layout = ((Activity) context).getWindow().getAttributes();
		layout.screenBrightness = brightnessValue;

		runOnUiThread(new Runnable() {

			public void run() {
				((Activity) context).getWindow().setAttributes(layout);
			}
		});
	}

	/**
	 * Metoden stänger eller sätter igågng av WiFi-anslutning i enheten
	 */
	public void adjustNetworkStatus(Boolean wantToTurnOn) {
		WifiManager wifiManager = (WifiManager) applicationContext
				.getSystemService(Context.WIFI_SERVICE);
		wifiManager.setWifiEnabled(wantToTurnOn);
	}

	public void tryToReconnectToServer(){
		SocketConnection connection = new SocketConnection();
		connection.setContext(applicationContext);
		connection.addObserver(new PullResponseHandler(applicationContext));
		connection.pullFromServer();
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
	public boolean isAllowedToStartContactBook() {
		if(BatterySaveModeIsActivated){
			return permissionToStartContactBook;
		}
		else{
			return permissionToStartContactBookOkay;
		}
	}
	public boolean isAllowedToStartCallLog(){
		if(BatterySaveModeIsActivated){
			return permissionToStartCallLog;
		}
		else{
			return permissionToStartCallLogOkay;
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
	public void setScreenBrightnessValueLow(Context context,float newScreenBrightnessLevel){
		screenBrightnesslevel = newScreenBrightnessLevel;
		System.out.println("Nytt värde på skärmstyrkan är sparat");
		if(BatterySaveModeIsActivated){
			adjustScreenBrightness(context, screenBrightnesslevel);
			System.out.println("Nu skärmstyrka är satt");
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

	public void setPermissionToStartContactBook(Boolean permissionStartContactBookLow) {
		permissionToStartContactBook = permissionStartContactBookLow;
	}

	public void setLowBatteryLevel(Context context, int batterylevel) {
		lowBatteryLevel = batterylevel;
		if(batterySaveModeIsActivated()){
			adjustToLowBatteryLevel(context);
		}
	}
	
	public void setPermissionToStartCallLog(Boolean permissionStartCallLog){
		permissionToStartCallLog = permissionStartCallLog;
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

	public Boolean getPermissionToStartContactBook(){
		return permissionToStartContactBook;
	}
	
	public Boolean getPermissionToStartCallLog(){
		return permissionToStartCallLog;
	}
}
