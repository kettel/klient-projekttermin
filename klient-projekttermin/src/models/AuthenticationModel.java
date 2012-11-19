package models;

public class AuthenticationModel {
	
	private String userName;
	private String passwordHash;
	private Boolean isAccessGranted = false;
	
	
	public AuthenticationModel(String userName, String passwordHash){
		this.userName = userName;
		this.passwordHash = passwordHash;
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
}
