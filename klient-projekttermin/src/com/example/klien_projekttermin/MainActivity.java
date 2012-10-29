package com.example.klien_projekttermin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SimpleAdapter;

public class MainActivity extends ListActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		String[] from = { "line1", "line2" };
		int[] to = { android.R.id.text1, android.R.id.text2 };

		setListAdapter(new SimpleAdapter(this, generateMenuContent(),
				android.R.layout.simple_list_item_2, from, to));
		getListView().setOnItemClickListener(new OnItemClickListener() {

<<<<<<< HEAD
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent myIntent;
				//Har man lagt till ett nytt menyval l�gger man till en action f�r dessa h�r.
=======
<<<<<<< HEAD
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent myIntent;
				//Har man lagt till ett nytt menyval l�gger man till en action f�r dessa h�r.
=======
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3){
				Intent myIntent;
				//Har man lagt till ett nytt menyval lägger man till en action för dessa här.
>>>>>>> e102b58... Add map activity
>>>>>>> 10d02d7... Removed .classpath, .settings/
				switch (arg2) {
				case 0:
					// myIntent= new Intent(from.this,
					// to.class);
					break;
				case 1:
					// myIntent= new Intent(from.this,
					// to.class);
					break;

				default:
					// myIntent= new Intent(from.this,
					// to.class);
					break;
				}
				// SomeView.this.startActivity(myIntent);
			}

		});
	}
	/**
<<<<<<< HEAD
	 * Genererar de menyval som ska g� att g�ra.
=======
<<<<<<< HEAD
	 * Genererar de menyval som ska g� att g�ra.
>>>>>>> 10d02d7... Removed .classpath, .settings/
	 * @return
	 * En List<HashMap<String, String>> d�r varje map bara har tv� v�rden. Ett f�r f�rsta raden och ett f�r andra.
	 */
	private List<HashMap<String, String>> generateMenuContent(){
		List<HashMap<String, String>>content=new ArrayList<HashMap<String,String>>();
		//Om menyn ska ut�kas ska man l�gga till de nya valen i dessa arrayer. Nptera att det kr�vs en subtitle till varje item.
		String[] menuItems={"Karta","Uppdragshanterare","Kontakter"};
<<<<<<< HEAD
		String[] menuSubtitle={"Visar en karta","L�gg till, ta bort eller �ndra uppdrag","Visar kontaktlista"};
		//�ndra inget h�r under
=======
		String[] menuSubtitle={"Visar en karta","L�gg till, ta bort eller �ndra uppdrag","Visar kontaktlista"};
		//�ndra inget h�r under
=======
	 * Genererar de menyval som ska gå att göra.
	 * @return
	 * En List<HashMap<String, String>> där varje map bara har två värden. Ett för första raden och ett för andra.
	 */
	private List<HashMap<String, String>> generateMenuContent(){
		List<HashMap<String, String>>content=new ArrayList<HashMap<String,String>>();
		//Om menyn ska utökas ska man lägga till de nya valen i dessa arrayer. Nptera att det krävs en subtitle till varje item.
		String[] menuItems={"Karta","Uppdragshanterare","Kontakter"};
		String[] menuSubtitle={"Visar en karta","Lägg till, ta bort eller ändra uppdrag","Visar kontaktlista"};
		//Ändra inget här under
>>>>>>> e102b58... Add map activity
>>>>>>> 10d02d7... Removed .classpath, .settings/
		for (int i = 0; i < menuItems.length; i++) {
			HashMap<String, String> hashMap = new HashMap<String, String>();
			hashMap.put("line1",menuItems[i] );
			hashMap.put("line2",menuSubtitle[i]);
			content.add(hashMap);
		}
		return content;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
}
