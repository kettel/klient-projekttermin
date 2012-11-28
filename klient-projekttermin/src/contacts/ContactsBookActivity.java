package contacts;

import loginFunction.InactivityListener;
import models.Contact;

import com.klient_projekttermin.R;
import com.klient_projekttermin.R.layout;
import com.klient_projekttermin.R.menu;

import database.AssignmentTable;
import database.ContactTable;
import database.Database;

import android.os.Bundle;
import android.app.Activity;
import android.app.ListActivity;
import android.view.Menu;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class ContactsBookActivity extends InactivityListener {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Database db = Database.getInstance(this);
		setContentView(R.layout.activity_contacts_book);
		ListView lv = (ListView) findViewById(android.R.id.list);
		lv.setAdapter(new ContactsCursorAdapter(this,
				getContentResolver().query(
						ContactTable.Contacts.CONTENT_URI, null, null,
						null, null), 0));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_contacts_book, menu);
		return true;
	}
}
