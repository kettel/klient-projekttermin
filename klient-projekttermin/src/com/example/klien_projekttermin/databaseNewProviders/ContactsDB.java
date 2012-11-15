package com.example.klien_projekttermin.databaseNewProviders;

import com.example.klien_projekttermin.databaseNewProviders.Contact.Contacts;

import net.sqlcipher.database.SQLiteDatabase;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;


public class ContactsDB {
	private static boolean isLibraryLoaded = false;
    /**
     * 
     */
    private ContactsDB () {
    }

    private static final ContactsDB  instance = new ContactsDB ();

    public static ContactsDB  getInstance(Context context) {
    	// Ladda vid behov in SQLCipher-bibliotek filer
    	if (!isLibraryLoaded) {
    		SQLiteDatabase.loadLibs(context);
    		isLibraryLoaded = true;
    	}
        return instance;
    }

    // L�gg till en ny kontakt
    public void addContact(ContentResolver contentResolver, String name) {
        ContentValues contentValue = new ContentValues();
        // note that we don't have to add an id as our table set id as autoincrement
        contentValue.put(Contacts.NAME, name);
        contentResolver.insert(Contacts.CONTENT_URI, contentValue);
    }
    
    public int getCount(ContentResolver contentResolver){
    	int returnCount = 0;
    	// SELECT * WHERE _id IS NOT null
    	Cursor cursor = contentResolver.query(
    			Contacts.CONTENT_URI, null,Contacts.CONTACT_ID + " IS NOT null", null, null);
    	returnCount = cursor.getCount();
    	cursor.close();
    	return returnCount;
    }
    
    public String getAll(ContentResolver contentResolver){
    	Cursor cursor = contentResolver.query(
    			Contacts.CONTENT_URI, null,Contacts.CONTACT_ID + " IS NOT null", null, null);
    	Log.d("DB","Cursorstorlek: " + cursor.getCount());
    	String ret = new String();
    	if (cursor.moveToFirst()) {
			do {
				Log.d("DB","ID: " + Integer.toString(cursor.getInt(0)));
				Log.d("DB","Namn: " + cursor.getString(1));
				ret += cursor.getString(1);
			} while (cursor.moveToNext());
    	}
    	cursor.close();
    	return ret;
    }
    
    public void updateContact(ContentResolver contentResolver, String newName, String id) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(Contacts.NAME, newName);
        contentResolver.update(Contacts.CONTENT_URI, contentValue, Contacts.CONTACT_ID + "='" + id + "'", null);
    }

    // erases all entries in the database
    public void refreshCache(ContentResolver contentResolver) {
        int delete = contentResolver.delete(Contacts.CONTENT_URI, null, null);
        System.out.println("DELETED " + delete + " RECORDS FROM CONTACTS DB");
    }

}

