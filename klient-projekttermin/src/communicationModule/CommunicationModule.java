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
	ClientToServerTransmisson ClientToServer = null;
	//konstruktorn
	public CommunicationModule(){
		ClientToServer = new ClientToServerTransmisson();
		ClientToServer.start();
	}

	public void sendMessage(MessageModel message){
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
	
	



}