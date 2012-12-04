package database;

import java.util.ArrayList;
import java.util.List;

import models.Contact;
import models.ModelInterface;

import database.ContactTable.Contacts;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;


public class ContactsDB {
	
    /**
     * 
     */
    private ContactsDB () {
    }

    private static final ContactsDB  instance = new ContactsDB ();

    public static ContactsDB getInstance() {
        return instance;
    }

    // L�gg till en ny kontakt
    public void addContact(ContentResolver contentResolver, Contact contact) {
        ContentValues values = new ContentValues();
        values.put(Contacts.NAME, contact.getContactName());
        contentResolver.insert(Contacts.CONTENT_URI, values);
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
    }

	public List<ModelInterface> getAllContacts(ContentResolver contentResolver) {
		List<ModelInterface> returnList = new ArrayList<ModelInterface>();
		Cursor cursor = contentResolver.query(
    			Contacts.CONTENT_URI, null,Contacts.CONTACT_ID + " IS NOT null", null, null);
    	if (cursor.moveToFirst()) {
			do {
				long id = 0;
				String name = new String();
				for (int i = 0; i < cursor.getColumnCount(); i++){
					if(cursor.getColumnName(i).equalsIgnoreCase(Contacts.CONTACT_ID)){
						id = cursor.getInt(i);
					}else if(cursor.getColumnName(i).equalsIgnoreCase(Contacts.NAME)){
						name = cursor.getString(i);
					}
				}
				Contact contact = new Contact(id,name);
				returnList.add(contact);
			} while (cursor.moveToNext());
    	}
    	cursor.close();
    	return returnList;
	}

	public void delete(ContentResolver contentResolver, Contact contact) {
		contentResolver.delete(Contacts.CONTENT_URI, Contacts.CONTACT_ID + " = " + Long.toString(contact.getId()), null);
		
	}
	
	public void updateContact(ContentResolver contentResolver, Contact contact) {
		ContentValues values = new ContentValues();
        values.put(Contacts.NAME, contact.getContactName());
        int updated = contentResolver.update(Contacts.CONTENT_URI, values, null, null);
        Log.d("DB", "Uppdaterade " + updated + " contacts.");
	}

}

