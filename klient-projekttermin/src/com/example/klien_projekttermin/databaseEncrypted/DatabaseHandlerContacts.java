package com.example.klien_projekttermin.databaseEncrypted;

import java.util.ArrayList;
import java.util.List;

import com.example.klien_projekttermin.models.Contact;
import com.example.klien_projekttermin.models.ModelInterface;

import net.sqlcipher.database.SQLiteDatabase;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class DatabaseHandlerContacts extends DatabaseHandler{

	// Contacts tabellnamn
    private static final String TABLE_CONTACTS = "contact";

    // Contacts tabellkolumnnamn
    private static final String KEY_CONTACT_NAME = "contact_name";
    private static final String KEY_PH_NO = "phone_number";
	private static final String KEY_EMAIL = "email";
	private static final String KEY_CLEARANCE_LEVEL = "clearance_level";
	private static final String KEY_CLASSIFICATION = "classification";
	private static final String KEY_COMMENT = "comment";
	
	public DatabaseHandlerContacts(Context c) {
		// Skicka Context till DatabaseHandler
		super(c);
		
		// Initiera Messages-tabellen.
		initiateModelDatabase();
	}
	
	@Override
	public void initiateModelDatabase() {
		SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(databaseFile, PASSWORD, null);
		String CREATE_CONTACTS_TABLE = "CREATE TABLE IF NOT EXISTS " 
				+ TABLE_CONTACTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"  
        		+ KEY_CONTACT_NAME + " TEXT,"
                + KEY_PH_NO + " TEXT,"
        		+ KEY_EMAIL + " TEXT,"
                + KEY_CLEARANCE_LEVEL + " TEXT,"
                + KEY_CLASSIFICATION + " TEXT,"
                + KEY_COMMENT + " TEXT"+")";
        database.execSQL(CREATE_CONTACTS_TABLE);
        database.close();
	}

	@Override
	public void addModel(ModelInterface m) {
		Contact contact = (Contact) m;
		SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(databaseFile, PASSWORD, null);
		
		ContentValues values = new ContentValues();
        values.put(KEY_CONTACT_NAME, contact.getContactName());
        values.put(KEY_PH_NO, contact.getContactPhoneNumber().toString());
        values.put(KEY_EMAIL, contact.getContactEmail());
        values.put(KEY_CLEARANCE_LEVEL, contact.getContactClearanceLevel());
        values.put(KEY_CLASSIFICATION, contact.getContactClassification());
        values.put(KEY_COMMENT, contact.getContactComment());
        
        // Lägg till kontakter i databasen
        database.insert(TABLE_CONTACTS, null, values);
        
        // Stäng databasen. MYCKET VIKTIGT!!
        database.close(); 
	}

	@Override
	public void updateModel(ModelInterface m) {
		Contact contact = (Contact) m;
		SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(databaseFile, PASSWORD, null);
		
		String UPDATE_CONTACTS = "UPDATE " + TABLE_CONTACTS + " SET "
        		+ KEY_CONTACT_NAME + " = \"" + contact.getContactName() + "\", "
                + KEY_PH_NO + " = \"" + contact.getContactPhoneNumber() + "\", "
                + KEY_CLEARANCE_LEVEL + " = \"" +contact.getContactClearanceLevel()  + "\", "
                + KEY_EMAIL + " = \"" + contact.getContactEmail() + "\", "
                + KEY_CLASSIFICATION + " = \"" + contact.getContactClassification() + "\", "
                + KEY_COMMENT + " = \"" + contact.getContactComment() + "\" "
                + "WHERE " + KEY_ID + " = " + Long.toString(contact.getId());
		
        database.execSQL(UPDATE_CONTACTS);
        
        database.close();
	}

	@Override
	public List<ModelInterface> getAllModels(ModelInterface m) {
		List<ModelInterface> contactList = new ArrayList<ModelInterface>();
		// Select All frågan. Ze classic! Dvs, hämta allt från MESSAGES-databasen
        String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS;
 
        SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(databaseFile, PASSWORD, null);
        Cursor cursor = database.rawQuery(selectQuery, null);
 
        // Loopa igenom alla rader och lägg till dem i listan 
        // TODO: Få ordning på BLOB, dvs hämta och dona med bild.
        
        if (cursor.moveToFirst()) {
            do {
                Contact contact = new Contact(
                		Long.valueOf(cursor.getString(0)),
                		cursor.getString(1),
                		Long.valueOf(cursor.getString(2)),
                		cursor.getString(3),
                		cursor.getString(4),
                		cursor.getString(5),
                		cursor.getString(6));
                
                contactList.add(contact);
            } while (cursor.moveToNext());
        }
 		cursor.close();
 		database.close();
        // Returnera meddelandelistan
		return contactList;
	}
}