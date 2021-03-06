package models;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nutiteq.components.WgsPoint;

public class Assignment implements ModelInterface {
	// Typen av model
	private String databaseRepresentation = "assignment";
	// Id för modellen (Sätts av databasen så pilla inte)
	private long id = -1;
	//Globalt ID för uppdraget
	private String globalID = "";
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
	// Prioritetsnivå
	private AssignmentPriority assignmentPrio;

	/**
	 * Tom konstruktor. Används bland annat för att hämta från databasen.
	 */
	public Assignment() {
	}
	/**
	 * Används för remove
	 * @param id
	 */
	public Assignment(String globalID) {
		this.globalID = globalID;
	}

	/**
	 * Konstruktor för att skapa ett uppdrag utan kartmarkering utan bild med
	 * bara en adress (ej siteName)
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
	public Assignment(String name, String sender, boolean externalMission,
			String assignmentDescription, String timeSpan,
			AssignmentStatus assignmentStatus, String streetName,
			AssignmentPriority assignmentPrio) {
		this.name = name;
		this.sender = sender;
		this.externalMission = externalMission;
		this.assignmentDescription = assignmentDescription;
		this.timeSpan = timeSpan;
		this.assignmentStatus = assignmentStatus;
		this.streetName = streetName;
		this.assignmentTimeStamp = Calendar.getInstance().getTimeInMillis();
		this.assignmentPrio = assignmentPrio;
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
	public Assignment(String name, String sender, boolean externalMission,
			String assignmentDescription, String timeSpan,
			AssignmentStatus assignmentStatus, String streetName,
			String siteName, AssignmentPriority assignmentPrio) {
		this.name = name;
		this.sender = sender;
		this.externalMission = externalMission;
		this.assignmentDescription = assignmentDescription;
		this.timeSpan = timeSpan;
		this.assignmentStatus = assignmentStatus;
		this.streetName = streetName;
		this.siteName = siteName;
		this.assignmentTimeStamp = Calendar.getInstance().getTimeInMillis();
		this.assignmentPrio = assignmentPrio;
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
	public Assignment(String name, String sender, boolean externalMission,
			String assignmentDescription, String timeSpan,
			AssignmentStatus assignmentStatus, byte[] cameraImage,
			String streetName, String siteName,
			AssignmentPriority assignmentPrio) {

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
		this.assignmentPrio = assignmentPrio;
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
	public Assignment(String name, double lat, double lon, String sender,
			boolean externalMission, String assignmentDescription,
			String timeSpan, AssignmentStatus assignmentStatus,
			String streetName, String siteName,
			AssignmentPriority assignmentPrio) {
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
		this.assignmentPrio = assignmentPrio;
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
	public Assignment(String name, double lat, double lon, String sender,
			boolean externalMission, String assignmentDescription,
			String timeSpan, AssignmentStatus assignmentStatus,
			byte[] cameraImage, String streetName, String siteName,
			AssignmentPriority assignmentPrio) {
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
		this.assignmentPrio = assignmentPrio;
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
	public Assignment(String name, String region, String sender,
			boolean externalMission, String assignmentDescription,
			String timeSpan, AssignmentStatus assignmentStatus,
			String streetName, String siteName,
			AssignmentPriority assignmentPrio) {
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
		this.assignmentPrio = assignmentPrio;
	}

	/**
	 * Konstruktor för att skapa ett uppdrag utifrån en region med kamerabild.
	 * Använd den här.
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
	public Assignment(String name, String region, String sender,
			boolean externalMission, String assignmentDescription,
			String timeSpan, AssignmentStatus assignmentStatus,
			byte[] cameraImage, String streetName, String siteName,
			AssignmentPriority assignmentPrio) {
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
		this.assignmentPrio = assignmentPrio;
	}

	/**
	 * Konstruktor som används av databasen för att återskapa ett objekt och
	 * sätta dess id.
	 * 
	 * @param id
	 * @param globalID
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
	 * @param timeStamp
	 */
	public Assignment(long id, String globalID, String name, double lat, double lon,
			String region, List<Contact> agents, String sender,
			boolean externalMission, String assignmentDescription,
			String timeSpan, AssignmentStatus assignmentStatus,
			byte[] cameraImage, String streetName, String siteName,
			Long timeStamp, AssignmentPriority assignmentPrio) {
		this.id = id;
		this.globalID = globalID;
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
		this.assignmentPrio = assignmentPrio;
	}

	public String getRegion() {
		if (region == null) {
			Gson gson = new Gson();
			WgsPoint[] p = new WgsPoint[1];
			p[0] = new WgsPoint(0, 0);
			Type type = new TypeToken<WgsPoint[]>() {
			}.getType();
			region = gson.toJson(p, type);
		}
		return region;
	}

	public boolean isExternalMission() {
		return externalMission;
	}

	public List<Contact> getAgents() {
		return agents;
	}

	public void addAgents(Contact self) {
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
		if (cameraImage == null) {
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

	public void setAssignmentStatus(AssignmentStatus newStatus) {
		assignmentStatus = newStatus;
	}

	public Long getTimeStamp() {
		return assignmentTimeStamp;
	}

	public AssignmentPriority getAssignmentPriority() {
		return assignmentPrio;
	}

	public String getAssignmentPriorityToString() {

		final String PRIO_HIGH = "Hög prioritering";
		final String PRIO_NORMAL = "Normal prioritering";
		final String PRIO_LOW = "Låg prioritering";
		final String PRIO_NONEEXISTANT = "Prioritering ej satt";
		
		if (assignmentPrio == null) {
			return PRIO_NONEEXISTANT;
		} else if (assignmentPrio == AssignmentPriority.PRIO_HIGH) {
			return PRIO_HIGH;
		} else if (assignmentPrio == AssignmentPriority.PRIO_NORMAL) {
			return PRIO_NORMAL;
		} else if (assignmentPrio == AssignmentPriority.PRIO_LOW) {
			return PRIO_LOW;
		} else {
			return PRIO_NONEEXISTANT;
		}

	}

	/**
	 * Hämta datum i format yyyy-MM-dd HH:mm:ss för tidszon CET
	 * 
	 * @return String
	 */
	public String getMessageTimeStampSmart() {
		Date date = new Date(assignmentTimeStamp);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
				Locale.US);
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
	public String getGlobalID(){
		return globalID;
	}
	public void setGlobalID(String user){
		this.globalID = user + assignmentTimeStamp.toString();
	}
}