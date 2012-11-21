package loginFunction;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import models.AuthenticationModel;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.klient_projekttermin.MainActivity;
import com.klient_projekttermin.R;
import communicationModule.CommunicationService;
import communicationModule.CommunicationService.CommunicationBinder;

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
	private boolean communicationBond = false;

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
		
		sendAuthenticationRequestToServer(v, authenticationModel);
		passwordView.getEditableText().clear();
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
		
		
		if (authenticationModel.getPasswordHash().equals(passwordHashReference)&&authenticationModel.getUserName().equals(userNameReference)) {
			accessGranted();
		}
		
		else {
			// get your custom_toast.xml ayout
//			LayoutInflater inflater = getLayoutInflater();
//
//			View layout = inflater.inflate(R.layout.activity_log_in_function,(ViewGroup) findViewById(R.id.LogInFunction));
//			
//			Toast toast = new Toast(getApplicationContext());
			Toast.makeText(getApplicationContext(), "Användarnamn eller lösenord är felaktigt, försök igen!", Toast.LENGTH_SHORT).show();
//			toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
//			toast.setView(layout);
//			toast.show();
		}
	}
		
	public void accessGranted(){
		Intent intent = new Intent(this, MainActivity.class);
		intent.putExtra("USER", userName);
		startActivity(intent);
	}
	
	/*
	 * Metoden skapar en hashrepresentation av ett hårdkodat lösenord
	 */
	public void createPassWordHashRepresentation() throws NoSuchAlgorithmException{
		String password = "fredrik";
		userNameReference = "A";
		
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
