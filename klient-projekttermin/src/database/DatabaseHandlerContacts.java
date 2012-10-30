package database;

import android.content.Context;
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
    private static final String KEY_CONTACT_NAME = "contact name";
    private static final String KEY_PH_NO = "phone number";
	private static final String KEY_EMAIL = "email";
	private static final String KEY_CLEARANCE_LEVEL = "clearance level";
	private static final String KEY_CLASSIFICATION = "classification";
	private static final String KEY_COMMENT = "comment";

 
    public DatabaseHandlerContacts(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
 
    // Skapa tabell
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_ASSIGNMENTS_TABLE = "CREATE TABLE " + TABLE_CONTACTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"  
        		+ KEY_CONTACT_NAME + " TEXT,"
                + KEY_PH_NO + " TEXT,"
        		+ KEY_EMAIL + " TEXT,"
                + KEY_CLEARANCE_LEVEL + " TEXT,"
                + KEY_CLASSIFICATION + " TEXT,"
                + KEY_COMMENT + ")";
        db.execSQL(CREATE_ASSIGNMENTS_TABLE);
    	//executeSQLScript(db, "assignments.sql", this);
    }
 
    // Uppgradera databasen vid behov (om en äldre version existerar)
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Om en äldre version existerar, ta bort den
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ASSIGNMENTS);
 
        // Skapa sedan databasen igen
        onCreate(db);
    }
}
