package com.example.klien_projekttermin;


import database.Database;

import models.Assignment;
import models.Contact;
import models.MessageModel;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.View;

public class MainActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    public void testDB(View view){
    	testWriteReadToDB((Context)this);
    }
    
    /**
     * Testa att skriva lite skräpdata till databasen i appen. I detta fall en 
     * improviserad kontakt-databas.
     * 
     * @param context 	Programkontexten så att rätt databas används (alltså den 
     * 					databas som används för denna app)
     * 					
     */
    public void testWriteReadToDB(Context context){
    	Log.d("DB","Börjar att testa");
    	// Skapa en tom database för att skriva godtycklig datatyp (assignment, contact, message) 
    	// till rätt databas
		Database db = new Database();
		Log.d("DB", "Klarade av att skapa en tom databas");
		// Testa contacts
		//Contact testContact = new Contact("Nisse", Long.valueOf(12345), "nallecom","A","A","lirare");
		//db.addToDB(testContact, context);
		//Log.d("DB","Contacts DB size: "+db.getDBCount(testContact, context));
		
		// Testa assignments
		//int w = 100, h = 100;
		//Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
		//Bitmap bmp = Bitmap.createBitmap(w, h, conf);
		Time time = new Time();
		time.setToNow();
		//Assignment testAssignment = new Assignment("uppdrag", Long.valueOf(123456), Long.valueOf(654321), "Mott", "Sandare", "Katt i trad", time,"Status", bmp,"Allgatan 1","Ryd");
		//db.addToDB(testAssignment,context);
		//Log.d("DB","Assignment DB size: "+db.getDBCount(testAssignment, context));
		
		// Testa messages
		MessageModel testMessage = new MessageModel("Hej hej", "Kalle",time.toString());
		db.addToDB(testMessage, context);
		Log.d("DB","Message DB size: " + db.getDBCount(testMessage, context));
	}
}
