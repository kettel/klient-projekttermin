package models;

import java.util.Calendar;

public class MessageModel implements ModelInterface {

	private String databaseRepresentation = "message";
	private String messageContent;
	private String reciever;
	private long messageTimeStamp;
	
	/**
	 * Tom konstruktor. Används för att hämta från databasen.
	 */
	public MessageModel() {

	}
	
	/**
	 * Konstruktor för att skapa ett nytt meddelande
	 * @param messageContent
	 * @param reciever
	 */
	public MessageModel(String messageContent, String reciever) {
		this.messageContent =  messageContent;
		this.reciever = reciever;
		messageTimeStamp = Calendar.getInstance().getTimeInMillis();
	}


	/**
	 * Konstruktor för att återskapa ett existerande meddelande
	 * @param messageContent
	 * @param reciever
	 * @param timeStamp
	 */
	public MessageModel(String messageContent, String reciever,	long timeStamp) {
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

	public long getMessageTimeStamp() {
		return messageTimeStamp;
	}

	public String getDatabaseRepresentation() {
		// TODO Auto-generated method stub
		return databaseRepresentation;
	}
}