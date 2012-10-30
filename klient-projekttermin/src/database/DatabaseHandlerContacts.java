package database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandlerContacts extends SQLiteOpenHelper{
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
    private static final String KEY_LAT = "lat";
	private static final String KEY_LON = "long";
	private static final String KEY_RECEIVER = "receiver";
	private static final String KEY_SENDER = "sender";
	private static final String KEY_ASSIGNMENTDESCRIPTION = "description";
	private static final String KEY_TIMESPAN = "timespan";
	private static final String KEY_ASSIGNMENTSTATUS = "assignmentstatus";
	private static final String KEY_STREETNAME ="streetname";
	private static final String KEY_SITENAME = "sitename";
 
    public DatabaseHandlerAssignment(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
 
    // Skapa tabell
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_ASSIGNMENTS_TABLE = "CREATE TABLE " + TABLE_ASSIGNMENTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"  
        		+ KEY_NAME + " TEXT,"
                + KEY_LAT + " TEXT,"
        		+ KEY_LON + " TEXT,"
                + KEY_RECEIVER + " TEXT,"
                + KEY_SENDER + " TEXT,"
                + KEY_ASSIGNMENTDESCRIPTION + " TEXT,"
                + KEY_TIMESPAN + " TEXT,"
                + KEY_ASSIGNMENTSTATUS + " TEXT,"
                + KEY_STREETNAME + " TEXT,"
                + KEY_SITENAME + "TEXT" + ")";
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
