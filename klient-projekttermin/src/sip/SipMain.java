package sip;

import java.util.List;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
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
		setContentView(R.layout.activity_sip_main);
		
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			currentUser = extras.getString("USER");
		}
		
		String uri = "tel:" + "1002" ;
		Intent intent = new Intent(Intent.ACTION_CALL);
		intent.setData(Uri.parse(uri));
		startActivity(intent);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_sip_main, menu);
		return true;
	}
}
