package database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import models.Assignment;
import models.AuthenticationModel;
import models.Contact;
import models.MessageModel;
import models.ModelInterface;
import net.sqlcipher.database.SQLiteDatabase;

public class Database {
	// SQLCipher-lösen
	public static final String PASSWORD = "password";
	
	public static boolean isLibraryLoaded = false;
	
	private static AssignmentsDB assignmentsDB;
	private static ContactsDB contactsDB;
	private static MessagesDB messagesDB;
	private static AuthenticationDB authenticationDB;
	
	private Database(){}
	
	private static Database instance = new Database();
	
	public static Database getInstance(Context context){
		// Ladda vid behov in SQLCipher-bibliotek filer
    	if (!isLibraryLoaded) {
    		SQLiteDatabase.loadLibs(context);
    		isLibraryLoaded = true;
    	}
    	// Hämta DB från var och en av de tre (snart fyra ContentProv wrappers)
    	assignmentsDB = AssignmentsDB.getInstance();
    	contactsDB = ContactsDB.getInstance();
    	messagesDB = MessagesDB.getInstance();
    	authenticationDB = AuthenticationDB.getInstance();
        return instance;
	}
	
	public void addToDB(ModelInterface m, ContentResolver contentResolver){
		String dbRep = m.getDatabaseRepresentation();
		if (dbRep.equalsIgnoreCase("assignment")) {
			assignmentsDB.addAssignment(contentResolver, (Assignment) m);
		}
		else if(dbRep.equalsIgnoreCase("contact")){
			contactsDB.addContact(contentResolver, (Contact) m);
		}
		else if(dbRep.equalsIgnoreCase("message")){
			messagesDB.addMessage(contentResolver, (MessageModel) m);
		}
		else if(dbRep.equalsIgnoreCase("authentication")){
			authenticationDB.addAuthenticationContent(contentResolver, (AuthenticationModel) m);
		}
	}
	
	public int getDBCount(ModelInterface m, ContentResolver contentResolver){
		String dbRep = m.getDatabaseRepresentation();
		int returnCount = 0;
		if (dbRep.equalsIgnoreCase("assignment")) {
			returnCount = assignmentsDB.getCount(contentResolver);
		}
		else if(dbRep.equalsIgnoreCase("contact")){
			returnCount = contactsDB.getCount(contentResolver);
		}
		else if(dbRep.equalsIgnoreCase("message")){
			returnCount = messagesDB.getCount(contentResolver);
		}
		else if(dbRep.equalsIgnoreCase("authentication")){
			returnCount = authenticationDB.getCount(contentResolver);
		}
		return returnCount;
	}
	
	public void deleteFromDB(ModelInterface m, ContentResolver contentResolver){
		String dbRep = m.getDatabaseRepresentation();
		if (dbRep.equalsIgnoreCase("assignment")) {
			assignmentsDB.delete(contentResolver,(Assignment) m);
		}
		else if(dbRep.equalsIgnoreCase("contact")){
			contactsDB.delete(contentResolver, (Contact)m);
		}
		else if(dbRep.equalsIgnoreCase("message")){
			messagesDB.delete(contentResolver, (MessageModel)m);
		}
		else if(dbRep.equalsIgnoreCase("authentication")){
			authenticationDB.delete(contentResolver, (AuthenticationModel)m);
		}
	}
	
	public List<ModelInterface> getAllFromDB(ModelInterface m, ContentResolver contentResolver){
		String dbRep = m.getDatabaseRepresentation();
		List<ModelInterface> returnList = new ArrayList<ModelInterface>();
		if (dbRep.equalsIgnoreCase("assignment")) {
			returnList = assignmentsDB.getAllAssignments(contentResolver);
		}
		else if(dbRep.equalsIgnoreCase("contact")){
			returnList = contactsDB.getAllContacts(contentResolver);
		}
		else if(dbRep.equalsIgnoreCase("message")){
			returnList = messagesDB.getAllMessages(contentResolver);
		}
		else if(dbRep.equalsIgnoreCase("authentication")){
			returnList = authenticationDB.getAllAuthenticationModels(contentResolver);
		}
		return returnList;
	}
	
	public void updateModel(ModelInterface m, ContentResolver contentResolver){
		String dbRep = m.getDatabaseRepresentation();
		if (dbRep.equalsIgnoreCase("assignment")) {
			assignmentsDB.updateAssignment(contentResolver, (Assignment) m);
		}
		else if(dbRep.equalsIgnoreCase("contact")){
			contactsDB.updateContact(contentResolver, (Contact) m);
		}
		else if(dbRep.equalsIgnoreCase("message")){
			messagesDB.updateMessage(contentResolver, (MessageModel)m);
		}
		else if(dbRep.equalsIgnoreCase("authentication")){
			authenticationDB.updateAuthentication(contentResolver, (AuthenticationModel) m);
		}
	}
}
