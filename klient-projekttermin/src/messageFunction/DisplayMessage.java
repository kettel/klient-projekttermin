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
        
        //Textruta i vyn för meddelandetexten
    	messageBox = (TextView) findViewById(R.id.DisplayMessageContent);
    	
    	//Textruta i vyn för avsändarens namn
    	sender = (TextView) findViewById(R.id.DisplayMessageSender);
        
    	//Sätter den medskickade informationen till variabler. 
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
     * Metoden sätter meddelandetexten och avsändaren i två textrutor i vyn.
     */
    public void loadMessage(String messageContent, String correspondant){
    	messageBox.setText(messageContent);
    	sender.setText(correspondant);
    	
    }
}
