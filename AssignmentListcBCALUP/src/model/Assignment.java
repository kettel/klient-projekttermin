package model;

import android.graphics.Bitmap;

public class Assignment implements ModelInterface {

	// Typen av modell
	private String databasetRepresentation = "assignment";
	// Id f�r modellen (S�tts av databasen s� pilla inte)
	private long id = -1;
	// Namnet p� uppdraget
	private String name;
	// Latitud f�r uppdragspositionen
	private long lat;
	// Longitud f�r uppdragspositionen
	private long lon;
	// Anv�ndarnamnet p� mottagaren f�r ett uppdrag (Om man vill specificera
	// det)
	private String receiver;
	// Anv�ndarnamnet p� den person som skapade uppdraget.
	private String sender;
	// Textbeskrivning av uppdraget
	private String assignmentDescription;
	// Tidsbeskrivning av hur l�ng tid uppdraget kommer ta (1 timme, 20
	// minuter...)
	private String timeSpan;
	// Textbeskrivning av uppdragets nuvarande status (Icke p�b�rjat, P�b�rjat,
	// Beh�ver hj�lp)
	private String assignmentStatus;
	// Bild kopplat till uppdraget
	private Bitmap cameraImage;
	// Gatunamn f�r platsen d�r uppdraget utspelas
	private String streetName;
	// Platsnamn d�r uppdraget utspelas
	private String siteName;

	/**
	 * Tom konstruktor for Assignment
	 */
	public Assignment() {

	}

	/**
	 * 
	 * @param name
	 *            String Namn p� uppdrag
	 * @param lat
	 *            long Latitud f�r uppdraget
	 * @param lon
	 *            long Longitud f�r uppdraget
	 * @param receiver
	 *            String Mottagare av uppgradet
	 * @param sender
	 *            String S�ndare av uppdraget
	 * @param assignmentDescription
	 *            String Beskrivning av uppdraget
	 * @param timeSpan
	 *            String Hur l�ng tid uppdraget v�ntas ta
	 * @param assignmentStatus
	 *            String Status f�r uppdraget
	 * @param cameraImage
	 *            Bitmap En bifogad bild p� uppdragsplatsen
	 * @param streetName
	 *            String Gatunamn
	 * @param siteName
	 *            String Platsnamn
	 */
	public Assignment(String name, long lat, long lon, String receiver,
			String sender, String assignmentDescription, String timeSpan,
			String assignmentStatus, Bitmap cameraImage, String streetName,
			String siteName) {
		this.lat = lat;
		this.lon = lon;
		this.name = name;
		this.receiver = receiver;
		this.sender = sender;
		this.assignmentDescription = assignmentDescription;
		this.timeSpan = timeSpan;
		this.assignmentStatus = assignmentStatus;
		this.cameraImage = cameraImage;
		this.streetName = streetName;
		this.siteName = siteName;
	}

	/**
	 * Konstruktor f�r att �terskapa ett meddelande fr�n databasen med ett Id
	 * som h�mtas fr�n databasen
	 * 
	 * @param id
	 * @param name
	 * @param lat
	 * @param lon
	 * @param receiver
	 * @param sender
	 * @param assignmentDescription
	 * @param timeSpan
	 * @param assignmentStatus
	 * @param cameraImage
	 * @param streetName
	 * @param siteName
	 */

	public Assignment(long id, String name, long lat, long lon,
			String receiver, String sender, String assignmentDescription,
			String timeSpan, String assignmentStatus, Bitmap cameraImage,
			String streetName, String siteName) {
		this.id = id;
		this.name = name;
		this.lat = lat;
		this.lon = lon;
		this.receiver = receiver;
		this.sender = sender;
		this.assignmentDescription = assignmentDescription;
		this.timeSpan = timeSpan;
		this.assignmentStatus = assignmentStatus;
		this.cameraImage = cameraImage;
		this.streetName = streetName;
		this.siteName = siteName;
	}

	public String getName() {
		return name;
	}

	public void setName(String nameToBeSet) {
		this.name = nameToBeSet;
	}

	public String getStreetName() {
		return streetName;
	}

	public void setStreetName(String streetNameToBeSet) {
		this.streetName = streetNameToBeSet;
	}

	public String getSiteName() {
		return siteName;
	}

	public void setSiteName(String siteNameToBeSet) {
		this.siteName = siteNameToBeSet;
	}

	public long getLat() {
		return lat;
	}

	public void setLat(long latToBeSet) {
		this.lat = latToBeSet;
	}

	public long getLon() {
		return lon;
	}

	public void setLon(long lonToBeSet) {
		this.lon = lonToBeSet;
	}

	public String getReceiver() {
		return receiver;
	}

	public void setReceiver(String receiverToBeSet) {
		this.receiver = receiverToBeSet;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String senderToBeSet) {
		this.sender = senderToBeSet;
	}

	public Bitmap getCameraImage() {
		return cameraImage;
	}

	public void captureCameraImage(Bitmap cameraImageToBeSet) {
		this.cameraImage = cameraImageToBeSet;
	}

	public String getAssignmentDescription() {
		return assignmentDescription;
	}

	public void setAssignmentDescription(String assignmentDescriptionToBeSet) {
		this.assignmentDescription = assignmentDescriptionToBeSet;
	}

	public String getTimeSpan() {
		return timeSpan;
	}

	public void setTimeSpan(String timeSpanToBeSet) {
		this.timeSpan = timeSpanToBeSet;
	}

	public String getAssignmentStatus() {
		return assignmentStatus;
	}

	public void setAssignmentStatus(String assignmentStatusToBeSet) {
		this.assignmentStatus = assignmentStatusToBeSet;
	}

	public String getDatabaseRepresentation() {
		return databasetRepresentation;
	}

	public long getId() {
		return id;
	}
}