package qosManager;

import java.util.Observable;
import java.util.Observer;

import com.klient_projekttermin.R;

import android.os.BatteryManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

public class QoSManager extends Activity implements Observer {
	
	BatteryCheckingFunction batteryCheckingFunction;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battery_checker);
    	batteryCheckingFunction = new BatteryCheckingFunction(getApplicationContext());
    	batteryCheckingFunction.addObserver(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_battery_checker, menu);
        return true;
    }
    
    public void checkBatteryStatus(View v){    	
    	batteryCheckingFunction.checkBatteryStatus();
    }

    public void update(Observable observable, Object data) {
    	System.out.println("WUUUUT");
//		runOnUiThread(new Runnable() {
//			
//			public void run() {
//				System.out.println("INNE I UPDATE");
//				
//			}
//		});
//        Toast.makeText(getApplicationContext(), "Batteri status: "+data+"%", Toast.LENGTH_SHORT).show();
		
	}
}
