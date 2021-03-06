package messageFunction;

import login.User;
import models.MessageModel;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import com.klient_projekttermin.ActivityConstants;
import com.klient_projekttermin.SecureActivity;
import com.klient_projekttermin.R;
import communicationModule.SocketConnection;

import contacts.ContactsBookActivity;
import contacts.ContactsCursorAdapter;
import database.Database;

public class CreateMessage extends SecureActivity {
	private AutoCompleteTextView reciever;
	private EditText message;
	private MessageModel messageObject;
	private String messageContent;
	private Database dataBase;
	private String currentUser;
	private int caller;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_new_message);
		dataBase = Database.getInstance(getApplicationContext());

		User user = User.getInstance();
		currentUser = user.getAuthenticationModel().getUserName();
		
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			caller = extras.getInt("calling-activity");
			messageContent = extras.getString("MESSAGE");
		}

		message = (EditText) this.findViewById(R.id.message);
		reciever = (AutoCompleteTextView) this.findViewById(R.id.receiver);

		switch (caller) {
		case ActivityConstants.ADD_CONTACT_TO_MESSAGE:
			String name = extras.getString(ContactsBookActivity.contact);
			reciever.setText(name);
			break;
		default:
			break;
		}

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
			if (message.getText() == null) {
				showAlertMessage();
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	/*
	 * Metoden skapar ett meddelande objekt och skickar det vidare till
	 * komunikationsmodulen. Metoden sparar ocks� de skapade meddelandena i
	 * skickat mappen
	 */
	public boolean sendMessage(MenuItem v) {
		String recievingContact = reciever.getText().toString();
		messageObject = new MessageModel(message.getText().toString(),
				recievingContact, currentUser);

		// Sparar messageObject i databasen
		dataBase.addToDB(messageObject, getContentResolver());
		// Skicka till kommunikationsmodulen

		SocketConnection connection = new SocketConnection();
		connection.sendModel(messageObject);

		finish();

		// Öppnar konversatinsvyn för kontakten man skickade till
		Intent intent = new Intent(this, DisplayOfConversation.class);
		intent.putExtra("ChosenContact", recievingContact);
		startActivity(intent);
		return true;
	}

	public void showAlertMessage() {
		AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.setTitle("Avsluta?");
		alertDialog.setMessage("Vill du avsluta?");
		alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "JA",
				new DialogInterface.OnClickListener() {

					// Om användaren trycker på ja så körs metoden
					// eraseMessage()
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				});
		alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "NEJ",
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						// Gör inget
					}
				});
		alertDialog.show();
	}
}
