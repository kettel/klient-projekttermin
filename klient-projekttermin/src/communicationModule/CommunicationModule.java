package communicationModule;

import java.io.PrintWriter;
import java.net.Socket;

import com.google.gson.Gson;

import models.Assignment;
import models.Contact;
import models.MessageModel;

public class CommunicationModule {
	private Gson gson = new Gson();
	private String transmisson = null;
	//konstruktorn
	public CommunicationModule(){
		
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
