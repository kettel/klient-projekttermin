package qos;


import com.klient_projekttermin.R;

import loginFunction.InactivityListener;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class QoS_Controller extends InactivityListener implements OnSeekBarChangeListener{
	private CheckBox chkQosAuto, chkQosOn, chkQosOff;
	private Button btnHelp, btnQoSIns;
	private float newProgressValue,currentProgress;
	public SharedPreferences qoSIns ;
	private String Key_PROGRESS = "key_progress";
	private String qosAuto = "QosAuto";
	private String qosOn = "QosOn";
	private String qosOff = "QosOff";
	
	


	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.qos_controller);
		qoSIns = getSharedPreferences("qoSIns" , Context.MODE_WORLD_READABLE);
	    currentProgress = qoSIns.getFloat(Key_PROGRESS, currentProgress);
		SeekBar sb = (SeekBar)findViewById(R.id.slider);
        sb.setMax(100);
        sb.setProgress((int) (currentProgress*100));
        sb.setOnSeekBarChangeListener(this);
        

		addListenerOnChkQosAuto();
		addListenerOnChkQosOn();
		addListenerOnChkQosOff();
		addListenerOnQoSIns();
		addListenerOnHelp();
	}
	public void addListenerOnChkQosAuto() {

		chkQosAuto = (CheckBox) findViewById(R.id.chkQosAuto);

		chkQosAuto.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				if (((CheckBox) v).isChecked()) {
					chkQosOn.setChecked(false);
					chkQosOff.setChecked(false);
					//sätt vad som ska skrivas in i SPen
					Toast.makeText(QoS_Controller.this,
							"Systemet sätter igång energisparning när battriet är under (sh.energi) %"  , Toast.LENGTH_LONG).show();
				}

			}
		});

	}
	public void addListenerOnChkQosOn() {

		chkQosOn = (CheckBox) findViewById(R.id.chkQosOn);
		chkQosOn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (((CheckBox) v).isChecked()) {
					chkQosAuto.setChecked(false);
					chkQosOff.setChecked(false);
					//sätt vad som ska skrivas in i SPen
					Toast.makeText(QoS_Controller.this,
							"Systemet är nu i constant energisparnings läge", Toast.LENGTH_LONG).show();
				}

			}
		});

	}
	public void addListenerOnChkQosOff() {

		chkQosOff = (CheckBox) findViewById(R.id.chkQosOff);

		chkQosOff.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				if (((CheckBox) v).isChecked()) {
					chkQosAuto.setChecked(false);
					chkQosOn.setChecked(false);
					//sätt vad som ska skrivas in i SPen
					Toast.makeText(QoS_Controller.this,
							"Systemet kommer inte gå in i energisparnings läge", Toast.LENGTH_LONG).show();
				}

			}
		});

	}
	
	public void onProgressChanged(SeekBar v, int progress, boolean isUser) {
		TextView tv = (TextView)findViewById(R.id.percent);
		tv.setText(Integer.toString(progress)+"%");
	}

	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}

	public void onStopTrackingTouch(SeekBar seekBar) {
		newProgressValue = seekBar.getProgress();
		newProgressValue= newProgressValue*100;
        currentProgress = newProgressValue ;
        SharedPreferences.Editor editor = qoSIns.edit();
        editor.putFloat(Key_PROGRESS, newProgressValue);
        editor.commit();
		
	}
	public void addListenerOnQoSIns() {
		
		btnQoSIns.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent myIntent = new Intent(QoS_Controller.this, QoS_Options.class);
				QoS_Controller.this.startActivity(myIntent);
			}
		});
	}
	public void addListenerOnHelp() {
		
		btnHelp.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
			}
		});

		

	}
}
