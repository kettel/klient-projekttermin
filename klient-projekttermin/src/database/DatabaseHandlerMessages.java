package database;

import java.util.ArrayList;
import java.util.List;

import models.MessageModel;
import models.ModelInterface;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHandlerMessages extends SQLiteOpenHelper{
	// Alla statiska variabler
    // Databas version
    private static final int DATABASE_VERSION = 1;
 
    // Databasens namn
    private static final String DATABASE_NAME = "messageManager";
 
    // Contacts tabellnamn
    private static final String TABLE_MESSAGES = "messages";

    // Contacts tabellkolumnnamn
    private static final String KEY_ID = "id";
    private static final String KEY_MESSAGE_CONTENT = "content";
    private static final String KEY_RECEIVER = "receiver";
	private static final String KEY_MESSAGE_TIMESTAMP = "timestamp";

 
    public DatabaseHandlerMessages(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
 
    // Skapa tabell
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_MESSAGES_TABLE = "CREATE TABLE " + TABLE_MESSAGES + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"  
        		+ KEY_MESSAGE_CONTENT + " TEXT,"
                + KEY_RECEIVER + " TEXT,"
                + KEY_MESSAGE_TIMESTAMP + " TEXT" + ")";
        db.execSQL(CREATE_MESSAGES_TABLE);
    	//executeSQLScript(db, "assignments.sql", this);
    }
 
    // Uppgradera databasen vid behov (om en äldre version existerar)
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Om en äldre version existerar, ta bort den
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGES);
 
        // Skapa sedan databasen igen
        onCreate(db);
    }
    
    /**
     * Lägg till ett meddelande
     * @param Contact	Den kontakt som ska läggas till i databasen
     */
    public void addMessage(MessageModel message) {
        SQLiteDatabase db = this.getWritableDatabase();
 
        ContentValues values = new ContentValues();
        values.put(KEY_MESSAGE_CONTENT, message.getMessageContent().toString());
        values.put(KEY_RECEIVER, message.getReciever().toString());
        values.put(KEY_MESSAGE_TIMESTAMP, message.getMessageTimeStamp());

        // Lägg till kontakter i databasen
        db.insert(TABLE_MESSAGES, null, values);
        // Stäng databasen. MYCKET VIKTIGT!!
        db.close(); 
    }
    
    /**
     * Räkna antal meddelanden i messages-databasen
     * @return int 		Antal meddelanden
     */
	public int getMessageCount() {
		String countQuery = "SELECT * FROM " + TABLE_MESSAGES;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
	}

	public List<ModelInterface> getAllMessages() {
		List<ModelInterface> messageList = new ArrayList<ModelInterface>();
		

		return messageList;
	}
}
