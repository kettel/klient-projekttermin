package contacts;

import java.util.List;

import loginFunction.User;
import map.CustomAdapter;
import messageFunction.CreateMessage;
import models.Contact;
import models.ModelInterface;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.klient_projekttermin.ActivityConstants;
import com.klient_projekttermin.SecureActivity;
import com.klient_projekttermin.R;

import database.Database;

public class ContactsBookActivity extends SecureActivity {

	private String[] contacts;
	private Database db;
	private String[] contactAlts = { "Skicka meddelande till kontakt",
			"Ring kontakt" };
	public static String contact;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contacts_book);
		ListView lv = (ListView) findViewById(android.R.id.list);
		db = Database.getInstance(this);
		List<ModelInterface> lista = db.getAllFromDB(new Contact(),
				getContentResolver());
		System.out.println(lista.size()+ " ANTAL KONTAKTER");
		contacts = new String[lista.size()];
		int i = 0;
		for (ModelInterface m : lista) {
			Contact c = (Contact) m;
			contacts[i] = c.getContactName();
			i++;
		}
		lv.setAdapter(new ContacsAdapter(this,
				android.R.layout.simple_list_item_1, contacts));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_contacts_book, menu);
		return true;
	}

	public void contactOptions(View v) {
		for (ModelInterface temp : db.getAllFromDB(new Contact(),
				getContentResolver())) {
			Contact c = (Contact) temp;
			if (c.getContactName().equals(contacts[v.getId()])) {
					showAlertDialog(c);
			}
		}
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

					break;
				default:
					break;
				}
			}
		});
		dialog.show();
	}

}
