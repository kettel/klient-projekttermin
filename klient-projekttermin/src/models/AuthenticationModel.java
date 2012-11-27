package models;

public class AuthenticationModel implements ModelInterface {
	
	private String userName;
	private String passwordHash;
	private String isAccessGranted;
	private String databaseRepresentation = "authentication";
	private long id = -1;
	
	public AuthenticationModel(){
		
	}
	
	public AuthenticationModel(String userName, String passwordHash){
		this.userName = userName;
		this.passwordHash = passwordHash;
	}
	
	public AuthenticationModel(long id, String userName, String passwordHash){
		this.userName = userName;
		this.passwordHash = passwordHash;
		this.id = id;
	}
	
	public AuthenticationModel(long id, String userName, String passwordHash, String isAccessGranted){
		this.userName = userName;
		this.passwordHash = passwordHash;
		this.id = id;
		this.isAccessGranted = isAccessGranted;
	}
	
	public AuthenticationModel(String accessDecision){
		
		this.isAccessGranted = accessDecision;
	}
	
	public String getUserName(){
		return userName;
	}
	
	public String getPasswordHash(){
		return passwordHash;
	}
	
	/**
	 * Metoden returnerar true om användaren får access, annars false;
	 */
	public String isAccessGranted(){
		return isAccessGranted;
	}
	
	public String getDatabaseRepresentation() {
		return databaseRepresentation;
	}
	
	/**
	 * Hämta databas-id för objektet i databasen. Har det varit i databasen
	 * är det något annat än -1.
	 * @return long id
	 */
	public long getId() {
		return id;
	}
}
