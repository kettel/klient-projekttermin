package com.example.klien_projekttermin;


import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;

public class StartsidaMeddelanden extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_startsida_meddelanden);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_startsida_meddelanden, menu);
		return true;    
	}

	/**
	 * Metoden startar en ny aktivitet där meddelanden går att skapa
	 * @param v
	 */
	public void openMessageCreator(View v){
		Intent intent = new Intent(this, CreateNewMessage.class);
        startActivity(intent);
	}
	
	/**
	 * Metoden öppnar en ny aktivitet som heter inkorgen
	 * @param v
	 */
	public void openInbox(View v){
		Intent intent = new Intent(this, Inbox.class);
        startActivity(intent);
	}
	
	/**
	 * Metoden öppnar en ny aktivitet som heter utkast
	 * @param v
	 */
	public void openDraft(View v){

	}
	
	/**
	 * Metoden öppnar en ny aktivitet som heter sparade meddelanden
	 * @param v
	 */
	public void openSavedMessages(View v){

	}
}
