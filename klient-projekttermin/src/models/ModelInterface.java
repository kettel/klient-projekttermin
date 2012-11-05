package models;

public interface ModelInterface {

	/**
	 * Returnerar datatyp för den aktuella modellen
	 * @return		String		namn på datatypen
	 */

	public String getDatabaseRepresentation();
	
	public long getId();
}
