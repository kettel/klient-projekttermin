package models;

import java.sql.Time;

public class Message {

	private String messageContent;
	private String reciever;
	private Long messageID;
	private Time messageTimeStamp;
	
	
	public Message(String messageContent, String reciever, Long messageID, Time messageTimeStamp){
		this.messageContent=messageContent;
		this.reciever=reciever;
		this.messageID=messageID;
		this.messageTimeStamp=messageTimeStamp;
		
	}
	
	public String getMessageContent(){
		return messageContent;
	}
	
	public void setMessageContent(String messageContentToBeSet){
		this.messageContent=messageContentToBeSet;
	}
	
	public String getReciever(){
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
}
