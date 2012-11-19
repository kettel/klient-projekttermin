package com.example.klien_projekttermin.database;

import java.util.HashMap;

import com.example.klien_projekttermin.database.AuthenticationTable.Authentications;


import net.sqlcipher.SQLException;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;
import net.sqlcipher.database.SQLiteQueryBuilder;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

public class AuthenticationContentProvider extends ContentProvider{

	private static final String PASSWORD = Database.PASSWORD;

	private static final String TAG = "AuthenticationContentProvider";

	private static final String DATABASE_NAME = "authentication.db";

	private static final int DATABASE_VERSION = 1;

	public static final String AUTHORITY = "com.example.klien_projekttermin.database.AuthenticationContentProvider";

	private static final UriMatcher sUriMatcher;

	private static final int AUTHENTICATIONS = 1;

	private static final int AUTHENTICATIONS_ID = 2;

	private static HashMap<String, String> authenticationProjectionMap;

	private static class DatabaseHelper extends SQLiteOpenHelper {

		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}


		@Override
		public void onCreate(SQLiteDatabase db) {
			String DATABASE_CREATE = "create table "
					+ Authentications.TABLE_NAME + "("
					+ Authentications.USERNAME + " text, "
					+ Authentications.PASSWORD + " text);";
			db.execSQL(DATABASE_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS " + Authentications.TABLE_NAME);
			onCreate(db);
		}
	}

	private DatabaseHelper dbHelper;

	@Override
	public int delete(Uri uri, String where, String[] whereArgs) {
		SQLiteDatabase db = dbHelper.getWritableDatabase(PASSWORD);
		switch (sUriMatcher.match(uri)) {
		case AUTHENTICATIONS:
			break;
		case AUTHENTICATIONS_ID:
			where = where + "_id = " + uri.getLastPathSegment();
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		int count = db.delete(Authentications.TABLE_NAME, where, whereArgs);
		// Underr�tta lyssnare
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	@Override
	public String getType(Uri uri) {
		switch (sUriMatcher.match(uri)) {
		case AUTHENTICATIONS:
			return Authentications.CONTENT_TYPE;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues initialValues) {
		if (sUriMatcher.match(uri) != AUTHENTICATIONS) {
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		ContentValues values;
		if (initialValues != null) {
			values = new ContentValues(initialValues);
		} else {
			values = new ContentValues();
		}

		SQLiteDatabase db = dbHelper.getWritableDatabase(PASSWORD);
		long rowId = db.insert(Authentications.TABLE_NAME, Authentications.USERNAME, values);
		if (rowId > 0) {
			Uri noteUri = ContentUris.withAppendedId(Authentications.CONTENT_URI, rowId);
			getContext().getContentResolver().notifyChange(noteUri, null);
			return noteUri;
		}

		throw new SQLException("Failed to insert row into " + uri);
	}

	@Override
	public boolean onCreate() {
		dbHelper = new DatabaseHelper(getContext());

		// Om Assignments inte är skapad än samt om SQLite-biblioteken 
		// inte är laddade
		if(!Database.isLibraryLoaded){
			SQLiteDatabase.loadLibs(getContext());
			SQLiteDatabase db = dbHelper.getWritableDatabase(PASSWORD);
			db.close();
			Database.isLibraryLoaded = true;
		}

		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		qb.setTables(Authentications.TABLE_NAME);
		qb.setProjectionMap(authenticationProjectionMap);

		switch (sUriMatcher.match(uri)) {    
		case AUTHENTICATIONS:
			break;
		case AUTHENTICATIONS_ID:
			selection = selection + "_id = " + uri.getLastPathSegment();
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		SQLiteDatabase db = dbHelper.getReadableDatabase(PASSWORD);
		Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);

		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}

	@Override
	public int update(Uri uri, ContentValues values, String where, String[] whereArgs) {
		SQLiteDatabase db = dbHelper.getWritableDatabase(PASSWORD);
		int count;
		switch (sUriMatcher.match(uri)) {
		case AUTHENTICATIONS:
			count = db.update(Authentications.TABLE_NAME, values, where, whereArgs);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	static {
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sUriMatcher.addURI(AUTHORITY, Authentications.TABLE_NAME, AUTHENTICATIONS);
		sUriMatcher.addURI(AUTHORITY, Authentications.TABLE_NAME + "/#", AUTHENTICATIONS_ID);

		authenticationProjectionMap = new HashMap<String, String>();
		authenticationProjectionMap.put(Authentications.AUTHENTICATION_ID, Authentications.AUTHENTICATION_ID);
		authenticationProjectionMap.put(Authentications.USERNAME, Authentications.USERNAME);
		authenticationProjectionMap.put(Authentications.PASSWORD, Authentications.PASSWORD);
	
	}
}
