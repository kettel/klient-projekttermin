package qosManager;

import java.util.Observable;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.util.Log;

public class BatteryCheckingFunction extends Observable {

	private int batteryLevel = 0;
	private Thread batteryCheckThread;
	private Boolean threadBoolean = true;

	public BatteryCheckingFunction(Context context) {
		startCheckThread(context);
	}

	public void startCheckThread(final Context context) {
		threadBoolean=true;
		IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		final Intent batteryStatus = context.registerReceiver(null, ifilter);

		batteryCheckThread = new Thread(new Runnable() {

			public void run() {
				while (threadBoolean) {
					int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);

					if (level != batteryLevel) {
						batteryLevel = level;
						sendNotification(level);
					}
					timeToWait();
				}
			}
		});
		batteryCheckThread.start();
	}

	private synchronized void timeToWait() {
		int waitTime = 1800000;
		try {
			Thread.sleep(waitTime);
		} catch (Exception e) {
			Log.e("Thread", "Wating error: " + e.toString());
		}
	}

	private void sendNotification(int level) {
		setChanged();
		notifyObservers(level);
	}

	public void stopBatteryCheckFunction(){
		threadBoolean=false;
	}

	public boolean isBatteryBeingChecked(){
		return threadBoolean;
	}
}
