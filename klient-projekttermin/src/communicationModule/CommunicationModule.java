package communicationModule;

import java.io.PrintWriter;
import java.net.Socket;

import com.google.gson.Gson;

import models.MessageModel;

public class CommunicationModule {
	//konstruktorn
	public CommunicationModule(){
		
	}
	
	public void sendMessage(MessageModel message){
		System.out.println("Starting");
		Gson gson = new Gson();
		System.out.println("Gson created");
		String transmisson = gson.toJson(message);
		System.out.println(transmisson);
		ClientToServerTransmisson test = new ClientToServerTransmisson(transmisson);
		test.execute(transmisson);
//		sendToServer(transmisson);
	}
	
	private void sendToServer(String transmisson){
		
	}
	
}
