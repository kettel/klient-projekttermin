package database;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class DatabaseHelpers {
	/**
	 * Läs SQL-frågor från en extern fil.
	 * @param database
	 * @param dbname
	 */
	public void executeSQLScript(SQLiteDatabase database, String dbname, Context context) {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	    byte buf[] = new byte[1024];
	    int len;
	    AssetManager assetManager = context.getAssets();
	    InputStream inputStream = null;
	         
	    try{
	        inputStream = assetManager.open(dbname);
	        while ((len = inputStream.read(buf)) != -1) {
	            outputStream.write(buf, 0, len);
	        }
	        outputStream.close();
	        inputStream.close();
	             
	        String[] createScript = outputStream.toString().split(";");
	        for (int i = 0; i < createScript.length; i++) {
	             String sqlStatement = createScript[i].trim();
	            // TODO You may want to parse out comments here
	            if (sqlStatement.length() > 0) {
	                    database.execSQL(sqlStatement + ";");
	            }
	        }
	    } catch (IOException e){
	        // TODO Handle Script Failed to Load
	    } catch (SQLException e) {
	        // TODO Handle Script Failed to Execute
	    }
	}
}