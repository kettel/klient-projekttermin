package database;

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
    private static final String KEY_ID = "id";
    private static final String KEY_CONTACT_NAME = "contact_name";
    private static final String KEY_PH_NO = "phone_number";
	private static final String KEY_EMAIL = "email";
	private static final String KEY_CLEARANCE_LEVEL = "clearance_level";
	private static final String KEY_CLASSIFICATION = "classification";
	private static final String KEY_COMMENT = "comment";

 
    public DatabaseHandlerContacts(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
 
    // Skapa tabell
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_CONTACTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"  
        		+ KEY_CONTACT_NAME + " TEXT,"
                + KEY_PH_NO + " TEXT,"
        		+ KEY_EMAIL + " TEXT,"
                + KEY_CLEARANCE_LEVEL + " TEXT,"
                + KEY_CLASSIFICATION + " TEXT,"
                + KEY_COMMENT + " TEXT"+")";
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
     * @param Contact	Den kontakt som ska läggas till i databasen
     */
    public void addContact(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();
 
        ContentValues values = new ContentValues();
        values.put(KEY_CONTACT_NAME, contact.getContactName());
        values.put(KEY_PH_NO, contact.getContactPhoneNumber().toString());
        values.put(KEY_EMAIL, contact.getContactEmail());
        values.put(KEY_CLEARANCE_LEVEL, contact.getContactClearanceLevel());
        values.put(KEY_CLASSIFICATION, contact.getContactClassification());
        values.put(KEY_COMMENT, contact.getContactComment());
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
                		cursor.getString(1),
                		Long.valueOf(cursor.getString(2)),
                		cursor.getString(3),
                		cursor.getString(4),
                		cursor.getString(5),
                		cursor.getString(6));
                
                contactList.add(contact);
            } while (cursor.moveToNext());
        }
 		
        // Returnera meddelandelistan
		return contactList;
	}

	public void removeContact(Contact contact) {
		SQLiteDatabase db = this.getWritableDatabase();
    	db.delete(TABLE_CONTACTS, KEY_ID + " = ?",
                new String[] { String.valueOf(contact.getId()) });
        db.close();
	}
}
