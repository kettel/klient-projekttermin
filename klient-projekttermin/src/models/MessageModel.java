package models;


public class MessageModel implements ModelInterface {

	private CharSequence messageContent;
	private CharSequence reciever;
	private CharSequence sender;
	private String messageTimeStamp;
	private String databasetRepresentation = "message";

	
	//Mesage borde döpas om till något annat liknande
	public MessageModel(CharSequence messageContent,CharSequence sender, CharSequence reciever, String messageTimeStamp){
		this.messageContent=messageContent;
		this.sender=sender;
		this.reciever=reciever;
		this.messageTimeStamp=messageTimeStamp;
		
	}
	
	public CharSequence getMessageContent(){
		return messageContent;
	}
	
	public void setMessageContent(String messageContentToBeSet){
		this.messageContent=messageContentToBeSet;
	}
	
	public CharSequence getSender(){
		return sender;
	}
	
	public void setSender(String senderToBeSet){
		this.sender=senderToBeSet;
	}
	
	public CharSequence getReciever(){
		return reciever;
	}
	
	public void setReciever(String recieverToBeSet){
		this.reciever=recieverToBeSet;
	}
	
	public String getMessageTimeStamp(){
		return messageTimeStamp;
	}
	
	public void setMessageTimeStamp(String messageTimeStampToBeSet){
		this.messageTimeStamp=messageTimeStampToBeSet;
	}

	public String getDatabaseRepresentation() {
		// TODO Auto-generated method stub
		return databasetRepresentation;
	}
}
