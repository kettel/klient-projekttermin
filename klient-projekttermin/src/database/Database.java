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
			DatabaseHandlerAssignment dha = new DatabaseHandlerAssignment(context);
			dha.addAssignment((Assignment)m);
		}
		else if(dbRep.equalsIgnoreCase("contact")){
			DatabaseHandlerContacts dhc = new DatabaseHandlerContacts(context);
			dhc.addContact((Contact)m);
		}
		else if(dbRep.equalsIgnoreCase("message")){
			DatabaseHandlerMessages dhm = new DatabaseHandlerMessages(context);
			dhm.addMessage((MessageModel)m);
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
			returnCount = dha.getAssignmentCount();
		}
		else if(dbRep.equalsIgnoreCase("contact")){
			DatabaseHandlerContacts dhc = new DatabaseHandlerContacts(context);
			returnCount = dhc.getContactCount();
		}
		else if(dbRep.equalsIgnoreCase("message")){
			DatabaseHandlerMessages dhm = new DatabaseHandlerMessages(context);
			returnCount = dhm.getMessageCount();
		}
		return returnCount;
	}
}
