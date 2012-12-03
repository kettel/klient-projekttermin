package contacts;

import java.util.List;

import loginFunction.InactivityListener;
import map.CustomAdapter;
import messageFunction.CreateMessage;
import models.Contact;
import models.ModelInterface;
import sip.OutgoingCallReceiver;
import sip.RegisterWithSipServerService;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.klient_projekttermin.ActivityConstants;
import com.klient_projekttermin.R;

import database.Database;

public class ContactsBookActivity extends InactivityListener {
	// Hämta instans av SipServicen
	public RegisterWithSipServerService s;
	
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
    protected void onStart() {
        super.onStart();
        // Bind to LocalService
        Intent intent = new Intent(this, RegisterWithSipServerService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }
	
	/**
	 * Skapa en ServiceConnection till SipService
	 */
	private ServiceConnection mConnection = new ServiceConnection() {

	    public void onServiceConnected(ComponentName className, IBinder binder) {
	      s = ((RegisterWithSipServerService.MyBinder) binder).getService();
	    }
	    public void onServiceDisconnected(ComponentName className) {
	      s = null;
	    }
	  };
	/**
	 * Bind med SipService
	 */
	void doBindService() {
		bindService(new Intent(this, RegisterWithSipServerService.class), mConnection,
				Context.BIND_AUTO_CREATE);
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
					Intent i= new Intent();
//					i.putExtra("contactToCall", "1002");
//					i.setAction(OutgoingCallReceiver.OUTGOING_CALL);
//					Log.d("SIP/Contactbook","Ska ringa 1002...");
//					getApplicationContext().sendBroadcast(i);
					//RegisterWithSipServerService.initiateCall();
					Log.d("SIP/ContactBookActivity","Ska starta ett utgående samtal...");
					s.initiateCall();
					break;
				default:
					break;
				}
			}
		});
		dialog.show();
	}

}
