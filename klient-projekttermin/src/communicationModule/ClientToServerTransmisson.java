package communicationModule;

import java.io.BufferedReader;
import java.io.InputStreamReader;
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
	private BufferedReader input = null;
	private String inputString = null;
	
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
		System.out.println("this transmisson: " + transmisson);
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
			input = new BufferedReader(new InputStreamReader(requestSocet.getInputStream()));
			output = new PrintWriter(requestSocet.getOutputStream(), true);
			setConnetion(true);
		} catch (Exception e) {
			setConnetion(false);
			Log.e("Connection", ("Error: " + e.toString()));
		}
		
		while(true){
			try {
				if(input.ready()){
					inputString = input.readLine();
					Log.e("incomeing", inputString);	
				}
			} catch (Exception e) {
				Log.e("carcha", "inputString: " + e.toString());
			}
			if(!requestSocet.isConnected()){
				Log.e("Dissconnect","No connection");
				try{
					requestSocet = new Socket(ServerIP,ServerPort);
					output = new PrintWriter(requestSocet.getOutputStream(), true);	
				}catch(Exception e) {
					Log.e("catch","requestSocet: " + e.toString());
				}
			}
			
			if(sendData){
				output.println(transmisson);
				Log.e("output", "sending TRAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAANS");
				sendData(false);
			}
			
		}
	}


}