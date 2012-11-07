package messageFunction;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import models.MessageModel;
import models.ModelInterface;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.klien_projekttermin.R;

import database.Database;

public class DisplayOfConversation extends Activity {

	private ListView listViewOfConversationInputs;
	private TextView message;
	private List<ModelInterface> listOfMassageModels;
	private String [] conversationContentArray;
	private HashMap<String, Long> messageAndIdMap = new HashMap<String, Long>();
	private String chosenContact;
	private Database dataBase;
	private String user = "Steffe";
	private MessageModel messageObject;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_of_conversation);

		message = (TextView) this.findViewById(R.id.messageBox);

		dataBase = new Database();
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
		listViewOfConversationInputs.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				openMessage(conversationContentArray[position]);
			}
		});
	}

	/*
	 * Tillsätt lyssnare i meddelandelistan som lyssnar efter långa tryckningar på listobjekt
	 */
	public void addOnLongClickListener(){
		//Skapar en lyssnare som lyssnar efter långa intryckningar 
		listViewOfConversationInputs.setOnItemLongClickListener(new OnItemLongClickListener() {

			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				showEraseOption(position);
				return true;
			}
		});	
	}

	/*
	 * Metoden skapar en listView över alla meddelanden som skickats och tagits emot. Dessa efterfrågas från databasen.
	 * Om ett meddelande klickas på så kallar metoden på en ny metod som startar en ny aktivitet där det valda meddelandet visas.
	 */
	public void loadConversation(String contact){

		conversationContentArray = getInformationFromDatabase(contact);

		// First paramenter - Context
		// Second parameter - Layout for the row
		// Third parameter - ID of the TextView to which the data is written
		// Forth - the Array of data
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, android.R.id.text1, conversationContentArray);

		// Assign adapter to ListView
		listViewOfConversationInputs.setAdapter(adapter); 
	}

	/*
	 * Metoden skapar en dialogruta som frågar användaren om denne vill ta bort en konversation
	 * Metoden ger också användaren två valmöjligheter, JA eller Avbryt
	 */
	public void showEraseOption(int position){
		final int messageNumber = position;

		AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.setTitle("RADERA?");
		alertDialog.setMessage("Vill du ta bort meddelandet?");
		alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "JA", new DialogInterface.OnClickListener() {

			//Om användaren trycker på ja så körs metoden eraseMessage()
			public void onClick(DialogInterface dialog, int which) {
				eraseMessage(conversationContentArray[messageNumber]);
			}
		});
		alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "AVBRYT", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				//Gör inget
			}
		});
		alertDialog.show();

	}

	public void eraseMessage(String messageText){
		InputMethodManager inm = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
		MessageModel messageModelInList;
		long id = messageAndIdMap.get(messageText);

		for (int i = 0; i < listOfMassageModels.size(); i++) {
			messageModelInList = (MessageModel) listOfMassageModels.get(i);

			if(messageModelInList.getId()==id){
				dataBase.deleteFromDB(messageModelInList, getApplicationContext());
				break;
			}
		}

		//Gömmer tangentbort och tar bort text ur textfältet 
		//om användaren raderar ett meddelande (omdessa visas vid raderingstillfället)
		if(inm.isActive()){
			//Gömmer tangentbordet på skärmen
			inm.hideSoftInputFromWindow(message.getWindowToken(), 0);
			//Tar bort texten ur textrutan
			message.getEditableText().clear();
			loadConversation(chosenContact);
		}	
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
		MessageModel messageModel;
		String[] stringArrayOfConversationContent;
		LinkedList<String> listOfConversations = new LinkedList<String>();
		Iterator<String> listIterator;

		//Hämtar en lista med alla messagemodels som finns i databasen.
		listOfMassageModels = dataBase.getAllFromDB(new MessageModel(),getApplicationContext());

		//		Den listview som kontakterna kommerpresenteras i
		listViewOfConversationInputs = (ListView) findViewById(R.id.displayOfConversation);
		//		//String array �ver anv�ndare

		// Sorterar ut meddelanden kopplade till den person man tryckt på.
		for (int i = 0; i < listOfMassageModels.size(); i++) {
			messageModel = (MessageModel) listOfMassageModels.get(i);

			if(messageModel.getReciever().toString().equals(Contact)){
				listOfConversations.add(messageModel.getMessageContent().toString());
				messageAndIdMap.put(messageModel.getMessageContent().toString(), messageModel.getId());
			}
		}

		//Skapar en string[] som är lika lång som listan som hämtades.
		stringArrayOfConversationContent = new String[listOfConversations.size()];
		listIterator = listOfConversations.descendingIterator();
		for (int i = 0; i < listOfConversations.size(); i++) {
			stringArrayOfConversationContent[i] = listIterator.next();
		}
		return stringArrayOfConversationContent;
	}

	public void sendMessage(View v){
		InputMethodManager inm = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
		messageObject = new MessageModel(user+": "+message.getText().toString(), chosenContact); 

		//Sparar messageObject i databasen
		dataBase.addToDB(messageObject,getApplicationContext());
		//Gömmer tangentbordet på skärmen
		inm.hideSoftInputFromWindow(message.getWindowToken(), 0);
		//Tar bort texten ur textrutan
		message.getEditableText().clear();

		loadConversation(chosenContact);

		//Skicka meddelande till server
	}
}
