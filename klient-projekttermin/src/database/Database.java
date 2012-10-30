package database;

import android.content.Context;
import model.Model;

public class Database{
	public void addToDB(Model m, Context context){
		int dbRep = m.getDBRep();
		switch (dbRep) {
		// Assignment
		case 1:
			DatabaseHandlerAssignment dha = new DatabaseHandlerAssignment(context);
			dha.addAssignment(m);
			break;

		// Contacts
		case 2:
			
			break;
		
		// Messages
		case 3:
			break;
		}
	}
}
