
package models;

import android.text.format.Time;

import java.util.Calendar;
import java.util.Date;


public class MessageModel implements ModelInterface {

	//Vilken typ av modell detta är
	private String databaseRepresentation = "message";
	//Id för igenkänning i databasen (Sätts av databasen,pilla inte)
	private long id = -1;
	//Är true om meddelandet i meddelandemodellen är läst, annars false
	private boolean isRead = false;
	//Meddelandet tillhörande meddelandemodellen
	private String messageContent;
	//Användarnamnet på den person som skickade meddelandemodellen
	private String sender;
	//Användarnamnet på den person man ska skicka till
	private String reciever;
	//Tidsstämpel på när ett meddelande skickats
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

	}
	public MessageModel(long id, String messageContent, String reciever,
			long messageTimeStamp, boolean isRead) {
		this.id = id;
		this.messageContent = messageContent;
		this.reciever = reciever;
		this.messageTimeStamp = messageTimeStamp;
		this.isRead = isRead;
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
		return databaseRepresentation;
	}

	public long getId() {
		return id;
	}

	public boolean isRead(){
		return isRead;
	}
}
