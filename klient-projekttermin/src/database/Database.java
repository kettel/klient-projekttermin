package database;

import models.Assignment;
import models.Contact;
import models.MessageModel;
import models.ModelInterface;
import android.content.Context;
import android.util.Log;

/**
 * En klass med metoder för Create, Remove, Update, Delete (CRUD)
 * operationer på databasen.
 * @author kettel
 *
 */
public class Database{
	
	/**
	 * Lägg till ett uppdrag/kontakt/meddelande till rätt databas
	 * @param m			ModellInterface av objekt som ska läggas till
	 * @param context	Aktivitetens kontext så data läggs i rätt databas
	 */
	public void addToDB(ModelInterface m, Context context){
		String dbRep = m.getDatabaseRepresentation();
		if (dbRep.equalsIgnoreCase("assignment")) {
			Log.d("DB","Det är en assignment som ska läggas till");
			DatabaseHandlerAssignment dha = new DatabaseHandlerAssignment(context);
			dha.addAssignment((Assignment)m);
		}
		else if(dbRep.equalsIgnoreCase("contact")){
			Log.d("DB","Det är en contact som ska läggas till");
			DatabaseHandlerContacts dhc = new DatabaseHandlerContacts(context);
			dhc.addContact((Contact)m);
		}
		else if(dbRep.equalsIgnoreCase("message")){
			Log.d("DB","Det är ett message som ska läggas till");
			DatabaseHandlerMessages dhm = new DatabaseHandlerMessages(context);
			dhm.addMessage((MessageModel)m);
		}
	}
	public int getDBCount(ModelInterface m, Context context){
		String dbRep = m.getDatabaseRepresentation();
		int returnCount = 0;
		if (dbRep.equalsIgnoreCase("assignment")) {
			Log.d("DB","Det är en assignment som ska läggas till");
			DatabaseHandlerAssignment dha = new DatabaseHandlerAssignment(context);
			returnCount = dha.getAssignmentCount();
		}
		else if(dbRep.equalsIgnoreCase("contact")){
			Log.d("DB","Det är en contact som ska läggas till");
			DatabaseHandlerContacts dhc = new DatabaseHandlerContacts(context);
			returnCount = dhc.getContactCount();
		}
		else if(dbRep.equalsIgnoreCase("message")){
			Log.d("DB","Det är ett message som ska läggas till");
			DatabaseHandlerMessages dhm = new DatabaseHandlerMessages(context);
			returnCount = dhm.getMessageCount();
		}
		return returnCount;
	}
}
