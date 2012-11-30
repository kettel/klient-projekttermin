package loginFunction;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import models.AuthenticationModel;
import models.ModelInterface;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
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
	private Database database;

	private int numberOfLoginTries = 4;

	private AuthenticationModel originalModel;
	private AuthenticationModel au;
	private ProgressDialog pd;
	private User user;

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

		originalModel = new AuthenticationModel(userName,
				hashPassword(password));

		user = User.getInstance();
		user.setAuthenticationModel(originalModel);

		tryOfflineLogin(originalModel);
	}

	public void tryOfflineLogin(AuthenticationModel loginInput) throws NoSuchAlgorithmException{

		System.out.println("Saker i databasen: "+database.getAllFromDB(loginInput, getContentResolver()).size());

		if(database.getDBCount(new AuthenticationModel(), getContentResolver())!=0){
			System.out.println("Försöker logga in offline");

			List modelList = database.getAllFromDB(loginInput, getContentResolver());
			AuthenticationModel loadedModel = (AuthenticationModel) modelList.get(0);

			if(loadedModel.getUserName().equals(loginInput.getUserName())){
				if(loadedModel.getPasswordHash().equals(loginInput.getPasswordHash())){
					accessGranted();
				}
				else{
					incorrectLogIn();
				}
			}
			else{
				removeLastUserFromDB();
				sendAuthenticationRequestToServer(loginInput);
			}
		}

		else{
			System.out.println("Försöker logga in online");
			database.addToDB(loginInput, getContentResolver());
			sendAuthenticationRequestToServer(loginInput);
		}
	}

	/**
	 * Metoden matchar inloggninguppgifterna mot de godkända kombinationerna som
	 * finns i databasen först och om så inte är fallet försöker den hämta
	 * informationen från servern.
	 */
	private void checkAuthenticity(AuthenticationModel authenticationModel) {
		pd.dismiss();

		if (authenticationModel.getUserName().equals(
				originalModel.getUserName())
				&& authenticationModel.isAccessGranted().equals("true")) {

			accessGranted();

		} else if (authenticationModel.getUserName().equals(
				originalModel.getUserName())
				&& authenticationModel.isAccessGranted().equals("false")) {
			incorrectLogIn();
		}

	}

	public void incorrectLogIn() {
		numberOfLoginTries--;
		if (numberOfLoginTries == 0) {
			removeLastUserFromDB();
			finish();
		} else {
			Toast toast = Toast.makeText(getApplicationContext(),
					"Felaktigt användarnamn eller lösenord! "
							+ numberOfLoginTries + " försök kvar!",
							Toast.LENGTH_LONG);
			toast.setGravity(Gravity.TOP, 0, 300);
			toast.show();
		}
	}

	public void removeLastUserFromDB(){
		List list = database.getAllFromDB(new AuthenticationModel(), getContentResolver());
		database.deleteFromDB((AuthenticationModel) list.get(0), getContentResolver());
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
	public void sendAuthenticationRequestToServer(AuthenticationModel authenticationModel){

		SocketConnection connection = new SocketConnection();
		connection.addObserver(this);
		connection.authenticate(authenticationModel);
		pd = ProgressDialog.show(LogInFunction.this, "", "Loggar in...", true,
				false);
	}

	public void accessGranted() {
		Intent intent = new Intent(this, MainActivity.class);
		intent.putExtra("USER", userName);
		startActivity(intent);
		finish();
	}

	public void update(Observable observable, Object data) {
		if (data instanceof AuthenticationModel) {
			System.out.println((AuthenticationModel) data);
			database.addToDB((AuthenticationModel) data, getContentResolver());
			checkAuthenticity((AuthenticationModel) data);
		}
	}

}
