package database;

import java.util.ArrayList;
import java.util.List;

import model.Assignment;
import model.Contact;
import model.MessageModel;
import model.ModelInterface;
import android.content.Context;

/**
 * En klass med metoder f�r Create, Remove, Update, Delete (CRUD) operationer p�
 * databasen.
 * 
 * @author kettel
 * 
 */
public class Database {

	/**
	 * L�gg till ett uppdrag/kontakt/meddelande till r�tt databas
	 * 
	 * @param m
	 *            ModellInterface av objekt som ska l�ggas till
	 * @param context
	 *            Aktivitetens kontext s� data l�ggs i r�tt databas
	 */
	public void addToDB(ModelInterface m, Context context) {
		String dbRep = m.getDatabaseRepresentation();
		if (dbRep.equalsIgnoreCase("assignment")) {
			DatabaseHandlerAssignment dha = new DatabaseHandlerAssignment(
					context);
			dha.addAssignment((Assignment) m);
		} else if (dbRep.equalsIgnoreCase("contact")) {
			DatabaseHandlerContacts dhc = new DatabaseHandlerContacts(context);
			dhc.addContact((Contact) m);
		} else if (dbRep.equalsIgnoreCase("message")) {
			DatabaseHandlerMessages dhm = new DatabaseHandlerMessages(context);
			dhm.addMessage((MessageModel) m);
		}
	}

	/**
	 * R�kna antal poster i vald databas
	 * 
	 * @param m
	 *            datatypen f�r den databas som ska r�knas samman
	 * @param context
	 *            programkontexten s� r�tt databas kan v�ljas
	 * @return
	 */
	public int getDBCount(ModelInterface m, Context context) {
		String dbRep = m.getDatabaseRepresentation();
		int returnCount = 0;
		if (dbRep.equalsIgnoreCase("assignment")) {
			DatabaseHandlerAssignment dha = new DatabaseHandlerAssignment(
					context);
			returnCount = dha.getAssignmentCount();
		} else if (dbRep.equalsIgnoreCase("contact")) {
			DatabaseHandlerContacts dhc = new DatabaseHandlerContacts(context);
			returnCount = dhc.getContactCount();
		} else if (dbRep.equalsIgnoreCase("message")) {
			DatabaseHandlerMessages dhm = new DatabaseHandlerMessages(context);
			returnCount = dhm.getMessageCount();
		}
		return returnCount;
	}

	public void deleteFromDB(ModelInterface m, Context context) {
		String dbRep = m.getDatabaseRepresentation();
		if (dbRep.equalsIgnoreCase("assignment")) {
			DatabaseHandlerAssignment dha = new DatabaseHandlerAssignment(
					context);
			dha.removeAssignment((Assignment) m);
		} else if (dbRep.equalsIgnoreCase("contact")) {
			DatabaseHandlerContacts dhc = new DatabaseHandlerContacts(context);
			dhc.removeContact((Contact) m);
		} else if (dbRep.equalsIgnoreCase("message")) {
			DatabaseHandlerMessages dhm = new DatabaseHandlerMessages(context);
			dhm.removeMessage((MessageModel) m);
		}
	}

	public List<ModelInterface> getAllFromDB(ModelInterface m, Context context) {
		String dbRep = m.getDatabaseRepresentation();
		List<ModelInterface> returnList = new ArrayList<ModelInterface>();
		if (dbRep.equalsIgnoreCase("assignment")) {
			DatabaseHandlerAssignment dha = new DatabaseHandlerAssignment(
					context);
			returnList = dha.getAllAssignments();
		} else if (dbRep.equalsIgnoreCase("contact")) {
			DatabaseHandlerContacts dhc = new DatabaseHandlerContacts(context);
			returnList = dhc.getAllContacts();
		} else if (dbRep.equalsIgnoreCase("message")) {
			DatabaseHandlerMessages dhm = new DatabaseHandlerMessages(context);
			returnList = dhm.getAllMessages();
		}
		return returnList;
	}
}