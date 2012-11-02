package communicationModule;

import java.io.PrintWriter;
import java.net.Socket;

import logger.logger;

import android.os.AsyncTask;
import android.util.Log;

public class ClientToServerTransmisson extends AsyncTask<String, Void, Void> {
	
	private String ServerIP = "10.0.2.2";
	private int ServerPort = 4576;
	private Socket requestSocet = null;
	private PrintWriter output = null;
	private String transmisson = null;
	
	public ClientToServerTransmisson(String JsonString){
		this.transmisson = JsonString;
	}
	
	@Override
	protected Void doInBackground(String... params) {
		try{
			requestSocet = new Socket(ServerIP,ServerPort);
			output = new PrintWriter(requestSocet.getOutputStream(), true);
			
		}catch(Exception e){
			Log.e("Run", "Error in connecton", e);
		}
		//transmisson AWSONME!
		try {
			sendTransmisson(this.transmisson);
		} catch (Exception e) {
			Log.e("Run", "Error in connecton (send message)", e);
		}
		//closeing
		try {
			output.close();
			requestSocet.close();
		} catch (Exception e) {
			Log.e("Run", "Error in connecton (closeing)", e);
		}
		return null;
	}
	private void sendTransmisson(String transmisson) {
		output.write(transmisson);
	}
	

}
