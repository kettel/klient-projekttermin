package com.example.klien_projekttermin.databaseEncryptedSingleton;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.sqlcipher.database.SQLiteDatabase;

import com.example.klien_projekttermin.models.ModelInterface;

import android.content.Context;
import android.util.Log;


public class Database {
	
	private static Context context = null;
	private static SQLiteDatabase database = null;

	protected static String PASSWORD = "password";
	protected static String KEY_ID = "id";
	protected static File dbFile = null;
	
	private Database(){}
	
	private static Database instance = new Database();
	
	public static Database getInstance(Context c){
		// Om kontexten redan är skapad (dvs om instansen hämtas efter att den är skapad)
		if(context== null){
			context = c;
		}
		
		//databaseFile = context.getDatabasePath("tddd36.db");
		dbFile = context.getDatabasePath("tddd36.db");
		
		// Om databasfilen inte existerar, skapa den
		if(!dbFile.exists()){
			dbFile.mkdirs();
			dbFile.delete();
		}
		Log.d("DB", "About to create DB");
		// Ladda in SQLCipher-bibliotek filer
		SQLiteDatabase.loadLibs(context);
		Log.d("DB","Libraries loaded");
		Log.d("DB","Databasefile to use: " + dbFile.toString());
		database = SQLiteDatabase.openOrCreateDatabase(dbFile, PASSWORD, null);
		
		return instance;
	}
	
	/**
	 * Lägg till ett uppdrag/kontakt/meddelande till rätt databas
	 * @param m			ModellInterface av objekt som ska läggas till
	 */
	public void addToDB(ModelInterface m){
		String dbRep = m.getDatabaseRepresentation();
		if (dbRep.equalsIgnoreCase("assignment")) {
			DatabaseHandlerAssignment dha = new DatabaseHandlerAssignment(database);
			dha.addModel(database, m);
		}
		else if(dbRep.equalsIgnoreCase("contact")){
			DatabaseHandlerContacts dhc = new DatabaseHandlerContacts(database);
			dhc.addModel(database, m);
		}
		else if(dbRep.equalsIgnoreCase("message")){
			DatabaseHandlerMessages dhm = new DatabaseHandlerMessages(database);
			dhm.addModel(database, m);
		}
	}
	
	/**
	 * Räkna antal poster i vald databas
	 * @param m			datatypen för den databas som ska räknas samman
	 * @return
	 */
	public int getDBCount(ModelInterface m){
		String dbRep = m.getDatabaseRepresentation();
		int returnCount = 0;
		if (dbRep.equalsIgnoreCase("assignment")) {
			DatabaseHandlerAssignment dha = new DatabaseHandlerAssignment(database);
			returnCount = dha.getCount(database, m);
		}
		else if(dbRep.equalsIgnoreCase("contact")){
			DatabaseHandlerContacts dhc = new DatabaseHandlerContacts(database);
			returnCount = dhc.getCount(database, m);
		}
		else if(dbRep.equalsIgnoreCase("message")){
			DatabaseHandlerMessages dhm = new DatabaseHandlerMessages(database);
			returnCount = dhm.getCount(database, m);
		}
		return returnCount;
	}
	
	/**
	 * Hämta alla objekt från databasen i en ArrayList
	 * @param m	ModelInterface	Den önskade returtypen
	 * @return	
	 */
	public List<ModelInterface> getAllFromDB(ModelInterface m){
		String dbRep = m.getDatabaseRepresentation();
		List<ModelInterface> returnList = new ArrayList<ModelInterface>();
		if (dbRep.equalsIgnoreCase("assignment")) {
			DatabaseHandlerAssignment dha = new DatabaseHandlerAssignment(database);
			returnList = dha.getAllModels(database, m);
		}
		else if(dbRep.equalsIgnoreCase("contact")){
			DatabaseHandlerContacts dhc = new DatabaseHandlerContacts(database);
			returnList = dhc.getAllModels(database, m);
		}
		else if(dbRep.equalsIgnoreCase("message")){
			DatabaseHandlerMessages dhm = new DatabaseHandlerMessages(database);
			returnList = dhm.getAllModels(database, m);
		}
		return returnList;
	}
	
	/**
	 * Ta bort ett objekt från databasen
	 * @param m
	 * @param context
	 */
	public void deleteFromDB(ModelInterface m){
		String dbRep = m.getDatabaseRepresentation();
		if (dbRep.equalsIgnoreCase("assignment")) {
			DatabaseHandlerAssignment dha = new DatabaseHandlerAssignment(database);
			dha.removeModel(database, m);
		}
		else if(dbRep.equalsIgnoreCase("contact")){
			DatabaseHandlerContacts dhc = new DatabaseHandlerContacts(database);
			dhc.removeModel(database, m);
		}
		else if(dbRep.equalsIgnoreCase("message")){
			System.out.println("Ska ta bort meddelande "+Long.toString(m.getId()) + ". Från " + m.getDatabaseRepresentation());
			DatabaseHandlerMessages dhm = new DatabaseHandlerMessages(database);
			dhm.removeModel(database, m);
		}
	}
	
	/**
	 * Uppdatera ett objekt i databasen
	 * @param m
	 */
	public void updateModel(ModelInterface m){
		String dbRep = m.getDatabaseRepresentation();
		if (dbRep.equalsIgnoreCase("assignment")) {
			DatabaseHandlerAssignment dha = new DatabaseHandlerAssignment(database);
			dha.updateModel(database, m);
		}
		else if(dbRep.equalsIgnoreCase("contact")){
			DatabaseHandlerContacts dhc = new DatabaseHandlerContacts(database);
			dhc.updateModel(database, m);
		}
		else if(dbRep.equalsIgnoreCase("message")){
			DatabaseHandlerMessages dhm = new DatabaseHandlerMessages(database);
			dhm.updateModel(database, m);
		}
	}

}