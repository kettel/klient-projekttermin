package model;

public interface ModelInterface {

	/**
	* Returnerar datatyp f�r den aktuella modellen
	* @return String namn p� datatypen
	*/
	public String getDatabaseRepresentation();

	/**
	* Returnerar id fr�n databasen om det finns, annars -1
	* @return
	*/
	public long getId();

	}
