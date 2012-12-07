package contacts;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import sip.RegisterWithSipSingleton;

import map.CustomAdapter;
import messageFunction.CreateMessage;
import models.Contact;
import models.ModelInterface;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import assignment.AddAssignment;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.klient_projekttermin.ActivityConstants;
import com.klient_projekttermin.MainActivity;
import com.klient_projekttermin.R;
import com.klient_projekttermin.SecureActivity;

import database.Database;

public class ContactsBookActivity extends SecureActivity {

	private String[] contacts;
	private Database db;
	private String[] contactAlts = { "Skicka meddelande till kontakt",
			"Ring kontakt" };
	public static String contact;
	private int callingActivity;
	private List<Contact> contactsToAssignment = new ArrayList<Contact>();
	private MenuItem useContacts;
	private ContacsAdapter ca;
	private HashMap<Integer, Boolean> h = new HashMap<Integer, Boolean>();

	private List<String> sortedContact;
	private List<ModelInterface> lista;
	private RegisterWithSipSingleton regSip;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contacts_book);
		callingActivity = getIntent().getIntExtra("calling-activity", 0);
		ListView lv = (ListView) findViewById(android.R.id.list);
		db = Database.getInstance(this);
		lista = db.getAllFromDB(new Contact(),
				getContentResolver());
		contacts = new String[lista.size()];
		sortedContact = new ArrayList<String>();
		for (ModelInterface m : lista) {
			Contact c = (Contact) m;
			sortedContact.add(c.getContactName());
		}

		int i = 0;
		Collections.sort(sortedContact);
		for (String string : sortedContact) {
			contacts[i] = string;
			i++;
		}
		ca = new ContacsAdapter(this, android.R.layout.simple_list_item_1,
				contacts, callingActivity);
		lv.setAdapter(ca);
	}

	@Override
    protected void onStart() {
        super.onStart();
        
        // Hämta regSip från MainActivity
        // .. är det här som nyttan med en service börjar uppenbara sig?
        regSip = MainActivity.regSip;
    }
	private List<Contact> getSortedContactList(){
		
		List<Contact> sortedList = new ArrayList<Contact>();
		
		for (String agent : sortedContact) {
			for (ModelInterface m : lista) {
				Contact c = (Contact) m;
				if (agent.equals(c.getContactName())) {
					sortedList.add(c);
				}
			}
		}
		return sortedList;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_contacts_book, menu);
		useContacts = menu.findItem(R.id.useContact);
		if (callingActivity == ActivityConstants.ADD_AGENTS) {
			useContacts.setVisible(true);
		}
		useContacts.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			public boolean onMenuItemClick(MenuItem item) {
				Intent intentAssignment = new Intent(ContactsBookActivity.this,
						AddAssignment.class);
				Gson gson = new Gson();
				Type type = new TypeToken<List<Contact>>() {
				}.getType();
				getContactsForAssignment();
				intentAssignment.putExtra("agents",
						gson.toJson(contactsToAssignment, type));
				setResult(ActivityConstants.RESULT_FROM_CONTACTS,
						intentAssignment);
				finish();
				return false;
			}
		});
		return true;
	}

	public void contactOptions(View v) {
		for (ModelInterface temp : db.getAllFromDB(new Contact(),
				getContentResolver())) {
			Contact c = (Contact) temp;
			if (c.getContactName().equals(contacts[v.getId()])) {
				switch (callingActivity) {
				case ActivityConstants.ADD_AGENTS:

					break;
				default:
					showAlertDialog(c);
					break;
				}
			}
		}
	}

	private void getContactsForAssignment() {
		int[] sel = getAllSelected();
		int cId = 0;
		HashMap<Integer, Contact> hs = new HashMap<Integer, Contact>();
		for (ModelInterface temp : getSortedContactList()) {
			Contact c = (Contact) temp;
			hs.put(cId, c);
			cId++;
		}
		for (int i = 0; i < sel.length; i++) {
			contactsToAssignment.add(hs.get(sel[i]));
		}
	}

	private int[] getAllSelected() {
		h = ca.getSelected();
		
		int k = 0;
		for (boolean b : h.values()) {
			if (b) {
				k++;
			}
		}
		int[] selectedContacs = new int[k];
		int j = 0;
		for (int i : h.keySet()) {
			if (h.get(i)) {
				selectedContacs[j] = i;
				j++;
			}
		}
		return selectedContacs;
	}

	private void showAlertDialog(Contact c) {
		final String name = c.getContactName();
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Kontakt alternativ");
		ListView modeList = new ListView(this);
		CustomAdapter modeAdapter = new CustomAdapter(this,
				android.R.layout.simple_list_item_1, android.R.id.text1,
				contactAlts);
		modeList.setAdapter(modeAdapter);
		builder.setView(modeList);
		final Dialog dialog = builder.create();
		modeList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				dialog.dismiss();
				switch (arg2) {
				case 0:
					Intent intent = new Intent(ContactsBookActivity.this,
							CreateMessage.class);
					intent.putExtra("calling-activity",
							ActivityConstants.ADD_CONTACT_TO_MESSAGE);
					intent.putExtra(contact, name);
					ContactsBookActivity.this.startActivity(intent);
					finish();
					break;
				case 1:
					regSip.initiateCall(name);
					break;
				default:
					break;
				}
			}
		});
		dialog.show();
	}

}
