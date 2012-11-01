package messageFunction;


import com.example.klien_projekttermin.R;
import com.example.klien_projekttermin.R.layout;
import com.example.klien_projekttermin.R.menu;

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

	/*
	 * Skapar ett nytt intent och startar aktiviteten CreateNewMessage
	 * Metoden skickar också med namnet på den användare som är inloggad på enheten. 
	 */
	public void openMessageCreator(View v){
		Intent intent = new Intent(this, CreateNewMessage.class);
		intent.putExtra("USER", "ANVÄNDARE1");
        startActivity(intent);
	}
	
	/*
	 * Metoden startar aktiviteten Inbox
	 */
	public void openInbox(View v){
		Intent intent = new Intent(this, Inbox.class);
        startActivity(intent);
	}
	
	/*
	 * Metoden startad aktiviteten Draft
	 */
	public void openDraft(View v){

	}
	
	/*
	 * Metoden startar aktiviteten savedMessages
	 */
	public void openSavedMessages(View v){

	}
}
