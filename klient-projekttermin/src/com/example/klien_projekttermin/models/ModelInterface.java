package com.example.klien_projekttermin.models;

public interface ModelInterface {

	/**
	 * Returnerar datatyp för den aktuella modellen
	 * @return		String		namn på datatypen
	 */
	public String getDatabaseRepresentation();
	
	/**
	 * Returnerar id från databasen om det finns, annars -1
	 * @return
	 */
	public long getId();
	
}
