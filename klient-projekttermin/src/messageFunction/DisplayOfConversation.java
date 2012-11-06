package messageFunction;

import java.util.HashSet;
import java.util.List;

import models.MessageModel;
import models.ModelInterface;

import com.example.klien_projekttermin.MainActivity;
import com.example.klien_projekttermin.R;
import com.example.klien_projekttermin.R.layout;
import com.example.klien_projekttermin.R.menu;

import database.Database;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class DisplayOfConversation extends Activity {

	private ListView ListOfConversationInputs;
	private String [] conversationContent;
	private String chosenContact;

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

	@Override
	public void onStart(){
		super.onStart();
		addOnClickListener();
		addOnLongClickListener();
	}

	/*
	 * Tillsätt lyssnare i meddelandelistan som lyssnar efter tryckningar på listobjekt
	 */
	public void addOnClickListener(){
		ListOfConversationInputs.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				Toast.makeText(getApplicationContext(),conversationContent[position], Toast.LENGTH_LONG).show();
				openMessage(conversationContent[position]);
			}
		});
	}

	/*
	 * Tillsätt lyssnare i meddelandelistan som lyssnar efter långa tryckningar på listobjekt
	 */
	public void addOnLongClickListener(){
		//Skapar en lyssnare som lyssnar efter långa intryckningar 
		ListOfConversationInputs.setOnItemLongClickListener(new OnItemLongClickListener() {

			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				showEraseOption();
				return true;
			}
		});	
	}


	/*
	 * Metoden skapar en listView över alla meddelanden som skickats och tagits emot. Dessa efterfrågas från databasen.
	 * Om ett meddelande klickas på så kallar metoden på en ny metod som startar en ny aktivitet där det valda meddelandet visas.
	 */
	public void loadConversation(String contact){

		conversationContent = getInformationFromDatabase(contact);

		// First paramenter - Context
		// Second parameter - Layout for the row
		// Third parameter - ID of the TextView to which the data is written
		// Forth - the Array of data
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, android.R.id.text1, conversationContent);

		// Assign adapter to ListView
		ListOfConversationInputs.setAdapter(adapter); 
	}

	/*
	 * Metoden skapar en dialogruta som frågar användaren om denne vill ta bort en konversation
	 * Metoden ger också användaren två valmöjligheter, JA eller Avbryt
	 */
	public void showEraseOption(){

		AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.setTitle("RADERA?");
		alertDialog.setMessage("Vill du ta bort meddelandet?");
		alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "JA", new DialogInterface.OnClickListener() {

			//Om användaren trycker på ja så körs metoden eraseMessage()
			public void onClick(DialogInterface dialog, int which) {
				eraseMessage();
			}
		});
		alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "AVBRYT", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				//Gör inget
			}
		});
		alertDialog.show();

	}

	public void eraseMessage(){
			// Peka på ett objekt i databasen och ta bort det.
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

	public String[] getInformationFromDatabase(String Contact){
		Database dataBase = new Database();
		MessageModel messageModel;
		List<ModelInterface> conversationContent;
		String[] arrayOfConversationContent;
		Object[] objectsInSetOfConversations;
		HashSet<String> setOfConversations = new HashSet<String>();


		//Hämtar en lista med alla messagemodels som finns i databasen.
		conversationContent = dataBase.getAllFromDB(new MessageModel(),getApplicationContext());

		//		Den listview som kontakterna kommerpresenteras i
		//		Toast.makeText(getApplicationContext(),"HÄR ÄR DU", Toast.LENGTH_LONG).show();

		ListOfConversationInputs = (ListView) findViewById(R.id.displayOfConversation);
		//		//String array �ver anv�ndare

		// Sorterar ut meddelanden kopplade till den person man tryckt på.
		for (int i = 0; i < conversationContent.size(); i++) {
			messageModel = (MessageModel) conversationContent.get(i);

			if(messageModel.getReciever().toString().equals(Contact)){
				setOfConversations.add(messageModel.getMessageContent().toString());
			}
		}

		//Skapar en string[] som är lika lång som listan som hämtades.
		arrayOfConversationContent = new String[setOfConversations.size()];
		objectsInSetOfConversations = new Object[setOfConversations.size()];
		objectsInSetOfConversations = setOfConversations.toArray();

		for (int i = 0; i < objectsInSetOfConversations.length; i++) {
			arrayOfConversationContent[i] = objectsInSetOfConversations[i].toString();
		}

		return arrayOfConversationContent;
	}


}
