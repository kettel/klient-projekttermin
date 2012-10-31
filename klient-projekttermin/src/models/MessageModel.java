package models;


public class MessageModel implements ModelInterface {

	private String databaseRepresentation = "message";
	private String messageContent;
	private String reciever;
	private String messageTimeStamp;


	//Mesage borde döpas om till något annat liknande
	public MessageModel(CharSequence messageContent, CharSequence reciever, CharSequence messageTimeStamp){
		this.messageContent = (String) messageContent;
		this.reciever = (String) reciever;
		this.messageTimeStamp = (String) messageTimeStamp;

	}

	public CharSequence getMessageContent(){
		return messageContent;
	}

	public void setMessageContent(CharSequence messageContentToBeSet){
		this.messageContent = (String) messageContentToBeSet;
	}

	public CharSequence getReciever(){
		return (CharSequence) reciever;
	}

	public void setReciever(CharSequence recieverToBeSet){
		this.reciever = (String) recieverToBeSet;
	}

	public CharSequence getMessageTimeStamp(){
		return messageTimeStamp;
	}

	public void setMessageTimeStamp(CharSequence messageTimeStampToBeSet){
		this.messageTimeStamp = (String) messageTimeStampToBeSet;
	}

	public String getDatabaseRepresentation() {
		// TODO Auto-generated method stub
		return databaseRepresentation;
	}
}