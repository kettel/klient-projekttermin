package database;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import model.Assignment;
import model.ModelInterface;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class DatabaseHandlerAssignment extends SQLiteOpenHelper {
// Alla statiska variabler
    // Databas version
    private static final int DATABASE_VERSION = 1;
 
    // Databasens namn - > assignmentManager
    private static final String DATABASE_NAME = "assignmentManager";
 
    // Assignments tabellnamn
    private static final String TABLE_ASSIGNMENTS = "assignment";

    // Assignments tabellkolumnnamn
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_LAT = "lat";
private static final String KEY_LON = "long";
private static final String KEY_RECEIVER = "receiver";
private static final String KEY_SENDER = "sender";
private static final String KEY_ASSIGNMENTDESCRIPTION = "description";
private static final String KEY_TIMESPAN = "timespan";
private static final String KEY_ASSIGNMENTSTATUS = "assignment_status";
private static final String KEY_CAMERAIMAGE = "camera_image";
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
                + KEY_CAMERAIMAGE + " BLOB,"
                + KEY_STREETNAME + " TEXT,"
                + KEY_SITENAME + " TEXT" + ")";
        db.execSQL(CREATE_ASSIGNMENTS_TABLE);
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
* @param assignment Det uppdrag som ska läggas till i databasen
*/
    public void addAssignment(Assignment assignment) {
        SQLiteDatabase db = this.getWritableDatabase();
 
        // Konvertera Bitmap -> Byte[] -> BLOB
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Bitmap bmp = assignment.getCameraImage();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] photo = baos.toByteArray();
        
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, assignment.getName());
        values.put(KEY_LAT, Long.toString(assignment.getLat()));
        values.put(KEY_LON, Long.toString(assignment.getLon()));
        values.put(KEY_RECEIVER, assignment.getReceiver());
        values.put(KEY_SENDER, assignment.getSender());
        values.put(KEY_ASSIGNMENTDESCRIPTION, assignment.getAssignmentDescription());
        values.put(KEY_TIMESPAN, assignment.getTimeSpan().toString());
        values.put(KEY_ASSIGNMENTSTATUS, assignment.getAssignmentStatus());
        // Hmm.. Hur i H-E kommer detta att fungera? Bild -> String -> Binär -> .. -> ???
        values.put(KEY_CAMERAIMAGE, photo);
        values.put(KEY_STREETNAME, assignment.getStreetName());
        values.put(KEY_SITENAME, assignment.getSiteName());
        // Lägg till assignment i databasen
        db.insert(TABLE_ASSIGNMENTS, null, values);
        // Stäng databasen. MYCKET VIKTIGT!!
        db.close();
    }
 
    public void removeAssignment(Assignment ass){
     SQLiteDatabase db = this.getWritableDatabase();
     db.delete(TABLE_ASSIGNMENTS, KEY_ID + " = ?",
                new String[] { String.valueOf(ass.getId()) });
        db.close();
    }
    
    /**
* Hämta alla uppdrag i databasen
* @return List<Assignment> En lista med Assignment-objekt
*/
    public List<ModelInterface> getAllAssignments() {
        List<ModelInterface> assignmentList = new ArrayList<ModelInterface>();
        
        // Select All frågan. Ze classic! Dvs, hämta allt från ASSIGNMENTS-databasen
        String selectQuery = "SELECT * FROM " + TABLE_ASSIGNMENTS;
 
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
 
        // Loopa igenom alla rader och lägg till dem i listan
        
        if (cursor.moveToFirst()) {
            do {
             // Konvertera BLOB -> Bitmap
             byte[] image = cursor.getBlob(9);
             ByteArrayInputStream imageStream = new ByteArrayInputStream(image);
             Bitmap theImage= BitmapFactory.decodeStream(imageStream);
            
                Assignment assignment = new Assignment(
                 Long.valueOf(cursor.getString(0)),
                 cursor.getString(1),
Long.parseLong(cursor.getString(2)),
Long.parseLong(cursor.getString(3)),
cursor.getString(4),
cursor.getString(5),
cursor.getString(6),
cursor.getString(7),
cursor.getString(8),
theImage,
cursor.getString(10),
cursor.getString(11));
                
                assignmentList.add(assignment);
            } while (cursor.moveToNext());
        }
 
        // Returnera kontaktlistan
        return assignmentList;
    }
 
    /**
* Räkna antal assignments i databasen
* @return int Antal assignments
*/
    public int getAssignmentCount() {
        String countQuery = "SELECT * FROM " + TABLE_ASSIGNMENTS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int returnCount = cursor.getCount();
        cursor.close();
 
        // Returnera antalet assignments
        return returnCount;
    }
    
}