package com.example.klien_projekttermin.providers;

import java.util.HashMap;

import com.example.klien_projekttermin.models.Note.Notes;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;
import net.sqlcipher.database.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;
 
/**
 * @author Jason Wei
 *
 */
public class NotesContentProvider extends ContentProvider {
 
    private static final String TAG = "NotesContentProvider";
    
    private static final String PASSWORD = "password";
 
    private static final String DATABASE_NAME = "notes.db";
 
    private static final int DATABASE_VERSION = 1;
 
    private static final String NOTES_TABLE_NAME = "notes";
 
    public static final String AUTHORITY = "com.example.klien_projekttermin.providers.NotesContentProvider";
 
    private static final UriMatcher sUriMatcher;
 
    private static final int NOTES = 1;
 
    private static final int NOTES_ID = 2;
 
    private static HashMap<String, String> notesProjectionMap;
 
    private static class DatabaseHelper extends SQLiteOpenHelper {
 
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
 
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + NOTES_TABLE_NAME + " (" + Notes.NOTE_ID
                    + " INTEGER PRIMARY KEY AUTOINCREMENT," + Notes.TITLE + " VARCHAR(255)," + Notes.TEXT + " LONGTEXT" + ");");
        }
 
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + NOTES_TABLE_NAME);
            onCreate(db);
        }
    }
 
    private DatabaseHelper dbHelper;
 
    @Override
    public int delete(Uri uri, String where, String[] whereArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase(PASSWORD);
        switch (sUriMatcher.match(uri)) {
            case NOTES:
                break;
            case NOTES_ID:
                where = where + "_id = " + uri.getLastPathSegment();
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
 
        int count = db.delete(NOTES_TABLE_NAME, where, whereArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }
 
    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case NOTES:
                return Notes.CONTENT_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }
 
    @Override
    public Uri insert(Uri uri, ContentValues initialValues) {
        if (sUriMatcher.match(uri) != NOTES) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
 
        ContentValues values;
        if (initialValues != null) {
            values = new ContentValues(initialValues);
        } else {
            values = new ContentValues();
        }
 
        SQLiteDatabase db = dbHelper.getWritableDatabase(PASSWORD);
        long rowId = db.insert(NOTES_TABLE_NAME, Notes.TEXT, values);
        if (rowId > 0) {
            Uri noteUri = ContentUris.withAppendedId(Notes.CONTENT_URI, rowId);
            getContext().getContentResolver().notifyChange(noteUri, null);
            return noteUri;
        }
 
        throw new SQLException("Failed to insert row into " + uri);
    }
 
    @Override
    public boolean onCreate() {
        dbHelper = new DatabaseHelper(getContext());
        return true;
    }
 
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(NOTES_TABLE_NAME);
        qb.setProjectionMap(notesProjectionMap);
 
        switch (sUriMatcher.match(uri)) {   
            case NOTES:
                break;
            case NOTES_ID:
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
            case NOTES:
                count = db.update(NOTES_TABLE_NAME, values, where, whereArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
 
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }
 
    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(AUTHORITY, NOTES_TABLE_NAME, NOTES);
        sUriMatcher.addURI(AUTHORITY, NOTES_TABLE_NAME + "/#", NOTES_ID);
 
        notesProjectionMap = new HashMap<String, String>();
        notesProjectionMap.put(Notes.NOTE_ID, Notes.NOTE_ID);
        notesProjectionMap.put(Notes.TITLE, Notes.TITLE);
        notesProjectionMap.put(Notes.TEXT, Notes.TEXT);
    }
}
