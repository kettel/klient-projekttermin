package com.example.klien_projekttermin;

import java.util.List;

import database.Contact;
import database.DatabaseHandler;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;

public class MainActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("Startar: ", "Main");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    public void testDB(View view){
    	Log.d("Startar: ", "TestDB");
    	testWriteReadToDB((Context)this);
    }
    
    /**
     * Testa att skriva lite skr�pdata till databasen i appen. I detta fall en 
     * improviserad kontakt-databas.
     * 
     * @param context 	Programkontexten s� att r�tt databas anv�nds (allts� den 
     * 					databas som anv�nds f�r denna app)
     * 					
     */
    public void testWriteReadToDB(Context context){
		DatabaseHandler db = new DatabaseHandler(context);
	 
	    /**
	     * CRUD Operations
	     * */
	    // L�gg till kontakter
	    Log.d("Insert: ", "Inserting ..");
	    db.addContact(new Contact("Ravi", "9100000000"));
	    db.addContact(new Contact("Jarmo", "9199999999"));
	    db.addContact(new Contact("Lasse", "9522222222"));
	    db.addContact(new Contact("K�tt och video", "9533333333"));
	
	    // L�s alla kontakter
	    Log.d("Reading: ", "Reading all contacts..");
	    List<Contact> contacts = db.getAllContacts();       
	
	    for (Contact cn : contacts) {
	        String log = "Id: "+cn.getID()+" ,Name: " + cn.getName() + " ,Phone: " + cn.getPhoneNumber();
	        // Skriv kontakter till debug-loggen
	        Log.d("Name: ", log);
	    }
	}
}
