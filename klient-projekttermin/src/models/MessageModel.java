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
	// Meddelandeinnehåll
	private String messageContent;
	// Mottagare av meddelandet
	private String reciever;
	// Vem som skickade meddelandet
	private String sender;
	// Tiddstämpel i UNIX Epoch-format för när meddelandet skapades
	private Long messageTimeStamp;
	
	private MessageStatus status;

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
	public MessageModel(String messageContent, String reciever, String sender, MessageStatus status) {
		this.messageContent = messageContent;
		this.reciever = reciever;
		this.sender = sender;
		this.status = status;
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
			String sender, Long messageTimeStamp, boolean isRead, MessageStatus status) {
		this.id = id;
		this.messageContent = messageContent;
		this.reciever = reciever;
		this.sender = sender;
		this.messageTimeStamp = messageTimeStamp;
		this.isRead = isRead;
		this.status = status;
	}
	
	/**
	 * Hämta meddelandeinnehåll
	 * @return	CharSequence
	 */
	public CharSequence getMessageContent() {
		return messageContent;
	}

	/**
	 * Hämta mottagare av meddelande
	 * @return	CharSequence
	 */
	public CharSequence getReciever() {
		return reciever;
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
	 * Hämta vem som är avsändare av meddelandet
	 * @return
	 */
	public String getSender(){
		return sender;
	}

	public MessageStatus getStatus() {
		return status;
	}

	public void setStatus(MessageStatus status) {
		this.status = status;
	}

	/**
	 * Svarar på om meddelandet är läst eller ej.
	 * @return true om meddelandet är läst, false annars.
	 */
	public boolean isRead() {
		return isRead;
	}
}