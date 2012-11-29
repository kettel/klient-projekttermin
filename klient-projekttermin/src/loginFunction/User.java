package loginFunction;

import models.AuthenticationModel;

public class User {

	private AuthenticationModel authenticationModel;

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
}
