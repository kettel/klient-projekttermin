package com.example.klien_projekttermin.databaseProvider;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.example.klien_projekttermin.models.MessageModel;
import com.example.klien_projekttermin.models.ModelInterface;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.SimpleCursorAdapter;


/**
 * En klass med metoder för Create, Remove, Update, Delete (CRUD)
 * operationer på databasen.
 * @author kettel
 *
 */
public class Database{
	private SimpleCursorAdapter adapter;
	private Uri messUri;
	

	
	/**
	 * Lägg till ett uppdrag/kontakt/meddelande till rätt databas
	 * @param m			ModellInterface av objekt som ska läggas till
	 * @param context	Aktivitetens kontext så data läggs i rätt databas
	 */
	public void addToDB(ModelInterface m, Context context){
		String dbRep = m.getDatabaseRepresentation();
		if (dbRep.equalsIgnoreCase("assignment")) {
		}
		else if(dbRep.equalsIgnoreCase("contact")){
		}
		else if(dbRep.equalsIgnoreCase("message")){
			MessageModel mess = (MessageModel) m;
			String content = mess.getMessageContent().toString();
		    String receiver = mess.getReciever().toString();
		    String timestamp = Long.toString(Calendar.getInstance().getTimeInMillis());

		    ContentValues values = new ContentValues();
		    values.put(MessageTable.COLUMN_CONTENT, content);
		    values.put(MessageTable.COLUMN_RECEIVER, receiver);
		    values.put(MessageTable.COLUMN_TIMESTAMP, timestamp);
		    values.put(MessageTable.COLUMN_ISREAD, "FALSE");

		    if (messUri == null) {
		    	// New message
		    	messUri = context.getContentResolver().insert(DatabaseContentProvider.CONTENT_URI, values);
		    }
		}
		
	}
	/**
	 * Räkna antal poster i vald databas
	 * @param m			datatypen för den databas som ska räknas samman
	 * @param context	programkontexten så rätt databas kan väljas
	 * @return
	 */
	public int getDBCount(ModelInterface m, Context context){
		String dbRep = m.getDatabaseRepresentation();
		int returnCount = 0;
		if (dbRep.equalsIgnoreCase("assignment")) {
		}
		else if(dbRep.equalsIgnoreCase("contact")){
		}
		else if(dbRep.equalsIgnoreCase("message")){
		}
		return returnCount;
	}
	
	/**
	 * Ta bort ett objekt från databasen
	 * @param m	ModelInterface	Det objekt som önskas tas bort
	 * @param context
	 */
	public void deleteFromDB(ModelInterface m, Context context){
		String dbRep = m.getDatabaseRepresentation();
		if (dbRep.equalsIgnoreCase("assignment")) {
		}
		else if(dbRep.equalsIgnoreCase("contact")){
		}
		else if(dbRep.equalsIgnoreCase("message")){
		}
	}
	
	/**
	 * Hämta alla poster i databasen för inskickad modell.
	 * @param m	ModelInterface	Modellen styr från vilken databas data hämtas
	 * @param context
	 * @return	List<ModelInterface>	Alla objekt från vald databas
	 */
	public List<ModelInterface> getAllFromDB(ModelInterface m, Context context){
		String dbRep = m.getDatabaseRepresentation();
		List<ModelInterface> returnList = new ArrayList<ModelInterface>();
		if (dbRep.equalsIgnoreCase("assignment")) {
		}
		else if(dbRep.equalsIgnoreCase("contact")){
		}
		else if(dbRep.equalsIgnoreCase("message")){
		}
		return returnList;
	}
	
	/**
	 * Uppdatera värden för ett objekt i databasen
	 * @param m	ModelInterface	Det uppdaterade objektet 
	 * 							(OBS! Måste ha samma Id-nummer som
	 * 							det objekt det ska ersätta)
	 * @param context
	 */
	public void updateModel(ModelInterface m, Context context) {
		String dbRep = m.getDatabaseRepresentation();
		if (dbRep.equalsIgnoreCase("assignment")) {
		}
		else if(dbRep.equalsIgnoreCase("contact")){
		}
		else if(dbRep.equalsIgnoreCase("message")){
		}
	}
}
