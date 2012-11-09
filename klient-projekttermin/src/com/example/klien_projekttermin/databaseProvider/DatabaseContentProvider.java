package com.example.klien_projekttermin.databaseProvider;

import java.util.Arrays;
import java.util.HashSet;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteQueryBuilder;

public class DatabaseContentProvider extends ContentProvider{

	private MessageDatabaseHelper database;
	
	private String PASSWORD = "password";

	// -BEGIN Används för UriMatcher så ContentProvidern kan användas
	private static final int TODOS = 10;
	private static final int TODO_ID = 20;

	private static final String AUTHORITY = "com.example.klien_projekttermin.databaseProvider.DatabaseContentProvider";

	private static final String BASE_PATH = "klien-projekttermin";
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
			+ "/" + BASE_PATH);

	public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
			+ "/todos";
	public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
			+ "/todo";

	private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	static {
		sURIMatcher.addURI(AUTHORITY, BASE_PATH, TODOS);
		sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/#", TODO_ID);
	}
	// -END URImatcher magi..

	@Override
	public boolean onCreate() {
		database = new MessageDatabaseHelper(getContext());
		// Varför falskt?
		return false;
	}
	
	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
			String sortOrder) {
		// Using SQLiteQueryBuilder instead of query() method
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

		// Check if the caller has requested a column which does not exists
		checkColumns(projection);

		// Set the table
		queryBuilder.setTables(MessageTable.TABLE_MESSAGE);

		int uriType = sURIMatcher.match(uri);
		switch (uriType) {
		case TODOS:
			break;
		case TODO_ID:
			// Adding the ID to the original query
			queryBuilder.appendWhere(MessageTable.COLUMN_ID + "="
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
		int rowsDeleted = 0;
		long id = 0;
		switch (uriType) {
		case TODOS:
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
		case TODOS:
			rowsDeleted = sqlDB.delete(MessageTable.TABLE_MESSAGE, selection,
					selectionArgs);
			break;
		case TODO_ID:
			String id = uri.getLastPathSegment();
			if (TextUtils.isEmpty(selection)) {
				rowsDeleted = sqlDB.delete(MessageTable.TABLE_MESSAGE,
						MessageTable.COLUMN_ID + "=" + id, 
						null);
			} else {
				rowsDeleted = sqlDB.delete(MessageTable.TABLE_MESSAGE,
						MessageTable.COLUMN_ID + "=" + id 
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
		case TODOS:
			rowsUpdated = sqlDB.update(MessageTable.TABLE_MESSAGE, 
					values, 
					selection,
					selectionArgs);
			break;
		case TODO_ID:
			String id = uri.getLastPathSegment();
			if (TextUtils.isEmpty(selection)) {
				rowsUpdated = sqlDB.update(MessageTable.TABLE_MESSAGE, 
						values,
						MessageTable.COLUMN_ID + "=" + id, 
						null);
			} else {
				rowsUpdated = sqlDB.update(MessageTable.TABLE_MESSAGE, 
						values,
						MessageTable.COLUMN_ID + "=" + id 
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
	
	private void checkColumns(String[] projection) {
		String[] available = { MessageTable.COLUMN_CONTENT,
				MessageTable.COLUMN_RECEIVER, MessageTable.COLUMN_ISREAD,
				MessageTable.COLUMN_ID };
		if (projection != null) {
			HashSet<String> requestedColumns = new HashSet<String>(Arrays.asList(projection));
			HashSet<String> availableColumns = new HashSet<String>(Arrays.asList(available));
			// Check if all columns which are requested are available
			if (!availableColumns.containsAll(requestedColumns)) {
				throw new IllegalArgumentException("Unknown columns in projection");
			}
		}
	}

}
