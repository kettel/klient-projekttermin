
package models;


import java.sql.Time;
import java.util.Calendar;

public class MessageModel implements ModelInterface {

	private String databaseRepresentation = "message";
	private String messageContent;
	private String reciever;
	private Calendar messageTimeStamp;

	/**
	 * Tom konstruktor for MessageModel
	 */
	public MessageModel(){
		
	}
	
	/**
	 * Konstruktor för att skapa ett nytt meddelande
	 * @param messageContent
	 * @param reciever
	 */
	public MessageModel(String messageContent, String reciever) {
		this.messageContent =  messageContent;
		this.reciever = reciever;
		Calendar c = Calendar.getInstance();
	}

	/**
	 * Konstruktor för att återskapa ett existerande meddelande
	 * @param messageContent
	 * @param reciever
	 * @param timeStamp
	 */
	public MessageModel(String messageContent, String reciever,
			Calendar timeStamp) {
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

	public Calendar getMessageTimeStamp() {
		return messageTimeStamp;
	}

	public String getDatabaseRepresentation() {
		// TODO Auto-generated method stub
		return databaseRepresentation;
	}
}
