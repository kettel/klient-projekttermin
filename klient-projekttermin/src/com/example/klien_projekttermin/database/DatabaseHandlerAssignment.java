package com.example.klien_projekttermin.database;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import models.Assignment;
import models.AssignmentStatus;
import models.Contact;
import models.ModelInterface;
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
	private static final String KEY_ID = "_id";
	private static final String KEY_NAME = "name";
	private static final String KEY_LAT = "lat";
	private static final String KEY_LON = "long";
	private static final String KEY_REGION = "region";
	private static final String KEY_RECEIVER = "receiver";
	private static final String KEY_SENDER = "sender";
	private static final String KEY_EXTERNAL_MISSION = "external_mission";
	private static final String KEY_ASSIGNMENTDESCRIPTION = "description";
	private static final String KEY_TIMESPAN = "timespan";
	private static final String KEY_ASSIGNMENTSTATUS = "assignment_status";
	private static final String KEY_CAMERAIMAGE = "camera_image";
	private static final String KEY_STREETNAME = "streetname";
	private static final String KEY_SITENAME = "sitename";
	private static final String KEY_TIMESTAMP = "timestamp";

	public DatabaseHandlerAssignment(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// Skapa tabell
	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_ASSIGNMENTS_TABLE = "CREATE TABLE " + TABLE_ASSIGNMENTS
				+ "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
				+ KEY_LAT + " TEXT," + KEY_LON + " TEXT," + KEY_REGION
				+ " TEXT," + KEY_RECEIVER + " TEXT," + KEY_SENDER + " TEXT,"
				+ KEY_EXTERNAL_MISSION + " TEXT," + KEY_ASSIGNMENTDESCRIPTION
				+ " TEXT," + KEY_TIMESPAN + " TEXT," + KEY_ASSIGNMENTSTATUS
				+ " TEXT," + KEY_CAMERAIMAGE + " BLOB," + KEY_STREETNAME
				+ " TEXT," + KEY_SITENAME + " TEXT" + KEY_TIMESTAMP + ")";
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
	 * 
	 * @param assignment
	 *            Det uppdrag som ska läggas till i databasen
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
		values.put(KEY_LAT, assignment.getLat());
		values.put(KEY_LON, assignment.getLon());
		values.put(KEY_REGION, assignment.getRegion());
		values.put(KEY_RECEIVER, assignment.getReceiver());
		values.put(KEY_SENDER, assignment.getSender());
		values.put(KEY_EXTERNAL_MISSION,
				Boolean.toString(assignment.isExternalMission()));
		values.put(KEY_ASSIGNMENTDESCRIPTION,
				assignment.getAssignmentDescription());
		values.put(KEY_TIMESPAN, assignment.getTimeSpan().toString());
		values.put(KEY_ASSIGNMENTSTATUS, assignment.getAssignmentStatus()
				.toString());
		// Hmm.. Hur i H-E kommer detta att fungera? Bild -> String -> Binär ->
		// .. -> ???
		values.put(KEY_CAMERAIMAGE, photo);
		values.put(KEY_STREETNAME, assignment.getStreetName());
		values.put(KEY_SITENAME, assignment.getSiteName());
		values.put(KEY_TIMESTAMP, Long.toString(assignment.getTimeStamp()));
		// Lägg till assignment i databasen
		db.insert(TABLE_ASSIGNMENTS, null, values);
		// Stäng databasen. MYCKET VIKTIGT!!
		db.close();
	}

	public void removeAssignment(Assignment ass) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_ASSIGNMENTS, KEY_ID + " = ?",
				new String[] { String.valueOf(ass.getId()) });
		db.close();
	}

	/**
	 * Hämta alla uppdrag i databasen
	 * 
	 * @return List<Assignment> En lista med Assignment-objekt
	 */
	public List<ModelInterface> getAllAssignments() {
		List<ModelInterface> assignmentList = new ArrayList<ModelInterface>();

		// Select All frågan. Ze classic! Dvs, hämta allt från
		// ASSIGNMENTS-databasen
		String selectQuery = "SELECT  * FROM " + TABLE_ASSIGNMENTS;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// Loopa igenom alla rader och lägg till dem i listan

		if (cursor.moveToFirst()) {
			do {
				// Konvertera BLOB -> Bitmap
				byte[] image = cursor.getBlob(9);
				ByteArrayInputStream imageStream = new ByteArrayInputStream(
						image);
				Bitmap theImage = BitmapFactory.decodeStream(imageStream);

				Assignment assignment = new Assignment(Long.valueOf(cursor
						.getString(0)), // id från DB
						cursor.getString(1), // name
						Long.parseLong(cursor.getString(2)), // lat
						Long.parseLong(cursor.getString(3)), // lon
						cursor.getString(4),// region
						cursor.getString(5), // receiver
						cursor.getString(6), // sender
						Boolean.parseBoolean(cursor.getString(7)), // isExternalMission
						cursor.getString(8), // assDesc
						cursor.getString(9), // timeSpan
						AssignmentStatus.valueOf(cursor.getString(10)), // assStatus
						theImage, // cameraImage
						cursor.getString(10), // streetName
						cursor.getString(11)); // siteName

				assignmentList.add(assignment);
			} while (cursor.moveToNext());
		}
		cursor.close();
		db.close();
		// Returnera kontaktlistan
		return assignmentList;
	}

	/**
	 * Räkna antal assignments i databasen
	 * 
	 * @return int Antal assignments
	 */
	public int getAssignmentCount() {
		String countQuery = "SELECT * FROM " + TABLE_ASSIGNMENTS;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		int returnCount = cursor.getCount();
		cursor.close();
		db.close();
		// Returnera antalet assignments
		return returnCount;
	}

	/**
	 * Uppdatera att uppdrag med alla fält förutom tidsstämpeln som sätts när
	 * ett uppdrag skapas
	 * 
	 * @param ass
	 *            Det nya, uppdaterade uppdraget.
	 */
	public void updateModel(Assignment ass) {
		SQLiteDatabase db = this.getReadableDatabase();
		// Slå ihop alla agenter för ett uppdrag till en sträng separerade med
		// ett /
		String agents = new String();
		List<Contact> receivers = ass.getAgents();
		for (Contact contact : receivers) {
			agents.concat(contact.getContactName() + "/");
		}
		String UPDATE_ASSIGNMENT = "UPDATE " + TABLE_ASSIGNMENTS + " SET "
				+ KEY_ASSIGNMENTDESCRIPTION + " = \""
				+ ass.getAssignmentDescription() + "\", "
				+ KEY_ASSIGNMENTSTATUS + " = \"" + ass.getAssignmentStatus()
				+ "\", " + KEY_CAMERAIMAGE + " = \"" + ass.getCameraImage()
				+ "\", " + KEY_LAT + " = \"" + ass.getLat() + "\", " + KEY_LON
				+ " = \"" + ass.getLon() + "\", " + KEY_REGION + " = \""
				+ ass.getRegion() + "\", " + KEY_EXTERNAL_MISSION + " = \""
				+ Boolean.toString(ass.isExternalMission()) + "\", " + KEY_NAME
				+ " = \"" + ass.getName() + "\", " + KEY_RECEIVER + " = \""
				+ agents + "\", " + KEY_SENDER + " = \"" + ass.getSender()
				+ "\", " + KEY_SITENAME + " = \"" + ass.getSiteName() + "\", "
				+ KEY_STREETNAME + " = \"" + ass.getStreetName() + "\", "
				+ KEY_TIMESPAN + " = \"" + ass.getTimeSpan() + "\" " + "WHERE "
				+ KEY_ID + " = " + ass.getId();

		db.execSQL(UPDATE_ASSIGNMENT);

		db.close();
	}
}
