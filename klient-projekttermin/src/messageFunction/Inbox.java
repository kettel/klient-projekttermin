package messageFunction;

import java.util.HashSet;
import java.util.List;

import models.MessageModel;
import models.ModelInterface;

import com.example.klien_projekttermin.R;
import com.example.klien_projekttermin.R.id;
import com.example.klien_projekttermin.R.layout;
import com.example.klien_projekttermin.R.menu;

import database.Database;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class Inbox extends Activity {

	private ListView listOfPeopleEngagedInConversation;
	private String[] peopleIveBeenTalkingTo;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_inbox);
		//Ropar p� en metod som skapar en lista �ver alla kontakter som anv�ndaren har haft en konversation med.
		loadListOfSenders();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_inbox, menu);
		return true;
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
		//
		listOfPeopleEngagedInConversation.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				//ropar p� metoden skapar en aktivitet som visar meddelanden fr�n den kontakt man tryckt p�
				openConversation(peopleIveBeenTalkingTo[position]);
			}
		});
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
		String[] arrayOfPeopleEngagedInConversation;
		Object[] objectsInSetOfPeople;
		Database dataBase = new Database();
		MessageModel messageModel;
		HashSet<String> setOfPeople = new HashSet<String>();

		//Hämtar en lista med alla MessageModels som finns lagrade i databasen
		List<ModelInterface> peopleEngagedInConversation = dataBase.getAllFromDB(new MessageModel(),getApplicationContext());;

		listOfPeopleEngagedInConversation = (ListView) findViewById(R.id.conversationContactsList);
		//String array över användare

		for (int i = 0; i < peopleEngagedInConversation.size(); i++) {
			messageModel = (MessageModel) peopleEngagedInConversation.get(i);

			if (!setOfPeople.contains(messageModel.getReciever().toString())) {
				setOfPeople.add(messageModel.getReciever().toString());
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
}
