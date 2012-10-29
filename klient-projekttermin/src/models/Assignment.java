package models;

public class Assignment{
	private String name;
	private long lat;
	private long lon;
	private String receiver;
	private String sender;
	
	public Assignment(String name, long lat, long lon, String receiver, String sender){
		this.lat=lat;
		this.lon=lon;
		this.name=name;
		this.receiver=receiver;
		this.sender=sender;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getLat() {
		return lat;
	}

	public void setLat(long lat) {
		this.lat = lat;
	}

	public long getLon() {
		return lon;
	}

	public void setLon(long lon) {
		this.lon = lon;
	}

	public String getReceiver() {
		return receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}
	
}