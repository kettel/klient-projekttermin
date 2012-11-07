package databaseEncrypted;


import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import models.ModelInterface;

public class Database {
	
	/**
	 * Lägg till ett uppdrag/kontakt/meddelande till rätt databas
	 * @param m			ModellInterface av objekt som ska läggas till
	 */
	public void addToDB(ModelInterface m, Context context){
		String dbRep = m.getDatabaseRepresentation();
		if (dbRep.equalsIgnoreCase("assignment")) {
			DatabaseHandlerAssignment dha = new DatabaseHandlerAssignment();
			//dha.addModel(m);
		}
		else if(dbRep.equalsIgnoreCase("contact")){
			DatabaseHandlerContacts dhc = new DatabaseHandlerContacts();
			//dhc.addModel(m);
		}
		else if(dbRep.equalsIgnoreCase("message")){
			DatabaseHandlerMessages dhm = new DatabaseHandlerMessages(context);
			dhm.addModel(m);
		}
	}
	
	/**
	 * Räkna antal poster i vald databas
	 * @param m			datatypen för den databas som ska räknas samman
	 * @return
	 */
	public int getDBCount(ModelInterface m, Context context){
		String dbRep = m.getDatabaseRepresentation();
		int returnCount = 0;
		if (dbRep.equalsIgnoreCase("assignment")) {
			DatabaseHandlerAssignment dha = new DatabaseHandlerAssignment();
			//returnCount = dha.getTotal(m.getDatabaseRepresentation());
		}
		else if(dbRep.equalsIgnoreCase("contact")){
			DatabaseHandlerContacts dhc = new DatabaseHandlerContacts();
			//returnCount = dhc.getTotal(m.getDatabaseRepresentation());
		}
		else if(dbRep.equalsIgnoreCase("message")){
			DatabaseHandlerMessages dhm = new DatabaseHandlerMessages(context);
			returnCount = dhm.getCount(m);
		}
		return returnCount;
	}
	
	/**
	 * Hämta alla objekt från databasen i en ArrayList
	 * @param m	ModelInterface	Den önskade returtypen
	 * @return	
	 */
	public List<ModelInterface> getAllFromDB(ModelInterface m, Context context){
		String dbRep = m.getDatabaseRepresentation();
		List<ModelInterface> returnList = new ArrayList<ModelInterface>();
		if (dbRep.equalsIgnoreCase("assignment")) {
			DatabaseHandlerAssignment dha = new DatabaseHandlerAssignment();
			//returnList = dha.getAllModels(m);
		}
		else if(dbRep.equalsIgnoreCase("contact")){
			DatabaseHandlerContacts dhc = new DatabaseHandlerContacts();
			//returnList = dhc.getAllModels(m);
		}
		else if(dbRep.equalsIgnoreCase("message")){
			DatabaseHandlerMessages dhm = new DatabaseHandlerMessages(context);
			returnList = dhm.getAllModels(m);
		}
		return returnList;
	}
	
	/**
	 * Ta bort ett objekt från databasen
	 * @param m
	 * @param context
	 */
	public void deleteFromDB(ModelInterface m, Context context){
		String dbRep = m.getDatabaseRepresentation();
		if (dbRep.equalsIgnoreCase("assignment")) {
			DatabaseHandlerAssignment dha = new DatabaseHandlerAssignment();
			//dha.removeModel(m.getDatabaseRepresentation(), Long.toString(m.getId()));
		}
		else if(dbRep.equalsIgnoreCase("contact")){
			DatabaseHandlerContacts dhc = new DatabaseHandlerContacts();
			//dhc.removeModel(m.getDatabaseRepresentation(), Long.toString(m.getId()));
		}
		else if(dbRep.equalsIgnoreCase("message")){
			System.out.println("Ska ta bort meddelande "+Long.toString(m.getId()) + ". Från " + m.getDatabaseRepresentation());
			DatabaseHandlerMessages dhm = new DatabaseHandlerMessages(context);
			dhm.removeModel(m);
		}
	}
	
	/**
	 * Uppdatera ett objekt i databasen
	 * @param m
	 */
	public void updateModel(ModelInterface m, Context context){
		String dbRep = m.getDatabaseRepresentation();
		if (dbRep.equalsIgnoreCase("assignment")) {
			DatabaseHandlerAssignment dha = new DatabaseHandlerAssignment();
			//dha.updateModel(m);
		}
		else if(dbRep.equalsIgnoreCase("contact")){
			DatabaseHandlerContacts dhc = new DatabaseHandlerContacts();
			//dhc.updateModel(m);
		}
		else if(dbRep.equalsIgnoreCase("message")){
			DatabaseHandlerMessages dhm = new DatabaseHandlerMessages(context);
			dhm.updateModel(m);
		}
	}

}