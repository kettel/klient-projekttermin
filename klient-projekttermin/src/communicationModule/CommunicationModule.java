package communicationModule;

import java.io.PrintWriter;
import java.net.Socket;

import android.content.Context;

import com.google.gson.Gson;

import logger.logger;
import models.Assignment;
import models.Contact;
import models.MessageModel;

public class CommunicationModule {
	private Gson gson = new Gson();
	private String transmisson = null;
	private logger comLog = null;
	private boolean acceptTransmissons;
	//konstruktorn
	public CommunicationModule(logger comLog, boolean acceptTransmissons){
		this.comLog = comLog;
		this.acceptTransmissons = acceptTransmissons;
		if(acceptTransmissons){
			ServerToClientTransmisson test = new ServerToClientTransmisson();
			test.execute();
			System.out.println("hej");
		}
	}
	
	public void sendMessage(MessageModel message){
		transmisson = gson.toJson(message);
		ClientToServerTransmisson messageToServer = new ClientToServerTransmisson(transmisson);
		messageToServer.execute(transmisson);
		
	}
	
	public void sendAssignment(Assignment assignment){
		transmisson = gson.toJson(assignment);
		ClientToServerTransmisson assignmentToServer = new ClientToServerTransmisson(transmisson);
		assignmentToServer.execute(transmisson);
		
	}
	
	public void sendContact (Contact contact){
		transmisson = gson.toJson(contact);
		ClientToServerTransmisson contactToServer = new ClientToServerTransmisson(transmisson);
		contactToServer.execute(transmisson);
	}
	
	
	
}
