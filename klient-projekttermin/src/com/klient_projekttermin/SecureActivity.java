package com.klient_projekttermin;

import loginFunction.LogInActivity;
import loginFunction.User;
import map.MapActivity;
import qosManager.QoSManager;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

/**
 * Bas aktivitet för alla aktiviteter. 
 * Alla aktiviteter extendar denna. 
 * @author nicklas
 *
 */
@SuppressLint("HandlerLeak")
public class SecureActivity extends Activity {
	private QoSManager qosManager;
	public static String inactivity;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		qosManager = QoSManager.getInstance();
		qosManager.startBatteryCheckingThread(this);
		User user =User.getInstance();
		if (!user.isLogged()) {
			Intent myIntent = new Intent(SecureActivity.this,
					LogInActivity.class);
			this.startActivity(myIntent);
		}
	}

	/**
	 * Sätter hur lång tid timeouten är på 
	 */
    public static final long DISCONNECT_TIMEOUT = 600000; 

    private Handler disconnectHandler = new Handler(){
        public void handleMessage(Message msg) {
        }
    };

    /**
     * Är detta som körs då timeouten har gått ut
     */
    private Runnable disconnectCallback = new Runnable() {
        public void run() {
            Intent intent = new Intent(SecureActivity.this, LogInActivity.class);
            intent.putExtra("calling-activity", ActivityConstants.INACTIVITY);
            SecureActivity.this.startActivity(intent);
        }
    };

    public void resetDisconnectTimer(){
        disconnectHandler.removeCallbacks(disconnectCallback);
        disconnectHandler.postDelayed(disconnectCallback, DISCONNECT_TIMEOUT);
    }

    public void stopDisconnectTimer(){
        disconnectHandler.removeCallbacks(disconnectCallback);
    }

    @Override
    public void onUserInteraction(){
        resetDisconnectTimer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        resetDisconnectTimer();
    }

    @Override
    public void onStop() {
        super.onStop();
        stopDisconnectTimer();
    }
}