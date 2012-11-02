package models;

import android.text.format.Time;


import java.sql.Time;
import java.util.Calendar;

public class MessageModel implements ModelInterface {

	private String databaseRepresentation = "message";
	private String messageContent;
	private String reciever;
	private Time messageTimeStamp;

	/**
	 * Konstruktor för att skapa ett nytt meddelande
	 * @param messageContent
	 * @param reciever
	 */
	public MessageModel(String messageContent, String reciever) {
		this.messageContent =  messageContent;
		this.reciever = reciever;
		Calendar c = Calendar.getInstance();
		Time now = new Time(c.getTimeInMillis());
		messageTimeStamp = now;
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