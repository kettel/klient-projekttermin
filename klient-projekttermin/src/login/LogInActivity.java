package login;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import models.AuthenticationModel;
import qosManager.QoSManager;
import sip.Darclass;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.klient_projekttermin.ActivityConstants;
import com.klient_projekttermin.MainActivity;
import com.klient_projekttermin.R;
import communicationModule.SocketConnection;

import database.Database;

public class LogInActivity extends Activity implements Observer {

	// Values for email and password at the time of the login attempt.
	private String mEmail;
	private String mPassword;

	// UI references.
	private EditText mEmailView;
	private EditText mPasswordView;
	private View mLoginFormView;
	private View mLoginStatusView;
	private TextView mLoginStatusMessageView;
	// Databas
	private Database database;

	private int numberOfLoginTries = 4;

	private AuthenticationModel originalModel;
	private User user;
	private int callingactivity;
	private QoSManager qosManager;
	public static final int LOGGED_IN_REQ_CODE = 1;
	public static final int SHUT_DOWN = 2;
	public static final int STAY_ALIVE = 3;

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

		// Set up the login form.
		mEmailView = (EditText) findViewById(R.id.userName);
		mEmailView.setText(mEmail);

		mPasswordView = (EditText) findViewById(R.id.password);
		mPasswordView
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					public boolean onEditorAction(TextView textView, int id,
							KeyEvent keyEvent) {
						if (id == R.id.login || id == EditorInfo.IME_NULL) {
							attemptLogin();
							return true;
						}
						return false;
					}
				});

		mLoginFormView = findViewById(R.id.login_form);
		mLoginStatusView = findViewById(R.id.login_status);
		mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);

		findViewById(R.id.sign_in_button).setOnClickListener(
				new View.OnClickListener() {
					public void onClick(View view) {
						attemptLogin();
					}
				});

		// Fråga om appen får vara DeviceAdmin (skärmlås i samtal ni vet..)
		isDeviceManager();
	}

	/**
	 * Fråga om användaren vill tillåta appen att vara DeviceManager (här
	 * istället för i CallDialog)
	 */
	private void isDeviceManager() {
		adminComponent = new ComponentName(LogInActivity.this, Darclass.class);
		devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);

		// Om device-rättigheter inte är instansierade, hämta dem genom en
		// JÄTTEIRRITERANDE ruta..
		if (!devicePolicyManager.isAdminActive(adminComponent)) {
			Intent intent = new Intent(
					DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
			intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,
					adminComponent);
			startActivityForResult(intent, REQUEST_ENABLE);
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
			System.out.println("Login ok!");
			database.addToDB(authenticationModel, getContentResolver());
			accessGranted();

		} else {
			System.out.println("Incorrect login credentials");
			incorrectLogIn();
		}
	}

	public void tryOfflineLogin(AuthenticationModel loginInput) {

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
			}
		} else {
			this.runOnUiThread(new Runnable() {

				public void run() {
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
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == LOGGED_IN_REQ_CODE) {
			if (resultCode == STAY_ALIVE) {
			} else if (resultCode == SHUT_DOWN) {
				finish();
			}
		}
	}

	public void accessGranted() {
		user.setLoggedIn(true);
		runOnUiThread(new Runnable() {

			public void run() {
				mPasswordView.getEditableText().clear();

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
			checkAuthenticity((AuthenticationModel) data);

		} else if (data instanceof String) {
			user.setOnlineConnection(false);
			runOnUiThread(new Runnable() {

				public void run() {
					showProgress(false);
					mPasswordView
							.setError(getString(R.string.error_incorrect_password));
					mPasswordView.requestFocus();
				}
			});

			tryOfflineLogin(originalModel);
		}
	}

	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	public void attemptLogin() {

		// Reset errors.
		mEmailView.setError(null);
		mPasswordView.setError(null);

		// Store values at the time of the login attempt.
		mEmail = mEmailView.getText().toString();
		mPassword = mPasswordView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid password.
		if (TextUtils.isEmpty(mPassword)) {
			mPasswordView.setError(getString(R.string.error_field_required));
			focusView = mPasswordView;
			cancel = true;
		}

		// Check for a valid email address.
		if (TextUtils.isEmpty(mEmail)) {
			mEmailView.setError(getString(R.string.error_field_required));
			focusView = mEmailView;
			cancel = true;
		}

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
			showProgress(true);
			doLogin();
		}
	}

	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mLoginStatusView.setVisibility(View.VISIBLE);
			mLoginStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			mLoginFormView.setVisibility(View.VISIBLE);
			mLoginFormView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginFormView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}

	private void doLogin() {
		try {
			originalModel = new AuthenticationModel(mEmail,
					hashPassword(mPassword));
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		user.setAuthenticationModel(originalModel);

		tryOnlineLogin(originalModel);
	}
}
