package messageFunction;

import java.util.List;

import com.example.klien_projekttermin.R;
import communicationModule.CommunicationService;
import communicationModule.CommunicationService.CommunicationBinder;

import database.Database;

import models.MessageModel;
import models.ModelInterface;
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
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class CreateMessage extends Activity {
	private TextView reciever;
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
		System.out.println("Nr 1");
		setContentView(R.layout.activity_create_new_message);
		dataBase = new Database();

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			user = extras.getString("USER");
			messageContent = extras.getString("MESSAGE");
		}
		
		Intent intent = new Intent(this, CommunicationService.class);
		bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

		message = (TextView) this.findViewById(R.id.editText2);
		reciever = (TextView) this.findViewById(R.id.editText1);
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
			showAlertMessage();
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

	/*
	 * Metoden ger användaren valet att avsluta meddelandeskaparfunktionen.
	 * Metoden skapar en AlertDialog-ruta och låter användaren svara på frågan om att avsluta
	 * Om användaren trycker ja avslutas aktiviteten, om användaren trycker nej stängs bara 
	 * AlertDialog-rutan ner.
	 * 
	 */
	public void cancelButton(View v){
		showAlertMessage();
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

