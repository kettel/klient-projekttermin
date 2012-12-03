package messageFunction;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import loginFunction.User;
import models.MessageModel;
import models.ModelInterface;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.klient_projekttermin.SecureActivity;
import com.klient_projekttermin.R;

import database.Database;

public class Inbox extends SecureActivity {

	private ListView listOfPeopleEngagedInConversation;
	private String[] peopleIveBeenTalkingTo;
	private HashMap<String, Long> contactAndIdMap = new HashMap<String, Long>();
	private List<ModelInterface> peopleEngagedInConversation;
	private Database dataBase; 
	// Sätter den som tom sträng för att undvika NULLPOINTER!
	private String userName;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_inbox);

		User user = User.getInstance();
		userName = user.getAuthenticationModel().getUserName();

		//Ropar p� en metod som skapar en lista �ver alla kontakter som anv�ndaren har haft en konversation med.
		loadListOfSenders();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.activity_inbox, menu);
		return true;
	}

	@Override
	public void onResume(){
		super.onResume();
		loadListOfSenders();
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		createNewMessage(new View(getApplicationContext()));
		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	public void onStart(){
		super.onStart();

		addOnClickListener();
		addOnLongClickListener();

	}
	/*
	 * Metoden laddar en ListView med alla kontakter man har haft en konversation med.
	 * Metoden s�tter ocks� en lyssnare som unders�ker om n�gon trycker p� n�got i listan
	 */
	public void loadListOfSenders(){

		peopleIveBeenTalkingTo = getInformationFromDatabase();

		//		// First paramenter - Context
		//		// Second parameter - Layout for the row
		//		// Third parameter - ID of the TextView to which the data is written
		//		// Forth - the Array of data
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, android.R.id.text1, peopleIveBeenTalkingTo);

		//		// Assign adapter to ListView
		listOfPeopleEngagedInConversation.setAdapter(adapter); 	
	}

	/*
	 * Tillsätt lyssnare i meddelandelistan som lyssnar efter tryckningar på listobjekt
	 */
	public void addOnClickListener(){
		listOfPeopleEngagedInConversation.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				//ropar p� metoden skapar en aktivitet som visar meddelanden fr�n den kontakt man tryckt p�
				openConversation(peopleIveBeenTalkingTo[position]);
			}
		});
	}

	/*
	 * Tillsätt lyssnare i meddelandelistan som lyssnar efter långa tryckningar på listobjekt
	 */
	public void addOnLongClickListener(){
		//Skapar en lyssnare som lyssnar efter långa intryckningar 
		listOfPeopleEngagedInConversation.setOnItemLongClickListener(new OnItemLongClickListener() {

			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				showEraseOption(position);
				return true;
			}
		});	
	}

	/*
	 * Metoden skapar en dialogruta som frågar användaren om denne vill ta bort en konversation
	 * Metoden ger också användaren två valmöjligheter, JA eller Avbryt
	 */
	public void showEraseOption(int position){
		final int conversationNumber = position;

		AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.setTitle("RADERA?");
		alertDialog.setMessage("Vill du ta bort konversation?");
		alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "JA", new DialogInterface.OnClickListener() {

			//Om användaren trycker på ja så körs metoden eraseMessage()
			public void onClick(DialogInterface dialog, int which) {
				eraseConversation(peopleIveBeenTalkingTo[conversationNumber]);
			}
		});
		alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "AVBRYT", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				//Gör inget
			}
		});

		alertDialog.show();
	}

	/*
	 * Metoden tar bort hela konversationen för ett valt namn i inboxen
	 */
	public void eraseConversation(String contact){
		//		InputMethodManager inm = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
		MessageModel messageModelInList;
		//		long id = contactAndIdMap.get(contact);

		for (int i = 0; i < peopleEngagedInConversation.size(); i++) {
			messageModelInList = (MessageModel) peopleEngagedInConversation.get(i);

			if(messageModelInList.getReciever().toString().equals(contact)){
				dataBase.deleteFromDB(messageModelInList, getContentResolver());
			}
		}
		loadListOfSenders();
	}

	/*
	 * Metoden skapar en ny aktivitet som visar alla meddelanden som anv�ndaren har skickat och tagit emot fr�n den kontakten som klickades p� i listan
	 */
	public void openConversation(String chosenContact){
		Intent intent = new Intent(this, DisplayOfConversation.class);
		//Metoden skickar med namnet p� den kontakt som klickades p�.
		intent.putExtra("ChosenContact", chosenContact);
		startActivity(intent);
	}

	public String[] getInformationFromDatabase(){
		dataBase = Database.getInstance(getApplicationContext());
		String[] arrayOfPeopleEngagedInConversation;
		Object[] objectsInSetOfPeople;
		MessageModel messageModel;
		HashSet<String> setOfPeople = new HashSet<String>();

		//Hämtar en lista med alla MessageModels som finns lagrade i databasen
		peopleEngagedInConversation = dataBase.getAllFromDB(new MessageModel(),getContentResolver());

		listOfPeopleEngagedInConversation = (ListView) findViewById(R.id.conversationContactsList);

		for (int i = 0; i < peopleEngagedInConversation.size(); i++) {
			messageModel = (MessageModel) peopleEngagedInConversation.get(i);
			// Fulhack för att lösa NullpointerException!
			if(userName == null){
				userName = new String();
			}
			if(messageModel.getReciever().toString().toLowerCase().equals(userName.toLowerCase())){
				if(!setOfPeople.contains(messageModel.getSender().toString())){
					setOfPeople.add(messageModel.getSender().toString());
					contactAndIdMap.put(messageModel.getSender().toString(), messageModel.getId());
				}
			}

			if(messageModel.getSender().toString().toLowerCase().equals(userName.toLowerCase())){

				if (!setOfPeople.contains(messageModel.getReciever().toString())){
					setOfPeople.add(messageModel.getReciever().toString());
					contactAndIdMap.put(messageModel.getReciever().toString(), messageModel.getId());
				}
			}	
		}
		//Skapar en string[] som är lika lång som listan som hämtades.
		arrayOfPeopleEngagedInConversation = new String[setOfPeople.size()];
		objectsInSetOfPeople = new Object[setOfPeople.size()];
		objectsInSetOfPeople = setOfPeople.toArray();

		for (int i = 0; i < objectsInSetOfPeople.length; i++) {
			arrayOfPeopleEngagedInConversation[i] = objectsInSetOfPeople[i].toString();
		}

		return arrayOfPeopleEngagedInConversation;
	}

	/*
	 * Skapar ett nytt intent och startar aktiviteten CreateNewMessage
	 * Metoden skickar ocks� med namnet p� den anv�ndare som �r inloggad p� enheten. 
	 */
	public void createNewMessage(View v){
		Intent intent = new Intent(this, CreateMessage.class);
		startActivity(intent);
	}
}
