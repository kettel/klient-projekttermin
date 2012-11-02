package messageFunction;

import com.example.klien_projekttermin.R;
import com.example.klien_projekttermin.R.layout;
import com.example.klien_projekttermin.R.menu;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class DisplayOfConversation extends Activity {

	private ListView ListOfConversationInputs;
	private String [] converationContent;
	private String chosenContact;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_of_conversation);

		//Metoden testar om nÂgonting skickades med frÂn Inbox och skriver i sÂ fall ut det till str‰ngen chosenContact
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			chosenContact = extras.getString("ChosenContact");
		}	 
		loadConversation(chosenContact);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_display_of_conversation, menu);
		return true;
	}

	/*
	 * Metoden skapar en listView ˆver alla meddelanden som skickats och tagits emot. Dessa efterfrÂgas frÂn databasen.
	 * Om ett meddelande klickas pÂ sÂ kallar metoden pÂ en ny metod som startar en ny aktivitet d‰r det valda meddelandet visas.
	 */
	public void loadConversation(String contact){
		
		//Be om lista ˆver meddelanden som skickats och tagits emot frÂn en specifik person.
		//Arraylist med meddelandena returneras
		
		//Listan ˆver skickade och mottagna meddelanden.
		ListOfConversationInputs = (ListView) findViewById(R.id.displayOfConversation);
		
		// En array som kommer frÂn servern med det skickade och mottagna meddelandena.
		converationContent = new String[] { "÷÷L", "Var ‰r jag?", "vem ‰‰‰‰‰‰‰‰‰‰‰‰‰‰‰‰‰‰‰‰‰‰‰‰‰‰‰‰‰‰‰‰‰‰‰‰‰‰‰r du?",
				"Erik L", "Erik K", "Rasmus", "Niko", "Nicke",
				"FEEEEZZzT", "KODAKODAKODA","Steffe","Bengan","Glenn","Alban","Laban" };

		// First paramenter - Context
		// Second parameter - Layout for the row
		// Third parameter - ID of the TextView to which the data is written
		// Forth - the Array of data
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, android.R.id.text1, converationContent);

		// Assign adapter to ListView
		ListOfConversationInputs.setAdapter(adapter); 
		ListOfConversationInputs.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				Toast.makeText(getApplicationContext(),converationContent[position], Toast.LENGTH_LONG).show();

				openMessage(converationContent[position]);
			}
		});
	}

	/*
	 * Metoden skapar en ny aktivitet som visar ett valt meddelanden.
	 * Metoden skickar med meddelandetexten och kontaktens kontaktens namn till den nya aktiviteten.
	 */
	public void openMessage(String specifiedMessage){
		Intent intent = new Intent(this, DisplayMessage.class);
		intent.putExtra("specifiedMessage", specifiedMessage);
		intent.putExtra("correspondant", chosenContact);
		startActivity(intent);
	}
}
