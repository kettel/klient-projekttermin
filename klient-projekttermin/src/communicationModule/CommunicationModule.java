package communicationModule;

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
		System.out.println("Transmisson created");
		System.out.println(transmisson);
		
		MessageModel test = gson.fromJson(transmisson, MessageModel.class);
		
		System.out.println(test.getMessageContent());
	}
	
}
