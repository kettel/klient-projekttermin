package models;

import java.sql.Time;

public class MessageModel implements ModelInterface {

	private CharSequence messageContent;
	private CharSequence reciever;
	private Long messageID;
	private Time messageTimeStamp;
	private String databasetRepresentation = "message";

	
	//Mesage borde döpas om till något annat liknande
	public MessageModel(CharSequence charSequence, CharSequence charSequence2, Long messageID){
		this.messageContent=charSequence;
		this.reciever=charSequence2;
		this.messageID=messageID;
		this.messageTimeStamp=messageTimeStamp;
		
	}
	
	public CharSequence getMessageContent(){
		return messageContent;
	}
	
	public void setMessageContent(String messageContentToBeSet){
		this.messageContent=messageContentToBeSet;
	}
	
	public CharSequence getReciever(){
		return reciever;
	}
	
	public void setReciever(String recieverToBeSet){
		this.reciever=recieverToBeSet;
	}
	
	public Long getMessageID(){
		return messageID;
	}
	
	public void setMessageID(Long messageIDToBeSet){
		this.messageID=messageIDToBeSet;
	}
	
	public Time getMessageTimeStamp(){
		return messageTimeStamp;
	}
	
	public void setMessageTimeStamp(Time messageTimeStampToBeSet){
		this.messageTimeStamp=messageTimeStampToBeSet;
	}

	public String getDatabaseRepresentation() {
		// TODO Auto-generated method stub
		return databasetRepresentation;
	}
}
