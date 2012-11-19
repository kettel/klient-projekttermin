package messageFunction;

import models.MessageModel;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import com.example.klien_projekttermin.R;
import com.example.klien_projekttermin.database.Database;
import communicationModule.CommunicationService;
import communicationModule.CommunicationService.CommunicationBinder;

import contacts.ContactsCursorAdapter;

public class CreateMessage extends Activity {
	private AutoCompleteTextView reciever;
	private EditText message;
	private MessageModel messageObject;
	private String messageContent;
	private Database dataBase;
	private String user;
	private CommunicationService communicationService;
	private boolean communicationBond = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_new_message);
		dataBase = Database.getInstance(getApplicationContext());

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			user = extras.getString("USER");
			messageContent = extras.getString("MESSAGE");
		}

		Intent intent = new Intent(this, CommunicationService.class);
		bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

		message = (EditText) this.findViewById(R.id.editText2);
		reciever = (AutoCompleteTextView) this.findViewById(R.id.receiver);

		reciever.setAdapter(new ContactsCursorAdapter(getApplicationContext(),
				null, 0));
		message.setText(messageContent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_create_new_message, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		sendMessage(item);
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			if (message.getText()==null) {
				showAlertMessage();
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * Metoden skapar ett meddelande objekt och skickar det vidare till
	 * komunikationsmodulen. Metoden sparar ocks� de skapade meddelandena i
	 * skickat mappen
	 * 
	 * @param v
	 */
	public boolean sendMessage(MenuItem v){
		//communicationService.setContext(getApplicationContext());
		String recievingContact = reciever.getText().toString();
		messageObject = new MessageModel(message.getText().toString(),
				recievingContact, user);


		// Sparar messageObject i databasen
		dataBase.addToDB(messageObject, getContentResolver());
		// Skicka till kommunikationsmodulen


		if (communicationBond) {
			communicationService.sendMessage(messageObject);
		}

		finish();

		// Öppnar konversatinsvyn för kontakten man skickade till
		Intent intent = new Intent(this, DisplayOfConversation.class);
		intent.putExtra("ChosenContact", recievingContact);
		intent.putExtra("USER", user);
		startActivity(intent);
		return true;
	}

	public void showAlertMessage() {
		AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.setTitle("Avsluta?");
		alertDialog.setMessage("Vill du avsluta?");
		alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "JA",
				new DialogInterface.OnClickListener() {

					// Om användaren trycker på ja så körs metoden
					// eraseMessage()
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				});
		alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "NEJ",
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						// Gör inget
					}
				});
		alertDialog.show();
	}

	private ServiceConnection serviceConnection = new ServiceConnection() {

		public void onServiceConnected(ComponentName className, IBinder service) {
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
