package com.example.klien_projekttermin.database;

import java.util.ArrayList;
import java.util.List;

import models.Contact;
import models.ModelInterface;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandlerContacts extends SQLiteOpenHelper{
	// Alla statiska variabler
    // Databas version
    private static final int DATABASE_VERSION = 1;
 
    // Databasens namn
    private static final String DATABASE_NAME = "contactManager";
 
    // Contacts tabellnamn
    private static final String TABLE_CONTACTS = "contacts";

    // Contacts tabellkolumnnamn
    private static final String KEY_ID = "_id";
    private static final String KEY_CONTACT_NAME = "contact_name";
 
    public DatabaseHandlerContacts(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
 
    // Skapa tabell
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_CONTACTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"  
        		+ KEY_CONTACT_NAME + " TEXT)";
        db.execSQL(CREATE_CONTACTS_TABLE);
    	//executeSQLScript(db, "assignments.sql", this);
    }
 
    // Uppgradera databasen vid behov (om en äldre version existerar)
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Om en äldre version existerar, ta bort den
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
 
        // Skapa sedan databasen igen
        onCreate(db);
    }
    
    /**
     * Lägg till en kontakt
     * @param ContactTable	Den kontakt som ska läggas till i databasen
     */
    public void addContact(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();
 
        ContentValues values = new ContentValues();
        values.put(KEY_CONTACT_NAME, contact.getContactName());
        // Lägg till kontakter i databasen
        db.insert(TABLE_CONTACTS, null, values);
        // Stäng databasen. MYCKET VIKTIGT!!
        db.close(); 
    }

	public int getContactCount() {
		String countQuery = "SELECT * FROM " + TABLE_CONTACTS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        return count;
	}

	public List<ModelInterface> getAllContacts() {
		List<ModelInterface> contactList = new ArrayList<ModelInterface>();
		// Select All frågan. Ze classic! Dvs, hämta allt från MESSAGES-databasen
        String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS;
 
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
 
        // Loopa igenom alla rader och lägg till dem i listan 
        // TODO: Få ordning på BLOB, dvs hämta och dona med bild.
        
        if (cursor.moveToFirst()) {
            do {
                Contact contact = new Contact(
                		Long.valueOf(cursor.getString(0)),
                		cursor.getString(1));
                
                contactList.add(contact);
            } while (cursor.moveToNext());
        }
 		cursor.close();
 		db.close();
        // Returnera meddelandelistan
		return contactList;
	}

	public void removeContact(Contact contact) {
		SQLiteDatabase db = this.getWritableDatabase();
    	db.delete(TABLE_CONTACTS, KEY_ID + " = ?",
                new String[] { String.valueOf(contact.getId()) });
        db.close();
	}

	public void updateModel(Contact c) {
		SQLiteDatabase db = this.getReadableDatabase();

		String UPDATE_CONTACTS = "UPDATE " + TABLE_CONTACTS + " SET "
        		+ KEY_CONTACT_NAME + " = \"" + c.getContactName() + "\", "
                + "WHERE " + KEY_ID + " = " + Long.toString(c.getId());

        db.execSQL(UPDATE_CONTACTS);
        
        db.close();
	}
}
