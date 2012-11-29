package loginFunction;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import models.AuthenticationModel;
import models.ModelInterface;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.klient_projekttermin.MainActivity;
import com.klient_projekttermin.R;
import communicationModule.CommunicationService;
import communicationModule.CommunicationService.CommunicationBinder;

import database.Database;

public class LogInFunction extends InactivityListener {
	private TextView userNameView;
	private TextView passwordView;
	private String userName;
	private String password;
	private String passwordHashReference;
	private String userNameReference;
	private CommunicationService communicationService;
	private boolean communicationBond = false;
	private List<ModelInterface> acceptedAuthenticationModels;
	private Database database;
	private int numberOfLoginTries = 3;
	private int waitTime = 1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_log_in_function);

		database = Database.getInstance(getApplicationContext());
		
		// Töm autentiseringsdatabasen
		List<ModelInterface> allAut = database.getAllFromDB(new AuthenticationModel(), getContentResolver());
		Log.d("LOGIN","Antal element i aut: " + allAut.size());
		for (ModelInterface modelInterface : allAut) {
			AuthenticationModel aut = (AuthenticationModel) modelInterface;
			database.deleteFromDB(aut, getContentResolver());
		}
		allAut = database.getAllFromDB(new AuthenticationModel(), getContentResolver());
		Log.d("LOGIN","Antal element i aut: " + allAut.size());
		
		Intent intent = new Intent(this.getApplicationContext(),
				CommunicationService.class);
		bindService(intent, communicationServiceConnection,
				Context.BIND_AUTO_CREATE);
	}

	@Override
	protected void onDestroy() {
		unbindService(communicationServiceConnection);
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_log_in_function, menu);
		return true;
	}

	/*
	 * Metoden hämtar data från textfälten i inloggningsfönstret
	 */
	public void logIn(View v) throws NoSuchAlgorithmException {
		communicationService.setContext(getApplicationContext());

		userNameView = (TextView) this.findViewById(R.id.userName);
		passwordView = (TextView) this.findViewById(R.id.password);
		userName = userNameView.getText().toString();
		password = passwordView.getText().toString();

		AuthenticationModel authenticationModel = new AuthenticationModel(
				userName, hashPassword(password));

		sendAuthenticationRequestToServer(authenticationModel);
		
		checkAuthenticity(authenticationModel);

	}

	/*
	 * Metoden matchar inloggninguppgifterna mot de godkända kombinationerna som
	 * finns i databasen först och om så inte är fallet försöker den hämta
	 * informationen från servern.
	 */
	private void checkAuthenticity(AuthenticationModel authenticationModel) {
		timeToWait();

		AuthenticationModel authenticationReference;
		acceptedAuthenticationModels = database.getAllFromDB(
				new AuthenticationModel(), getContentResolver());
		
		Log.d("LOGIN","Antal i check: " + acceptedAuthenticationModels.size());
		
		if (acceptedAuthenticationModels.size() != 0) {
			for (int i = 0; i < acceptedAuthenticationModels.size(); i++) {
				authenticationReference = (AuthenticationModel) acceptedAuthenticationModels
						.get(i);

				if (authenticationModel.getUserName().equals(
						authenticationReference.getUserName())
						&& authenticationReference.isAccessGranted().equals(
								"true")) {
					accessGranted();
					break;
				} else if (authenticationModel.getUserName().equals(
						authenticationReference.getUserName())
						&& authenticationReference.isAccessGranted().equals(
								"false")) {
					incorrectLogIn();
					break;
				}
			}
		}
	}

	/**
	 * väntar till reconnect, samt ökar väntetiden kontiueligt upp till en
	 * minut.
	 */
	private synchronized void timeToWait() {
		waitTime = 5000;
		try {
			this.wait(waitTime);
		} catch (Exception e) {
			Log.e("Thread", "Nu har du väntat fel: " + e.toString());
		}

	}

	public void loginFailure() {
		Toast.makeText(getApplicationContext(),
				"Get gick inte att hämta inloggningsuppgifter från servern",
				Toast.LENGTH_SHORT).show();
	}

	public void incorrectLogIn() {
		numberOfLoginTries--;
		if (numberOfLoginTries == 0) {
			finish();
		} else {
			Toast toast = Toast.makeText(getApplicationContext(),
					"Felaktigt användarnamn eller lösenord! "
							+ numberOfLoginTries + " försök kvar!",
					Toast.LENGTH_LONG);
			toast.setGravity(Gravity.TOP, 0, 50);
			toast.show();
		}
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
	public void sendAuthenticationRequestToServer(
			AuthenticationModel authenticationModel) {

		if (communicationBond) {
			communicationService.sendAuthentication(authenticationModel);
		}
	}

	public void accessGranted() {
		Intent intent = new Intent(this, MainActivity.class);
		intent.putExtra("USER", userName);
		startActivity(intent);
		finish();
	}

	private ServiceConnection communicationServiceConnection = new ServiceConnection() {

		public void onServiceConnected(ComponentName className, IBinder service) {
			CommunicationBinder binder = (CommunicationBinder) service;
			communicationService = binder.getService();
			communicationBond = true;
		}

		public void onServiceDisconnected(ComponentName arg0) {
			communicationBond = false;
		}
	};
}
