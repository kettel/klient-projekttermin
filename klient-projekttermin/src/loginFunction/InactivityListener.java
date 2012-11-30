package loginFunction;

import com.klient_projekttermin.ActivityConstants;

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
public class InactivityListener extends Activity {
	private QoSManager qosManager;
	public static String inactivity;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		qosManager = QoSManager.getInstance();
		qosManager.startBatteryCheckingThread(this);
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
            Intent intent = new Intent(InactivityListener.this, LogInFunction.class);
            intent.putExtra("calling-activity", ActivityConstants.INACTIVITY);
            InactivityListener.this.startActivity(intent);
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