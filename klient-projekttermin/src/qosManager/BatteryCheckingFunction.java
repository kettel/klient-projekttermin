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

	public BatteryCheckingFunction(Context context) {
		startCheckThread(context);
	}

	public void startCheckThread(final Context context) {
//		if(!isBatteryBeingChecked()){
			System.out.println("Nu startas en ny batterikontrolltråd");
			IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
			System.out.println("Efter iFilter");
			final Intent batteryStatus = context.registerReceiver(null, ifilter);
			System.out.println("efter Intent");

			batteryCheckThread = new Thread(new Runnable() {

				public void run() {
					System.out.println("Precis innan while");
					while (true) {
						System.out.println("Inne i while");
						int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);

						if (level != batteryLevel) {
							System.out.println("Inne i if-satsen");
							batteryLevel = level;
							sendNotification(level);
						}
						timeToWait();
					}
				}
			});
			batteryCheckThread.start();
//		}
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
		System.out.println("Inne i sendNotification");
		setChanged();
		System.out.println("Notifyar");
		notifyObservers(level);
	}

	public void stopBatteryCheckFunction(){
		batteryCheckThread.interrupt();
	}

	public synchronized Boolean isBatteryBeingChecked(){
		return batteryCheckThread.isAlive();
	}
}
