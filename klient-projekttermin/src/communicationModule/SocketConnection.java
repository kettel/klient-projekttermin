package communicationModule;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Observable;

import login.User;
import models.Assignment;
import models.AuthenticationModel;
import models.Contact;
import models.MessageModel;
import models.ModelInterface;

import android.util.Log;

import com.google.gson.Gson;
import com.klient_projekttermin.CommonUtilities;

public class SocketConnection extends Observable {
	private Gson gson = new Gson();
	private String ip = "94.254.72.38";
	private int port = 17234;
	private ArrayList<String[]> servers = new ArrayList<String[]>();
	Iterator<String[]> iterator;

	/**
	 * Konstruktor som även initierar serverlistan.
	 */
	public SocketConnection() {
		super();
		initServerList();
	}

	/**
	 * Skapar en array av addresser till servrar samt laddar in den första
	 */
	private void initServerList() {
		String[] i = { "94.254.72.38", "17234", "16783" };
		servers.add(i);
		String[] j = { "94.254.72.38", "18234", "17783" };
		servers.add(j);
		iterator = servers.iterator();
		loadNextServer();
	}

	/**
	 * Skickar en modell till servern
	 * 
	 * @param modelInterface
	 *            -modellen
	 */
	public void sendModel(ModelInterface modelInterface) {
		final ModelInterface model = modelInterface;
		new Thread(new Runnable() {
			public void run() {
				sendJSON(gson.toJson(model));
				Log.e("FEL", "Försöker skicka något från sendModel i socketconnection");
			}
		}).start();
	}

	/**
	 * Skickar en autentiseringsförfrågan till servern
	 * 
	 * @param authenticationModel
	 *            - modellen
	 */
	public void authenticate(AuthenticationModel authenticationModel) {
		final AuthenticationModel model = authenticationModel;
		new Thread(new Runnable() {

			public void run() {
				sendAuthentication(gson.toJson(model));
			}
		}).start();
	}

	public HashMap<String, int[]> getServer() {

		return null;
	}

	/**
	 * Skapar en ny socketanslutning och skriver en sträng på denna
	 * 
	 * @param json
	 *            - En sträng med det som ska skickas
	 */
	private void sendJSON(String json) {
		try {
			Socket socket = new Socket(ip, port);
			BufferedWriter bufferedWriter = new BufferedWriter(
					new OutputStreamWriter(socket.getOutputStream()));
			bufferedWriter.write(json + "\n");
			bufferedWriter.flush();
			socket.close();
		} catch (IOException e) {
			if (iterator.hasNext()) {
				loadNextServer();
				sendJSON(json);
			}
		}
	}

	/**
	 * Laddar in nästa server. Server addressen samt portar finnsi arrayen.Ip på
	 * index 0, port på index 1 och jettyport på index 2
	 */
	private void loadNextServer() {
		String[] server = iterator.next();
		ip = server[0];
		port = Integer.parseInt(server[1]);
		CommonUtilities.SERVER_URL = "http://" + server[0] + ":" + server[2];
	}

	/**
	 * Skapar en anslutning o skickar strängen till servern
	 * 
	 * @param json
	 *            - Strängen
	 */
	private void sendAuthentication(String json) {

		try {
			Socket socket = new Socket();
			socket.connect(new InetSocketAddress(ip, port), 10000);
			BufferedWriter bufferedWriter = new BufferedWriter(
					new OutputStreamWriter(socket.getOutputStream()));
			bufferedWriter.write(json + "\nclose\n");
			bufferedWriter.flush();
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(socket.getInputStream()));
			StringBuilder sb = new StringBuilder();
			String str;

			while ((str = bufferedReader.readLine()) != null) {
				sb.append(str);
			}
			bufferedReader.close();
			socket.close();
			setChanged();
			AuthenticationModel authenticationModel = gson.fromJson(
					sb.toString(), AuthenticationModel.class);
			notifyObservers(authenticationModel);
		} catch (IOException e) {
			e.printStackTrace();
			if (iterator.hasNext()) {
				loadNextServer();
				sendAuthentication(json);
			} else {
				setChanged();
				String fail = "failed to connect";
				notifyObservers(fail);
			}
		}
	}

	public void pullFromServer() {
		new Thread(new Runnable() {

			public void run() {
				try {
					Socket socket = new Socket(ip, port);
					BufferedWriter bufferedWriter = new BufferedWriter(
							new OutputStreamWriter(socket.getOutputStream()));
					User user = User.getInstance();
					String json = gson.toJson(user.getAuthenticationModel());
					bufferedWriter.write(json + "\n" + "pull\nclose\n");
					bufferedWriter.flush();
					BufferedReader bufferedReader = new BufferedReader(
							new InputStreamReader(socket.getInputStream()));
					String inputString;
					while ((inputString = bufferedReader.readLine()) != null) {
						if (inputString
								.contains("\"databaseRepresentation\":\"message\"")) {
							MessageModel message = gson.fromJson(inputString,
									MessageModel.class);
							setChanged();
							notifyObservers(message);
						} else if (inputString
								.contains("\"databaseRepresentation\":\"assignment\"")) {
							Assignment assignment = gson.fromJson(inputString,
									Assignment.class);
							setChanged();
							notifyObservers(assignment);
						} else if (inputString
								.contains("\"databaseRepresentation\":\"contact\"")) {
							Contact contact = gson.fromJson(inputString,
									Contact.class);
							setChanged();
							notifyObservers(contact);
						} else if (inputString
								.contains("\"databaseRepresentation\":\"authentication\"")) {
						} else {
						}
					}
					bufferedReader.close();
					socket.close();
					if (inputString == null) {
						setChanged();
						notifyObservers(null);
					}
				} catch (IOException e) {
					if (iterator.hasNext()) {
						loadNextServer();
						pullFromServer();
					} else {
						setChanged();
						String fail = "failed to connect";
						notifyObservers(fail);
					}
				}
			}
		}).start();
	}

	public void getAllContactsReq() {
		new Thread(new Runnable() {

			public void run() {
				try {
					Socket socket = new Socket(ip, port);
					BufferedWriter bufferedWriter = new BufferedWriter(
							new OutputStreamWriter(socket.getOutputStream()));
					User user = User.getInstance();
					String json = gson.toJson(user.getAuthenticationModel());
					bufferedWriter.write(json + "\ngetAllContacts\nclose\n");
					bufferedWriter.flush();
					BufferedReader bufferedReader = new BufferedReader(
							new InputStreamReader(socket.getInputStream()));
					String inputString;
					while ((inputString = bufferedReader.readLine()) != null) {
						if (inputString
								.contains("\"databaseRepresentation\":\"contact\"")) {
							Contact contact = gson.fromJson(inputString,
									Contact.class);
							setChanged();
							notifyObservers(contact);
						} else if (inputString
								.contains("\"databaseRepresentation\":\"authentication\"")) {
							// ska vara tom
						} else {
						}
					}
					bufferedReader.close();
					socket.close();
					if (inputString == null) {
						setChanged();
						notifyObservers(null);
					}
				} catch (IOException e) {
					if (iterator.hasNext()) {
						loadNextServer();
						getAllContactsReq();
					} else {
						setChanged();
						String fail = "failed to connect";
						notifyObservers(fail);
					}
				}
			}
		}).start();
	}

	public void logout() {
		
	}

}
