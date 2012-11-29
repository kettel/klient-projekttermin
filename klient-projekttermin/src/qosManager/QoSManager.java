package qosManager;

import java.util.Observable;
import java.util.Observer;

import loginFunction.InactivityListener;
import loginFunction.LogInFunction;

import com.klient_projekttermin.MainActivity;

import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.LoginFilter;
import android.view.WindowManager;

public class QoSManager implements Observer {
	private Boolean permissionToStartMap = true;
	private Boolean permissionToStartCamera = true;
	private Boolean permissionToStartMessages = true;
	private Boolean permissionToStartAssignment = true;

	private float screenBrightnesslevelDefault = (float) 0.5;
	private Boolean permissionToStartMapDefault = true;
	private Boolean permissionToUseNetworkDefault = true;
	private Boolean permissionToStartCameraDefault = true;
	private Boolean permissionToStartMessagesDefault = true;
	private Boolean permissionToStartAssignmentDefault = true;
	private Boolean okayBatterylevel = true;

	private float screenBrightnesslevelLow = (float) 0.3;
	private Boolean permissionToStartMapLow = true;
	private Boolean permissionToUseNetworkLow = true;
	private Boolean permissionToStartCameraLow = true;
	private Boolean permissionToStartMessagesLow = true;
	private Boolean permissionToStartAssignmentLow = true;

	private float screenBrightnesslevelCritical = (float) 0.2;
	private Boolean permissionToStartMapCritical = true;
	private Boolean permissionToUseNetworkCritical = true;
	private Boolean permissionToStartCameraCritical = true;
	private Boolean permissionToStartMessagesCritical = true;
	private Boolean permissionToStartAssignmentCritical = true;

	private BatteryCheckingFunction batteryCheckingFunction;
	private Context applicationContext;

	private QoSManager() {
	}

	private static QoSManager instance = new QoSManager();

	public static QoSManager getInstance() {
		return instance;
	}

	public void startBatteryCheckingThread(Context context) {
		applicationContext = context;
		batteryCheckingFunction = new BatteryCheckingFunction(context);
		batteryCheckingFunction.addObserver(this);
	}

	/**
	 * Denna metod anropas om batterinivån har ändrats då den undersöks i
	 * checkBattryStatus.
	 */
	public void update(Observable observable, Object data) {
		int batteryLevel = (Integer) data;

		if (batteryLevel > 30) {
			if (!okayBatterylevel) {
				adjustToOkayBatteryLevel();
			}
		}

		else if (batteryLevel < 30 && batteryLevel > 15) {
			System.out.println("Batterinivån är låg: " + batteryLevel + "%");
			adjustToLowBatteryLevel();
		}

		else if (batteryLevel < 15) {
			System.out
					.println("Batterinivån är kritisk: " + batteryLevel + "%");
			adjustToCriticalBatteryLevel();
		}
	}

	/**
	 * Metoden ändrar enhetens inställningar om batteriet når en bra
	 * laddningsnivå
	 */
	public void adjustToOkayBatteryLevel() {
		okayBatterylevel = true;
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
		okayBatterylevel = false;
		adjustScreenBrightness(screenBrightnesslevelLow);
		adjustNetworkStatus(permissionToUseNetworkLow);
		permissionToStartAssignment = permissionToStartAssignmentLow;
		permissionToStartCamera = permissionToStartCameraLow;
		permissionToStartMap = permissionToStartMapLow;
		permissionToStartMessages = permissionToStartMessagesLow;
	}

	/**
	 * Metoden ändrar enhetens inställningar om batteriet når en kritisk
	 * laddningsnivå
	 */
	public void adjustToCriticalBatteryLevel() {
		okayBatterylevel = false;
		adjustScreenBrightness(screenBrightnesslevelCritical);
		adjustNetworkStatus(permissionToUseNetworkCritical);
		permissionToStartAssignment = permissionToStartAssignmentCritical;
		permissionToStartCamera = permissionToStartCameraCritical;
		permissionToStartMap = permissionToStartMapCritical;
		permissionToStartMessages = permissionToStartMessagesCritical;
	}

	/**
	 * Metoden sätter de värden som ska tillämpas vid låg batterinivå
	 */
	public void setQoSValuesForLowBatteryLevel(float screenbrightnessLevel,
			Boolean permissionToUseNetwork, Boolean permissionToUseGPS,
			Boolean permissionToStartAssignment,
			Boolean permissionToStartCamera, Boolean permissionToStartMap,
			Boolean permissionToStartMessages) {

		screenBrightnesslevelLow = screenbrightnessLevel;
		permissionToUseNetworkLow = permissionToUseNetwork;
		permissionToStartAssignmentLow = permissionToStartAssignment;
		permissionToStartCameraLow = permissionToStartCamera;
		permissionToStartMapLow = permissionToStartMap;
		permissionToStartMessagesLow = permissionToStartMessages;
	}

	/**
	 * Metoden sätter de värden som ska tillämpas vid kritisk batterinivå
	 */
	public void setQoSValuesForCriticalBatteryLevel(
			float screenbrightnessLevel, Boolean permissionToUseNetwork,
			Boolean permissionToUseGPS, Boolean permissionToStartAssignment,
			Boolean permissionToStartCamera, Boolean permissionToStartMap,
			Boolean permissionToStartMessages) {

		screenBrightnesslevelCritical = screenbrightnessLevel;
		permissionToUseNetworkCritical = permissionToUseNetwork;
		permissionToStartAssignmentCritical = permissionToStartAssignment;
		permissionToStartCameraCritical = permissionToStartCamera;
		permissionToStartMapCritical = permissionToStartMap;
		permissionToStartMessagesCritical = permissionToStartMessages;
	}

	/**
	 * Metoden justerar skärmljusstyrkan med hjälp av ett inkommande floatvärde
	 * 
	 * @param value
	 *            kan vara ett valfritt float-värde mellan 0.0-1.0;
	 */
	public void adjustScreenBrightness(float brightnessValue) {
		WindowManager.LayoutParams layout = ((InactivityListener) applicationContext)
				.getWindow().getAttributes();
		layout.screenBrightness = brightnessValue;
		((InactivityListener) applicationContext).getWindow().setAttributes(layout);
	}

	/**
	 * Metoden stänger eller sätter igågng av WiFi-anslutning i enheten
	 */
	public void adjustNetworkStatus(Boolean wantToTurnOn) {
		System.out
				.println("Nätverksanslutningar är avstängda/startade i enheten");
		WifiManager wifiManager = (WifiManager) applicationContext
				.getSystemService(applicationContext.WIFI_SERVICE);
		wifiManager.setWifiEnabled(wantToTurnOn);
	}

	public boolean allowedToStartMap() {
		return permissionToStartMap;
	}

	public boolean allowedToStartAssignment() {
		return permissionToStartAssignment;
	}

	public boolean allowedToStartMessages() {
		return permissionToStartMessages;
	}

	public boolean allowedToStartCamera() {
		return permissionToStartCamera;
	}
}
