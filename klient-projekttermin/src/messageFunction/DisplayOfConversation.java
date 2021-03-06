package messageFunction;

import static com.klient_projekttermin.CommonUtilities.DISPLAY_MESSAGE_ACTION;
import static com.klient_projekttermin.CommonUtilities.EXTRA_MESSAGE;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import login.User;
import models.MessageModel;
import models.ModelInterface;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

import com.klient_projekttermin.R;
import com.klient_projekttermin.SecureActivity;
import communicationModule.SocketConnection;

import database.Database;

public class DisplayOfConversation extends SecureActivity {

	private ListView listViewOfConversationInputs;
	private TextView message;
	private List<ModelInterface> listOfMassageModels;
	private String[] conversationContentArray;
	private HashMap<String, Long> messageAndIdMap = new HashMap<String, Long>();
	private String chosenContact;
	private Database dataBase;
	private String currentUser;
	private MessageModel messageObject;
	private String[] options = { "Avbryt", "Radera", "Vidarebofordra" };
	private ArrayAdapter<String> adapter;
	private User user;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_of_conversation);
		user = User.getInstance();

		message = (TextView) this.findViewById(R.id.messageBox);

		dataBase = Database.getInstance(getApplicationContext());

		User user = User.getInstance();
		currentUser = user.getAuthenticationModel().getUserName();

		// Metoden testar om någonting skickades med från Inbox och skriver i så
		// fall ut det till strängen chosenContact
		Bundle extras = getIntent().getExtras();

		if (extras != null) {
			chosenContact = extras.getString("ChosenContact");
		}
		registerReceiver(mHandleMessageReceiver, new IntentFilter(
				DISPLAY_MESSAGE_ACTION));
		loadConversation(chosenContact);
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(mHandleMessageReceiver);
		super.onDestroy();
	}

	private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String newMessage = intent.getExtras().getString(EXTRA_MESSAGE);
			if (newMessage.equals("message")) {
				loadConversation(chosenContact);
				adapter.notifyDataSetChanged();

			}
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater()
				.inflate(R.menu.activity_display_of_conversation, menu);
		return true;
	}

	@Override
	public void onStart() {
		super.onStart();
		addOnLongClickListener();
	}

	/*
	 * Tillsätt lyssnare i meddelandelistan som lyssnar efter långa tryckningar
	 * på listobjekt
	 */
	public void addOnLongClickListener() {
		// Skapar en lyssnare som lyssnar efter långa intryckningar
		listViewOfConversationInputs
				.setOnItemLongClickListener(new OnItemLongClickListener() {

					public boolean onItemLongClick(AdapterView<?> parent,
							View view, int position, long id) {
						showLongClickOptions(position);
						return true;
					}
				});
	}

	/*
	 * Metoden skapar en listView över alla meddelanden som skickats och tagits
	 * emot. Dessa efterfrågas från databasen. Om ett meddelande klickas på så
	 * kallar metoden på en ny metod som startar en ny aktivitet där det valda
	 * meddelandet visas.
	 */
	public void loadConversation(String contact) {

		conversationContentArray = getInformationFromDatabase(contact);

		// First paramenter - Context
		// Second parameter - Layout for the row
		// Third parameter - ID of the TextView to which the data is written
		// Forth - the Array of data
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, android.R.id.text1,
				conversationContentArray);

		// Assign adapter to ListView
		listViewOfConversationInputs.setAdapter(adapter);
	}

	/*
	 * Metoden skapar en dialogruta som frågar användaren om denne vill ta bort
	 * en konversation Metoden ger också användaren två valmöjligheter, JA eller
	 * Avbryt
	 */
	public void showLongClickOptions(int position) {
		final int messageNumber = position;

		AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
		alertDialog.setTitle("Meny");
		ListView alertOptions = new ListView(this);

		ArrayAdapter<String> modeAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, android.R.id.text1,
				options);

		alertOptions.setAdapter(modeAdapter);

		alertDialog.setView(alertOptions);
		final Dialog dialog = alertDialog.create();

		alertOptions.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
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

	public void forwardMessage(String messageContent) {
		Intent intent = new Intent(this, CreateMessage.class);
		intent.putExtra("MESSAGE", messageContent);
		startActivity(intent);
	}

	public void eraseMessage(String messageText) {

		InputMethodManager inm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		MessageModel messageModelInList;
		long id = messageAndIdMap.get(messageText);

		for (int i = 0; i < listOfMassageModels.size(); i++) {
			messageModelInList = (MessageModel) listOfMassageModels.get(i);

			if (messageModelInList.getId() == id) {
				dataBase.deleteFromDB(messageModelInList, getContentResolver());
				break;
			}
		}
		// Gömmer tangentbort och tar bort text ur textfältet
		// om användaren raderar ett meddelande (omdessa visas vid
		// raderingstillfället)
		if (inm.isActive()) {
			// Gömmer tangentbordet på skärmen
			inm.hideSoftInputFromWindow(message.getWindowToken(), 0);
			// Tar bort texten ur textrutan
			message.getEditableText().clear();
		}

		if (listOfMassageModels.size() - 1 < 1) {
			finish();
		}
		loadConversation(chosenContact);

	}

	/*
	 * Tar in en long med ett meddelandes timestamp i millisekunder och gör om
	 * det till ett förståeligt format med
	 * år,månad,dag,timme,minut,sekund,hundradel
	 */
	public String understandableTimeStamp(Long millisecondTime) {
		SimpleDateFormat simpleTimeFormat = new SimpleDateFormat(
				"EEEEEEE, d MMM yyyy HH:mm:ss");

		return simpleTimeFormat.format(millisecondTime).toString();
	}

	public String[] getInformationFromDatabase(String Contact) {

		MessageModel messageModel;
		String[] stringArrayOfConversationContent;
		LinkedList<String> listOfConversations = new LinkedList<String>();
		Iterator<String> listIterator;

		// Hämtar en lista med alla messagemodels som finns i databasen.
		listOfMassageModels = dataBase.getAllFromDB(new MessageModel(),
				getContentResolver());

		// MessageModel a = (MessageModel) listOfMassageModels.get(0);

		// Den listview som kontakterna kommerpresenteras i
		listViewOfConversationInputs = (ListView) findViewById(R.id.displayOfConversation);

		// Sorterar ut meddelanden kopplade till den person man tryckt på.
		for (int i = 0; i < listOfMassageModels.size(); i++) {
			messageModel = (MessageModel) listOfMassageModels.get(i);

			if (messageModel.getReciever().toString().equals(Contact)
					&& messageModel.getSender().toString().equals(user.getAuthenticationModel().getUserName())) {
				listOfConversations.add(messageModel.getSender().toString()
						+ " ["
						+ understandableTimeStamp(messageModel
								.getMessageTimeStamp()) + "] " + "\n"
						+ messageModel.getMessageContent().toString());
				messageAndIdMap.put(
						messageModel.getSender().toString()
								+ " ["
								+ understandableTimeStamp(messageModel
										.getMessageTimeStamp()) + "] " + "\n"
								+ messageModel.getMessageContent().toString(),
						messageModel.getId());
			} else if (messageModel.getSender().toString().equals(Contact)
					&& messageModel.getReciever().toString().equals(user.getAuthenticationModel().getUserName())){
				listOfConversations.add(messageModel.getSender().toString()
						+ " ["
						+ understandableTimeStamp(messageModel
								.getMessageTimeStamp()) + "] " + "\n"
						+ messageModel.getMessageContent().toString());
				messageAndIdMap.put(
						messageModel.getSender().toString()
								+ " ["
								+ understandableTimeStamp(messageModel
										.getMessageTimeStamp()) + "] " + "\n"
								+ messageModel.getMessageContent().toString(),
						messageModel.getId());
			}
		}

		// Skapar en string[] som är lika lång som listan som hämtades.
		stringArrayOfConversationContent = new String[listOfConversations
				.size()];
		listIterator = listOfConversations.descendingIterator();

		for (int i = 0; i < listOfConversations.size(); i++) {
			stringArrayOfConversationContent[i] = listIterator.next();
		}
		return stringArrayOfConversationContent;
	}

	public void sendMessage(View v) {
		InputMethodManager inm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

		messageObject = new MessageModel(message.getText().toString(),
				chosenContact, currentUser);

		// Sparar messageObject i databasen
		dataBase.addToDB(messageObject, getContentResolver());
		// Gömmer tangentbordet på skärmen
		inm.hideSoftInputFromWindow(message.getWindowToken(), 0);
		// Tar bort texten ur textrutan
		message.getEditableText().clear();

		SocketConnection connection = new SocketConnection();
		connection.sendModel(messageObject);
		loadConversation(chosenContact);
	}
}
