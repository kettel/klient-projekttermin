package database;

import java.io.File;
import java.util.HashMap;

import database.AssignmentTable.Assignments;

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

public class AssignmentsContentProvider extends ContentProvider {

	private static final String PASSWORD = Database.PASSWORD;

	private static final String TAG = "AssignmentsContentProvider";

	private static final String DATABASE_NAME = "assignments.db";

	private static final int DATABASE_VERSION = 1;

	public static final String AUTHORITY = "database.AssignmentsContentProvider";

	private static final UriMatcher sUriMatcher;

	private static final int ASSIGNMENTS = 1;

	private static final int ASSIGNMENTS_ID = 2;

	private static HashMap<String, String> assignmentsProjectionMap;

	private static class DatabaseHelper extends SQLiteOpenHelper {

		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);

			// FIX FÖR GALAXY-TABBEN!
			File dbFile = context.getDatabasePath(DATABASE_NAME);

			// Om databasfilen inte existerar, skapa den
			if (!dbFile.exists()) {
				dbFile.mkdirs();
				dbFile.delete();
			}
			// Initiera en skrivbar databas (FIX för testDB)
			SQLiteDatabase db = this.getWritableDatabase(PASSWORD);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {

			String DATABASE_CREATE = "CREATE TABLE "

			+ Assignments.TABLE_NAME + "(" + Assignments.ASSIGNMENT_ID
					+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ Assignments.GLOBAL_ASSIGNMENT_ID + " BIGINT, "
					+ Assignments.NAME + " VARCHAR(255), " + Assignments.LAT
					+ " VARCHAR(255), " + Assignments.LON + " VARCHAR(255), "
					+ Assignments.REGION + " LONGTEXT, " + Assignments.AGENTS
					+ " LONGTEXT, " + Assignments.SENDER + " VARCHAR(255), "
					+ Assignments.EXTERNAL_MISSION + " VARCHAR(255), "
					+ Assignments.DESCRIPTION + " TEXT, "
					+ Assignments.TIMESPAN + " VARCHAR(255), "
					+ Assignments.STATUS + " VARCHAR(255), "
					+ Assignments.CAMERAIMAGE + " BLOB, "
					+ Assignments.STREETNAME + " VARCHAR(255), "
					+ Assignments.SITENAME + " VARCHAR(255), "
					+ Assignments.TIMESTAMP + " VARCHAR(255), "
					+ Assignments.PRIORITY + " VARCHAR(255));";

			db.execSQL(DATABASE_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS " + Assignments.TABLE_NAME);
			onCreate(db);
		}
	}

	private DatabaseHelper dbHelper;

	@Override
	public int delete(Uri uri, String where, String[] whereArgs) {
		SQLiteDatabase db = dbHelper.getWritableDatabase(PASSWORD);
		switch (sUriMatcher.match(uri)) {
		case ASSIGNMENTS:
			break;
		case ASSIGNMENTS_ID:
			where = where + "_id = " + uri.getLastPathSegment();
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		int count = db.delete(Assignments.TABLE_NAME, where, whereArgs);
		// Underrätta lyssnare
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	@Override
	public String getType(Uri uri) {
		switch (sUriMatcher.match(uri)) {
		case ASSIGNMENTS:
			return Assignments.CONTENT_TYPE;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues initialValues) {
		if (sUriMatcher.match(uri) != ASSIGNMENTS) {
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		ContentValues values;
		if (initialValues != null) {
			values = new ContentValues(initialValues);
		} else {
			values = new ContentValues();
		}

		SQLiteDatabase db = dbHelper.getWritableDatabase(PASSWORD);
		long rowId = db
				.insert(Assignments.TABLE_NAME, Assignments.NAME, values);
		if (rowId > 0) {
			Uri noteUri = ContentUris.withAppendedId(Assignments.CONTENT_URI,
					rowId);
			getContext().getContentResolver().notifyChange(noteUri, null);
			return noteUri;
		}

		throw new SQLException("Failed to insert row into " + uri);
	}

	@Override
	public boolean onCreate() {
		// Om Assignments inte är skapad än samt om SQLite-biblioteken
		// inte är laddade
		if (!Database.isLibraryLoaded) {
			SQLiteDatabase.loadLibs(getContext());
			Database.isLibraryLoaded = true;
		}
		dbHelper = new DatabaseHelper(getContext());
		SQLiteDatabase db = dbHelper.getWritableDatabase(PASSWORD);
		db.close();
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		qb.setTables(Assignments.TABLE_NAME);
		qb.setProjectionMap(assignmentsProjectionMap);

		switch (sUriMatcher.match(uri)) {
		case ASSIGNMENTS:
			break;
		case ASSIGNMENTS_ID:
			selection = selection + "_id = " + uri.getLastPathSegment();
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		SQLiteDatabase db = dbHelper.getReadableDatabase(PASSWORD);
		Cursor c = qb.query(db, projection, selection, selectionArgs, null,
				null, sortOrder);

		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}

	@Override
	public int update(Uri uri, ContentValues values, String where,
			String[] whereArgs) {
		SQLiteDatabase db = dbHelper.getWritableDatabase(PASSWORD);
		int count;
		switch (sUriMatcher.match(uri)) {
		case ASSIGNMENTS:
			count = db.update(Assignments.TABLE_NAME, values, where, whereArgs);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	static {
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sUriMatcher.addURI(AUTHORITY, Assignments.TABLE_NAME, ASSIGNMENTS);
		sUriMatcher.addURI(AUTHORITY, Assignments.TABLE_NAME + "/#",
				ASSIGNMENTS_ID);

		assignmentsProjectionMap = new HashMap<String, String>();
		assignmentsProjectionMap.put(Assignments.ASSIGNMENT_ID,
				Assignments.ASSIGNMENT_ID);
		assignmentsProjectionMap.put(Assignments.GLOBAL_ASSIGNMENT_ID,
				Assignments.GLOBAL_ASSIGNMENT_ID);
		assignmentsProjectionMap.put(Assignments.NAME, Assignments.NAME);
		assignmentsProjectionMap.put(Assignments.LAT, Assignments.LAT);
		assignmentsProjectionMap.put(Assignments.LON, Assignments.LON);
		assignmentsProjectionMap.put(Assignments.REGION, Assignments.REGION);
		assignmentsProjectionMap.put(Assignments.AGENTS, Assignments.AGENTS);
		assignmentsProjectionMap.put(Assignments.SENDER, Assignments.SENDER);
		assignmentsProjectionMap.put(Assignments.EXTERNAL_MISSION,
				Assignments.EXTERNAL_MISSION);
		assignmentsProjectionMap.put(Assignments.DESCRIPTION,
				Assignments.DESCRIPTION);
		assignmentsProjectionMap
				.put(Assignments.TIMESPAN, Assignments.TIMESPAN);
		assignmentsProjectionMap.put(Assignments.STATUS, Assignments.STATUS);
		assignmentsProjectionMap.put(Assignments.CAMERAIMAGE,
				Assignments.CAMERAIMAGE);
		assignmentsProjectionMap.put(Assignments.STREETNAME,
				Assignments.STREETNAME);
		assignmentsProjectionMap
				.put(Assignments.SITENAME, Assignments.SITENAME);
		assignmentsProjectionMap.put(Assignments.TIMESTAMP,
				Assignments.TIMESTAMP);
		assignmentsProjectionMap
				.put(Assignments.PRIORITY, Assignments.PRIORITY);
	}
}
