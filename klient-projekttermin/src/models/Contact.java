package models;

public class Contact implements ModelInterface {

	private String contactName;
	private Long contactPhoneNumber;
	private String contactEmail;
	private String contactClearanceLevel;
	private String contactClassification;
	private String contactComment;
	private String databasetRepresentation = "contact";

	
	
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
	
	public void setContactPhoneNumber(Long contactPhoneNumberToBeSet){
		this.contactPhoneNumber=contactPhoneNumberToBeSet;
	}
	
	public String getContactEmail(){
		return contactEmail;
	}
	
	public void setContactEmail(String contactEmailToBeSet){
		this.contactEmail=contactEmailToBeSet;
	}
	
	public String getContactClearanceLevel(){
		return contactClearanceLevel;
	}
	
	public void setContactClearanceLevel(String clearanceLevelToBeSet){
		this.contactClearanceLevel=clearanceLevelToBeSet;
	}
	
	public String getContactClassification(){
		return contactClassification;
	}
	
	public void setContactClassification(String contactClassificationToBeSet){
		this.contactClassification=contactClassificationToBeSet;
	}
	
	public String getContactComment(){
		return contactComment;
	}
	
	public void setContactComment(String contactCommentToBeSet){
		this.contactComment=contactCommentToBeSet;
	}

	public String getDatabaseRepresentation() {
		// TODO Auto-generated method stub
		return databasetRepresentation;
	}
}
