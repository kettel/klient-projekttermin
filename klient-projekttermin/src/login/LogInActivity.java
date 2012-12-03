package login;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import models.AuthenticationModel;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
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

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_log_in_function);
		database = Database.getInstance(getApplicationContext());
		Intent intent = getIntent();
		callingactivity = intent.getIntExtra("calling-activity", 0);
		
	}
	@Override
	public void onBackPressed() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    builder.setTitle("Title");
	    builder.setMessage("Vill du logga ut?");
	    builder.setPositiveButton("Ja", new OnClickListener() {
	            public void onClick(DialogInterface dialog, int arg1) {
	                dialog.dismiss();
	                SocketConnection socketConnection=new SocketConnection();
	                socketConnection.logout();
	                /*
	                 * Notify the system to finalize and collect all objects of the app
	                 * on exit so that the virtual machine running the app can be killed
	                 * by the system without causing issues. NOTE: If this is set to
	                 * true then the virtual machine will not be killed until all of its
	                 * threads have closed.
	                 */
	                System.runFinalization();

	                /*
	                 * Force the system to close the app down completely instead of
	                 * retaining it in the background. The virtual machine that runs the
	                 * app will be killed. The app will be completely created as a new
	                 * app in a new virtual machine running in a new process if the user
	                 * starts the app again.
	                 */
	                System.exit(0);
	            }});
	    builder.setNegativeButton("Nej", new OnClickListener() {
	            public void onClick(DialogInterface dialog, int arg1) {
	                dialog.dismiss();
	            }});
	    builder.setCancelable(false);
	    builder.create().show();
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

		tryOnlineLogin(originalModel);
	}

	public void tryOfflineLogin(AuthenticationModel loginInput)
			throws NoSuchAlgorithmException {

		System.out.println("Saker i databasen: "
				+ database.getAllFromDB(loginInput, getContentResolver())
				.size());

		if (database
				.getDBCount(new AuthenticationModel(), getContentResolver()) != 0) {
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
			} else {
				removeLastUserFromDB();
//				tryOnlineLogin(loginInput);
			}
		}

		else {
			System.out.println("Försöker logga in online");
			tryOnlineLogin(loginInput);
		}
	}

	/**
	 * Metoden matchar inloggninguppgifterna mot de godkända kombinationerna som
	 * finns i databasen först och om så inte är fallet försöker den hämta
	 * informationen från servern.
	 */
	private void checkAuthenticity(AuthenticationModel authenticationModel) {
		if (authenticationModel.getUserName().equals(
				originalModel.getUserName())
				&& authenticationModel.isAccessGranted().equals("true")) {
			database.addToDB(authenticationModel, getContentResolver());
			accessGranted();

		} else {
			incorrectLogIn();
		}

	}

	public void incorrectLogIn() {
		numberOfLoginTries--;
		if (numberOfLoginTries == 0) {
			if(database.getDBCount(new AuthenticationModel(), getContentResolver())!=0){
				removeLastUserFromDB();
			}
			finish();
		} else {
			this.runOnUiThread(new Runnable() {

				public void run() {
					Toast toast = Toast.makeText(getApplicationContext(),
							"Felaktigt användarnamn eller lösenord! "
									+ numberOfLoginTries + " försök kvar!",
									Toast.LENGTH_LONG);
					toast.setGravity(Gravity.TOP, 0, 300);
					toast.show();
				}
			});
		}
	}

	public void removeLastUserFromDB() {
		List list = database.getAllFromDB(new AuthenticationModel(),
				getContentResolver());
		System.out.println("DATABASSTORLEK: "+list.size());
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
		connection.addObserver(this);
		connection.authenticate(authenticationModel);
		System.out.println("Skapar en ny ProgressDialog");
		pd = ProgressDialog.show(LogInActivity.this, "", "Loggar in...", true,true);
	}

	public void accessGranted() {
		switch (callingactivity) {
		case ActivityConstants.INACTIVITY:
			break;
		default:
			Intent intent = new Intent(this, MainActivity.class);
			intent.putExtra("USER", userName);
			startActivity(intent);
			break;
		}
		user.setLoggedIn(true);
		finish();
	}

	public void update(Observable observable, Object data) {		
		if (data instanceof AuthenticationModel) {
			this.runOnUiThread(new Runnable() {

				public void run() {
					pd.dismiss();
				}
			});
			checkAuthenticity((AuthenticationModel) data);
		}
		else {
			this.runOnUiThread(new Runnable() {

				public void run() {
					pd.dismiss();
					Toast toast = Toast.makeText(getApplicationContext(),
							"Det gick inte att ansluta till servern!, Försöker logga in offline",
									Toast.LENGTH_LONG);
					toast.setGravity(Gravity.TOP, 0, 300);
					toast.show();
				}
			});
			
			try {
				tryOfflineLogin(originalModel);
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
