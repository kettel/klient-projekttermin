package communicationModule;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.IBinder;

import com.google.gson.Gson;

import models.Assignment;
import models.Contact;
import models.MessageModel;

/**
 * Denna Servicen sköter kommunikationen med servern.
 * Det är lite knepigt att implemetera den men läs "CommunicationService lathund" om du blir osäker.
 * @author Eric
 *
 */
public class CommunicationService extends Service{
	private final IBinder binder = new CommunicationBinder();
	
	private Gson gson = new Gson();
	private String transmisson = "";
	ClientToServerConnection ClientToServer = null;
	/**
	 * Denna konstruktor körs när servicen registeras. skapar och kör ClientToServerConnection
	 */
	public CommunicationService(){
		ClientToServer = new ClientToServerConnection();
		ClientToServer.start();
	}
	/**
	 * Skickar om möjligt ett medelande till servern.
	 * @param message medelandet som ska skickas.
	 */
	public void sendMessage(MessageModel message){
		System.out.println("sendMessage");
		transmisson = gson.toJson(message);
		ClientToServer.sendTransmisson(transmisson);
	}
	/**
	 * Skickar om möjligt ett uppdrag till serven.
	 * @param assignment uppdraget som ska skickas.
	 */
	public void sendAssignment(Assignment assignment){
		transmisson = gson.toJson(assignment);
		ClientToServer.sendTransmisson(transmisson);
	}
	/**
	 * Skickar om möjligt en kontakt till serven.
	 * @param contact kontakten som ska skickas.
	 */
	public void sendContact (Contact contact){
		transmisson = gson.toJson(contact);
		ClientToServer.sendTransmisson(transmisson);
	}
	/**
	 * För att kunna skriva till databasen krävs applikationens context.
	 * @param context (Skicka in getApplicationContext())
	 */
	public void setContext (Context context){
		ClientToServer.setContext(context);
	}
	/**
	 * Används när en aktivitet binder sig till servicen.
	 */
	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}
	/**
	 * 	Används när en aktivitet binder sig till servicen.
	 * @author Eric
	 *
	 */
	public class CommunicationBinder extends Binder{
		public CommunicationService getService(){
			return CommunicationService.this;
		}
	}

}

