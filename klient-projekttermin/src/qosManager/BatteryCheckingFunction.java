package qosManager;

import java.util.Observable;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Looper;
import android.sax.StartElementListener;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class BatteryCheckingFunction extends Observable{

	private int batteryLevel = 0;
	
	public BatteryCheckingFunction(Context context){
		System.out.println("Tjena");
		startCheckThread(context);
	}

	public void startCheckThread(final Context context) {
		IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		final Intent batteryStatus = context.registerReceiver(null, ifilter);
		
		System.out.println("KÖR startCheckThread");
		new Thread(new Runnable() {

			public void run() {
				Looper.prepare();
				while(true){
					int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);

					if(level!=batteryLevel){
						batteryLevel = level;
						sendNotification(level);
					}
					timeToWait();
				}
			}
		}).start();
	}

	private synchronized void timeToWait(){
		int waitTime = 1800000;
		try {
			Thread.sleep(waitTime);
		} catch (Exception e) {
			Log.e("Thread", "Wating error: " + e.toString());
		}
	}
		
	private void sendNotification(int level){
		setChanged();
		notifyObservers(level);
	}
}
