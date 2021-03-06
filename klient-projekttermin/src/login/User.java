package login;

import models.AuthenticationModel;

public class User {

	private AuthenticationModel authenticationModel;
	private boolean loggedIn=false;
	private boolean onlineConnection = false;

	private User(){}

	private static User instance = new User();
	
	public static User getInstance(){
		return instance;
	}

	public AuthenticationModel getAuthenticationModel() {
		return authenticationModel;
	}

	public void setAuthenticationModel(AuthenticationModel authenticationModel) {
		this.authenticationModel = authenticationModel;
	}
	
	public boolean gotInlineConnection(){
		return onlineConnection;
	}
	
	public void setOnlineConnection(Boolean bol){
		onlineConnection = bol;
	}
	
	public boolean isLoggedIn() {
		return loggedIn;
	}
	public void setLoggedIn(boolean b){
		this.loggedIn=b;
	}
}
