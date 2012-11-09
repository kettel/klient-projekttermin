package com.example.klien_projekttermin.databaseEncryptedSingleton;
import java.io.File;
import java.util.List;

import com.example.klien_projekttermin.models.ModelInterface;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import net.sqlcipher.database.SQLiteDatabase;

public abstract class DatabaseHandler {
		
	/**
	 * Initiera databasen för vald modell så rätt tabeller vid behov skapas.
	 */
	public abstract void initiateModelDatabase(SQLiteDatabase database);
	
	/**
	 * Lägg till en modell till databasen.
	 * @param m
	 */
	public abstract void addModel(SQLiteDatabase database, ModelInterface m);
	
	/**
	 * Uppdatera fält för en modell i databasen.
	 * @param m
	 */
	public abstract void updateModel(SQLiteDatabase database, ModelInterface m);
	
	/**
	 * Hämta alla modeller från databasen.
	 * @param m
	 * @return
	 */
	public abstract List<ModelInterface> getAllModels(SQLiteDatabase database, ModelInterface m);
	
	/**
	 * Räkna alla rader i databasen för önskad modell.
	 * @param m
	 * @return
	 */
	public int getCount(SQLiteDatabase database, ModelInterface m){
		//SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(databaseFile, PASSWORD, null);
		
		// Select All frågan. Ze classic! Dvs, hämta allt från MESSAGES-databasen
        String selectQuery = "SELECT  * FROM " + m.getDatabaseRepresentation();
 
        Cursor cursor = database.rawQuery(selectQuery, null);
        int returnCount = cursor.getCount();
        
        cursor.close();
        //database.close();
        
        return returnCount;
	}
	
	/**
	 * Ta bort (o)önskad modell från databasen.
	 * @param m
	 */
	public void removeModel(SQLiteDatabase database, ModelInterface m){
		//SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(databaseFile, 
		//															PASSWORD, null);
		database.execSQL("DELETE FROM " + m.getDatabaseRepresentation() + 
						" WHERE " + Database.KEY_ID + " = " + Long.toString(m.getId()));
		//database.close();
	}
	
}
