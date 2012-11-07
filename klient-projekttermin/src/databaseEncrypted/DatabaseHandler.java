package databaseEncrypted;
import java.io.File;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import models.Contact;
import models.ModelInterface;
import net.sqlcipher.database.SQLiteDatabase;

public class DatabaseHandler {
	
	private Context context = null;
	private File databaseFile = null;
	private SQLiteDatabase database = null;
	
	protected String password = "password";
	
	public DatabaseHandler(Context c){
		this.context = c;
		this.databaseFile = context.getDatabasePath("tddd36.db");
		Log.d("DB","Sökväg: " + databaseFile.toString());
		// Om databasfilen inte existerar, skapa den
		if(!this.databaseFile.exists()){
			this.databaseFile.mkdirs();
			this.databaseFile.delete();
		}
	}
	
	
	public void addToDB(ModelInterface m){
		SQLiteDatabase.loadLibs(context);
		database = SQLiteDatabase.openOrCreateDatabase(databaseFile, password, null);
		database.execSQL("create table if not exists t1(a, b)");
        database.execSQL("insert into t1(a, b) values(?, ?)", new Object[]{"one for the money",
                                                                        "two for the show"});
        
        Cursor cursor = database.rawQuery("SELECT * FROM t1", null);
        
        if (cursor.moveToFirst()) {
            do {
                Log.d("DB",cursor.getString(0));
                Log.d("DB",cursor.getString(1));
            } while (cursor.moveToNext());
        }
 		cursor.close();
 		database.close();
	}
	
}
