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

import loginFunction.User;
import models.Assignment;
import models.AuthenticationModel;
import models.Contact;
import models.MessageModel;
import models.ModelInterface;
import android.util.Log;

import com.google.gson.Gson;

public class SocketConnection extends Observable {
	private Gson gson = new Gson();
	private String ip = "94.254.72.38";
	private int port = 17234;

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
			bufferedWriter.write(json + "\n");
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
		try {
			Socket socket = new Socket(ip, port);
			System.out.println("Socketen lyckades ansluta");
			BufferedWriter bufferedWriter = new BufferedWriter(
					new OutputStreamWriter(socket.getOutputStream()));
			bufferedWriter.write(json + "\nclose\n");
			bufferedWriter.flush();
			System.out.println("Socketen lyckades skriva");
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(socket.getInputStream()));
			StringBuilder sb = new StringBuilder();
			String str;
			while ((str = bufferedReader.readLine()) != null) {
				sb.append(str);
			}
			bufferedReader.close();
			socket.close();
			System.out.println("Socketen tog emot: " + sb.toString());
			setChanged();
			System.out.println(hasChanged());
			AuthenticationModel authenticationModel = gson.fromJson(
					sb.toString(), AuthenticationModel.class);
			notifyObservers(authenticationModel);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void pullFromServer() {
		new Thread(new Runnable() {

			public void run() {
				ip = getAvailableIP();
				port = getPortForIP(ip);
				try {
					Socket socket = new Socket(ip, port);
					System.out.println("Socketen lyckades ansluta");
					BufferedWriter bufferedWriter = new BufferedWriter(
							new OutputStreamWriter(socket.getOutputStream()));
					User user=User.getInstance();
					String json=gson.toJson(user.getAuthenticationModel());
					bufferedWriter.write(json+"\n"+"pull\nclose\n");
					bufferedWriter.flush();
					System.out.println("Socketen lyckades skriva");
					BufferedReader bufferedReader = new BufferedReader(
							new InputStreamReader(socket.getInputStream()));
					StringBuilder sb = new StringBuilder();
					String inputString;
					while ((inputString = bufferedReader.readLine()) != null) {
						if (inputString
								.contains("\"databaseRepresentation\":\"message\"")) {
							System.out.println("message");
							MessageModel message = gson.fromJson(inputString,
									MessageModel.class);
							setChanged();
							notifyObservers(message);
						} else if (inputString
								.contains("\"databasetRepresentation\":\"assignment\"")) {
							System.out.println("assignment");
							Assignment assignment = gson.fromJson(inputString,
									Assignment.class);
							setChanged();
							notifyObservers(assignment);
						} else if (inputString
								.contains("\"databasetRepresentation\":\"contact\"")) {
							System.out.println("contact");
							Contact contact = gson.fromJson(inputString,
									Contact.class);
							setChanged();
							notifyObservers(contact);
						} else {
							Log.e("Database input problem",
									"Did not recognise inputtype.");
						}
					}
					bufferedReader.close();
					inputString = sb.toString();

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
	}

}
