package communicationModule;

import java.util.ArrayList;
import java.util.List;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.google.gson.Gson;

import models.Assignment;
import models.AuthenticationModel;
import models.Contact;
import models.MessageModel;

/**
 * Denna Servicen sköter kommunikationen med servern.
 * Det är lite knepigt att implemetera den men läs "CommunicationService lathund" om du blir osäker.
 * @author Eric
 *
 */
public class CommunicationService extends Service implements IncomeingDataListners{
	private final IBinder binder = new CommunicationBinder();
	
	private Gson gson = new Gson();
	private String transmisson = "";
	private List<IncomeingDataListners> messageListners = new ArrayList<IncomeingDataListners>();
	private List<IncomeingDataListners> AssignmentListners = new ArrayList<IncomeingDataListners>();
	ClientToServerConnection ClientToServer = null;
	/**
	 * Denna konstruktor körs när servicen registeras. skapar och kör ClientToServerConnection
	 */
	public CommunicationService(){
		ClientToServer = new ClientToServerConnection();
		ClientToServer.RegisterCommunicationService(this);
		ClientToServer.start();
	}
	/**
	 * Skickar om möjligt ett medelande till servern.
	 * @param message medelandet som ska skickas.
	 */
	public void sendMessage(MessageModel message){
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
	
	public void sendAuthentication (AuthenticationModel authentication){
		transmisson = gson.toJson(authentication);
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
    // HÄR FÖLJER ALLA LYSNARMETODER
	public void handelIncomeingMessage() {
		if(messageListners != null){
			for (int i = 0; i < messageListners.size(); i++) {
				messageListners.get(i).handelIncomeingMessage();
			}
		}
	}

	public void registerIncomeingMessagelistener(IncomeingDataListners newListener){
		if(!messageListners.contains(newListener)){
			messageListners.add(newListener);
		}
	}
	
	public void unregisterIncomeingMessagelistener(IncomeingDataListners listener){
		if(messageListners.contains(listener)){
			messageListners.remove(listener);
		}
	}
	
	public void handelIncomeingAssignment() {
		if(AssignmentListners != null){
			for (int i = 0; i < AssignmentListners.size(); i++) {
				AssignmentListners.get(i).handelIncomeingAssignment();
			}
		}
	}
	
	public void registerIncomeingAssignmentListners (IncomeingDataListners newListener){
		if(!AssignmentListners.contains(newListener)){
			AssignmentListners.add(newListener);
		}
	}
	public void unregisterIncomeingAssignmentListners(IncomeingDataListners listener){
		if(AssignmentListners.contains(listener)){
			AssignmentListners.remove(listener);
		}
	}
}

