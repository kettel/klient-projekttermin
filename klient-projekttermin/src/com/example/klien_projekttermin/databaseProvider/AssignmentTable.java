package com.example.klien_projekttermin.databaseProvider;

import net.sqlcipher.database.SQLiteDatabase;
import android.util.Log;

public class AssignmentTable {
	// Assignments tabellnamn
	public static final String TABLE_ASSIGNMENTS = "assignment";

	// Assignments tabellkolumnnamn
	public static final String COLUMN_NAME = "name";
	public static final String COLUMN_LAT = "lat";
	public static final String COLUMN_LON = "long";
	public static final String COLUMN_RECEIVER = "receiver";
	public static final String COLUMN_SENDER = "sender";
	public static final String COLUMN_ASSIGNMENTDESCRIPTION = "description";
	public static final String COLUMN_TIMESPAN = "timespan";
	public static final String COLUMN_ASSIGNMENTSTATUS = "assignment_status";
	public static final String COLUMN_CAMERAIMAGE = "camera_image";
	public static final String COLUMN_STREETNAME ="streetname";
	public static final String COLUMN_SITENAME = "sitename";

	// Database creation SQL statement
	private static final String DATABASE_CREATE = "CREATE TABLE IF NOT EXISTS " 
			+ TABLE_ASSIGNMENTS + "("
            + Database.KEY_ID + " integer primary key autoincrement,"  
    		+ COLUMN_NAME + " TEXT,"
            + COLUMN_LAT + " TEXT,"
    		+ COLUMN_LON + " TEXT,"
            + COLUMN_RECEIVER + " TEXT,"
            + COLUMN_SENDER + " TEXT,"
            + COLUMN_ASSIGNMENTDESCRIPTION + " TEXT,"
            + COLUMN_TIMESPAN + " TEXT,"
            + COLUMN_ASSIGNMENTSTATUS + " TEXT,"
            + COLUMN_CAMERAIMAGE + " BLOB,"
            + COLUMN_STREETNAME + " TEXT,"
            + COLUMN_SITENAME + " TEXT" + ")";

	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
	}

	public static void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		Log.w(MessageTable.class.getName(), "Upgrading database from version "
				+ oldVersion + " to " + newVersion
				+ ", which will destroy all old data");
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_ASSIGNMENTS);
		onCreate(database);
	}
}
