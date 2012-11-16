package models;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import android.graphics.Bitmap;

public class ImageModel implements ModelInterface {
	// Databasrepresentation för bilden
	private String databaseRepresentation = "image";
	// Id för ett meddelande är -1 tils dess id är satt av databasen
	private long id = -1;
	// bilden
	private Bitmap image;
	// Mottagare av bilden
	private String reciever;
	// Vem som skickade bilden
	private String sender;
	// Tiddstämpel i UNIX Epoch-format för när bilden skapades
	private Long imageTimeStamp;

	/**
	 * Tom konstruktor. Används för att hämta från databasen.
	 */
	public ImageModel() {

	}

	/**
	 * Konstruktor för att skapa en bild
	 * 
	 * @param image
	 * @param reciever
	 * @param image 
	 */
	public ImageModel(Bitmap image, String reciever, String sender) {
		this.image = image;
		this.reciever = reciever;
		this.sender = sender;
		imageTimeStamp = Calendar.getInstance().getTimeInMillis();
	}

	/**
	 * Konstruktor för att återskapa en bild
	 * 
	 * @param image
	 * @param reciever
	 * @param image 
	 * @param timeStamp
	 */
	public ImageModel(long id, Bitmap image, String reciever,
			String sender, Long imageTimeStamp) {
		this.id = id;
		this.image = image;
		this.reciever = reciever;
		this.sender = sender;
		this.imageTimeStamp = imageTimeStamp;
	}
	
	/**
	 * Hämta bilden
	 * @return	en bild som är en Bitmap
	 */
	public Bitmap getImage() {
		return image;
	}

	/**
	 * Hämta mottagare av bilden
	 * @return	CharSequence
	 */
	public CharSequence getReciever() {
		return (CharSequence) reciever;
	}

	/**
	 * Hämta tidsstämpel för bilden
	 * @return	Long
	 */
	public Long getImageTimeStamp() {
		return imageTimeStamp;
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
	public String getImageTimeStampSmart() {
		Date date = new Date(imageTimeStamp);
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
	 * Hämta vem som är avsändare av bilden
	 * @return
	 */
	public String getSender(){
		return sender;
	}
}