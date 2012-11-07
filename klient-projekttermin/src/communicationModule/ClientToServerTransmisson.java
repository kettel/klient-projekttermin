package communicationModule;

import java.io.PrintWriter;
import java.net.Socket;

import logger.logger;

import android.os.AsyncTask;
import android.util.Log;

public class ClientToServerTransmisson extends Thread  {

	private String ServerIP = "94.254.72.38";
	private int ServerPort = 17234;
	private Socket requestSocet = null;
	private String transmisson = null;
	
	private boolean sendData = false;
	private boolean connected = true;
	
	public ClientToServerTransmisson(){
		System.out.println("Constuct done");
	}
	
	private synchronized void sendData(boolean enabel){
		sendData = enabel;
	}
	
	public synchronized void sendTransmisson(String transmisson){
		this.transmisson = transmisson;
		sendData = true;
	}
	
	public synchronized void setConnetion(boolean enabel){
		connected = enabel;
	}
	public synchronized boolean isConnection(){
		return connected;
	}

	public void run() {
		PrintWriter  output = null;
		try {
			requestSocet = new Socket(ServerIP,ServerPort);
			output = new PrintWriter(requestSocet.getOutputStream(), true);
			setConnetion(true);
		} catch (Exception e) {
			Log.e("Run", "Error in connecton", e);
			setConnetion(false);
		}
		
		while(isConnection()){
			
			if(sendData){
				output.println(transmisson);
				sendData(false);
			}
			
		}
	}


}