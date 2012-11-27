package qosManager;

import java.util.Observable;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.view.View;
import android.widget.Toast;

public class BatteryCheckingFunction extends Observable {
	private Context context;

	public BatteryCheckingFunction(Context context){
		this.context=context;
	}
	
	public void checkBatteryStatus(){    	
    	IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, ifilter);
        
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        int batteryPct = (level / scale)*100;
        
        if(batteryPct >= 90){
        	System.out.println("HÄÄR");
        	setChanged();
        	notifyObservers(batteryPct);
        }
    }
}
