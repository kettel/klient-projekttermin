package database;

import java.util.ArrayList;
import java.util.List;

import database.AuthenticationTable.Authentications;


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

	public void addAuthenticationContent(ContentResolver contentResolver, AuthenticationModel authentication) {
		System.out.println("LÄGGER TILL EN MODELL I DATABASEN");
		ContentValues values = new ContentValues();
		values.put(Authentications.USERNAME, authentication.getUserName());
		values.put(Authentications.PASSWORD, authentication.getPasswordHash());
		values.put(Authentications.ISACCESSGRANTED, authentication.isAccessGranted());

		System.out.println("Values: "+values.toString());
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

	public void delete(ContentResolver contentResolver, AuthenticationModel authentication) {
		contentResolver.delete(Authentications.CONTENT_URI, Authentications.AUTHENTICATION_ID + " = " + Long.toString(authentication.getId()), null);
	}

	public List<ModelInterface> getAllAuthenticationModels(ContentResolver contentResolver) {
		List<ModelInterface> returnList = new ArrayList<ModelInterface>();
		Cursor cursor = contentResolver.query(Authentications.CONTENT_URI, null,
				Authentications.AUTHENTICATION_ID + " IS NOT null", null, null);
		System.out.println("CURSOR COUNT: "+cursor.getCount());
		if (cursor.moveToFirst()) {
			do {
				String userName = new String(), password = new String(), isAccessGranted = new String(); long authenticationId = 0;

				for (int i = 0; i < cursor.getColumnCount(); i++) {
					String currentCol = cursor.getColumnName(i);
					if (currentCol.equalsIgnoreCase(Authentications.USERNAME)) {
						userName = cursor.getString(i);
					} else if (currentCol.equalsIgnoreCase(Authentications.AUTHENTICATION_ID)) {
						authenticationId = cursor.getInt(i);
					} else if (currentCol.equalsIgnoreCase(Authentications.PASSWORD)) {
						password = cursor.getString(i);
					}else if (currentCol.equalsIgnoreCase(Authentications.ISACCESSGRANTED)) {
						isAccessGranted = cursor.getString(i);
					}
					
				}
				AuthenticationModel authenticationModel = new AuthenticationModel(authenticationId, userName, password, isAccessGranted);
				returnList.add((ModelInterface) authenticationModel);
			} while (cursor.moveToNext());
		}
		return returnList;
	}

	public void updateAuthentication(ContentResolver contentResolver, AuthenticationModel authenticationModel) {
		ContentValues values = new ContentValues();
		values.put(Authentications.USERNAME, authenticationModel.getUserName());
		values.put(Authentications.PASSWORD, authenticationModel.getPasswordHash());
		values.put(Authentications.ISACCESSGRANTED, authenticationModel.isAccessGranted());
		int updated = contentResolver.update(Authentications.CONTENT_URI, values, Authentications.AUTHENTICATION_ID + " = " + Long.toString(authenticationModel.getId()),null);
		Log.d("DB", "Uppdaterade " + updated + " messages.");
	}

}
