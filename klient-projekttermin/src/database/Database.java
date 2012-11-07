package database;

import java.util.ArrayList;
import java.util.List;

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
	
	/**
	 * Ta bort ett objekt från databasen
	 * @param m	ModelInterface	Det objekt som önskas tas bort
	 * @param context
	 */
	public void deleteFromDB(ModelInterface m, Context context){
		String dbRep = m.getDatabaseRepresentation();
		if (dbRep.equalsIgnoreCase("assignment")) {
			DatabaseHandlerAssignment dha = new DatabaseHandlerAssignment(context);
			dha.removeAssignment((Assignment)m);
		}
		else if(dbRep.equalsIgnoreCase("contact")){
			DatabaseHandlerContacts dhc = new DatabaseHandlerContacts(context);
			dhc.removeContact((Contact)m);
		}
		else if(dbRep.equalsIgnoreCase("message")){
			DatabaseHandlerMessages dhm = new DatabaseHandlerMessages(context);
			dhm.removeMessage((MessageModel)m);
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
			returnList = dha.getAllAssignments();
		}
		else if(dbRep.equalsIgnoreCase("contact")){
			DatabaseHandlerContacts dhc = new DatabaseHandlerContacts(context);
			returnList = dhc.getAllContacts();
		}
		else if(dbRep.equalsIgnoreCase("message")){
			DatabaseHandlerMessages dhm = new DatabaseHandlerMessages(context);
			returnList = dhm.getAllMessages();
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
			dha.updateModel((Assignment)m);
		}
		else if(dbRep.equalsIgnoreCase("contact")){
			DatabaseHandlerContacts dhc = new DatabaseHandlerContacts(context);
			dhc.updateModel((Contact)m);
		}
		else if(dbRep.equalsIgnoreCase("message")){
			DatabaseHandlerMessages dhm = new DatabaseHandlerMessages(context);
			dhm.updateModel((MessageModel)m);
		}
	}
}
