package com.example.klien_projekttermin;

import models.MessageModel;
import android.app.Activity;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class CreateNewMessage extends Activity {
	private TextView reciever;
	private TextView message;
	private Button button;
	private Time messageTimeStamp;
	private MessageModel messageObject;
	private Time timeStamp;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_message);
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
     * Metoden skapar ett meddelande objekt och skickar det vidare till komunikationsmodulen. Metoden sparar också de skapade meddelandena i skickat mappen
     * @param v
     */
    public void sendMessage(View v){
//    	System.out.println("Skickade");
    	message.setText("Skickat");
//    	messageObject = new MessageModel(message.getText(), reciever.getText(), messageTimeStamp.toString());
    	
    	//Lägg till till i databas
    	
    }  
    
    /**
     * Metoden skapar ett meddelandeobjekt av inmatad information och sparar det i sparade meddelande mappen på enheten
     * @param v
     */
    public void saveMessage(View v){
    	message.setText("sparat");
//    	System.out.println("Sparade");
    }
    
   /**
    * Metoden skapar ett meddelandeobjekt av inmatad information och sparar det i utkastmappen på enheten
    * @param v
    */
    public void cancelMessage(View v){
    	message.setText("Avbrutit");
//    	System.out.println("Avbröt");
    }
}

