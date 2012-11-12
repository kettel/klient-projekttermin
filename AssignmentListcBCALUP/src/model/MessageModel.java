package model;

import java.util.Calendar;

public class MessageModel implements ModelInterface {

	// Vilken typ av modell detta �r
	private String databaseRepresentation = "message";
	// Id f�r igenk�nning i databasen (S�tts av databasen,pilla inte)
	private long id = -1;
	// �r true om meddelandet i meddelandemodellen �r l�st, annars false
	private boolean isRead = false;
	// Meddelandet tillh�rande meddelandemodellen
	private String messageContent;
	// Anv�ndarnamnet p� den person som skickade meddelandemodellen
	private String sender;
	// Anv�ndarnamnet p� den person man ska skicka till
	private String reciever;
	// Tidsst�mpel p� n�r ett meddelande skickats
	private long messageTimeStamp;

	/**
	 * Tom konstruktor. Anv�nds f�r att h�mta fr�n databasen.
	 */
	public MessageModel() {

	}

	/**
	 * Konstruktor f�r att skapa ett nytt meddelande
	 * 
	 * @param messageContent
	 * @param reciever
	 */
	public MessageModel(String messageContent, String reciever) {
		this.messageContent = messageContent;
		this.reciever = reciever;

		messageTimeStamp = Calendar.getInstance().getTimeInMillis();
	}

	/**
	 * Konstruktor f�r att �terskapa ett existerande meddelande
	 * 
	 * @param messageContent
	 * @param reciever
	 * @param timeStamp
	 */

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

	public boolean isRead() {
		return isRead;
	}
}
