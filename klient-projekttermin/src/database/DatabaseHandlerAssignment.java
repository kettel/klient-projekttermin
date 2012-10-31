package database;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import models.Assignment;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.format.Time;

public class DatabaseHandlerAssignment extends SQLiteOpenHelper {
	// Alla statiska variabler
    // Databas version
    private static final int DATABASE_VERSION = 1;
 
    // Databasens namn
    private static final String DATABASE_NAME = "assignmentManager";
 
    // Assignments tabellnamn
    private static final String TABLE_ASSIGNMENTS = "assignment";

    // Assignmenst tabellkolumnnamn
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
 
    /**
     * Alla CRUD(Create, Read, Update, Delete) operationer
     */
    
    /**
     * Lägg till ett uppdrag
     * @param assignment	Det uppdrag som ska läggas till i databasen
     */
    public void addAssignment(Assignment assignment) {
        SQLiteDatabase db = this.getWritableDatabase();
 
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, assignment.getName()); 
        values.put(KEY_LAT, assignment.getLat());
        values.put(KEY_LON, assignment.getLon());
        values.put(KEY_RECEIVER, assignment.getReceiver());
        values.put(KEY_SENDER, assignment.getSender());
        values.put(KEY_ASSIGNMENTDESCRIPTION, assignment.getAssignmentDescription());
        values.put(KEY_TIMESPAN, assignment.getTimeSpan().toString());
        values.put(KEY_ASSIGNMENTSTATUS, assignment.getAssignmentStatus());
        values.put(KEY_STREETNAME, assignment.getStreetName());
        values.put(KEY_SITENAME, assignment.getSiteName());

        // Lägg till assignment i databasen
        db.insert(TABLE_ASSIGNMENTS, null, values);
        // Stäng databasen. MYCKET VIKTIGT!!
        db.close(); 
    }
 
    /**
     * Hämta ett uppdrag
     * @param id	id för det sökta uppdraget
     * @return	Assignment	det funna uppdraget
     */
    public Assignment getAssignment(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
 
        Cursor cursor = db.query(TABLE_ASSIGNMENTS, 
        		new String[] {KEY_ID, KEY_NAME, KEY_LAT, KEY_LON, 
        		KEY_RECEIVER, KEY_SENDER, KEY_ASSIGNMENTDESCRIPTION, 
        		KEY_TIMESPAN, KEY_ASSIGNMENTSTATUS, KEY_STREETNAME, 
        		KEY_SITENAME}, KEY_ID + "=?", new String[] { String.valueOf(id) }
        		, null, null, null, null);
        
        if (cursor != null)
            cursor.moveToFirst();
 
        // Ignorera cursors första element då det är ID-nummer från databasen för assignment
        // Vad vi har sagt än så är det irrelevant.
        Assignment assignment = new Assignment(cursor.getString(1),
        									Long.parseLong(cursor.getString(2)), 
        									Long.parseLong(cursor.getString(3)),
        									cursor.getString(4),
        									cursor.getString(5),
        									cursor.getString(6),
        									new Time(cursor.getString(7)),
        									cursor.getString(8),
        									cursor.getString(9),
        									cursor.getString(10));
        // Returnera den funna kontakten
        return assignment;
    }
 
    /**
     * Hämta alla uppdrag i databasen
     * @return	List<Assignment>	En lista med Assignment-objekt
     */
    public List<Assignment> getAllAssignments() {
        List<Assignment> assignmentList = new ArrayList<Assignment>();
        
        // Select All frågan. Ze classic! Dvs, hämta allt från ASSIGNMENTS-databasen
        String selectQuery = "SELECT  * FROM " + TABLE_ASSIGNMENTS;
 
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
 
        // Loopa igenom alla rader och lägg till dem i listan 
        if (cursor.moveToFirst()) {
            do {
                Assignment assignment = new Assignment(cursor.getString(1),
						Long.parseLong(cursor.getString(2)), 
						Long.parseLong(cursor.getString(3)),
						cursor.getString(4),
						cursor.getString(5),
						cursor.getString(6),
						new Time(cursor.getString(7)),
						cursor.getString(8),
						cursor.getString(9),
						cursor.getString(10));
                
                assignmentList.add(assignment);
            } while (cursor.moveToNext());
        }
 
        // Returnera kontaktlistan
        return assignmentList;
    }
 
    /**
     * Uppdatera ett uppdrag
     * @param assignment	Uppdraget som önskas uppdateras
     * @return	int			id för den kontakt som uppdaterades
     * TODO: Lägg till ID för assignment för att det ska gå att uppdatera ett uppdrag 
     * på ett tillfredsställande vis
     */
    public int updateAssignment(Assignment assignment) {
        SQLiteDatabase db = this.getWritableDatabase();
 
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, assignment.getName()); 
        values.put(KEY_LAT, assignment.getLat());
        values.put(KEY_LON, assignment.getLon());
        values.put(KEY_RECEIVER, assignment.getReceiver());
        values.put(KEY_SENDER, assignment.getSender());
        values.put(KEY_ASSIGNMENTDESCRIPTION, assignment.getAssignmentDescription());
        values.put(KEY_TIMESPAN, assignment.getTimeSpan().toString());
        values.put(KEY_ASSIGNMENTSTATUS, assignment.getAssignmentStatus());
        values.put(KEY_STREETNAME, assignment.getStreetName());
        values.put(KEY_SITENAME, assignment.getSiteName());
 
        // Uppdatera rad för kontakten som ska uppdateras
        //return db.update(TABLE_ASSIGNMENTS, values, KEY_ID + " = ?",
        		// TODO: ID för ett assignments behövs för att kunna uppdateras
                //new String[] { String.valueOf(contact.getID()) });
        // Returnera -1 så länge som metoden är KASS!
        return -1;
    }
 
    /**
     * Ta bort en kontakt
     * @param contact	Kontakten som ska tas bort
     * TODO: Kirra ID för assignments för att kunna ta bort assignments
     */
    public void deleteAssignment(Assignment assignment) {
        //SQLiteDatabase db = this.getWritableDatabase();
        //db.delete(TABLE_ASSIGNMENTS, KEY_ID + " = ?",
        		// TODO: Assignment behöver visst ID för att man på ett tillfredsställande
        		// 		 vis ska kunna radera assignments.
        //        new String[] { String.valueOf(assignment.getID()) });
        //db.close();
    }
 
    /**
     * Räkna antal assignments i databasen
     * @return	int		Antal assignments
     */
    public int getContactsCount() {
        String countQuery = "SELECT  * FROM " + TABLE_ASSIGNMENTS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();
 
        // Returnera antalet assignments
        return cursor.getCount();
    }
    
    /**
     * Tanken med denna metod är att man ska läsa SQL-kommandon från fil och exekvera dessa 
     * istället för att hårdkoda dessa i Java.
     * @param database
     * @param dbname
     * @param context
     */
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
