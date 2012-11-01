package messageFunction;

import com.example.klien_projekttermin.R;
import com.example.klien_projekttermin.R.id;
import com.example.klien_projekttermin.R.layout;
import com.example.klien_projekttermin.R.menu;

import models.MessageModel;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class CreateNewMessage extends Activity {
	private TextView reciever;
	private TextView message;
	private Button button;
	private Time messageTimeStamp;
	private MessageModel messageObject;
	private Time timeStamp;
	private String sender;
	private String storageFile = "massorMedText.txt";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_create_new_message);
       
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
    	messageObject = new MessageModel(message.getText(),sender, reciever.getText(),"tidsSt�mpel");
    	Toast.makeText(getApplicationContext(),"Meddelandet �r skickat", Toast.LENGTH_LONG).show();
    	
    	//Skicka till kommunikationsmoduloch spara p� databasen.	
    }  
    
    /**
     * Metoden skapar ett meddelandeobjekt av inmatad information och skapar en ny instans av saveMessage-klassen som sparar meddelandet
     * AAVS�NDARE M�STE L�GGAS TILL
     * Problem med timestamp 
     * @param v
     */
    public void saveMessage(View v){
    	messageObject = new MessageModel(message.getText(),sender, reciever.getText(), "tidsSt�mpel");
    	Toast.makeText(getApplicationContext(),"Meddelandet �r sparat", Toast.LENGTH_LONG).show();
    	
    	// Spara meddelandet p� databas
    	
    }
    
   /**
    * Metoden skapar ett meddelandeobjekt av inmatad information och sparar det i utkastmappen p� enheten
    * @param v
    */
    public void cancelMessage(View v){
    	messageObject = new MessageModel(message.getText(),sender, reciever.getText(), "tidsSt�mpel");
    	Toast.makeText(getApplicationContext(),"Meddelandet avbr�ts", Toast.LENGTH_LONG).show();

    	// Spara meddelandet p� databasen och avsluta funktionen

    }
}

