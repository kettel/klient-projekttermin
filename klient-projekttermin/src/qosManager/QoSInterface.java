package qosManager;

import com.klient_projekttermin.R;
import com.klient_projekttermin.R.id;

import android.os.Bundle;
import android.app.Activity;
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
		mapPermission.setOnCheckedChangeListener(this);
		messagePermission = (CheckBox) findViewById(id.messageFunctionCheckBox);
		messagePermission.setOnCheckedChangeListener(this);
		assignmentPermission = (CheckBox) findViewById(id.assignmentFunctionCheckBox);
		assignmentPermission.setOnCheckedChangeListener(this);
		cameraPermission = (CheckBox) findViewById(id.cameraFunctionCheckBox);
		cameraPermission.setOnCheckedChangeListener(this);
		wifiPermission = (CheckBox) findViewById(id.WiFiConnectionCheckBox);
		wifiPermission.setOnCheckedChangeListener(this);
		lowBatteryLevelText = (TextView) findViewById(id.lowBatteryValue);
		screenBrightnessLevelText = (TextView) findViewById(id.lowScreenBrightnessValue);

		if(qosManager.batterySaveModeIsActivated()){
			System.out.println("Batterisparläge är aktiverat");
			batterySaveToggle.setChecked(true);
		}

		setCurrentValues();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_qo_sinterface, menu);
		return true;
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
		System.out.println("Nu uppdateras current values");

		if(!qosManager.isAllowedToStartMap()){
			mapPermission.setChecked(true);
		}
		else{
			mapPermission.setChecked(false);
		}
		if(!qosManager.isAllowedToStartMessages()){
			System.out.println("Message sätts till checked");
			messagePermission.setChecked(true);
		}
		else{
			System.out.println("Message sätts till unchecked");
			messagePermission.setChecked(false);
		}
		if(!qosManager.isAllowedToStartAssignment()){
			System.out.println("Uppdrag sätts till checked");
			assignmentPermission.setChecked(true);
		}
		else{
			System.out.println("Uppdrag sätts till unchecked");
			assignmentPermission.setChecked(false);
		}
		if(!qosManager.isAllowedToStartCamera()){
			cameraPermission.setChecked(true);
		}
		else{
			cameraPermission.setChecked(false);
		}
		if(!qosManager.isAllowedToUseWiFi()){
			wifiPermission.setChecked(true);
		}
		else{
			wifiPermission.setChecked(false);
		}
		screenBrightnessBar.setProgress((int) (qosManager.getScreenBrightnessValue()*100));
		batterylevelBar.setProgress(qosManager.getLowBatteryLevel());
	}

	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {

		if(seekBar.equals(screenBrightnessBar)){
			System.out.println("Nu hände det något med ljusstyrkan");
			float value = ((float) progress/100);

			if(value<=0.1){
				value=(float) 0.1;
			}
			screenBrightnessLevelText.setText(progress+" %");
			qosManager.setScreenBrightnessValueLow(value);
		}
		else{
			System.out.println("Nu hände det något med baterinivån");
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
			System.out.println("DU TRYCKTE PÅ MESSAGE");
			setMessagePermission(buttonView);
		}
		else if(assignmentChkId==buttonView.getId()){
			System.out.println("DU TRYCKTE PÅ ASSIGNMENT");
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

