package qosManager;

import com.klient_projekttermin.R;
import com.klient_projekttermin.R.id;
import com.klient_projekttermin.R.layout;
import com.klient_projekttermin.R.menu;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

public class QoSInterface extends Activity {
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
        qosManager.startBatteryCheckingThread(this);
        batterySaveToggle = (ToggleButton) findViewById(id.toggleForManualActivationOfBatterySaveMode);
        automaticSaveModeToggel = (ToggleButton) findViewById(id.automaticQos);
        screenBrightnessBar = (SeekBar) findViewById(id.lowBatteyLevelSeekBar);
        mapPermission = (CheckBox) findViewById(id.mapFunctionCheckBox);
        messagePermission = (CheckBox) findViewById(id.messageFunctionCheckBox);
        assignmentPermission = (CheckBox) findViewById(id.assignmentFunctionCheckBox);
        cameraPermission = (CheckBox) findViewById(id.cameraFunctionCheckBox);
        wifiPermission = (CheckBox) findViewById(id.WiFiConnectionCheckBox);
        lowBatteryLevelText = (TextView) findViewById(id.lowBatteryLevel);
        screenBrightnessLevelText = (TextView) findViewById(id.lowScreenBrightnessValue);
        
        if(qosManager.isBatteryCheckThreadStarted()){
        	automaticSaveModeToggel.setChecked(true);
        }
        
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
        	qosManager.startBatteryCheckingThread(getApplicationContext());
    	}
    	else{
    		qosManager.stopBatteryCheckThread();
    	}
    }
    
    /**
     * Metoden justerar värdet för låg batterinivå
     */
    public void adjustLowBatteryLevel(View v){
    	lowBatteryLevelText.setText(batterylevelBar.getProgress()+" %");
    	System.out.println("VALUE: "+batterylevelBar.getProgress());
    	qosManager.setLowBatteryLevel(batterylevelBar.getProgress());
    }
    
    /**
     * Metoden justerar skärmljusstyrkan
     */
    public void adjustScreenBrightness(View v){
    	float value = (screenBrightnessBar.getProgress()/100);
    	System.out.println("SKÄRMSTYRKA: "+value);
    	screenBrightnessLevelText.setText(screenBrightnessBar.getProgress()+" %");
    	qosManager.setScreenBrightnessValue(value);
    }
    
    public void setMapPermission(View v){
    	if(mapPermission.isChecked()){
    		qosManager.setPermissionToStartMapLow(false);
    	}
    	else {
			qosManager.setPermissionToStartMapLow(true);
		}
    }
    
    public void setMessagePermission(View v){
    	if(messagePermission.isChecked()){
    		qosManager.setPermissionToStartMessagesLow(false);
    	}
    	else {
			qosManager.setPermissionToStartMessagesLow(true);
		}
    }
    
    public void setAssignmentPermission(View v){
    	if(assignmentPermission.isChecked()){
    		qosManager.setPermissionToStartMessagesLow(false);
    	}
    	else {
			qosManager.setPermissionToStartMessagesLow(true);
		}
    }
    
    public void setCameraPermission(View v){
    	if(cameraPermission.isChecked()){
    		qosManager.setPermissionToStartCameraLow(false);
    	}
    	else {
			qosManager.setPermissionToStartCameraLow(true);
		}
    }
    
    public void setWiFiPermission(View v){
    	if(wifiPermission.isChecked()){
    		qosManager.setPermissionToUseNetworkLow(false);
    	}
    	else {
			qosManager.setPermissionToUseNetworkLow(true);
		}
    }
    
    public void setDefaultValues(View v){
    	qosManager.adjustToOkayBatteryLevel();
    }
    
}
