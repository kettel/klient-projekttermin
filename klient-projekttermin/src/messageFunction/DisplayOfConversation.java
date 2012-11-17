package messageFunction;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import models.MessageModel;
import models.ModelInterface;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
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
import com.example.klien_projekttermin.database.Database;

import communicationModule.CommunicationService;
import communicationModule.CommunicationService.CommunicationBinder;


public class DisplayOfConversation extends Activity {

	private ListView listViewOfConversationInputs;
	private TextView message;
	private List<ModelInterface> listOfMassageModels;
	private String [] conversationContentArray;
	private HashMap<String, Long> messageAndIdMap = new HashMap<String, Long>();
	private String chosenContact;
	private Database dataBase;
	private String user;
	private MessageModel messageObject;
	private String[] options = {"AVBRYT","RADERA","VIDAREBEFORDRA"};
	private CommunicationService communicationService;
	private boolean communicationBond = false;
	//	private CommunicationModule communicationModule = new CommunicationModule();

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
			user = extras.getString("USER");
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
		addOnLongClickListener();
	}

	/*
	 * Tillsätt lyssnare i meddelandelistan som lyssnar efter långa tryckningar på listobjekt
	 */
	public void addOnLongClickListener(){
		//Skapar en lyssnare som lyssnar efter långa intryckningar 
		listViewOfConversationInputs.setOnItemLongClickListener(new OnItemLongClickListener() {

			public boolean onItemLongClick(AdapterView<?> parent, View view,int position, long id) {
				showLongClickOptions(position);
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
	public void showLongClickOptions(int position){
		final int messageNumber = position;

		AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
		alertDialog.setTitle("Meny");
		ListView alertOptions = new ListView(this);

		ArrayAdapter<String> modeAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, android.R.id.text1,options);

		alertOptions.setAdapter(modeAdapter);

		alertDialog.setView(alertOptions);
		final Dialog dialog = alertDialog.create();

		alertOptions.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
				dialog.dismiss();
				switch (arg2) {
				case 0:
					break;
				case 1:
					eraseMessage(conversationContentArray[messageNumber]);
					break;
				case 2:
					forwardMessage(conversationContentArray[messageNumber]);
					break;
				default:
					break;
				}
			}
		});

		dialog.show();
	}

	public void forwardMessage(String messageContent){
		Intent intent = new Intent(this, CreateMessage.class);
		intent.putExtra("MESSAGE",messageContent);
		startActivity(intent);
	}

	public void eraseMessage(String messageText){

		InputMethodManager inm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
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
		}

		if(listOfMassageModels.size()-1<1){
			finish();
		}
		loadConversation(chosenContact);

	}

	/*
	 * Tar in en long med ett meddelandes timestamp i millisekunder och gör om det till ett förståeligt format
	 * med år,månad,dag,timme,minut,sekund,hundradel
	 */
	public String understandableTimeStamp(Long millisecondTime){
		SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("EEEEEEE, d MMM yyyy HH:mm:ss");

		return simpleTimeFormat.format(millisecondTime).toString();
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

		// Sorterar ut meddelanden kopplade till den person man tryckt på.
		for (int i = 0; i < listOfMassageModels.size(); i++) {
			messageModel = (MessageModel) listOfMassageModels.get(i);
			
			if(messageModel.getReciever().toString().equals(Contact)||messageModel.getSender().toString().equals(Contact)){
				
				listOfConversations.add(messageModel.getSender().toString()+" ["+understandableTimeStamp(messageModel.getMessageTimeStamp())+"] "+"\n"+messageModel.getMessageContent().toString());
				messageAndIdMap.put(messageModel.getSender().toString()+" ["+understandableTimeStamp(messageModel.getMessageTimeStamp())+"] "+"\n"+messageModel.getMessageContent().toString(), messageModel.getId());
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
		communicationService.setContext(getApplicationContext());
		Intent intent = new Intent(this, CommunicationService.class);
		
		bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

		InputMethodManager inm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);

		messageObject = new MessageModel(message.getText().toString(), chosenContact, user); 

		//Sparar messageObject i databasen
		dataBase.addToDB(messageObject,getApplicationContext());
		//Gömmer tangentbordet på skärmen
		inm.hideSoftInputFromWindow(message.getWindowToken(), 0);
		//Tar bort texten ur textrutan
		message.getEditableText().clear();

		if(communicationBond){
			communicationService.sendMessage(messageObject);
		}

		loadConversation(chosenContact);
	}

	private ServiceConnection serviceConnection = new ServiceConnection() {

		public void onServiceConnected(ComponentName className,IBinder service) {
			System.out.println("OnServiceConnection");
			CommunicationBinder binder = (CommunicationBinder) service;
			communicationService = binder.getService();
			communicationBond = true;
		}
		public void onServiceDisconnected(ComponentName arg0) {
			communicationBond = false;
		}
	};
}
