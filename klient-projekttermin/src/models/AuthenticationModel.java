package models;

public class AuthenticationModel implements ModelInterface {
	
	private String userName;
	private String passwordHash;
	private Boolean isAccessGranted = false;
	private String databaseRepresentation = "authentication";
	private long id = -1;

	
	
	public AuthenticationModel(long id, String userName, String passwordHash){
		this.userName = userName;
		this.passwordHash = passwordHash;
		this.id = id;
	}
	
	public AuthenticationModel(String userName, String passwordHash){
		this.userName = userName;
		this.passwordHash = passwordHash;
	}
	
	public AuthenticationModel(){
		
	}
	
	public AuthenticationModel(Boolean accessDecision){
		
		this.isAccessGranted = accessDecision;
	}
	
	public String getUserName(){
		return userName;
	}
	
	public String getPasswordHash(){
		return passwordHash;
	}
	
	/*
	 * Metoden returnerar true om användaren får access, annars false;
	 */
	public Boolean isAccessGranted(){
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
