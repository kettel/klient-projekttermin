package messageFunction;

import com.example.klien_projekttermin.R;
import com.example.klien_projekttermin.R.layout;
import com.example.klien_projekttermin.R.menu;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.TextView;

public class DisplayMessage extends Activity {

	TextView messageBox;
	TextView sender;
	String correspondant;
	String messageContent;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);
        
        //Textruta i vyn f�r meddelandetexten
    	messageBox = (TextView) findViewById(R.id.DisplayMessageContent);
    	
    	//Textruta i vyn f�r avs�ndarens namn
    	sender = (TextView) findViewById(R.id.DisplayMessageSender);
        
    	//S�tter den medskickade informationen till variabler. 
        Bundle extras = getIntent().getExtras();
	       if (extras != null) {
	    	    messageContent = extras.getString("specifiedMessage");
	    	    correspondant = extras.getString("correspondant");
	    	}	    
        
        loadMessage(messageContent, correspondant);
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_message_view, menu);
        return true;
    }
    
    /*
     * Metoden s�tter meddelandetexten och avs�ndaren i tv� textrutor i vyn.
     */
    public void loadMessage(String messageContent, String correspondant){
    	messageBox.setText(messageContent);
    	sender.setText(correspondant);
    	
    }
}
