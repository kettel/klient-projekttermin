package com.example.klien_projekttermin.database;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


import com.example.klien_projekttermin.models.Assignment;
import com.example.klien_projekttermin.models.Contact;
import com.example.klien_projekttermin.models.MessageModel;
import com.example.klien_projekttermin.models.ModelInterface;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


/**
 * En klass med metoder för Create, Remove, Update, Delete (CRUD)
 * operationer på databasen.
 * @author kettel
 *
 */
public class Database{
	
	private Database(){
		
	}
	
	private static Database instance = new Database();
	private static File databaseFile = null;
	private static SQLiteDatabase db = null;
	private static Context context = null;
	
	public static Database getInstance(Context context){
		Log.d("DB","get instance..");
		if(databaseFile != context.getDatabasePath("tddd36.db")){
			databaseFile = context.getDatabasePath("tddd36.db");
		}
		// Om databasfilen inte existerar, skapa den
		if(!databaseFile.exists()){
			databaseFile.mkdirs();
			databaseFile.delete();
		}
		// Om databasen inte är initierad
		if(db != null){
			
			db = SQLiteDatabase.openOrCreateDatabase(databaseFile, null);
		}
		Log.d("DB","got instance..");
		return instance;
	}
	
	
	
	/**
	 * Lägg till ett uppdrag/kontakt/meddelande till rätt databas
	 * @param m			ModellInterface av objekt som ska läggas till
	 * @param context	Aktivitetens kontext så data läggs i rätt databas
	 */
	public void addToDB(ModelInterface m, Context context){
		String dbRep = m.getDatabaseRepresentation();
		if (dbRep.equalsIgnoreCase("assignment")) {
			DatabaseHandlerAssignment dha = new DatabaseHandlerAssignment(context);
			dha.addAssignment(db, (Assignment)m);
		}
		else if(dbRep.equalsIgnoreCase("contact")){
			Log.d("DB","About to add contact");
			DatabaseHandlerContacts dhc = new DatabaseHandlerContacts(context);
			dhc.addContact(db, (Contact)m);
		}
		else if(dbRep.equalsIgnoreCase("message")){
			DatabaseHandlerMessages dhm = new DatabaseHandlerMessages(context);
			dhm.addMessage(db, (MessageModel)m);
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
			DatabaseHandlerAssignment dha = new DatabaseHandlerAssignment(context);
			returnCount = dha.getAssignmentCount(db);
		}
		else if(dbRep.equalsIgnoreCase("contact")){
			DatabaseHandlerContacts dhc = new DatabaseHandlerContacts(context);
			returnCount = dhc.getContactCount(db);
		}
		else if(dbRep.equalsIgnoreCase("message")){
			DatabaseHandlerMessages dhm = new DatabaseHandlerMessages(context);
			returnCount = dhm.getMessageCount(db);
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
			DatabaseHandlerAssignment dha = new DatabaseHandlerAssignment(context);
			dha.removeAssignment(db, (Assignment)m);
		}
		else if(dbRep.equalsIgnoreCase("contact")){
			DatabaseHandlerContacts dhc = new DatabaseHandlerContacts(context);
			dhc.removeContact(db, (Contact)m);
		}
		else if(dbRep.equalsIgnoreCase("message")){
			DatabaseHandlerMessages dhm = new DatabaseHandlerMessages(context);
			dhm.removeMessage(db, (MessageModel)m);
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
			DatabaseHandlerAssignment dha = new DatabaseHandlerAssignment(context);
			returnList = dha.getAllAssignments(db);
		}
		else if(dbRep.equalsIgnoreCase("contact")){
			DatabaseHandlerContacts dhc = new DatabaseHandlerContacts(context);
			returnList = dhc.getAllContacts(db);
		}
		else if(dbRep.equalsIgnoreCase("message")){
			DatabaseHandlerMessages dhm = new DatabaseHandlerMessages(context);
			returnList = dhm.getAllMessages(db);
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
			DatabaseHandlerAssignment dha = new DatabaseHandlerAssignment(context);
			dha.updateModel(db, (Assignment)m);
		}
		else if(dbRep.equalsIgnoreCase("contact")){
			DatabaseHandlerContacts dhc = new DatabaseHandlerContacts(context);
			dhc.updateModel(db, (Contact)m);
		}
		else if(dbRep.equalsIgnoreCase("message")){
			DatabaseHandlerMessages dhm = new DatabaseHandlerMessages(context);
			dhm.updateModel(db, (MessageModel)m);
		}
	}
}
