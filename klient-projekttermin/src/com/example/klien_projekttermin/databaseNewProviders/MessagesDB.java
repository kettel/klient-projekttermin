package com.example.klien_projekttermin.databaseNewProviders;

import java.util.List;

import com.example.klien_projekttermin.databaseNewProviders.ContactTable.Contacts;
import com.example.klien_projekttermin.databaseNewProviders.MessageTable.Messages;

import models.MessageModel;
import models.ModelInterface;
import android.content.ContentResolver;
import android.database.Cursor;

public class MessagesDB {
	private MessagesDB(){}
	
	private static MessagesDB instance = new MessagesDB();
	
	
	public static MessagesDB getInstance() {
		// TODO Auto-generated method stub
		return instance;
	}


	public void addMessage(ContentResolver contentResolver, MessageModel message) {
		// TODO Auto-generated method stub
		
	}


	public int getCount(ContentResolver contentResolver) {
		int returnCount = 0;
		Cursor cursor = contentResolver.query(
    			Messages.CONTENT_URI, null,Messages.MESSAGE_ID+ " IS NOT null", null, null);
    	returnCount = cursor.getCount();
    	cursor.close();
    	return returnCount;
	}


	public void delete(ContentResolver contentResolver, MessageModel m) {
		// TODO Auto-generated method stub
		
	}


	public List<ModelInterface> getAllMessages(ContentResolver contentResolver) {
		// TODO Auto-generated method stub
		return null;
	}


	public void updateMessage(ContentResolver contentResolver, MessageModel m) {
		// TODO Auto-generated method stub
		
	}

}
