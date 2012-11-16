package com.example.klien_projekttermin.databaseNewProviders;

import java.util.ArrayList;
import java.util.List;

import com.example.klien_projekttermin.databaseNewProviders.AssignmentTable.Assignments;
import com.example.klien_projekttermin.databaseNewProviders.MessageTable.Messages;

import models.MessageModel;
import models.ModelInterface;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

public class MessagesDB {
	private MessagesDB() {
	}

	private static MessagesDB instance = new MessagesDB();

	public static MessagesDB getInstance() {
		return instance;
	}

	public void addMessage(ContentResolver contentResolver, MessageModel message) {
		ContentValues values = new ContentValues();
		values.put(Messages.CONTENT, message.getMessageContent()
				.toString());
		values.put(Messages.RECEIVER, message.getReciever().toString());
		values.put(Messages.SENDER, message.getSender().toString());
		values.put(Messages.TIMESTAMP,
				Long.toString(message.getMessageTimeStamp()));
		values.put(Messages.ISREAD, Boolean.toString(message.isRead()));
		contentResolver.insert(Messages.CONTENT_URI, values);
	}

	public int getCount(ContentResolver contentResolver) {
		int returnCount = 0;
		Cursor cursor = contentResolver.query(Messages.CONTENT_URI, null,
				Messages.MESSAGE_ID + " IS NOT null", null, null);
		returnCount = cursor.getCount();
		cursor.close();
		return returnCount;
	}

	public void delete(ContentResolver contentResolver, MessageModel message) {
		contentResolver.delete(Messages.CONTENT_URI, Messages.MESSAGE_ID + " = " + Long.toString(message.getId()), null);
	}

	public List<ModelInterface> getAllMessages(ContentResolver contentResolver) {
		List<ModelInterface> returnList = new ArrayList<ModelInterface>();
		Cursor cursor = contentResolver.query(Messages.CONTENT_URI, null,
				Messages.MESSAGE_ID + " IS NOT null", null, null);
		if (cursor.moveToFirst()) {
			do {
				String content = new String(), receiver = new String(), sender = new String();
				boolean isRead = false;
				Long timestamp = Long.valueOf("0");
				long id = 0;

				for (int i = 0; i < cursor.getColumnCount(); i++) {
					String currentCol = cursor.getColumnName(i);
					if (currentCol.equalsIgnoreCase(Messages.CONTENT)) {
						content = cursor.getString(i);
					} else if (currentCol.equalsIgnoreCase(Messages.MESSAGE_ID)) {
						id = cursor.getInt(i);
					} else if (currentCol.equalsIgnoreCase(Messages.RECEIVER)) {
						receiver = cursor.getString(i);
					} else if (currentCol.equalsIgnoreCase(Messages.SENDER)) {
						sender = cursor.getString(i);
					} else if (currentCol.equalsIgnoreCase(Messages.TIMESTAMP)) {
						timestamp = Long.valueOf(cursor.getString(i));
					} else if (currentCol.equalsIgnoreCase(Messages.ISREAD)) {
						isRead = Boolean.valueOf(cursor.getString(i));
					}
				}
				MessageModel message = new MessageModel(id, content, receiver,
						sender, timestamp, isRead);
				returnList.add(message);
			} while (cursor.moveToNext());
		}
		return null;
	}

	public void updateMessage(ContentResolver contentResolver, MessageModel message) {
		ContentValues values = new ContentValues();
		values.put(Messages.CONTENT, message.getMessageContent()
				.toString());
		values.put(Messages.RECEIVER, message.getReciever().toString());
		values.put(Messages.SENDER, message.getSender().toString());
		values.put(Messages.TIMESTAMP,
				Long.toString(message.getMessageTimeStamp()));
		values.put(Messages.ISREAD, Boolean.toString(message.isRead()));
		int updated = contentResolver.update(Assignments.CONTENT_URI, values,
				Messages.MESSAGE_ID + " = " + Long.toString(message.getId()),
				null);
		Log.d("DB", "Uppdaterade " + updated + " messages.");
	}

}
