package database;

import android.content.Context;
import model.Assignment;
import model.Model;

public class Database{
	public void addToDB(Model m, Context context){
		int dbRep = m.getDBRep();
		switch (dbRep) {
		// Assignment
		case 1:
			DatabaseHandlerAssignment dha = new DatabaseHandlerAssignment(context);
			dha.addAssignment((Assignment)m);
			break;

		// Contacts
		case 2:
			DatabaseHandlerContacts dhc = new DatabaseHandlerContacts(context);
			dhc.addContact((Contact)m);
			break;
		
		// Messages
		case 3:
			DatabaseHandlerMessages dhm = new DatabaseHandlerMessages(context);
			dhm.addMessage((Message)m);
			break;
		}
	}
}
