package com.example.klien_projekttermin.database;

import java.util.ArrayList;
import java.util.List;

import com.example.klien_projekttermin.database.AuthenticationTable.Authentications;

import models.AuthenticationModel;
import models.ModelInterface;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

public class AuthenticationDB {
	private AuthenticationDB() {
	}

	private static AuthenticationDB instance = new AuthenticationDB();

	public static AuthenticationDB getInstance() {
		return instance;
	}

	public void addAuthenticationComponents(ContentResolver contentResolver, AuthenticationModel authenticationModel) {
		ContentValues values = new ContentValues();
		values.put(Authentications.USERNAME, authenticationModel.getUserName());
		values.put(Authentications.PASSWORD, authenticationModel.getPasswordHash());
		contentResolver.insert(Authentications.CONTENT_URI, values);
	}

	public int getCount(ContentResolver contentResolver) {
		int returnCount = 0;
		Cursor cursor = contentResolver.query(Authentications.CONTENT_URI, null,
				Authentications.AUTHENTICATION_ID + " IS NOT null", null, null);
		returnCount = cursor.getCount();
		cursor.close();
		return returnCount;
	}

	public void delete(ContentResolver contentResolver, AuthenticationModel authenticationModel) {
		contentResolver.delete(Authentications.CONTENT_URI, Authentications.AUTHENTICATION_ID + " = " + Long.toString(authenticationModel.getId()), null);
	}

	public List<ModelInterface> getAllAuthenticationComponents(ContentResolver contentResolver) {
		List<ModelInterface> returnList = new ArrayList<ModelInterface>();
		Cursor cursor = contentResolver.query(Authentications.CONTENT_URI, null,
				Authentications.AUTHENTICATION_ID + " IS NOT null", null, null);
		if (cursor.moveToFirst()) {
			do {
				String userName = new String(), password = new String();
				long id = 0;

				for (int i = 0; i < cursor.getColumnCount(); i++) {
					String currentCol = cursor.getColumnName(i);
					if (currentCol.equalsIgnoreCase(Authentications.AUTHENTICATION_ID)) {
						id = cursor.getInt(i);
					} else if (currentCol.equalsIgnoreCase(Authentications.USERNAME)) {
						userName = cursor.getString(i);
					} else if (currentCol.equalsIgnoreCase(Authentications.PASSWORD)) {
						password = cursor.getString(i);
					} 
				}
				AuthenticationModel authenticationModel = new AuthenticationModel(id, userName, password);
				returnList.add(authenticationModel);
			} while (cursor.moveToNext());
		}
		return returnList;
	}

	public void updateAuthentication(ContentResolver contentResolver, AuthenticationModel authenticationModel) {
		
		ContentValues values = new ContentValues();
		values.put(Authentications.USERNAME, authenticationModel.getUserName());
		values.put(Authentications.PASSWORD, authenticationModel.getPasswordHash());
		
		int updated = contentResolver.update(Authentications.CONTENT_URI, values,
				Authentications.AUTHENTICATION_ID + " = " + Long.toString(authenticationModel.getId()),null);
		Log.d("DB", "Uppdaterade " + updated + " authentications.");
	}

}
