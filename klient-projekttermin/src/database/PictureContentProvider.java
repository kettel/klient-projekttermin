package database;

import java.util.HashMap;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;
import net.sqlcipher.database.SQLiteQueryBuilder;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.util.Log;
import database.PictureTable.Pictures;

public class PictureContentProvider extends ContentProvider{
		
	    private static final String TAG = "PictureContentProvider";

	    private static final String DATABASE_NAME = "pictures.db";
	    
	    private static final String PASSWORD = Database.PASSWORD;

	    private static final int DATABASE_VERSION = 1;

	    private static final String PICTURES_TABLE_NAME = "pictures";

	    public static final String AUTHORITY = "database.PictureContentProvider";

	    private static final UriMatcher sUriMatcher;

	    private static final int PICTURES = 1;

	    private static final int PICTURE_ID = 2;

	    private static HashMap<String, String> picturesProjectionMap;

	    private static class DatabaseHelper extends SQLiteOpenHelper {

	        DatabaseHelper(Context context) {
	            super(context, DATABASE_NAME, null, DATABASE_VERSION);
	        }

	       
	        @Override
	        public void onCreate(SQLiteDatabase db) {
	            db.execSQL("CREATE TABLE IF NOT EXISTS "
	            		+ PICTURES_TABLE_NAME
	            		+ " (" + Pictures.PICTURE_ID
	                    + " INTEGER PRIMARY KEY AUTOINCREMENT," + Pictures.PICTURE + " VARCHAR(255));");
	        }

	        @Override
	        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	            Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
	            db.execSQL("DROP TABLE IF EXISTS " + PICTURES_TABLE_NAME);
	            onCreate(db);
	        }
	    }

	    private DatabaseHelper dbHelper;

	    @Override
	    public int delete(Uri uri, String where, String[] whereArgs) {
			SQLiteDatabase db = dbHelper.getWritableDatabase(PASSWORD);
	        switch (sUriMatcher.match(uri)) {
	            case PICTURES:
	                break;
	            case PICTURE_ID:
	                where = where + "_id = " + uri.getLastPathSegment();
	                break;
	            default:
	                throw new IllegalArgumentException("Unknown URI " + uri);
	        }

	        int count = db.delete(PICTURES_TABLE_NAME, where, whereArgs);
	        // Underr�tta lyssnare
	        getContext().getContentResolver().notifyChange(uri, null);
	        return count;
	    }

	    @Override
	    public String getType(Uri uri) {
	        switch (sUriMatcher.match(uri)) {
	            case PICTURES:
	                return Pictures.CONTENT_TYPE;
	            default:
	                throw new IllegalArgumentException("Unknown URI " + uri);
	        }
	    }

	    @Override
	    public Uri insert(Uri uri, ContentValues initialValues) {
	        if (sUriMatcher.match(uri) != PICTURES) {
	            throw new IllegalArgumentException("Unknown URI " + uri);
	        }

	        ContentValues values;
	        if (initialValues != null) {
	            values = new ContentValues(initialValues);
	        } else {
	            values = new ContentValues();
	        }

	        SQLiteDatabase db = dbHelper.getWritableDatabase(PASSWORD);
	        long rowId = db.insert(PICTURES_TABLE_NAME, Pictures.PICTURE, values);
	        if (rowId > 0) {
	            Uri noteUri = ContentUris.withAppendedId(Pictures.CONTENT_URI, rowId);
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
	        qb.setTables(PICTURES_TABLE_NAME);
	        qb.setProjectionMap(picturesProjectionMap);

	        switch (sUriMatcher.match(uri)) {    
	            case PICTURES:
	                break;
	            case PICTURE_ID:
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
	            case PICTURES:
	                count = db.update(PICTURES_TABLE_NAME, values, where, whereArgs);
	                break;
	            default:
	                throw new IllegalArgumentException("Unknown URI " + uri);
	        }

	        getContext().getContentResolver().notifyChange(uri, null);
	        return count;
	    }

	    static {
	        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	        sUriMatcher.addURI(AUTHORITY, PICTURES_TABLE_NAME, PICTURES);
	        sUriMatcher.addURI(AUTHORITY, PICTURES_TABLE_NAME + "/#", PICTURE_ID);

	        picturesProjectionMap = new HashMap<String, String>();
	        picturesProjectionMap.put(Pictures.PICTURE_ID, Pictures.PICTURE_ID);
	        picturesProjectionMap.put(Pictures.PICTURE, Pictures.PICTURE);
	    }
	}
