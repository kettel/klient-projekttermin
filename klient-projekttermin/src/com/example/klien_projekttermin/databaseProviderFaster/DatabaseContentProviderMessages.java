package com.example.klien_projekttermin.databaseProviderFaster;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteQueryBuilder;

public class DatabaseContentProviderMessages extends ContentProvider{

	private DatabaseHelper database;
	
	private String PASSWORD = Database.PASSWORD;

	// -BEGIN Används för UriMatcher så ContentProvidern kan användas
	private static final int MESSAGES = 10;
	private static final int MESSAGE_ID = 20;

	private static final String AUTHORITY = "com.example.klien_projekttermin.databaseProvider.DatabaseContentProviderMessages";

	private static final String BASE_PATH = "klien-projekttermin/message";
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
			+ "/" + BASE_PATH);

	public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
			+ "/todos";
	public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
			+ "/todo";

	private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	static {
		sURIMatcher.addURI(AUTHORITY, BASE_PATH, MESSAGES);
		sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/#", MESSAGE_ID);
	}
	// -END URImatcher magi..

	@Override
	public boolean onCreate() {
		database = new DatabaseHelper(getContext());
		return false;
	}
	
	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
			String sortOrder) {
		// Using SQLiteQueryBuilder instead of query() method
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		
		// Set the table
		queryBuilder.setTables(MessageTable.TABLE_MESSAGE);

		int uriType = sURIMatcher.match(uri);
		switch (uriType) {
		case MESSAGES:
			break;
		case MESSAGE_ID:
			// Adding the ID to the original query
			queryBuilder.appendWhere(Database.KEY_ID + "="
					+ uri.getLastPathSegment());
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		SQLiteDatabase db = database.getWritableDatabase(PASSWORD);
		Cursor cursor = queryBuilder.query(db, projection, selection,
				selectionArgs, null, null, sortOrder);
		// Make sure that potential listeners are getting notified
		cursor.setNotificationUri(getContext().getContentResolver(), uri);

		return cursor;
	}
	
	@Override
	public String getType(Uri uri) {
		return null;
	}
	
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		int uriType = sURIMatcher.match(uri);
		SQLiteDatabase sqlDB = database.getWritableDatabase(PASSWORD);
		long id = 0;
		switch (uriType) {
		case MESSAGES:
			id = sqlDB.insert(MessageTable.TABLE_MESSAGE, null, values);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return Uri.parse(BASE_PATH + "/" + id);
	}
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int uriType = sURIMatcher.match(uri);
		SQLiteDatabase sqlDB = database.getWritableDatabase(PASSWORD);
		int rowsDeleted = 0;
		switch (uriType) {
		case MESSAGES:
			rowsDeleted = sqlDB.delete(MessageTable.TABLE_MESSAGE, selection,
					selectionArgs);
			break;
		case MESSAGE_ID:
			String id = uri.getLastPathSegment();
			if (TextUtils.isEmpty(selection)) {
				rowsDeleted = sqlDB.delete(MessageTable.TABLE_MESSAGE,
						Database.KEY_ID + "=" + id, 
						null);
			} else {
				rowsDeleted = sqlDB.delete(MessageTable.TABLE_MESSAGE,
						Database.KEY_ID + "=" + id 
						+ " and " + selection,
						selectionArgs);
			}
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return rowsDeleted;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		int uriType = sURIMatcher.match(uri);
		SQLiteDatabase sqlDB = database.getWritableDatabase(PASSWORD);
		int rowsUpdated = 0;
		switch (uriType) {
		case MESSAGES:
			rowsUpdated = sqlDB.update(MessageTable.TABLE_MESSAGE, 
					values, 
					selection,
					selectionArgs);
			break;
		case MESSAGE_ID:
			String id = uri.getLastPathSegment();
			if (TextUtils.isEmpty(selection)) {
				rowsUpdated = sqlDB.update(MessageTable.TABLE_MESSAGE, 
						values,
						Database.KEY_ID + "=" + id, 
						null);
			} else {
				rowsUpdated = sqlDB.update(MessageTable.TABLE_MESSAGE, 
						values,
						Database.KEY_ID + "=" + id 
						+ " and " 
						+ selection,
						selectionArgs);
			}
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return rowsUpdated;
	}
	
}
