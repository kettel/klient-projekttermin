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
//	private PrintWriter output = null;
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

//	@Override
//	protected Void doInBackground(String... params) {
//		try{
//			requestSocet = new Socket(ServerIP,ServerPort);
//			output = new PrintWriter(requestSocet.getOutputStream(), true);
//			connetion(true);
//		}catch(Exception e){
//			Log.e("Run", "Error in connecton", e);
//			connetion(false);
//		}
//		
//		while(connected){
//			
//		}
//		
//		//transmisson AWSONME!
////		try {
////			sendTransmisson(this.transmisson);
////		} catch (Exception e) {
////			Log.e("Run", "Error in connecton (send message)", e);
//////		}
//		
////	}
//	private void sendTransmisson(String transmisson) {
//		output.write(transmisson);
//	}

	public void run() {
		Log.e("Run", "RUUUUUUUUUUUUUJNNNN");
		PrintWriter  output = null;
		try {
			requestSocet = new Socket(ServerIP,ServerPort);
			output = new PrintWriter(requestSocet.getOutputStream(), false);
			//testa
			transmisson = "Olles lada";
			output.write(transmisson);
			output.flush();
			output.close();
			if(transmisson == null){
				Log.e("Run", "Shitface");
				
			}
			setConnetion(true);
		} catch (Exception e) {
			Log.e("Run", "Error in connecton", e);
			setConnetion(false);
		}
		
		while(isConnection()){
			
			if(sendData){
				output.write("Olles lilla låda i skogen");
				sendData(false);
			}
			
		}
	}


}