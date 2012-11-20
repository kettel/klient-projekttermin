package loginFunction;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.KeyStore.PasswordProtection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import messageFunction.DisplayOfConversation;
import models.AuthenticationModel;
import models.MessageModel;
import models.ModelInterface;

import com.example.klien_projekttermin.MainActivity;
import com.example.klien_projekttermin.R;
import com.example.klien_projekttermin.R.layout;
import com.example.klien_projekttermin.R.menu;
import com.example.klien_projekttermin.database.Database;

import communicationModule.CommunicationService;
import communicationModule.CommunicationService.CommunicationBinder;

import android.R.id;
import android.os.Bundle;
import android.os.IBinder;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class LogInFunction extends Activity {
	private TextView userNameView;
	private TextView passwordView;
	private String userName;
	private String password;
	private String passwordHashReference;
	private String userNameReference;
	private Boolean isAccessGranted = false;
	private CommunicationService communicationService;
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
		//		Intent intent = new Intent(this, CommunicationService.class);
		//		bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_log_in_function, menu);
		return true;
	}

	/*
	 * Metoden hämtar data från textfälten i inloggningsfönstret
	 */
	public void logIn(View v) throws NoSuchAlgorithmException, UnsupportedEncodingException{
		userNameView = (TextView) this.findViewById(R.id.userName);
		passwordView = (TextView) this.findViewById(R.id.password);
		userName = userNameView.getText().toString();
		password = passwordView.getText().toString();

		AuthenticationModel authenticationModel = new AuthenticationModel(userName, hashPassword(password));


//				authenticate(authenticationModel);

				sendAuthenticationRequestToServer(v, authenticationModel);
				passwordView.getEditableText().clear();
	}

	/*
	 * Metoden skapar ett användar inlogg
	 */
	public void createUser(View v) throws NoSuchAlgorithmException{
		dataBase = Database.getInstance(getApplicationContext());
		userNameView = (TextView) this.findViewById(R.id.userName);
		passwordView = (TextView) this.findViewById(R.id.password);
		userName = userNameView.getText().toString();
		password = passwordView.getText().toString();

		AuthenticationModel authenticationModel = new AuthenticationModel(userName, hashPassword(password));

		dataBase.addToDB(authenticationModel, getContentResolver());
	}

	/*
	 * Metoden hämtar authenticeringsinformationen från databasen
	 */
	public void authenticate(AuthenticationModel authenticationModel){

		dataBase = Database.getInstance(getApplicationContext());

		listOfAuthenticationModels = dataBase.getAllFromDB(new AuthenticationModel(), getContentResolver());
		AuthenticationModel authenticatioReference;

//		for (int i = 0; i < listOfAuthenticationModels.size(); i++) {
//			authenticatioReference = (AuthenticationModel) listOfAuthenticationModels.get(i);
//
//			if(authenticatioReference.getUserName().equals(authenticationModel.getUserName())&&
//					authenticatioReference.getPasswordHash().equals(authenticationModel.getPasswordHash())){
//				accessGranted();
//			}
//		}
//		//Visas om fel lösenord eller användarnamn skrivs in.
//		Toast.makeText(getApplicationContext(), "Användarnamn eller lösenord är felaktigt, försök igen!", Toast.LENGTH_SHORT).show();
	}
	/*
	 * Metoden skapar en hashrepresentation av de inmatade lösenordet med hjälp av SHA-2
	 */
	public String hashPassword(String password) throws NoSuchAlgorithmException{

		MessageDigest md = MessageDigest.getInstance("SHA-256");
		md.update(password.toString().getBytes());

		byte byteData[] = md.digest();

		//convert the byte to hex format method 2
		StringBuffer hexString = new StringBuffer();
		for (int i=0;i<byteData.length;i++) {
			String hex=Integer.toHexString(0xff & byteData[i]);
			if(hex.length()==1) hexString.append('0');
			hexString.append(hex);
		}
		return hexString.toString();
	}

	/*
	 * Metoden skickar iväg autenticeringsförfrågan till servern
	 */
	public void sendAuthenticationRequestToServer(View v, AuthenticationModel authenticationModel){

		//		if(communicationBond){
		//			communicationService.sendAuthenticationModel(authenticationModel);
		//		}
		sendAuthenticationRequestToLocalDatabase(v, authenticationModel);
	}

	/*
	 * Metoden authenticerar användaren mot den lokala databasen
	 */
	private void sendAuthenticationRequestToLocalDatabase(View v, AuthenticationModel authenticationModel){

	

		
		if (authenticationModel.getUserName().toString().equals(userNameReference)&&authenticationModel.getPasswordHash().equals(passwordHashReference)) {
			accessGranted();
		}

		else {
			Toast.makeText(getApplicationContext(), "Användarnamn eller lösenord är felaktigt, försök igen!", Toast.LENGTH_SHORT).show();
		}
	}

	public void accessGranted(){
		Intent intent = new Intent(this, MainActivity.class);
		intent.putExtra("USER", userName);
		startActivity(intent);
		finish();
	}

	/*
	 * Metoden skapar en hashrepresentation av ett hårdkodat lösenord
	 */
	public void createPassWordHashRepresentation() throws NoSuchAlgorithmException{
		String password = "a";
		userNameReference = "fredde";

		AM = new AuthenticationModel(password, userNameReference);

		MessageDigest md = MessageDigest.getInstance("SHA-256");
		md.update(password.getBytes());

		byte byteData[] = md.digest();

		//convert the byte to hex format
		StringBuffer hexString = new StringBuffer();
		for (int i=0;i<byteData.length;i++) {
			String hex=Integer.toHexString(0xff & byteData[i]);
			if(hex.length()==1) hexString.append('0');
			hexString.append(hex);
		}
		passwordHashReference = hexString.toString();
	}

	private ServiceConnection serviceConnection = new ServiceConnection() {

		public void onServiceConnected(ComponentName className,IBinder service) {
			System.out.println("OnServiceConnection");
			CommunicationBinder binder = (CommunicationBinder) service;
			communicationService = binder.getService();
			communicationBond = true;
		}
		public void onServiceDisconnected(ComponentName arg0) {
			communicationBond = false;
		}
	};
}
