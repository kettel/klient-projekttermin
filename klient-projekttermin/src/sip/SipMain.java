package sip;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import com.klient_projekttermin.R;

import contacts.ContacsAdapter;
import database.Database;
import loginFunction.InactivityListener;
import models.Contact;
import models.ModelInterface;

public class SipMain extends InactivityListener{
	private String currentUser;
	private String[] contacts;
	private Database db;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contacts_book);
		ListView lv = (ListView) findViewById(android.R.id.list);
		Intent intent = getIntent();
		currentUser = intent.getStringExtra("USER");
		db = Database.getInstance(this);
		
	}
}
