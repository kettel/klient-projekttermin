package com.example.klien_projekttermin.databaseProviderFaster;

import net.sqlcipher.database.SQLiteDatabase;
import android.util.Log;

public class MessageTable {


	// Database table
	public static final String TABLE_MESSAGE = "message";
	public static final String COLUMN_CONTENT = "content";
	public static final String COLUMN_RECEIVER = "receiver";
	public static final String COLUMN_SENDER = "sender";
	public static final String COLUMN_TIMESTAMP = "timestamp";
	public static final String COLUMN_ISREAD = "isRead";

	// Database creation SQL statement
	private static final String DATABASE_CREATE = "create table if not exists "
			+ TABLE_MESSAGE + "(" 
			+ Database.KEY_ID + " integer primary key autoincrement, " 
			+ COLUMN_CONTENT + " text not null, " 
			+ COLUMN_RECEIVER + " text not null, " 
			+ COLUMN_SENDER + " text not null, "
			+ COLUMN_TIMESTAMP + " text, "
			+ COLUMN_ISREAD + " text not null)";

	public static void onCreate(SQLiteDatabase database) {
		Log.d("DB","Skapar tabell: " + DATABASE_CREATE);
		database.execSQL(DATABASE_CREATE);
	}

	public static void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		Log.w(MessageTable.class.getName(), "Upgrading database from version "
				+ oldVersion + " to " + newVersion
				+ ", which will destroy all old data");
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGE);
		onCreate(database);
	}
}