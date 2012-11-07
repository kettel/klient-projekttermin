package communicationModule;

import java.io.PrintWriter;
import java.net.Socket;

import logger.logger;

import android.os.AsyncTask;
import android.util.Log;

public class ClientToServerTransmisson extends Thread  {

	private String ServerIP = "94.254.72.38";
	private int ServerPort = 17234;
	private Socket requestSocet =  null;
	private String transmisson = null;
	private PrintWriter  output = null;
	
	private boolean sendData = false;
	private boolean connected = false;
	
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
	// gfagas
	public void run() {
		try {
			requestSocet = new Socket(ServerIP,ServerPort);
			requestSocet.
			output = new PrintWriter(requestSocet.getOutputStream(), true);
			setConnetion(true);
		} catch (Exception e) {
			setConnetion(false);
			Log.e("Connection", ("Error: " + e.toString()));
		}
		
		while(true){
			if(!requestSocet.isConnected()){
				Log.e("Dissconnect","No connection");
				try{
					requestSocet = new Socket(ServerIP,ServerPort);
					output = new PrintWriter(requestSocet.getOutputStream(), true);	
				}catch(Exception e) {
					Log.e("catch","fafsa");
				}
			}
			if(sendData){
				output.println(transmisson);
				sendData(false);
			}
			
		}
	}


}