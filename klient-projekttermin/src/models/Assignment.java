package models;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import com.nutiteq.components.WgsPoint;


public class Assignment implements ModelInterface {
	// Typen av model
	private String databaseRepresentation = "assignment";
	// Id för modellen (Sätts av databasen så pilla inte)
	private long id = -1;
	// Namnet på uppdraget
	private String name;
	// Latitud för uppdragspositionen
	private double lat;
	// Longitud för uppdragspositionen
	private double lon;
	// JSON-sträng med WSG-punkter för polygonmarkering av region på kartan
	private String region;
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
	private byte[] cameraImage;
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
	 * med bara en adress (ej siteName)
	 * 
	 * @param name
	 * @param sender
	 * @param externalMission
	 * @param agents
	 * @param assignmentDescription
	 * @param timeSpan
	 * @param assignmentStatus
	 * @param cameraImage
	 * @param streetName
	 */
	public Assignment(String name, String sender,
			boolean externalMission, String assignmentDescription,
			String timeSpan, AssignmentStatus assignmentStatus,
			String streetName) {
		this.name = name;
		this.sender = sender;
		this.externalMission = externalMission;
		this.assignmentDescription = assignmentDescription;
		this.timeSpan = timeSpan;
		this.assignmentStatus = assignmentStatus;
		this.streetName = streetName;
		this.assignmentTimeStamp = Calendar.getInstance().getTimeInMillis();
	}
	
	/**
	 * Konstruktor för att skapa ett uppdrag utan kartmarkering utan bild
	 * 
	 * @param name
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
	public Assignment(String name, String sender,
			boolean externalMission, String assignmentDescription,
			String timeSpan, AssignmentStatus assignmentStatus,
			String streetName, String siteName) {
		this.name = name;
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
	 * @param sender
	 * @param externalMission
	 * @param assignmentDescription
	 * @param timeSpan
	 * @param assignmentStatus
	 * @param cameraImage
	 * @param streetName
	 * @param siteName
	 */
	public Assignment(String name, String sender,
			boolean externalMission, String assignmentDescription,
			String timeSpan, AssignmentStatus assignmentStatus,
			byte[] cameraImage, String streetName, String siteName) {
		this.name = name;
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
	 * @param sender
	 * @param externalMission
	 * @param assignmentDescription
	 * @param timeSpan
	 * @param assignmentStatus
	 * @param streetName
	 * @param siteName
	 */
	public Assignment(String name, double lat, double lon, 
			String sender, boolean externalMission,
			String assignmentDescription, String timeSpan,
			AssignmentStatus assignmentStatus, String streetName,
			String siteName) {
		this.name = name;
		this.lat = lat;
		this.lon = lon;
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
	 * @param sender
	 * @param externalMission
	 * @param assignmentDescription
	 * @param timeSpan
	 * @param assignmentStatus
	 * @param cameraImage
	 * @param streetName
	 * @param siteName
	 */
	public Assignment(String name, double lat, double lon, 
			String sender, boolean externalMission,
			String assignmentDescription, String timeSpan,
			AssignmentStatus assignmentStatus, byte[] cameraImage,
			String streetName, String siteName) {
		this.name = name;
		this.lat = lat;
		this.lon = lon;
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
	 * @param sender
	 * @param externalMission
	 * @param assignmentDescription
	 * @param timeSpan
	 * @param assignmentStatus
	 * @param streetName
	 * @param siteName
	 */
	public Assignment(String name, String region, 
			String sender, boolean externalMission,
			String assignmentDescription, String timeSpan,
			AssignmentStatus assignmentStatus, String streetName,
			String siteName) {
		this.name = name;
		this.region = region;
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
	 * Det är denna som används just nu
	 * 
	 * @param name
	 * @param region
	 * @param sender
	 * @param externalMission
	 * @param assignmentDescription
	 * @param timeSpan
	 * @param assignmentStatus
	 * @param cameraImage
	 * @param streetName
	 * @param siteName
	 */
	public Assignment(String name, String region, 
			String sender, boolean externalMission,
			String assignmentDescription, String timeSpan,
			AssignmentStatus assignmentStatus, byte[] cameraImage,
			String streetName, String siteName) {
		this.name = name;
		this.region = region;
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
	 * @param sender
	 * @param externalMission
	 * @param assignmentDescription
	 * @param timeSpan
	 * @param assignmentStatus
	 * @param cameraImage
	 * @param streetName
	 * @param siteName
	 */
	public Assignment(long id, String name, double lat, double lon, String region,
			List<Contact> agents,
			String sender, boolean externalMission,
			String assignmentDescription, String timeSpan,
			AssignmentStatus assignmentStatus, byte[] cameraImage,
			String streetName, String siteName, Long timeStamp) {
		this.id = id;
		this.name = name;
		this.lat = lat;
		this.lon = lon;
		this.region = region;
		this.agents = agents;
		this.sender = sender;
		this.externalMission = externalMission;
		this.assignmentDescription = assignmentDescription;
		this.timeSpan = timeSpan;
		this.assignmentStatus = assignmentStatus;
		this.cameraImage = cameraImage;
		this.streetName = streetName;
		this.siteName = siteName;
		this.assignmentTimeStamp = timeStamp;
	}

	public String getRegion() {
		if(region == null){
			region = "[{\"Lat\":0,\"Lon\":0}]";
		}
		return region;
	}

	public boolean isExternalMission() {
		return externalMission;
	}

	public List<Contact> getAgents() {
		return agents;
	}
	public void addAgents(Contact self){
		agents.add(self);
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

	public double getLat() {
		return lat;
	}

	public double getLon() {
		return lon;
	}

	public String getSender() {
		return sender;
	}

	public byte[] getCameraImage() {
		// Om bilden är null och den ska hämtas...
		if (cameraImage == null){
			cameraImage = new byte[2];
		}
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
	public void setAssignmentStatus(AssignmentStatus newStatus){
		assignmentStatus = newStatus;
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

	public void setId(long id) {
		this.id = id;
	}
}