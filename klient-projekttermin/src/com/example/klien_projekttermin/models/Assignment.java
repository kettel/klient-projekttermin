package com.example.klien_projekttermin.models;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import android.graphics.Bitmap;

public class Assignment implements ModelInterface {
	// Typen av model
	private String databaseRepresentation = "assignment";
	// Id för modellen (Sätts av databasen så pilla inte)
	private long id = -1;
	// Namnet på uppdraget
	private String name;
	// Latitud för uppdragspositionen
	private long lat;
	// Longitud för uppdragspositionen
	private long lon;
	// JSON-sträng med WSG-punkter för polygonmarkering av region på kartan
	private String region;
	// Användarnamnet på mottagaren för ett uppdrag (Om man vill specificera
	// det)
	private String receiver;
	// Användarnamnet på den person som skapade uppdraget.
	private String sender;
	// Om uppdraget ska skickas till externa aktörer
	private boolean externalMission;
	// Lista över kontakter (agenter) som har accepterat uppdraget
	List<Contact> agents = new ArrayList<Contact>();
	// Textbeskrivning av uppdraget
	private String assignmentDescription;
	// Tidsbeskrivning av hur lång tid uppdraget kommer ta (1 timme, 20
	// minuter...)
	private String timeSpan;
	// Tidsstämpel när uppdraget skapades
	private Long assignmentTimeStamp;
	// Textbeskrivning av uppdragets nuvarande status (Icke påbörjat, Påbörjat,
	// Behöver hjälp)
	private AssignmentStatus assignmentStatus;
	// Bild kopplat till uppdraget
	private Bitmap cameraImage;
	// Gatunamn för platsen där uppdraget utspelas
	private String streetName;
	// Platsnamn där uppdraget utspelas
	private String siteName;

	/**
	 * Tom konstruktor. Används bland annat för att hämta från databasen.
	 */
	public Assignment() {
	}

	/**
	 * Konstruktor för att skapa ett uppdrag utan kartmarkering utan bild
	 * 
	 * @param name
	 * @param receiver
	 * @param sender
	 * @param externalMission
	 * @param agents
	 * @param assignmentDescription
	 * @param timeSpan
	 * @param assignmentStatus
	 * @param cameraImage
	 * @param streetName
	 * @param siteName
	 */
	public Assignment(String name, String receiver, String sender,
			boolean externalMission, String assignmentDescription,
			String timeSpan, AssignmentStatus assignmentStatus,
			String streetName, String siteName) {
		this.name = name;
		this.receiver = receiver;
		this.sender = sender;
		this.externalMission = externalMission;
		this.assignmentDescription = assignmentDescription;
		this.timeSpan = timeSpan;
		this.assignmentStatus = assignmentStatus;
		this.streetName = streetName;
		this.siteName = siteName;
		this.assignmentTimeStamp = Calendar.getInstance().getTimeInMillis();
	}

	/**
	 * Konstruktor för att skapa ett uppdrag utan kartmarkering med bild
	 * 
	 * @param name
	 * @param receiver
	 * @param sender
	 * @param externalMission
	 * @param assignmentDescription
	 * @param timeSpan
	 * @param assignmentStatus
	 * @param cameraImage
	 * @param streetName
	 * @param siteName
	 */
	public Assignment(String name, String receiver, String sender,
			boolean externalMission, String assignmentDescription,
			String timeSpan, AssignmentStatus assignmentStatus,
			Bitmap cameraImage, String streetName, String siteName) {
		this.name = name;
		this.receiver = receiver;
		this.sender = sender;
		this.externalMission = externalMission;
		this.assignmentDescription = assignmentDescription;
		this.timeSpan = timeSpan;
		this.assignmentStatus = assignmentStatus;
		this.cameraImage = cameraImage;
		this.streetName = streetName;
		this.siteName = siteName;
		this.assignmentTimeStamp = Calendar.getInstance().getTimeInMillis();
	}

	/**
	 * Konstruktor för att skapa ett uppdrag utifrån en koordinat utan
	 * kamerabild
	 * 
	 * @param name
	 * @param lat
	 * @param lon
	 * @param receiver
	 * @param sender
	 * @param externalMission
	 * @param assignmentDescription
	 * @param timeSpan
	 * @param assignmentStatus
	 * @param streetName
	 * @param siteName
	 */
	public Assignment(String name, long lat, long lon, String receiver,
			String sender, boolean externalMission,
			String assignmentDescription, String timeSpan,
			AssignmentStatus assignmentStatus, String streetName,
			String siteName) {
		this.name = name;
		this.lat = lat;
		this.lon = lon;
		this.receiver = receiver;
		this.sender = sender;
		this.externalMission = externalMission;
		this.assignmentDescription = assignmentDescription;
		this.timeSpan = timeSpan;
		this.assignmentStatus = assignmentStatus;
		this.streetName = streetName;
		this.siteName = siteName;
		this.assignmentTimeStamp = Calendar.getInstance().getTimeInMillis();
	}

