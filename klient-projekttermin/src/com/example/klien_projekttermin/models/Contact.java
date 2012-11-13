package com.example.klien_projekttermin.models;


public class Contact implements ModelInterface {
	
	private String databaseRepresentation = "contact";
	private long id = -1;
	
	private String contactName;

	public Contact() {
	}
	
	public Contact(String contactName){
		this.contactName=contactName;
	}
	
	public Contact(long id, String contactName) {
		this.id = id;
		this.contactName = contactName;
	}

	public String getContactName(){
		return contactName;
	}

	public long getId() {
		return id;
	}

	public String getDatabaseRepresentation() {
		return databaseRepresentation;
	}
}
