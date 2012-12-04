package qosManager;

import com.klient_projekttermin.R;
import com.klient_projekttermin.R.id;
import com.klient_projekttermin.R.layout;
import com.klient_projekttermin.R.menu;

import android.os.Bundle;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
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
	protected void onStart() {
    	setDefaultValues(new View(getApplicationContext()) );
    	super.onStart();
	}

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qo_sinterface);
 
        
        qosManager = QoSManager.getInstance();
        qosManager.setContext(this);
        qosManager.startBatteryCheckingThread(this);
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
        
        if(qosManager.isBatterySaveModeActivated()){
        	batterySaveToggle.setChecked(true);
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
    	System.out.println("INNE I WIFIpermission");
    	if(wifiPermission.isChecked()){
    		qosManager.setPermissionToUseNetworkLow(false);
    	}
    	else {
    		System.out.println("DU Bockade ut WIFIn");
			qosManager.setPermissionToUseNetworkLow(true);
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
    	
    	qosManager.setPermissionToStartMapLow(false);
    	qosManager.setPermissionToUseNetworkLow(false);
    	qosManager.setPermissionToStartCameraLow(false);
    	qosManager.setPermissionToStartMessagesLow(true);
    	qosManager.setPermissionToStartAssignmentLow(false);
    	qosManager.setLowBatteryLevel(20);
    	qosManager.setScreenBrightnessValueLow((float) 0.2);
    	
    	if(batterySaveToggle.isChecked()){
    	qosManager.adjustToLowBatteryLevel();
    	}
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
		System.out.println(buttonView.getId());
		if(2131165224==buttonView.getId()){
			setMapPermission(buttonView);
		}
		else if(2131165226==buttonView.getId()){
			setMessagePermission(buttonView);
		}
		else if(2131165228==buttonView.getId()){
			setAssignmentPermission(buttonView);
		}
		else if(2131165230==buttonView.getId()){
			setCameraPermission(buttonView);
		}
		else{
			setWiFiPermission(buttonView);
		}
		if(batterySaveToggle.isChecked()){
			qosManager.adjustToLowBatteryLevel();
		}
	}
}
