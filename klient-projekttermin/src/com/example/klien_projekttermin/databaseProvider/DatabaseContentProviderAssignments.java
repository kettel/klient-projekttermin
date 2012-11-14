package com.example.klien_projekttermin.databaseProvider;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteQueryBuilder;

public class DatabaseContentProviderAssignments extends ContentProvider{
	DatabaseHelper database;
	private String PASSWORD = Database.PASSWORD;

	// -BEGIN Används för UriMatcher så ContentProvidern kan användas
	private static final int ASSIGNMENTS = 10;
	private static final int ASSIGNMENT_ID = 20;

	private static final String AUTHORITY = "com.example.klien_projekttermin.databaseProvider.DatabaseContentProviderAssignments";

	private static final String BASE_PATH = "klien-projekttermin/assignment";
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
			+ "/" + BASE_PATH);

	public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
			+ "/contacts";
	public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
			+ "/contact";

	private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	static {
		sURIMatcher.addURI(AUTHORITY, BASE_PATH, ASSIGNMENTS);
		sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/#", ASSIGNMENT_ID);
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

		// Check if the caller has requested a column which does not exists
		//checkColumns(projection);

		// Set the table
		queryBuilder.setTables(AssignmentTable.TABLE_ASSIGNMENTS);

		int uriType = sURIMatcher.match(uri);
		switch (uriType) {
		case ASSIGNMENTS:
			break;
		case ASSIGNMENT_ID:
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
		db.close();
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
		case ASSIGNMENTS:
			id = sqlDB.insert(AssignmentTable.TABLE_ASSIGNMENTS, null, values);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		sqlDB.close();
		return Uri.parse(BASE_PATH + "/" + id);
	}
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int uriType = sURIMatcher.match(uri);
		SQLiteDatabase sqlDB = database.getWritableDatabase(PASSWORD);
		int rowsDeleted = 0;
		switch (uriType) {
		case ASSIGNMENTS:
			rowsDeleted = sqlDB.delete(AssignmentTable.TABLE_ASSIGNMENTS, selection,
					selectionArgs);
			break;
		case ASSIGNMENT_ID:
			String id = uri.getLastPathSegment();
			if (TextUtils.isEmpty(selection)) {
				rowsDeleted = sqlDB.delete(AssignmentTable.TABLE_ASSIGNMENTS,
						Database.KEY_ID + "=" + id, 
						null);
			} else {
				rowsDeleted = sqlDB.delete(AssignmentTable.TABLE_ASSIGNMENTS,
						Database.KEY_ID + "=" + id 
						+ " and " + selection,
						selectionArgs);
			}
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		sqlDB.close();
		return rowsDeleted;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		int uriType = sURIMatcher.match(uri);
		SQLiteDatabase sqlDB = database.getWritableDatabase(PASSWORD);
		int rowsUpdated = 0;
		switch (uriType) {
		case ASSIGNMENTS:
			rowsUpdated = sqlDB.update(AssignmentTable.TABLE_ASSIGNMENTS, 
					values, 
					selection,
					selectionArgs);
			break;
		case ASSIGNMENT_ID:
			String id = uri.getLastPathSegment();
			if (TextUtils.isEmpty(selection)) {
				rowsUpdated = sqlDB.update(AssignmentTable.TABLE_ASSIGNMENTS, 
						values,
						Database.KEY_ID + "=" + id, 
						null);
			} else {
				rowsUpdated = sqlDB.update(AssignmentTable.TABLE_ASSIGNMENTS, 
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
		sqlDB.close();
		return rowsUpdated;
	}

}
