package models;

public class Contact {

	private String contactName;
	private Long contactPhoneNumber;
	private String contactEmail;
	private String contactClearanceLevel;
	private String contactClassification;
	private String contactComment;
	
	
	public Contact(String contactName, Long contactPhoneNumber, String contactEmail, String contactClearanceLevel, String contactClassification, String contactComment){
		this.contactName=contactName;
		this.contactPhoneNumber=contactPhoneNumber;
		this.contactEmail=contactEmail;
		this.contactClearanceLevel=contactClearanceLevel;
		this.contactClassification=contactClassification;
		this.contactComment=contactComment;
	
	}
	
	public String getContactName(){
		return contactName;
	}
	
	public void setContactName(String nameToBeSet){
		this.contactName=nameToBeSet;
	}
	
	public Long getContactPhoneNumber(){
		return contactPhoneNumber;
	}
	
	public String
	
}
