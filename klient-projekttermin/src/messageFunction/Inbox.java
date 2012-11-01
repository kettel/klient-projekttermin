package messageFunction;

import com.example.klien_projekttermin.R;
import com.example.klien_projekttermin.R.id;
import com.example.klien_projekttermin.R.layout;
import com.example.klien_projekttermin.R.menu;

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

	ListView listOfPeopleEngagedInConversation;
	String[] peopleEngagedInConversation;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_inbox);
		
		//Ropar på en metod som skapar en lista över alla kontakter som användaren har haft en konversation med.
		loadListOfSenders();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_inbox, menu);
		return true;
	}

	/*
	 * Metoden laddar en ListView med alla kontakter man har haft en konversation med.
	 * Metoden sätter också en lyssnare som undersöker om någon trycker på något i listan
	 */
	public void loadListOfSenders(){

		//Be om lista över personer som man har konverserat med.
		//Arraylist med personerna returneras

		//Den listview som kontakterna kommerpresenteras i
		listOfPeopleEngagedInConversation = (ListView) findViewById(R.id.conversationContactsList);
		
		//String array över användare
		peopleEngagedInConversation = new String[] {"Anna", "Fredrik", "Wiktor",
				"Erik L", "Erik K", "Rasmus", "Niko", "Nicke",
				"Kristoffer", "Bosse","Steffe","Bengan","Glenn","Alban","Laban"};

		// First paramenter - Context
		// Second parameter - Layout for the row
		// Third parameter - ID of the TextView to which the data is written
		// Forth - the Array of data
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, android.R.id.text1, peopleEngagedInConversation);

		// Assign adapter to ListView
		listOfPeopleEngagedInConversation.setAdapter(adapter); 

		listOfPeopleEngagedInConversation.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				//ropar på metoden skapar en aktivitet som visar meddelanden från den kontakt man tryckt på
				openConversation(peopleEngagedInConversation[position]);
			}
		});
	}
	
	/*
	 * Metoden skapar en ny aktivitet som visar alla meddelanden som användaren har skickat och tagit emot från den kontakten som klickades på i listan
	 */
	public void openConversation(String chosenContact){
		Intent intent = new Intent(this, DisplayOfConversation.class);
		
		//Metoden skickar med namnet på den kontakt som klickades på.
		intent.putExtra("ChosenContact", chosenContact);
		startActivity(intent);
	}
}
