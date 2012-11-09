
package models;

import java.text.SimpleDateFormat;
import java.util.Calendar;

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
	private Long messageTimeStamp;
	//Tidsstämpel på när meddelandeobjektet skapades i ett smart format
	private String messageTimeStampSmart;

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
	public MessageModel(String messageContent, String reciever, String sender) {
		this.messageContent =  messageContent;
		this.reciever = reciever;
		this.sender=sender;
		this.messageTimeStamp = Calendar.getInstance().getTimeInMillis();
		SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");
		this.messageTimeStampSmart=sdf.format(messageTimeStamp).toString();
	}

	/**
	 * Konstruktor för att återskapa ett existerande meddelande
	 * @param messageContent
	 * @param reciever
	 * @param timeStamp
	 */
	public MessageModel(String messageContent, String reciever, String sender,	Long timeStamp) {

	}

	public MessageModel(long id, String messageContent, String reciever,String sender,
			Long messageTimeStamp, boolean isRead) {
		this.id = id;
		this.messageContent = messageContent;
		this.reciever = reciever;
		this.messageTimeStamp = messageTimeStamp;
		this.isRead = isRead;
		this.sender=sender;
		this.messageTimeStamp = messageTimeStamp;
		SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");
		this.messageTimeStampSmart=sdf.format(messageTimeStamp).toString();

	}

	public CharSequence getMessageContent() {
		return (CharSequence) messageContent;
	}

	public CharSequence getReciever() {
		return (CharSequence) reciever;
	}

	public CharSequence getSender(){
		return (CharSequence) sender;
	}

	public Long getTimeStamp(){
		return messageTimeStamp;
	}

	/*
	 * Konverterar tiden i millisekunder till timmar, minuter, sekunder
	 */
	public String getTimeStampInUnderstandableFormat(){
		return messageTimeStampSmart;
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
