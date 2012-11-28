package communicationModule;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
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
	private String ip="94.254.72.38";
	private int port=17234;

	public void sendModel(ModelInterface modelInterface) {
		final ModelInterface model = modelInterface;
		new Thread(new Runnable() {

			public void run() {
				sendJSON(gson.toJson(model));
			}
		}).start();

	}

	public void authenticate(AuthenticationModel authenticationModel) {
		final AuthenticationModel model = authenticationModel;
		new Thread(new Runnable() {

			public void run() {
				sendAuthentication(gson.toJson(model));
			}
		}).start();
	}

	private void sendJSON(String json) {
		ip = getAvailableIP();
		port = getPortForIP(ip);
		try {
			Socket socket = new Socket(ip, port);
			BufferedWriter bufferedWriter = new BufferedWriter(
					new OutputStreamWriter(socket.getOutputStream()));
			bufferedWriter.write(json);
			bufferedWriter.flush();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private int getPortForIP(String ip) {
		return 17234;
	}

	private String getAvailableIP() {
		return "94.254.72.38";
	}

	private void sendAuthentication(String json) {
		ip = getAvailableIP();
		port = getPortForIP(ip);
		SocketAddress inetAddress = new InetSocketAddress(ip, port);

		Socket socket = new Socket();
		try {
			socket.connect(inetAddress);

			BufferedWriter bufferedWriter = new BufferedWriter(
					new OutputStreamWriter(socket.getOutputStream()));
			bufferedWriter.write(json);
			bufferedWriter.flush();

			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(socket.getInputStream()));
			StringBuilder sb = new StringBuilder();
			String str;
			while ((str = bufferedReader.readLine()) != null) {
				sb.append(str + "\n");
			}
			bufferedReader.close();
			socket.close();
		} catch (IOException e) {
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
			inputString = sb.toString();
			if (inputString.contains("\"databaseRepresentation\":\"message\"")) {
				MessageModel message = gson.fromJson(inputString,
						MessageModel.class);
				hasChanged();
				notifyObservers(message);
			} else if (inputString
					.contains("\"databasetRepresentation\":\"assignment\"")) {
				Assignment assignment = gson.fromJson(inputString,
						Assignment.class);
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
