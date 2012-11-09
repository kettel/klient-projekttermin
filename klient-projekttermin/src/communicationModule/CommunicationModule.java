package communicationModule;

import com.google.gson.Gson;

//import com.google.gson.Gson;

import models.Assignment;
import models.Contact;
import models.MessageModel;

public class CommunicationModule {
	private Gson gson = new Gson();
	private String transmisson = "test";
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