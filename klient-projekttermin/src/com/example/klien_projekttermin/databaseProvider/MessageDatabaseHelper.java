package com.example.klien_projekttermin.databaseProvider;

import android.content.Context;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;

public class MessageDatabaseHelper extends SQLiteOpenHelper{

	private static final String DATABASE_NAME = "tddd36.db";
	private static final int DATABASE_VERSION = 1;
	
	public MessageDatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	// När databasen skapas kallas denna metod
	@Override
	public void onCreate(SQLiteDatabase database) {
		MessageTable.onCreate(database);
	}
	
	// Om databasen uppgraderas kallas denna metod
	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		MessageTable.onUpgrade(database, oldVersion, newVersion);
	}

}
