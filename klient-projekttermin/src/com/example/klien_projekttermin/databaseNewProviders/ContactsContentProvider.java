package com.example.klien_projekttermin.databaseNewProviders;

import java.util.HashMap;

import com.example.klien_projekttermin.databaseNewProviders.Contact.Contacts;

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

/**
 * @author Jason Wei
 * 
 */
public class ContactsContentProvider extends ContentProvider {
	String PASSWORD;
    private static final String TAG = "ContactsContentProvider";

    private static final String DATABASE_NAME = "contacts.db";

    private static final int DATABASE_VERSION = 1;

    private static final String CONTACTS_TABLE_NAME = "contacts";

    public static final String AUTHORITY = "com.example.testacontentprovider.providers.ContactsContentProvider";

    private static final UriMatcher sUriMatcher;

    private static final int CONTACTS = 1;

    private static final int CONTACTS_ID = 2;

    private static HashMap<String, String> contactsProjectionMap;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

       
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + CONTACTS_TABLE_NAME + " (" + Contacts.CONTACT_ID
                    + " INTEGER PRIMARY KEY AUTOINCREMENT," + Contacts.NAME + " VARCHAR(255));");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + CONTACTS_TABLE_NAME);
            onCreate(db);
        }
    }

    private DatabaseHelper dbHelper;

    @Override
    public int delete(Uri uri, String where, String[] whereArgs) {
		SQLiteDatabase db = dbHelper.getWritableDatabase(PASSWORD);
        switch (sUriMatcher.match(uri)) {
            case CONTACTS:
                break;
            case CONTACTS_ID:
                where = where + "_id = " + uri.getLastPathSegment();
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        int count = db.delete(CONTACTS_TABLE_NAME, where, whereArgs);
        // Underr�tta lyssnare
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case CONTACTS:
                return Contacts.CONTENT_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues initialValues) {
        if (sUriMatcher.match(uri) != CONTACTS) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        ContentValues values;
        if (initialValues != null) {
            values = new ContentValues(initialValues);
        } else {
            values = new ContentValues();
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase(PASSWORD);
        long rowId = db.insert(CONTACTS_TABLE_NAME, Contacts.NAME, values);
        if (rowId > 0) {
            Uri noteUri = ContentUris.withAppendedId(Contacts.CONTENT_URI, rowId);
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
        qb.setTables(CONTACTS_TABLE_NAME);
        qb.setProjectionMap(contactsProjectionMap);

        switch (sUriMatcher.match(uri)) {    
            case CONTACTS:
                break;
            case CONTACTS_ID:
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
            case CONTACTS:
                count = db.update(CONTACTS_TABLE_NAME, values, where, whereArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(AUTHORITY, CONTACTS_TABLE_NAME, CONTACTS);
        sUriMatcher.addURI(AUTHORITY, CONTACTS_TABLE_NAME + "/#", CONTACTS_ID);

        contactsProjectionMap = new HashMap<String, String>();
        contactsProjectionMap.put(Contacts.CONTACT_ID, Contacts.CONTACT_ID);
        contactsProjectionMap.put(Contacts.NAME, Contacts.NAME);
    }
}

