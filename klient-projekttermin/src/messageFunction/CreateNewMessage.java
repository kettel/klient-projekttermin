package messageFunction;

import com.example.klien_projekttermin.R;

import database.Database;

import models.MessageModel;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

public class CreateNewMessage extends Activity {
	private TextView reciever;
	private TextView message;
	private Button button;
	private MessageModel messageObject;
	private String sender;
	private Database dataBase;
	private String user = "Steffe";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_create_new_message);
       dataBase = new Database();
       
       Bundle extras = getIntent().getExtras();
       if (extras != null) {
    	    sender = extras.getString("USER");
    	}
       
       button = (Button) this.findViewById(R.id.button1);
       message = (TextView) this.findViewById(R.id.editText2);
       reciever = (TextView) this.findViewById(R.id.editText1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_create_new_message, menu);
        return true;
    }
    
    /**
     * Metoden skapar ett meddelande objekt och skickar det vidare till komunikationsmodulen. Metoden sparar ocks� de skapade meddelandena i skickat mappen
     * @param v
     */
    public void sendMessage(View v){
    	String recievingContact = reciever.getText().toString();
    	InputMethodManager inm = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
    	messageObject = new MessageModel(user+": "+message.getText().toString(), recievingContact); 

		//Sparar messageObject i databasen
		dataBase.addToDB(messageObject,getApplicationContext());
		//Gömmer tangentbordet på skärmen
		inm.hideSoftInputFromWindow(message.getWindowToken(), 0);
		//Tar bort texten ur textrutan
		message.getEditableText().clear();
	
		//Skicka till kommunikationsmodulen
		
		finish();
		
		//Öppnar konversatinsvyn för kontakten man skickade till 
		Intent intent = new Intent(this, DisplayOfConversation.class);
		intent.putExtra("ChosenContact", recievingContact);
		startActivity(intent);
    }  
    
   /*
    * Metoden ger användaren valet att avsluta meddelandeskaparfunktionen.
    * Metoden skapar en AlertDialog-ruta och låter användaren svara på frågan om att avsluta
    * Om användaren trycker ja avslutas aktiviteten, om användaren trycker nej stängs bara 
    * AlertDialog-rutan ner.
    * 
    */
    public void cancelMessage(View v){
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
}

