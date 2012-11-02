package communicationModule;

import java.net.ServerSocket;

import android.os.AsyncTask;

public class ServerToClientTransmisson extends AsyncTask<String, Void, Void>{
	boolean acceptTransmissons = true;
	ServerSocket serverSocket = null;
	
	@Override
	protected Void doInBackground(String... arg0) {
		try {
			serverSocket = new ServerSocket(4576);
			while(acceptTransmissons){
				System.out.println("waiting for accept");
				MultiServerThread testMulti = new MultiServerThread(serverSocket.accept());
				System.out.println("after accept");
			}
			
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}
	

}
