package database;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import model.Assignment;
import model.Model;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.format.Time;

import database.DatabaseHelpers;

public class DatabaseHandlerAssignment extends SQLiteOpenHelper {
	// Alla statiska variabler
    // Databas version
    private static final int DATABASE_VERSION = 1;
 
    // Databasens namn
    private static final String DATABASE_NAME = "assignmentManager";
 
    // Contacts tabellnamn
    private static final String TABLE_ASSIGNMENTS = "assignment";

    // Contacts tabellkolumnnamn
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static long KEY_LAT;
	private static long KEY_LON;
	private static String KEY_RECEIVER;
	private static String KEY_SENDER;
	private static String KEY_ASSIGNMENTDESCRIPTION;
	private static Time KEY_TIMESPAN;
	private static String KEY_ASSIGNMENTSTATUS;
	private static String KEY_STREETNAME;
	private static String KEY_ITENAME;
 
    public DatabaseHandlerAssignment(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
 
    // Skapa tabell
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_ASSIGNMENTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"  
        		+ KEY_NAME + " TEXT,"
                + KEY_PH_NO + " TEXT" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    	//executeSQLScript(db, "assignments.sql", this);
    }
 
    // Uppgradera databasen
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Om en äldre version existerar, ta bort den
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ASSIGNMENTS);
 
        // Skapa sedan databasen igen
        onCreate(db);
    }
 
    /**
     * Alla CRUD(Create, Read, Update, Delete) operationer
     */
    
    /**
     * Lägg till en kontakt
     * @param contact	Den kontakt som ska läggas till i databasen
     */
    public void addAssignment(Assignment assignment) {
        SQLiteDatabase db = this.getWritableDatabase();
 
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, assignment.getName()); // Kontaktens namn
        values.put(KEY_PH_NO, assignment.getPhoneNumber()); // Kontaktens telefon
 
        // Lägg till kontakten i databasen
        db.insert(TABLE_ASSIGNMENTS, null, values);
        // Stäng databasen. MYCKET VIKTIGT!!
        db.close(); 
    }
 
    /**
     * Hämta en kontakt
     * @param id	id för den sökta kontakten
     * @return	Contact
     */
    public Contact getContact(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
 
        Cursor cursor = db.query(TABLE_ASSIGNMENTS, new String[] { KEY_ID,
                KEY_NAME, KEY_PH_NO }, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
 
        Contact contact = new Contact(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), cursor.getString(2));
        // Returnera den funna kontakten
        return contact;
    }
 
    /**
     * Hämta alla kontakter
     * @return	List<Contact>	En lista med Contact-objekt
     */
    public List<Contact> getAllContacts() {
        List<Contact> contactList = new ArrayList<Contact>();
        // Select All frågan. Ze classic!
        String selectQuery = "SELECT  * FROM " + TABLE_ASSIGNMENTS;
 
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
 
        // Loopa igenom alla rader och lägg till dem i listan 
        if (cursor.moveToFirst()) {
            do {
                Contact contact = new Contact();
                contact.setID(Integer.parseInt(cursor.getString(0)));
                contact.setName(cursor.getString(1));
                contact.setPhoneNumber(cursor.getString(2));
                // Adding contact to list
                contactList.add(contact);
            } while (cursor.moveToNext());
        }
 
        // Returnera kontaktlistan
        return contactList;
    }
 
    /**
     * Uppdatera en kontakt
     * @param contact	Kontakten som önskas uppdateras
     * @return	int		id för den kontakt som uppdaterades
     */
    public int updateContact(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();
 
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, contact.getName());
        values.put(KEY_PH_NO, contact.getPhoneNumber());
 
        // Uppdatera rad för kontakten som ska uppdateras
        return db.update(TABLE_ASSIGNMENTS, values, KEY_ID + " = ?",
                new String[] { String.valueOf(contact.getID()) });
    }
 
    /**
     * Ta bort en kontakt
     * @param contact	Kontakten som ska tas bort
     */
    public void deleteContact(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ASSIGNMENTS, KEY_ID + " = ?",
                new String[] { String.valueOf(contact.getID()) });
        db.close();
    }
 
    /**
     * Räkna antal kontakter i databasen
     * @return	int	Antal kontakter
     */
    public int getContactsCount() {
        String countQuery = "SELECT  * FROM " + TABLE_ASSIGNMENTS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();
 
        // Returnera antalet kontakter
        return cursor.getCount();
    }
    
    private void executeSQLScript(SQLiteDatabase database, String dbname, Context context) {
    	ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte buf[] = new byte[1024];
        int len;
        AssetManager assetManager = context.getAssets();
        InputStream inputStream = null;
             
        try{
            inputStream = assetManager.open(dbname);
            while ((len = inputStream.read(buf)) != -1) {
                outputStream.write(buf, 0, len);
            }
            outputStream.close();
            inputStream.close();
                 
            String[] createScript = outputStream.toString().split(";");
            for (int i = 0; i < createScript.length; i++) {
                 String sqlStatement = createScript[i].trim();
                // TODO You may want to parse out comments here
                if (sqlStatement.length() > 0) {
                        database.execSQL(sqlStatement + ";");
                }
            }
        } catch (IOException e){
            // TODO Handle Script Failed to Load
        } catch (SQLException e) {
            // TODO Handle Script Failed to Execute
        }
    }
}
