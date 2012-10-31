package com.example.klien_projekttermin;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class Inbox extends Activity {
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);
        loadListOfSenders();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_inbox, menu);
        return true;
    }
    
    /**
     * Laddar en ListView med alla kontakter man har haft en konversation med.
     */
    public void loadListOfSenders(){
    	
    	ListView listView = (ListView) findViewById(R.id.conversationContactsList);
    	String[] values = new String[] { "Anna", "Fredrik", "Wiktor",
    	  "Erik L", "Erik K", "Rasmus", "Niko", "Nicke",
    	  "Kristoffer", "Bosse","Steffe","Bengan","Glenn","Alban","Laban" };
    	
    	// First paramenter - Context
    	// Second parameter - Layout for the row
    	// Third parameter - ID of the TextView to which the data is written
    	// Forth - the Array of data
    	ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
    	  android.R.layout.simple_list_item_1, android.R.id.text1, values);

    	// Assign adapter to ListView
    	listView.setAdapter(adapter); 
    	
    	listView.setOnItemClickListener(new OnItemClickListener() {
    		  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    		    Toast.makeText(getApplicationContext(),
    		      "Click ListItem Number " + position, Toast.LENGTH_LONG)
    		      .show();
    		  }
    		});
    }
    
    
    	
   
}
