package model;

import java.util.Date;

public class MessageModel implements ModelInterface {

	private String databaseRepresentation = "message";
	private String messageContent;
	private String reciever;
	private Date messageTimeStamp;

	public MessageModel() {

	}

	// Mesage borde döpas om till något annat liknande
	public MessageModel(CharSequence messageContent, CharSequence reciever) {
		this.messageContent = (String) messageContent;
		this.reciever = (String) reciever;
		messageTimeStamp = new Date();
	}

	public MessageModel(CharSequence messageContent, CharSequence reciever,
			Date timeStamp) {
		this.messageContent = (String) messageContent;
		this.reciever = (String) reciever;
		this.messageTimeStamp = timeStamp;
	}

	public CharSequence getMessageContent() {
		return messageContent;
	}

	public CharSequence getReciever() {
		return (CharSequence) reciever;
	}

	public Date getMessageTimeStamp() {
		return messageTimeStamp;
	}

	public String getDatabaseRepresentation() {
		// TODO Auto-generated method stub
		return databaseRepresentation;
	}
}