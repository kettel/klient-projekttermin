package messageFunction;

import models.Contact;
import models.MessageModel;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.Bundle;
import android.os.IBinder;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.example.klien_projekttermin.R;
import com.example.klien_projekttermin.databaseProvider.Database;
import com.example.klien_projekttermin.databaseProvider.DatabaseContentProviderContacts;
import communicationModule.CommunicationService;
import communicationModule.CommunicationService.CommunicationBinder;

import contacts.ContactsCursorAdapter;

public class CreateMessage extends Activity {
	private AutoCompleteTextView reciever;
	private TextView message;
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
		Contact c=new Contact("eric");
		dataBase.addToDB(c, getApplicationContext());
		c=new Contact("erica");
		dataBase.addToDB(c, getApplicationContext());
		System.out.println("äre någeeeeeee "+dataBase.getDBCount(new Contact(), getApplicationContext()));
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			user = extras.getString("USER");
			messageContent = extras.getString("MESSAGE");
		}

		Intent intent = new Intent(this, CommunicationService.class);
		bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

		message = (TextView) this.findViewById(R.id.message);
		reciever = (AutoCompleteTextView) this.findViewById(R.id.receiver);
		Cursor cu = this.getContentResolver().query(
				DatabaseContentProviderContacts.CONTENT_URI, null,
				"_id IS NOT null", null, null);
		System.out.println("finns de någe? "+cu.moveToNext());
		reciever.setAdapter(new ContactsCursorAdapter(getApplicationContext(), cu, 0));
		cu.close();
		message.setText(messageContent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_create_new_message, menu);
		return true;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			if (!message.getText().equals("")) {
				showAlertMessage();
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * Metoden skapar ett meddelande objekt och skickar det vidare till komunikationsmodulen. Metoden sparar ocks� de skapade meddelandena i skickat mappen
	 * @param v
	 */
	public void sendMessage(View v){
		String recievingContact = reciever.getText().toString();
		InputMethodManager inm = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
		messageObject = new MessageModel(message.getText().toString(), recievingContact, user); 

		//Sparar messageObject i databasen
		dataBase.addToDB(messageObject,getApplicationContext());
		//Skicka till kommunikationsmodulen

		if(communicationBond){
			communicationService.sendMessage(messageObject);
		}

		finish();

		//Öppnar konversatinsvyn för kontakten man skickade till 
		Intent intent = new Intent(this, DisplayOfConversation.class);
		intent.putExtra("ChosenContact", recievingContact);
		intent.putExtra("USER", user);
		startActivity(intent);
	}		

	public void showAlertMessage(){
		AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.setTitle("Avsluta?");
		alertDialog.setMessage("Vill du avsluta?");
		alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "JA", new DialogInterface.OnClickListener() {

			//Om användaren trycker på ja så körs metoden eraseMessage()
			public void onClick(DialogInterface dialog, int which) {
				finish();
			}
		});
		alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "NEJ", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				//Gör inget
			}
		});
		alertDialog.show();
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

