package com.example.klien_projekttermin.databaseNotEncrypted;
import java.io.File;
import java.util.List;

import com.example.klien_projekttermin.models.ModelInterface;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public abstract class DatabaseHandler {

	Context context = null;
	File databaseFile = null;

	protected String PASSWORD = "password";
	protected String KEY_ID = "id";

	/**
	 * Konstruktor som initierar databasen med rätt context.
	 * @param c
	 */
	public DatabaseHandler(Context c){
		this.context = c;
		this.databaseFile = context.getDatabasePath("tddd36.db");

		// Om databasfilen inte existerar, skapa den
		if(!this.databaseFile.exists()){
			this.databaseFile.mkdirs();
			this.databaseFile.delete();
		}
	}

	/**
	 * Initiera databasen för vald modell så rätt tabeller vid behov skapas.
	 */
	public abstract void initiateModelDatabase();

	/**
	 * Lägg till en modell till databasen.
	 * @param m
	 */
	public abstract void addModel(ModelInterface m);

	/**
	 * Uppdatera fält för en modell i databasen.
	 * @param m
	 */
	public abstract void updateModel(ModelInterface m);

	/**
	 * Hämta alla modeller från databasen.
	 * @param m
	 * @return
	 */
	public abstract List<ModelInterface> getAllModels(ModelInterface m);

	/**
	 * Räkna alla rader i databasen för önskad modell.
	 * @param m
	 * @return
	 */
	public int getCount(ModelInterface m){
		SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(databaseFile, null);

		// Select All frågan. Ze classic! Dvs, hämta allt från MESSAGES-databasen
        String selectQuery = "SELECT  * FROM " + m.getDatabaseRepresentation();
 
        Cursor cursor = database.rawQuery(selectQuery, null);
        int returnCount = cursor.getCount();
        
        cursor.close();
        database.close();
        
        return returnCount;
	}

	/**
	 * Ta bort (o)önskad modell från databasen.
	 * @param m
	 */
	public void removeModel(ModelInterface m){
		SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(databaseFile, null);
		database.execSQL("DELETE FROM " + m.getDatabaseRepresentation() + 
						" WHERE " + KEY_ID + " = " + Long.toString(m.getId()));
		database.close();
	}

}