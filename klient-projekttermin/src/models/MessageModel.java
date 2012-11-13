package models;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class MessageModel implements ModelInterface {
	// Databasrepresentation för meddelande
	private String databaseRepresentation = "message";
	// Id för ett meddelande är -1 tils dess id är satt av databasen
	private long id = -1;
	// Om ett meddelande är oläst eller ej
	private boolean isRead = false;

	private String messageContent;
	private String reciever;
	private Long messageTimeStamp;

	/**
	 * Tom konstruktor. Används för att hämta från databasen.
	 */
	public MessageModel() {

	}

	/**
	 * Konstruktor för att skapa ett nytt meddelande
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
	 * Konstruktor för att återskapa ett existerande meddelande
	 * 
	 * @param messageContent
	 * @param reciever
	 * @param timeStamp
	 */
	public MessageModel(long id, String messageContent, String reciever,
			Long messageTimeStamp, boolean isRead) {
		this.id = id;
		this.messageContent = messageContent;
		this.reciever = reciever;
		this.messageTimeStamp = messageTimeStamp;
		this.isRead = isRead;
	}
	
	/**
	 * Hämta meddelandeinnehåll
	 * @return	CharSequence
	 */
	public CharSequence getMessageContent() {
		return (CharSequence) messageContent;
	}

	/**
	 * Hämta mottagare av meddelande
	 * @return	CharSequence
	 */
	public CharSequence getReciever() {
		return (CharSequence) reciever;
	}

	/**
	 * Hämta tidsstämpel för meddelande
	 * @return	Long
	 */
	public Long getMessageTimeStamp() {
		return messageTimeStamp;
	}

	/**
	 * Hämta databasrepresentation
	 */
	public String getDatabaseRepresentation() {
		return databaseRepresentation;
	}

	/**
	 * Hämta datum i format yyyy-MM-dd HH:mm:ss för tidszon CET
	 * @return String
	 */
	public String getMessageTimeStampSmart() {
		Date date = new Date(messageTimeStamp);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
		format.setTimeZone(TimeZone.getTimeZone("CET"));
		String smartTime = format.format(date).toString();
		return smartTime;
	}
	
	/**
	 * Hämta databas-id för objektet i databasen. Har det varit i databasen
	 * är det något annat än -1.
	 * @return long id
	 */
	public long getId() {
		return id;
	}

	/**
	 * Svarar på om meddelandet är läst eller ej.
	 * @return true om meddelandet är läst, false annars.
	 */
	public boolean isRead() {
		return isRead;
	}
}