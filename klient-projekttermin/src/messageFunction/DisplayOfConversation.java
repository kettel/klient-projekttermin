package messageFunction;

import java.util.List;

import models.MessageModel;
import models.ModelInterface;

import com.example.klien_projekttermin.R;
import com.example.klien_projekttermin.R.layout;
import com.example.klien_projekttermin.R.menu;

import database.Database;

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
	private List<ModelInterface> conversationContent;
	private String [] arrayOfconverationContent;
	private String chosenContact;
	private Database dataBase;
	private MessageModel messageModel;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_of_conversation);

		//Metoden testar om någonting skickades med från Inbox och skriver i så fall ut det till strängen chosenContact
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
	 * Metoden skapar en listView över alla meddelanden som skickats och tagits emot. Dessa efterfrågas från databasen.
	 * Om ett meddelande klickas på så kallar metoden på en ny metod som startar en ny aktivitet där det valda meddelandet visas.
	 */
	public void loadConversation(String contact){
		dataBase = new Database();
		conversationContent = dataBase.getAllFromDB(new MessageModel(),getApplicationContext());
		arrayOfconverationContent = new String[conversationContent.size()];

		
		//Be om lista �ver personer som man har konverserat med.
		//Arraylist med personerna returneras

		//Den listview som kontakterna kommerpresenteras i
		//		Toast.makeText(getApplicationContext(),"HÄR ÄR DU", Toast.LENGTH_LONG).show();

				ListOfConversationInputs = (ListView) findViewById(R.id.displayOfConversation);
		//		//String array �ver anv�ndare

		for (int i = 0; i < conversationContent.size(); i++) {
				messageModel = (MessageModel) conversationContent.get(i);
				arrayOfconverationContent[i] = messageModel.getMessageContent().toString();
		}

		
		//		// First paramenter - Context
		//		// Second parameter - Layout for the row
		//		// Third parameter - ID of the TextView to which the data is written
		//		// Forth - the Array of data
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
						android.R.layout.simple_list_item_1, android.R.id.text1, arrayOfconverationContent);
		
		//		// Assign adapter to ListView
				ListOfConversationInputs.setAdapter(adapter); 
		ListOfConversationInputs.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				Toast.makeText(getApplicationContext(),arrayOfconverationContent[position], Toast.LENGTH_LONG).show();

				openMessage(arrayOfconverationContent[position]);
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
