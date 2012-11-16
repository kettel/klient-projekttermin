package com.example.klien_projekttermin.databaseNewProviders;

import java.util.ArrayList;
import java.util.List;

import com.example.klien_projekttermin.databaseNewProviders.AssignmentTable.Assignments;
import com.example.klien_projekttermin.databaseNewProviders.ContactTable.Contacts;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import models.Assignment;
import models.Contact;
import models.ModelInterface;
import net.sqlcipher.database.SQLiteDatabase;

public class Database {
	// SQLCipher-lösen
	public static final String PASSWORD = "password";
	
	public static boolean isLibraryLoaded = false;
	
	private static AssignmentsDB assignmentsDB;
	private static ContactsDB contactsDB;
	// TODO: messageDB
	
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
    	// TODO: messageDB
        return instance;
	}
	
	public void addToDB(ModelInterface m, ContentResolver contentResolver){
		String dbRep = m.getDatabaseRepresentation();
		if (dbRep.equalsIgnoreCase("assignment")) {
			Assignment assignment = (Assignment) m;
			assignmentsDB.addAssignment(contentResolver, assignment);
		}
		else if(dbRep.equalsIgnoreCase("contact")){
			Contact contact = (Contact) m;
			contactsDB.addContact(contentResolver, contact);
		}
		else if(dbRep.equalsIgnoreCase("message")){
			// TODO
		}
	}
	
	public int getDBCount(ModelInterface m, ContentResolver contentResolver){
		String dbRep = m.getDatabaseRepresentation();
		int returnCount = 0;
		if (dbRep.equalsIgnoreCase("assignment")) {
			Cursor cursor = contentResolver.query(
	    			Assignments.CONTENT_URI, null,Assignments.ASSIGNMENT_ID + " IS NOT null", null, null);
	    	returnCount = cursor.getCount();
	    	cursor.close();
		}
		else if(dbRep.equalsIgnoreCase("contact")){
			Cursor cursor = contentResolver.query(
	    			Contacts.CONTENT_URI, null,Contacts.CONTACT_ID + " IS NOT null", null, null);
	    	returnCount = cursor.getCount();
	    	cursor.close();
		}
		else if(dbRep.equalsIgnoreCase("message")){
			// TODO Message Count
		}
		return returnCount;
	}
	
	public void deleteFromDB(ModelInterface m, ContentResolver contentResolver){
		String dbRep = m.getDatabaseRepresentation();
		if (dbRep.equalsIgnoreCase("assignment")) {
			// TODO
		}
		else if(dbRep.equalsIgnoreCase("contact")){
			// TODO
		}
		else if(dbRep.equalsIgnoreCase("message")){
			// TODO
		}
	}
	
	public List<ModelInterface> getAllFromDB(ModelInterface m, ContentResolver contentResolver){
		String dbRep = m.getDatabaseRepresentation();
		List<ModelInterface> returnList = new ArrayList<ModelInterface>();
		if (dbRep.equalsIgnoreCase("assignment")) {
			returnList = assignmentsDB.getAllAssignments(contentResolver);
		}
		else if(dbRep.equalsIgnoreCase("contact")){
			returnList = contactsDB.getAllAssignments(contentResolver);
		}
		else if(dbRep.equalsIgnoreCase("message")){
			// TODO
		}
		return returnList;
	}
	
	public void updateModel(ModelInterface m, ContentResolver contentResolver){
		String dbRep = m.getDatabaseRepresentation();
		if (dbRep.equalsIgnoreCase("assignment")) {
			Assignment assignment = (Assignment) m;
			assignmentsDB.addAssignment(contentResolver, assignment);
		}
		else if(dbRep.equalsIgnoreCase("contact")){
			Contact contact = (Contact) m;
			contactsDB.addContact(contentResolver, contact);
		}
		else if(dbRep.equalsIgnoreCase("message")){
			// TODO
		}
	}
}
