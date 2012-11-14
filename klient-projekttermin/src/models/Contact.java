package models;

/**
 * Datatyp/model för en kontakt.
 * 
 * @author kettel
 * 
 */
public class Contact implements ModelInterface {
	// Databasrepresentation för kontakt
	private String databaseRepresentation = "contact";
	// Databas-id för en kontakt. Är -1 när instansen ej varit i databasen,
	// något annat sen. Sätts av databasen så pilla ej!
	private long id = -1;
	// Kontaktens namn
	private String contactName;

	/**
	 * Tom konstruktor för en kontakt. Används primärt för att hämta alla
	 * kontakter från databasen.
	 */
	public Contact() {
	}

	/**
	 * Konstruktor för att skapa en kontakt utefter ett namn.
	 * 
	 * @param contactName
	 */
	public Contact(String contactName) {
		this.contactName = contactName;
	}

	/**
	 * Konstruktor för att återskapa ett meddelande när det hämtas från
	 * databasen.
	 * 
	 * @param id
	 *            long
	 * @param contactName
	 *            String
	 */
	public Contact(long id, String contactName) {
		this.id = id;
		this.contactName = contactName;
	}
	
	/**
	 * Hämta kontaktens namn.
	 * @return	String
	 */
	public String getContactName() {
		return contactName;
	}

	/**
	 * Hämta databas-id. -1 om kontakten inte varit i databasen, annars något annat.
	 * @return	long
	 */
	public long getId() {
		return id;
	}

	/**
	 * Hämta databasrepresentationen.
	 * @return String
	 */
	public String getDatabaseRepresentation() {
		return databaseRepresentation;
	}
}