	/**
	 * Konstruktor för att skapa ett uppdrag utifrån en koordinat med kamerabild
	 * 
	 * @param name
	 * @param lat
	 * @param lon
	 * @param receiver
	 * @param sender
	 * @param externalMission
	 * @param assignmentDescription
	 * @param timeSpan
	 * @param assignmentStatus
	 * @param cameraImage
	 * @param streetName
	 * @param siteName
	 */
	public Assignment(String name, long lat, long lon, String receiver,
			String sender, boolean externalMission,
			String assignmentDescription, String timeSpan,
			AssignmentStatus assignmentStatus, Bitmap cameraImage,
			String streetName, String siteName) {
		this.name = name;
		this.lat = lat;
		this.lon = lon;
		this.receiver = receiver;
		this.sender = sender;
		this.externalMission = externalMission;
		this.assignmentDescription = assignmentDescription;
		this.timeSpan = timeSpan;
		this.assignmentStatus = assignmentStatus;
		this.cameraImage = cameraImage;
		this.streetName = streetName;
		this.siteName = siteName;
		this.assignmentTimeStamp = Calendar.getInstance().getTimeInMillis();
	}

	/**
	 * Konstruktor för att skapa ett uppdrag utifrån en region utan kamerabild
	 * 
	 * @param name
	 * @param region
	 * @param receiver
	 * @param sender
	 * @param externalMission
	 * @param assignmentDescription
	 * @param timeSpan
	 * @param assignmentStatus
	 * @param streetName
	 * @param siteName
	 */
	public Assignment(String name, String region, String receiver,
			String sender, boolean externalMission,
			String assignmentDescription, String timeSpan,
			AssignmentStatus assignmentStatus, String streetName,
			String siteName) {
		this.name = name;
		this.region = region;
		this.receiver = receiver;
		this.sender = sender;
		this.externalMission = externalMission;
		this.assignmentDescription = assignmentDescription;
		this.timeSpan = timeSpan;
		this.assignmentStatus = assignmentStatus;
		this.streetName = streetName;
		this.siteName = siteName;
		this.assignmentTimeStamp = Calendar.getInstance().getTimeInMillis();
	}

	/**
	 * Konstruktor för att skapa ett uppdrag utifrån en region med kamerabild
	 * 
	 * @param name
	 * @param region
	 * @param receiver
	 * @param sender
	 * @param externalMission
	 * @param assignmentDescription
	 * @param timeSpan
	 * @param assignmentStatus
	 * @param cameraImage
	 * @param streetName
	 * @param siteName
	 */
	public Assignment(String name, String region, String receiver,
			String sender, boolean externalMission,
			String assignmentDescription, String timeSpan,
			AssignmentStatus assignmentStatus, Bitmap cameraImage,
			String streetName, String siteName) {
		this.name = name;
		this.region = region;
		this.receiver = receiver;
		this.sender = sender;
		this.externalMission = externalMission;
		this.assignmentDescription = assignmentDescription;
		this.timeSpan = timeSpan;
		this.assignmentStatus = assignmentStatus;
		this.cameraImage = cameraImage;
		this.streetName = streetName;
		this.siteName = siteName;
		this.assignmentTimeStamp = Calendar.getInstance().getTimeInMillis();
	}

	/**
	 * Konstruktor som används av databasen för att återskapa ett objekt och
	 * sätta dess id.
	 * 
	 * @param id
	 * @param name
	 * @param lat
	 * @param lon
	 * @param region
	 * @param receiver
	 * @param sender
	 * @param externalMission
	 * @param assignmentDescription
	 * @param timeSpan
	 * @param assignmentStatus
	 * @param cameraImage
	 * @param streetName
	 * @param siteName
	 */
	public Assignment(long id, String name, long lat, long lon, String region,
			String receiver, String sender, boolean externalMission,
			String assignmentDescription, String timeSpan,
			AssignmentStatus assignmentStatus, Bitmap cameraImage,
			String streetName, String siteName) {
		this.id = id;
		this.name = name;
		this.lat = lat;
		this.lon = lon;
		this.region = region;
		this.receiver = receiver;
		this.sender = sender;
		this.externalMission = externalMission;
		this.assignmentDescription = assignmentDescription;
		this.timeSpan = timeSpan;
		this.assignmentStatus = assignmentStatus;
		this.cameraImage = cameraImage;
		this.streetName = streetName;
		this.siteName = siteName;
		this.assignmentTimeStamp = Calendar.getInstance().getTimeInMillis();
	}

	public String getRegion() {
		return region;
	}

	public boolean isExternalMission() {
		return externalMission;
	}

	public List<Contact> getAgents() {
		return agents;
	}

	public String getName() {
		return name;
	}

	public String getStreetName() {
		return streetName;
	}

	public String getSiteName() {
		return siteName;
	}

	public long getLat() {
		return lat;
	}

	public long getLon() {
		return lon;
	}

	public String getReceiver() {
		return receiver;
	}

	public String getSender() {
		return sender;
	}

	public Bitmap getCameraImage() {
		return cameraImage;
	}

	public String getAssignmentDescription() {
		return assignmentDescription;
	}

	public String getTimeSpan() {
		return timeSpan;
	}

	public AssignmentStatus getAssignmentStatus() {
		return assignmentStatus;
	}
	
	public Long getTimeStamp(){
		return assignmentTimeStamp;
	}
	
	/**
	 * Hämta datum i format yyyy-MM-dd HH:mm:ss för tidszon CET
	 * @return String
	 */
	public String getMessageTimeStampSmart() {
		Date date = new Date(assignmentTimeStamp);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
		format.setTimeZone(TimeZone.getTimeZone("CET"));
		String smartTime = format.format(date).toString();
		return smartTime;
	}
	
	public String getDatabaseRepresentation() {
		return databaseRepresentation;
	}

	public long getId() {
		return id;
	}
}