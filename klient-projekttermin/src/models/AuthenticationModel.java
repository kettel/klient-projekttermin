package models;

public class AuthenticationModel implements ModelInterface {
	
	private String userName;
	private String passwordHash;
	private Boolean isAccessGranted = false;
	private long id = -1;
	private String databaseRepresentation = "authentication";
	
	
	public AuthenticationModel(String userName, String passwordHash){
		this.userName = userName;
		this.passwordHash = passwordHash;
	}
	
	public AuthenticationModel(long id, String userName, String passwordHash){
		this.id = id;
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
	
	public long getId(){
		return id;
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
}
