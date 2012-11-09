package com.example.klien_projekttermin.databaseProvider;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import net.sqlcipher.database.SQLiteDatabase;

import com.example.klien_projekttermin.models.MessageModel;
import com.example.klien_projekttermin.models.ModelInterface;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
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
	private static Context context = null;
	private static SQLiteDatabase database = null;

	public static String PASSWORD = "password";
	protected static String KEY_ID = "_id";
	
	private Database(){}
	
	private static Database instance = new Database();
	
	public static Database getInstance(Context c){
		// Om kontexten redan är skapad (dvs om instansen hämtas efter att den är skapad)
		if(context== null){
			context = c;
		}
		
		//databaseFile = context.getDatabasePath("tddd36.db");
		File dbFile = context.getDatabasePath("tddd36.db");
		
		// Om databasfilen inte existerar, skapa den
		if(!dbFile.exists()){
			dbFile.mkdirs();
			dbFile.delete();
		}
		Log.d("DB", "About to create DB");
		// Ladda in SQLCipher-bibliotek filer
		SQLiteDatabase.loadLibs(context);
		database = SQLiteDatabase.openOrCreateDatabase(dbFile, "password", null);
		return instance;
	}

	
	/**
	 * Lägg till ett uppdrag/kontakt/meddelande till rätt databas
	 * @param m			ModellInterface av objekt som ska läggas till
	 * @param context	Aktivitetens kontext så data läggs i rätt databas
	 */
	public void addToDB(ModelInterface m, Context c){
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

	    	// Nytt meddelande
		    // messUri borde väl egentligen kunna returneras för enkel åtkomst?
		    messUri = context.getContentResolver().insert(DatabaseContentProviderMessages.CONTENT_URI, values);
		}
		
	}
	/**
	 * Räkna antal poster i vald databas
	 * @param m			datatypen för den databas som ska räknas samman
	 * @param context	programkontexten så rätt databas kan väljas
	 * @return
	 */
	public int getDBCount(ModelInterface m, Context c){
		String dbRep = m.getDatabaseRepresentation();
		int returnCount = 0;
		if (dbRep.equalsIgnoreCase("assignment")) {
		}
		else if(dbRep.equalsIgnoreCase("contact")){
		}
		else if(dbRep.equalsIgnoreCase("message")){
			// SELECT * WHERE _id IS NOT null
			Cursor cursor = context.getContentResolver().query(DatabaseContentProviderMessages.CONTENT_URI, null, Database.KEY_ID + " IS NOT null",null, null);
			
			returnCount = cursor.getCount();
			Log.d("DB","Cursor: " + cursor.toString());
			cursor.close();
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
