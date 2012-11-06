package databaseEncrypted;
import java.io.File;

import android.content.Context;
import android.util.Log;
import models.ModelInterface;
import net.sqlcipher.database.SQLiteDatabase;

public class DatabaseHandler {
	
	private Context context = null;
	private String dbPath = null;
	private SQLiteDatabase database = null;
	
	protected String password = "password";
	
	public DatabaseHandler(Context c){
		this.context = c;
		this.dbPath = context.getDatabasePath("test.db").getPath();
		initializeSQLCipher();
	}
	
	private void initializeSQLCipher() {
		SQLiteDatabase.loadLibs(this.context);
		database = SQLiteDatabase.openOrCreateDatabase(dbPath, password, null);
	}
	
	public void addToDB(ModelInterface m){
		database.execSQL("create table if not exists t1(a, b)");
        database.execSQL("insert into t1(a, b) values(?, ?)", new Object[]{"one for the money",
                                                                        "two for the show"});
        
	}
	
}
