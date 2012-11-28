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
	private int waitTime;

	public void startCheckThread(final Context context) {
		IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		final Intent batteryStatus = context.registerReceiver(null, ifilter);
		
		System.out.println("KÖR startCHeckThread");
		new Thread(new Runnable() {

			public void run() {
				Looper.prepare();
				while(true){
					int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);

					if(level!=batteryLevel){
						batteryLevel = level;
						sendNotification(level);
					}

					waitTime = 30000;
					timeToWait();

				}
			}
		}).start();
	}

	private synchronized void timeToWait(){
		try {
			this.wait(waitTime);	
		} catch (Exception e) {
			Log.e("Thread", "Wating error: " + e.toString());
		}

	}
	private void sendNotification(int level){
		setChanged();
		notifyObservers(level);
	}
}
