package com.example.klien_projekttermin.databaseProvider;

import com.example.klien_projekttermin.models.Note.Notes;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;

public class NotesDB {

	/**
	 * 
	 */
	private NotesDB () {
	}

	private static final NotesDB  instance = new NotesDB ();

	public static NotesDB  getInstance() {
		return instance;
	}

	// adds a new note with a given title and text
	public void addNewNote(ContentResolver contentResolver, String title, String text) {
		ContentValues contentValue = new ContentValues();
		// note that we don't have to add an id as our table set id as autoincrement
		contentValue.put(Notes.TITLE, title);
		contentValue.put(Notes.TEXT, text);
		contentResolver.insert(Notes.CONTENT_URI, contentValue);
	}

	// checks to see if a note with a given title is in our database
	public boolean isNoteInDB(ContentResolver contentResolver, String title) {
		boolean ret = false;
		Cursor cursor = contentResolver.query(Notes.CONTENT_URI, null, Notes.TITLE + "='" + title + "'", null, null);
		if (null != cursor && cursor.moveToNext()) {
			ret = true;
		}
		cursor.close();
		return ret;
	}

	// get the note text from the title of the note
	public String getTextFromTitle(ContentResolver contentResolver, String title) {
		String ret = "";
		String[] projection = new String[] { Notes.TEXT };
		Cursor cursor = contentResolver.query(Notes.CONTENT_URI, projection, Notes.TITLE + "='" + title + "'", null, null);
		if (null != cursor && cursor.moveToNext()) {
			int index = cursor.getColumnIndex(Notes.TEXT);
			ret = cursor.getString(index);
		}
		cursor.close();
		return ret;
	}

	public void updateTextFromTitle(ContentResolver contentResolver, String title, String text) {
		ContentValues contentValue = new ContentValues();
		contentValue.put(Notes.TEXT, text);
		contentResolver.update(Notes.CONTENT_URI, contentValue, Notes.TITLE + "='" + title + "'", null);
	}

	// erases all entries in the database
	public void refreshCache(ContentResolver contentResolver) {
		int delete = contentResolver.delete(Notes.CONTENT_URI, null, null);
		System.out.println("DELETED " + delete + " RECORDS FROM CONTACTS DB");
	}
	

}

