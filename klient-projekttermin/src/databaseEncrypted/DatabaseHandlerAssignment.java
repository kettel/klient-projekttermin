package databaseEncrypted;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import models.Assignment;
import models.ModelInterface;
import net.sqlcipher.database.SQLiteDatabase;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class DatabaseHandlerAssignment extends DatabaseHandler{

	// Assignments tabellnamn
    private static final String TABLE_ASSIGNMENTS = "assignment";

    // Assignments tabellkolumnnamn
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
	
	public DatabaseHandlerAssignment(Context c) {
		// Skicka Context till DatabaseHandler
		super(c);
		
		// Initiera Messages-tabellen.
		initiateModelDatabase();
	}
	
	@Override
	public void initiateModelDatabase() {
		SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(databaseFile, PASSWORD, null);
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
        database.execSQL(CREATE_ASSIGNMENTS_TABLE);
	}

	@Override
	public void addModel(ModelInterface m) {
		Assignment assignment = (Assignment) m;
		SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(databaseFile, PASSWORD, null);
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
        database.insert(TABLE_ASSIGNMENTS, null, values);
        // Stäng databasen. MYCKET VIKTIGT!!
        database.close();
	}

	@Override
	public void updateModel(ModelInterface m) {
		Assignment ass = (Assignment) m;
		SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(databaseFile, PASSWORD, null);
		
		String UPDATE_ASSIGNMENT = "UPDATE " + TABLE_ASSIGNMENTS + " SET "
        		+ KEY_ASSIGNMENTDESCRIPTION + " = \"" + ass.getAssignmentDescription() + "\", "
        		+ KEY_ASSIGNMENTSTATUS + " = \"" + ass.getAssignmentStatus() +"\", "
        		+ KEY_CAMERAIMAGE + " = \"" + ass.getCameraImage() + "\", "
        		+ KEY_LAT + " = \"" + Long.toString(ass.getLat()) + "\", "
        		+ KEY_LON + " = \"" + Long.toString(ass.getLon()) + "\", "
        		+ KEY_NAME + " = \"" + ass.getName() + "\", "
        		+ KEY_RECEIVER + " = \"" + ass.getReceiver() + "\", "
        		+ KEY_SENDER + " = \"" + ass.getSender() + "\", "
        		+ KEY_SITENAME + " = \"" + ass.getSiteName() + "\", "
        		+ KEY_STREETNAME + " = \"" + ass.getStreetName() + "\", "
        		+ KEY_TIMESPAN + " = \"" + ass.getTimeSpan() + "\" "
        		+ "WHERE " + KEY_ID + " = " + ass.getId();
        
        database.execSQL(UPDATE_ASSIGNMENT);
        database.close();
	}

	@Override
	public List<ModelInterface> getAllModels(ModelInterface m) {
		List<ModelInterface> assignmentList = new ArrayList<ModelInterface>();
        
        // Select All frågan. Ze classic! Dvs, hämta allt från ASSIGNMENTS-databasen
        String selectQuery = "SELECT  * FROM " + TABLE_ASSIGNMENTS;
 
        SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(databaseFile, PASSWORD, null);
        Cursor cursor = database.rawQuery(selectQuery, null);
 
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
 		
        cursor.close();
        database.close();
        // Returnera kontaktlistan
        return assignmentList;
	}
}