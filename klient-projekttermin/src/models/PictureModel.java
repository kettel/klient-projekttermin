package models;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class PictureModel implements ModelInterface {
	// Databasrepresentation för bilden
	private String databaseRepresentation = "picture";
	// Id för ett meddelande är -1 tils dess id är satt av databasen
	private long id = -1;
	// bilden
	private byte[] picture;
	// Tiddstämpel i UNIX Epoch-format för när bilden skapades
	private Long pictureTimeStamp;

	/**
	 * Tom konstruktor. Används för att hämta från databasen.
	 */
	public PictureModel() {

	}

	/**
	 * Konstruktor för att skapa en bild
	 * 
	 * @param image 
	 */
	public PictureModel(byte[] picture) {
		this.picture = picture;
		pictureTimeStamp = Calendar.getInstance().getTimeInMillis();
	}
	
	public PictureModel(long id, byte[] picture) {
		this.id = id;
		this.picture = picture;
		pictureTimeStamp = Calendar.getInstance().getTimeInMillis();
	}

	/**
	 * Konstruktor för att återskapa en bild
	 * 
	 * @param image 
	 * @param timeStamp
	 */
	public PictureModel(long id, byte[] image, Long imageTimeStamp) {
		this.id = id;
		this.picture = image;
		this.pictureTimeStamp = imageTimeStamp;
	}
	
	/**
	 * Hämta bilden
	 * @return	en bild som är en Bitmap
	 */
	public byte[] getPicture() {
		if (picture == null) {
			System.out.println("IF PIC = 0");
			picture = new byte[2];
		}
		System.out.println(picture.length + " get pciture " + picture);
		return picture;
	}

	/**
	 * Hämta tidsstämpel för bilden
	 * @return	Long
	 */
	public Long getPictureTimeStamp() {
		return pictureTimeStamp;
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
	public String getPictureTimeStampSmart() {
		Date date = new Date(pictureTimeStamp);
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
}