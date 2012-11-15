package com.example.klien_projekttermin.databaseNewProviders;

import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import models.Contact;
import models.ModelInterface;
import net.sqlcipher.database.SQLiteDatabase;

public class Database {
	
	private static boolean isLibraryLoaded = false;
	
	// TODO: assignmentDB
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
    	// TODO: assignmentDB
    	contactsDB = ContactsDB.getInstance();
    	// TODO: messageDB
        return instance;
	}
	
	public void addToDB(ModelInterface m, ContentResolver contentResolver){
		String dbRep = m.getDatabaseRepresentation();
		if (dbRep.equalsIgnoreCase("assignment")) {
			// TODO
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
		return 0;
	}
	
	public void deleteFromDB(ModelInterface m, ContentResolver contentResolver){
		
	}
	
	public List<ModelInterface> getAllFromDB(ModelInterface m, ContentResolver contentResolver){
		return null;
	}
	
	public void updateModel(ModelInterface m, ContentResolver contentResolver){
		
	}
}
