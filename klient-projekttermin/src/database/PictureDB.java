package database;

import java.util.ArrayList;
import java.util.List;

import models.ModelInterface;
import models.PictureModel;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;
import database.PictureTable.Pictures;

public class PictureDB {

	/**
	     * 
	     */
	private PictureDB() {
	}

	private static final PictureDB instance = new PictureDB();

	public static PictureDB getInstance() {
		return instance;
	}

	// L�gg till en ny kontakt
	public void addPicture(ContentResolver contentResolver, PictureModel pic) {
		ContentValues values = new ContentValues();
		values.put(Pictures.PICTURE, pic.getPicture());
		contentResolver.insert(Pictures.CONTENT_URI, values);
	}

	public int getCount(ContentResolver contentResolver) {
		int returnCount = 0;
		// SELECT * WHERE _id IS NOT null
		Cursor cursor = contentResolver.query(Pictures.CONTENT_URI, null,
				Pictures.PICTURE_ID + " IS NOT null", null, null);
		returnCount = cursor.getCount();
		cursor.close();
		return returnCount;
	}

	public String getAll(ContentResolver contentResolver) {
		Cursor cursor = contentResolver.query(Pictures.CONTENT_URI, null,
				Pictures.PICTURE_ID + " IS NOT null", null, null);
		Log.d("DB", "Cursorstorlek: " + cursor.getCount());
		String ret = new String();
		if (cursor.moveToFirst()) {
			do {
				Log.d("DB", "ID: " + Integer.toString(cursor.getInt(0)));
				Log.d("DB", "Namn: " + cursor.getString(1));
				ret += cursor.getString(1);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return ret;
	}

	public void updateContact(ContentResolver contentResolver, String newName,
			String id) {
		ContentValues contentValue = new ContentValues();
		contentValue.put(Pictures.PICTURE, newName);
		contentResolver.update(Pictures.CONTENT_URI, contentValue,
				Pictures.PICTURE_ID + "='" + id + "'", null);
	}

	// erases all entries in the database
	public void refreshCache(ContentResolver contentResolver) {
		int delete = contentResolver.delete(Pictures.CONTENT_URI, null, null);
		System.out.println("DELETED " + delete + " RECORDS FROM PICTURES DB");
	}

	public List<ModelInterface> getAllPictures(ContentResolver contentResolver) {
		List<ModelInterface> returnList = new ArrayList<ModelInterface>();
		Cursor cursor = contentResolver.query(Pictures.CONTENT_URI, null,
				Pictures.PICTURE_ID + " IS NOT null", null, null);
		if (cursor.moveToFirst()) {
			do {
				long id = 0;
				byte[] picture = null;
				for (int i = 0; i < cursor.getColumnCount(); i++) {
					if (cursor.getColumnName(i).equalsIgnoreCase(
							Pictures.PICTURE_ID)) {
						id = cursor.getInt(i);
					} else if (cursor.getColumnName(i).equalsIgnoreCase(
							Pictures.PICTURE)) {
						picture = cursor.getBlob(i);
					}
				}
				PictureModel pic = new PictureModel(id, picture);
				returnList.add(pic);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return returnList;
	}

	public void delete(ContentResolver contentResolver, PictureModel pic) {
		contentResolver.delete(Pictures.CONTENT_URI, Pictures.PICTURE_ID
				+ " = " + Long.toString(pic.getId()), null);
	}

	public void updateContact(ContentResolver contentResolver, PictureModel pic) {
		ContentValues values = new ContentValues();
		values.put(Pictures.PICTURE, pic.getPicture());
		int updated = contentResolver.update(Pictures.CONTENT_URI, values,
				null, null);
		Log.d("DB", "Uppdaterade " + updated + " pictures.");
	}
}
