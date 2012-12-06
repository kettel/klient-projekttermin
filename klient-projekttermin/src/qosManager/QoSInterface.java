package qosManager;

import com.klient_projekttermin.R;
import com.klient_projekttermin.R.id;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.Menu;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.ToggleButton;

public class QoSInterface extends Activity implements OnSeekBarChangeListener, OnCheckedChangeListener {
	private QoSManager qosManager;
	private ToggleButton batterySaveToggle;
	private ToggleButton automaticSaveModeToggel;
	private SeekBar screenBrightnessBar;
	private SeekBar batterylevelBar;
	private CheckBox mapPermission;
	private CheckBox messagePermission;
	private CheckBox assignmentPermission;
	private CheckBox cameraPermission;
	private CheckBox wifiPermission;
	private TextView lowBatteryLevelText;
	private TextView screenBrightnessLevelText;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_qo_sinterface);

		qosManager = QoSManager.getInstance();
		qosManager.setContext(this);
		batterySaveToggle = (ToggleButton) findViewById(id.toggleForManualActivationOfBatterySaveMode);
		automaticSaveModeToggel = (ToggleButton) findViewById(id.automaticQos);
		screenBrightnessBar = (SeekBar) findViewById(id.screenBrihgtnessSeekBar);
		screenBrightnessBar.setOnSeekBarChangeListener(this);
		batterylevelBar = (SeekBar) findViewById(id.lowBatteyLevelSeekBar);
		batterylevelBar.setOnSeekBarChangeListener(this);
		mapPermission = (CheckBox) findViewById(id.mapFunctionCheckBox);
		messagePermission = (CheckBox) findViewById(id.messageFunctionCheckBox);
		assignmentPermission = (CheckBox) findViewById(id.assignmentFunctionCheckBox);
		cameraPermission = (CheckBox) findViewById(id.cameraFunctionCheckBox);
		wifiPermission = (CheckBox) findViewById(id.WiFiConnectionCheckBox);
		lowBatteryLevelText = (TextView) findViewById(id.lowBatteryValue);
		screenBrightnessLevelText = (TextView) findViewById(id.lowScreenBrightnessValue);

		if(qosManager.batterySaveModeIsActivated()){
			batterySaveToggle.setChecked(true);
		}

		setCurrentValues();

		mapPermission.setOnCheckedChangeListener(this);
		wifiPermission.setOnCheckedChangeListener(this);
		cameraPermission.setOnCheckedChangeListener(this);
		messagePermission.setOnCheckedChangeListener(this);
		assignmentPermission.setOnCheckedChangeListener(this);
	}

	/**
	 * Metoden aktiverar batterisparläget när knappen trycks på i gränssnittet
	 */
	public void manualStartBatterySaveMode(View v){
		if(batterySaveToggle.isChecked()){
			qosManager.adjustToLowBatteryLevel();
		}
		else{
			qosManager.adjustToOkayBatteryLevel();
		}
	}

	/**
	 * Metoden aktiverar det automatiska QoS-läget som justerar enheten baserat på batterinivå
	 */
	public void startAutomaticAdjustments(View v){
		if(automaticSaveModeToggel.isChecked()){
			if(!qosManager.isBatteryCheckThreadStarted()){
				qosManager.startBatteryCheckingThread(getApplicationContext());
			}
		}
		else{
			qosManager.stopBatteryCheckThread();
		}
	}

	public void setMapPermission(View v){
		if(mapPermission.isChecked()){
			qosManager.setPermissionToStartMap(false);
		}
		else {
			qosManager.setPermissionToStartMap(true);
		}
	}

	public void setMessagePermission(View v){
		if(messagePermission.isChecked()){
			qosManager.setPermissionToStartMessages(false);
		}
		else {
			qosManager.setPermissionToStartMessages(true);
		}
	}

	public void setAssignmentPermission(View v){
		if(assignmentPermission.isChecked()){
			qosManager.setPermissionToStartAssignment(false);
		}
		else {
			qosManager.setPermissionToStartAssignment(true);
		}
	}

	public void setCameraPermission(View v){
		if(cameraPermission.isChecked()){
			qosManager.setPermissionToStartCamera(false);
		}
		else {
			qosManager.setPermissionToStartCamera(true);
		}
	}

	public void setWiFiPermission(View v){
		if(wifiPermission.isChecked()){
			qosManager.setPermissionToUseNetwork(false);
		}
		else {
			qosManager.setPermissionToUseNetwork(true);
		}
	}

	public void setDefaultValues(View v){
		mapPermission.setChecked(true);
		wifiPermission.setChecked(true);
		cameraPermission.setChecked(true);
		messagePermission.setChecked(false);
		assignmentPermission.setChecked(true);
		screenBrightnessBar.setProgress(20);
		batterylevelBar.setProgress(20);

		if(qosManager.batterySaveModeIsActivated()){
			qosManager.setPermissionToStartMap(false);
			qosManager.setPermissionToUseNetwork(false);
			qosManager.setPermissionToStartCamera(false);
			qosManager.setPermissionToStartMessages(true);
			qosManager.setPermissionToStartAssignment(false);
			qosManager.setLowBatteryLevel(20);
			qosManager.setScreenBrightnessValueLow((float) 0.2);
			qosManager.adjustToLowBatteryLevel();
		}
	}

	private void setCurrentValues() {

		mapPermission.setChecked(!qosManager.getPermissionToStartMap());
		messagePermission.setChecked(!qosManager.getPermissionToStartMessages());
		assignmentPermission.setChecked(!qosManager.getPermissionToStartAssignment());
		cameraPermission.setChecked(!qosManager.getPermissionToStartCamera());
		wifiPermission.setChecked(!qosManager.getPermissionToUseWiFi());
		screenBrightnessBar.setProgress((int) (qosManager.getScreenBrightnessValue()*100));
		batterylevelBar.setProgress(qosManager.getLowBatteryLevel());
	}

	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {

		if(seekBar.equals(screenBrightnessBar)){
			float value = ((float) progress/100);

			if(value<=0.1){
				value=(float) 0.1;
			}
			screenBrightnessLevelText.setText(progress+" %");
			qosManager.setScreenBrightnessValueLow(value);
		}
		else{
			lowBatteryLevelText.setText(progress+" %");
			qosManager.setLowBatteryLevel(progress);
		}
	}

	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub

	}

	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub

	}

	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		int mapChkId = mapPermission.getId();
		int messageChkId = messagePermission.getId();
		int assignmentChkId = assignmentPermission.getId();
		int cameraChkId = cameraPermission.getId();
		int wifiChkId = wifiPermission.getId();

		if(mapChkId==buttonView.getId()){
			setMapPermission(buttonView);
		}
		else if(messageChkId==buttonView.getId()){
			setMessagePermission(buttonView);
		}
		else if(assignmentChkId==buttonView.getId()){
			setAssignmentPermission(buttonView);
		}
		else if(cameraChkId==buttonView.getId()){
			setCameraPermission(buttonView);
		}
		else if(wifiChkId==buttonView.getId()){
			setWiFiPermission(buttonView);
		}

		if(batterySaveToggle.isChecked()){
			qosManager.adjustToLowBatteryLevel();
		}
	}
}

