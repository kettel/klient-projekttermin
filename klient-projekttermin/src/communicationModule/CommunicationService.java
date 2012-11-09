package communicationModule;

import java.io.PrintWriter;
import java.net.Socket;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.google.gson.Gson;

//import com.google.gson.Gson;

import models.Assignment;
import models.Contact;
import models.MessageModel;

public class CommunicationService extends Service{
	private final IBinder binder = new CommunicationBinder();
	
	private Gson gson = new Gson();
	private String transmisson = "test";
	ClientToServerTransmisson ClientToServer = null;
	
	//konstruktorn
	public CommunicationService(){
		ClientToServer = new ClientToServerTransmisson();
		ClientToServer.start();
	}

	public void sendMessage(MessageModel message){
		System.out.println("sendMessage");
		transmisson = gson.toJson(message);
		ClientToServer.sendTransmisson(transmisson);
	}

	public void sendAssignment(Assignment assignment){
		transmisson = gson.toJson(assignment);
		ClientToServer.sendTransmisson(transmisson);
	}

	public void sendContact (Contact contact){
		transmisson = gson.toJson(contact);
		ClientToServer.sendTransmisson(transmisson);
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		System.out.println("onBind");
		return binder;
	}
	
	public class CommunicationBinder extends Binder{
		
		public CommunicationService getService(){
			System.out.println("getService");
			return CommunicationService.this;
		}
	}
}

