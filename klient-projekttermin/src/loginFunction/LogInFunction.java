package loginFunction;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import models.AuthenticationModel;
import models.ModelInterface;
import android.content.Intent;
import android.os.Bundle;
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
	private String passwordHashReference;
	private String userNameReference;
	private AuthenticationModel AM;
	private Database dataBase;
	private boolean communicationBond = false;
	private List<ModelInterface> listOfAuthenticationModels;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_log_in_function);

		try {
			createPassWordHashRepresentation();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_log_in_function, menu);
		return true;
	}

	/*
	 * Metoden hämtar data från textfälten i inloggningsfönstret
	 */
	public void logIn(View v) throws NoSuchAlgorithmException,
			UnsupportedEncodingException {
		userNameView = (TextView) this.findViewById(R.id.userName);
		passwordView = (TextView) this.findViewById(R.id.password);
		userName = userNameView.getText().toString();
		password = passwordView.getText().toString();

		AuthenticationModel authenticationModel = new AuthenticationModel(
				userName, hashPassword(password));

		sendAuthenticationRequestToServer(v, authenticationModel);
		passwordView.getEditableText().clear();
	}

	/*
	 * Metoden skapar ett användar inlogg
	 */
	public void createUser(View v) throws NoSuchAlgorithmException {
		dataBase = Database.getInstance(getApplicationContext());
		userNameView = (TextView) this.findViewById(R.id.userName);
		passwordView = (TextView) this.findViewById(R.id.password);
		userName = userNameView.getText().toString();
		password = passwordView.getText().toString();

		AuthenticationModel authenticationModel = new AuthenticationModel(
				userName, hashPassword(password));

		dataBase.addToDB(authenticationModel, getContentResolver());
	}

	/*
	 * Metoden hämtar authenticeringsinformationen från databasen
	 */
	public void authenticate(AuthenticationModel authenticationModel) {

		dataBase = Database.getInstance(getApplicationContext());

		listOfAuthenticationModels = dataBase.getAllFromDB(
				new AuthenticationModel(), getContentResolver());
		AuthenticationModel authenticatioReference;
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
	public void sendAuthenticationRequestToServer(View v,
			AuthenticationModel authenticationModel) {

		SocketConnection connection=new SocketConnection();
		connection.addObserver(this);
		connection.authenticate(authenticationModel);
	}

	public void accessGranted() {
		Intent intent = new Intent(this, MainActivity.class);
		intent.putExtra("USER", userName);
		startActivity(intent);
		finish();
	}

	/*
	 * Metoden skapar en hashrepresentation av ett hårdkodat lösenord
	 */
	public void createPassWordHashRepresentation()
			throws NoSuchAlgorithmException {
		String password = "a";
		userNameReference = "fredde";

		AM = new AuthenticationModel(password, userNameReference);

		MessageDigest md = MessageDigest.getInstance("SHA-256");
		md.update(password.getBytes());

		byte byteData[] = md.digest();

		// convert the byte to hex format
		StringBuffer hexString = new StringBuffer();
		for (int i = 0; i < byteData.length; i++) {
			String hex = Integer.toHexString(0xff & byteData[i]);
			if (hex.length() == 1)
				hexString.append('0');
			hexString.append(hex);
		}
		passwordHashReference = hexString.toString();
	}

	public void update(Observable observable, Object data) {
		
	}
}
