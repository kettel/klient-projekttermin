package databaseEncrypted;

import java.util.ArrayList;
import java.util.List;

import net.sqlcipher.database.SQLiteDatabase;

import models.MessageModel;
import models.ModelInterface;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class DatabaseHandlerMessages extends DatabaseHandler{

	// Contacts tabellnamn
    private static final String TABLE_MESSAGES = "message";

    // Contacts tabellkolumnnamn
    private static final String KEY_MESSAGE_CONTENT = "content";
    private static final String KEY_RECEIVER = "receiver";
	private static final String KEY_MESSAGE_TIMESTAMP = "timestamp";
	private static final String KEY_IS_READ = "isRead";
	
	public DatabaseHandlerMessages(Context c) {
		// Skicka Context till DatabaseHandler
		super(c);
		
		// Initiera Messages-tabellen.
		initiateModelDatabase();
	}
	
	@Override
	public void initiateModelDatabase() {
		SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(databaseFile, PASSWORD, null);
        database.execSQL("CREATE TABLE IF NOT EXISTS "+ TABLE_MESSAGES +"("
        				+ KEY_ID + " INTEGER PRIMARY KEY,"  
        				+ KEY_MESSAGE_CONTENT + " TEXT,"
        				+ KEY_RECEIVER + " TEXT,"
        				+ KEY_MESSAGE_TIMESTAMP + " TEXT," 
        				+ KEY_IS_READ + " TEXT"+ ")");
        database.close();
	}

	@Override
	public void addModel(ModelInterface m) {
		MessageModel message = (MessageModel) m;
		SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(databaseFile, PASSWORD, null);
		
		ContentValues values = new ContentValues();
        values.put(KEY_MESSAGE_CONTENT, message.getMessageContent().toString());
        values.put(KEY_RECEIVER, message.getReciever().toString());
        values.put(KEY_MESSAGE_TIMESTAMP, Long.toString(message.getMessageTimeStamp()));

        // Lägg till isRead som en String, TRUE om true, FALSE om false.
        values.put(KEY_IS_READ, (message.isRead()? "TRUE" : "FALSE"));
        
     // Lägg till meddelanden i databasen
        database.insert(TABLE_MESSAGES, null, values);
        // Stäng databasen. MYCKET VIKTIGT!!
        database.close(); 
	}

	@Override
	public void updateModel(ModelInterface m) {
		MessageModel message = (MessageModel) m;
		SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(databaseFile, PASSWORD, null);
		
		String UPDATE_MESSAGES = "UPDATE " + TABLE_MESSAGES + " SET "
        		+ KEY_MESSAGE_CONTENT + " = \""+ message.getMessageContent() + "\" ,"
                + KEY_RECEIVER + " = \""+ message.getReciever() + "\" ,"
                + KEY_IS_READ + " = \""+ (message.isRead()? "TRUE" : "FALSE") + "\" "
                + "WHERE " + KEY_ID + " = " + Long.toString(message.getId());
		
        database.execSQL(UPDATE_MESSAGES);
        
        database.close();
	}

	@Override
	public List<ModelInterface> getAllModels(ModelInterface m) {
		List<ModelInterface> messageList = new ArrayList<ModelInterface>();
		
		// Select All frågan. Ze classic! Dvs, hämta allt från MESSAGES-databasen
        String selectQuery = "SELECT  * FROM " + TABLE_MESSAGES;
 
        SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(databaseFile, PASSWORD, null);
        Cursor cursor = database.rawQuery(selectQuery, null);
 
        // Loopa igenom alla rader och lägg till dem i listan 
        
        if (cursor.moveToFirst()) {
            do {
            	MessageModel message = new MessageModel(Long.valueOf(cursor.getString(0)),
            											cursor.getString(1),
            											cursor.getString(2),
            											Long.valueOf(cursor.getString(3)),
            											Boolean.valueOf(cursor.getString(4)));
                messageList.add(message);
            } while (cursor.moveToNext());
        }
        
        cursor.close();
        database.close();
 		
        // Returnera meddelandelistan
		return messageList;
	}
}
