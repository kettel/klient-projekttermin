package loginFunction;

public class User {

	private String userName;
	private String password;
	
	

	private User(){}

	private static User instance = new User();
	
	public static User getInstance(){
		return instance;
	}
	
	public void setUserName(String userName){
		this.userName = userName;
	}
	
	public String getUserName(){
		return userName;
	}
	
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
