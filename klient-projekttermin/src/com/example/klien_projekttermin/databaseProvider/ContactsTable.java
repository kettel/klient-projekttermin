package com.example.klien_projekttermin.databaseProvider;

import net.sqlcipher.database.SQLiteDatabase;
import android.util.Log;

public class ContactsTable {


	// Database table
	public static final String TABLE_CONTACTS = "contact";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_CONTACT_NAME = "contact_name";
	public static final String COLUMN_PH_NO = "phone_number";
	public static final String COLUMN_EMAIL = "email";
	public static final String COLUMN_CLEARANCE_LEVEL = "clearance_level";
	public static final String COLUMN_CLASSIFICATION = "classification";
	public static final String COLUMN_COMMENT = "comment";

	// Database creation SQL statement
	private static final String DATABASE_CREATE = "CREATE TABLE IF NOT EXISTS " 
			+ TABLE_CONTACTS + "("
            + Database.KEY_ID + " integer primary key autoincrement, "  
    		+ COLUMN_CONTACT_NAME + " TEXT, "
            + COLUMN_PH_NO + " TEXT, "
    		+ COLUMN_EMAIL + " TEXT, "
            + COLUMN_CLEARANCE_LEVEL + " TEXT, "
            + COLUMN_CLASSIFICATION + " TEXT, "
            + COLUMN_COMMENT + " TEXT)";

	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
	}

	public static void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		Log.w(MessageTable.class.getName(), "Upgrading database from version "
				+ oldVersion + " to " + newVersion
				+ ", which will destroy all old data");
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
		onCreate(database);
	}
}