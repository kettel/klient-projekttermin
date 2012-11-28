package loginFunction;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import models.AuthenticationModel;
import models.ModelInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.klient_projekttermin.MainActivity;
import com.klient_projekttermin.R;
import communicationModule.SocketConnection;

import database.Database;

public class LogInFunction extends InactivityListener implements Observer {
	private TextView userNameView;
	private TextView passwordView;
	private String userName;
	private String password;

	private List<ModelInterface> acceptedAuthenticationModels;
	private Database database;
	private int numberOfLoginTries = 3;
	private int waitTime = 1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_log_in_function);

		database = Database.getInstance(getApplicationContext());
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
		System.out.println("NU TESTAR VI DET SOM KOMMIT!");

		AuthenticationModel authenticationReference;
		acceptedAuthenticationModels = database.getAllFromDB(
				new AuthenticationModel(), getContentResolver());

		if (acceptedAuthenticationModels.size() != 0) {
			System.out.println("Databasen är inte tom!");
			for (int i = 0; i < acceptedAuthenticationModels.size(); i++) {
				authenticationReference = (AuthenticationModel) acceptedAuthenticationModels
						.get(i);
				System.out.println("USERNAME: "
						+ authenticationReference.getUserName()
						+ ", BOOLEANVÄRDE: "
						+ authenticationReference.isAccessGranted());

				if (authenticationModel.getUserName().equals(
						authenticationReference.getUserName())
						&& authenticationReference.isAccessGranted().equals(
								"true")) {
					System.out.println("Nu accepterar vi!");
					accessGranted();
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
			Toast.makeText(
					getApplicationContext(),
					"Felaktigt användarnamn eller lösenord! "
							+ numberOfLoginTries + " försök kvar!",
					Toast.LENGTH_SHORT).show();
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
		SocketConnection connection = new SocketConnection();
		connection.addObserver(this);
		connection.authenticate(authenticationModel);
	}

	public void accessGranted() {
		Intent intent = new Intent(this, MainActivity.class);
		intent.putExtra("USER", userName);
		startActivity(intent);
		finish();
	}

	public void update(Observable observable, Object data) {
		System.out.println("update login");
		if (data instanceof AuthenticationModel) {
			System.out.println("instance");
			checkAuthenticity((AuthenticationModel) data);
		}
	}

}
