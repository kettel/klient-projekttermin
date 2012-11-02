package model;

import java.sql.Time;
import java.util.Calendar;

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

	/**
	 * Konstruktor för att återskapa ett existerande meddelande
	 * @param messageContent
	 * @param reciever
	 * @param timeStamp
	 */
	public MessageModel(String messageContent, String reciever,
			Time timeStamp) {
		this.messageContent = messageContent;
		this.reciever = reciever;
		this.messageTimeStamp = timeStamp;
	}

	public CharSequence getMessageContent() {
		return (CharSequence) messageContent;
	}

	public CharSequence getReciever() {
		return (CharSequence) reciever;
	}

	public Time getMessageTimeStamp() {
		return messageTimeStamp;
	}

	public String getDatabaseRepresentation() {
		// TODO Auto-generated method stub
		return databaseRepresentation;
	}
}