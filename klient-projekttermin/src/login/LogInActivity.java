package login;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import models.AuthenticationModel;
import qosManager.QoSManager;
import sip.CallDialogue;
import sip.Darclass;
import android.app.Activity;
import android.app.ProgressDialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.klient_projekttermin.ActivityConstants;
import com.klient_projekttermin.MainActivity;
import com.klient_projekttermin.R;
import communicationModule.SocketConnection;

import database.Database;

public class LogInActivity extends Activity implements Observer {
	private TextView userNameView;
	private TextView passwordView;
	private String userName;
	private String password;
	private Database database;

	private int numberOfLoginTries = 4;

	private AuthenticationModel originalModel;
	private ProgressDialog pd;
	private User user;
	private int callingactivity;
	private QoSManager qosManager;
	public static final int LOGGED_IN_REQ_CODE=1;
	public static final int SHUT_DOWN=2;
	public static final int STAY_ALIVE=3;
	
	// DevicePolicyManager för att kunna låsa skärmen
	protected static final int REQUEST_ENABLE = 0;
	private DevicePolicyManager devicePolicyManager;
	private ComponentName adminComponent;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_log_in_function);
		database = Database.getInstance(getApplicationContext());
		Intent intent = getIntent();
		callingactivity = intent.getIntExtra("calling-activity", 0);
		qosManager = QoSManager.getInstance();
		qosManager.setContext(getApplicationContext());
		user = User.getInstance();
		
		// Fråga om appen får vara DeviceAdmin (skärmlås i samtal ni vet..)
		isDeviceManager();
	}
	
	/**
	 * Fråga om användaren vill tillåta appen att vara DeviceManager (här istället för i CallDialog)
	 */
	private void isDeviceManager() {
        adminComponent = new ComponentName(LogInActivity.this, Darclass.class);
        devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        
        // Om device-rättigheter inte är instansierade, hämta dem genom en JÄTTEIRRITERANDE ruta..
        if (!devicePolicyManager.isAdminActive(adminComponent)) {
            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, adminComponent);
            startActivityForResult(intent, REQUEST_ENABLE);
        } 
	}

	@Override
	public void onBackPressed() {
		finish();
	}

	/*
	 * Metoden hämtar data från textfälten i inloggningsfönstret
	 */
	public void logIn(View v) throws NoSuchAlgorithmException {

		userNameView = (TextView) this.findViewById(R.id.userName);
		passwordView = (TextView) this.findViewById(R.id.password);
		userName = userNameView.getText().toString();
		password = passwordView.getText().toString();

		originalModel = new AuthenticationModel(userName,
				hashPassword(password));

		user.setAuthenticationModel(originalModel);

		tryOnlineLogin(originalModel);
	}

	/**
	 * Metoden matchar inloggninguppgifterna mot de godkända kombinationerna som
	 * finns i databasen först och om så inte är fallet försöker den hämta
	 * informationen från servern.
	 */
	private void checkAuthenticity(AuthenticationModel authenticationModel) {
	
		if (authenticationModel.getUserName().equals(originalModel.getUserName())
				&& authenticationModel.isAccessGranted().equals("true")) {
			System.out.println("Login ok!");
			database.addToDB(authenticationModel, getContentResolver());
			accessGranted();

		} else {
			System.out.println("Incorrect login credentials");
			incorrectLogIn();
		}
	}

	public void tryOfflineLogin(AuthenticationModel loginInput) {

		if (database.getDBCount(new AuthenticationModel(), getContentResolver()) != 0) {
			System.out.println("Försöker logga in offline");

			List modelList = database.getAllFromDB(loginInput,
					getContentResolver());
			AuthenticationModel loadedModel = (AuthenticationModel) modelList
					.get(0);

			if (loadedModel.getUserName().equals(loginInput.getUserName())) {
				if (loadedModel.getPasswordHash().equals(
						loginInput.getPasswordHash())
						&& loadedModel.isAccessGranted().equals("true")) {
					accessGranted();
				} else {
					incorrectLogIn();
				}
			} else{
				removeLastUserFromDB();
			}
		}else{
			this.runOnUiThread(new Runnable() {

				public void run() {
					pd.dismiss();
					Toast toast = Toast
							.makeText(
									getApplicationContext(),
									"Misslyckades med att logga in offline, inget i databasen",
									Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.TOP, 0, 300);
					toast.show();
				}
			});
		}
	}

	public void incorrectLogIn() {
		numberOfLoginTries--;
		if (numberOfLoginTries == 0) {
			if (database.getDBCount(new AuthenticationModel(),
					getContentResolver()) != 0) {
				removeLastUserFromDB();
			}
			finish();
		} else {
			this.runOnUiThread(new Runnable() {

				public void run() {
					Toast toast = Toast.makeText(getApplicationContext(),
							"Felaktigt användarnamn eller lösenord! "
									+ numberOfLoginTries + " försök kvar!",
							Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.TOP, 0, 300);
					toast.show();
				}
			});
		}
	}

	public void removeLastUserFromDB() {
		List list = database.getAllFromDB(new AuthenticationModel(),
				getContentResolver());
		database.deleteFromDB((AuthenticationModel) list.get(0),
				getContentResolver());
	}

	/*
	 * Metoden skapar en hashrepresentation av de inmatade lösenordet med hjälp
	 * av SHA-2
	 */
	public String hashPassword(String password) throws NoSuchAlgorithmException {

		MessageDigest md = MessageDigest.getInstance("SHA-256");
		md.update(password.toString().getBytes());

		byte byteData[] = md.digest();

		// convert the byte to hex format method 2
		StringBuffer hexString = new StringBuffer();
		for (int i = 0; i < byteData.length; i++) {
			String hex = Integer.toHexString(0xff & byteData[i]);
			if (hex.length() == 1)
				hexString.append('0');
			hexString.append(hex);
		}
		return hexString.toString();
	}

	/*
	 * Metoden skickar iväg autenticeringsförfrågan till servern
	 */
	public void tryOnlineLogin(AuthenticationModel authenticationModel) {

		SocketConnection connection = new SocketConnection();
		connection.setContext(getApplicationContext());
		connection.addObserver(this);
		connection.authenticate(authenticationModel);
		runOnUiThread(new Runnable() {

			public void run() {
				pd = ProgressDialog.show(LogInActivity.this, "",
						"Loggar in...", true, true);
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode==LOGGED_IN_REQ_CODE) {
			if (resultCode==STAY_ALIVE) {
			}else if (resultCode==SHUT_DOWN) {
				finish();
			}
		}
	}

	public void accessGranted() {
		user.setLoggedIn(true);
		runOnUiThread(new Runnable() {
			
			public void run() {
				passwordView.getEditableText().clear();
				
			}
		});
		switch (callingactivity) {
		case ActivityConstants.INACTIVITY:
			break;
		default:
			Intent intent = new Intent(this, MainActivity.class);
			startActivityForResult(intent, LOGGED_IN_REQ_CODE);
			break;
		}
	}

	public void update(Observable observable, Object data) {
		if (data instanceof AuthenticationModel) {
			user.setOnlineConnection(true);

			this.runOnUiThread(new Runnable() {

				public void run() {
					pd.dismiss();
				}
			});
			checkAuthenticity((AuthenticationModel) data);

		} else if (data instanceof String) {
			user.setOnlineConnection(false);
			this.runOnUiThread(new Runnable() {

				public void run() {
					pd.dismiss();
					Toast toast = Toast
							.makeText(
									getApplicationContext(),
									"Det gick inte att ansluta till servern! Försöker logga in offline",
									Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.TOP, 0, 300);
					toast.show();
				}
			});
			tryOfflineLogin(originalModel);
		}
	}
}
