package loginFunction;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.KeyStore.PasswordProtection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.example.klien_projekttermin.R;
import com.example.klien_projekttermin.R.layout;
import com.example.klien_projekttermin.R.menu;

import android.R.id;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class LogInFunction extends Activity {
	private TextView userNameView;
	private TextView passwordView;
	private CharSequence userName;
	private CharSequence password;
	private String userNameString;
	private String passwordHash;
	private String passwordHashReference;


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
	public void logIn(View v) throws NoSuchAlgorithmException, UnsupportedEncodingException{
		userNameView = (TextView) this.findViewById(R.id.userName);
		passwordView = (TextView) this.findViewById(R.id.password);
		userName = userNameView.getText();
		password = passwordView.getText();

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

		passwordHash = hexString.toString();

		Toast.makeText(getApplicationContext(), passwordHash, Toast.LENGTH_SHORT).show();

		
		if(passwordHash.equals(passwordHashReference)){
			Toast.makeText(getApplicationContext(), "JA!", Toast.LENGTH_SHORT).show();

//			finish();
		}
	}

	/*
	 * Metoden skapar en hashrepresentation av ett hårdkodat lösenord
	 */
	public void createPassWordHashRepresentation() throws NoSuchAlgorithmException{
		String password = "fredrik";

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
		
		Toast.makeText(getApplicationContext(), passwordHashReference, Toast.LENGTH_SHORT).show();

	}

	/*
	 * Metoden hashar lösenordet och verifierar hashsträngen mot lösenordsHashen databasen
	 */

	/*
	 * 
	 */
}
