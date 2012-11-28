package communicationModule;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Observable;

import models.Assignment;
import models.AuthenticationModel;
import models.Contact;
import models.MessageModel;
import models.ModelInterface;
import android.util.Log;

import com.google.gson.Gson;

public class SocketConnection extends Observable {
	private Gson gson = new Gson();
	private String ip;
	private int port;

	public void sendModel(ModelInterface m) {
		sendJSON(gson.toJson(m));
	}

	public void authenticate(AuthenticationModel authenticationModel) {
		sendAuthentication(gson.toJson(authenticationModel));
	}

	private void sendJSON(String json) {
		ip = getAvailableIP();
		port = getPortForIP(ip);
		InetSocketAddress inetAddress = new InetSocketAddress(ip, port);

		Socket socket = new Socket();
		try {
			socket.connect(inetAddress);

			BufferedWriter bufferedWriter = new BufferedWriter(
					new OutputStreamWriter(socket.getOutputStream()));
			bufferedWriter.write(json);
			bufferedWriter.flush();
			bufferedWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private int getPortForIP(String ip2) {
		// TODO Auto-generated method stub
		return 0;
	}

	private String getAvailableIP() {
		// TODO Auto-generated method stub
		return null;
	}

	private void sendAuthentication(String json) {
		ip = getAvailableIP();
		port = getPortForIP(ip);
		InetSocketAddress inetAddress = new InetSocketAddress(ip, port);

		Socket socket = new Socket();
		try {
			socket.connect(inetAddress);

			BufferedWriter bufferedWriter = new BufferedWriter(
					new OutputStreamWriter(socket.getOutputStream()));
			bufferedWriter.write(json);
			bufferedWriter.flush();
			bufferedWriter.close();

			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(socket.getInputStream()));
			StringBuilder sb = new StringBuilder();
			String str;
			while ((str = bufferedReader.readLine()) != null) {
				sb.append(str + "\n");
			}
			bufferedReader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void pullFromServer() {
		ip = getAvailableIP();
		port = getPortForIP(ip);
		InetSocketAddress inetAddress = new InetSocketAddress(ip, port);

		Socket socket = new Socket();
		try {
			socket.connect(inetAddress);

			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(socket.getInputStream()));
			StringBuilder sb = new StringBuilder();
			String inputString;
			while ((inputString = bufferedReader.readLine()) != null) {
				sb.append(inputString + "\n");
			}
			bufferedReader.close();
			inputString=sb.toString();
			if (inputString.contains("\"databaseRepresentation\":\"message\"")) {
				MessageModel message = gson.fromJson(inputString,
						MessageModel.class);
				hasChanged();
				notifyObservers(message);
			} else if (inputString
					.contains("\"databasetRepresentation\":\"assignment\"")) {
				Assignment assignment = gson
						.fromJson(inputString, Assignment.class);
				hasChanged();
				notifyObservers(assignment);
			} else if (inputString
					.contains("\"databasetRepresentation\":\"contact\"")) {
				Contact contact = gson.fromJson(inputString, Contact.class);
				hasChanged();
				notifyObservers(contact);
			} else {
				Log.e("Database input problem", "Did not recognise inputtype.");
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
