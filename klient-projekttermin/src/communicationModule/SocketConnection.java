package communicationModule;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Observable;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import login.User;
import models.Assignment;
import models.AuthenticationModel;
import models.Contact;
import models.MessageModel;
import models.ModelInterface;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.klient_projekttermin.CommonUtilities;

public class SocketConnection extends Observable {
	private Gson gson = new Gson();
	private String ip = "94.254.72.38";
	private int port = 17234;
	private ArrayList<String[]> servers = new ArrayList<String[]>();
	Iterator<String[]> iterator;
	private int tries = 0;
	private boolean failedToConnect = false;
	private Context context;
	private boolean ContextIsReady = false;

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
		Socket socket = createSocket();
		if (socket != null) {
			writeToSocket(socket, json + "\n");
			closeSocket(socket);
		}

	}

	/**
	 * Laddar in nästa server. Server addressen samt portar finnsi arrayen.Ip på
	 * index 0, port på index 1 och jettyport på index 2
	 */
	private void loadNextServer() {
		if (iterator.hasNext()) {
			String[] server = iterator.next();
			System.out.println("byter port: " + server[1]);
			ip = server[0];
			port = Integer.parseInt(server[1]);
			CommonUtilities.SERVER_URL = "http://" + server[0] + ":"
					+ server[2];
		} else {
			try {
				if (tries < 5) {
					System.out.println("reload servers");
					tries++;
					Thread.sleep(100);
					iterator = servers.iterator();
					System.out.println(iterator.hasNext());
				} else {
					failedToConnect = true;
					setChanged();
					notifyObservers("failed");
				}
			} catch (InterruptedException e) {
				System.out
						.println("Omladdning av serverlistan i loadNextServer i SocketConnection sket sig");
				e.printStackTrace();
			}
		}

	}

	/**
	 * Skapar en anslutning o skickar strängen till servern
	 * 
	 * @param json
	 *            - Strängen
	 */
	private void sendAuthentication(String json) {
		Socket socket = createSocket();
		if (socket != null) {
			writeToSocket(socket, json + "\nclose\n");
			readSocket(socket);
			closeSocket(socket);
		} else {
			setChanged();
			notifyObservers(json);
		}
	}

	public void pullFromServer() {
		System.out.println("Sending pullrequest");
		new Thread(new Runnable() {

			public void run() {
				Socket socket = createSocket();
				if (socket != null) {
					User user = User.getInstance();
					String json = gson.toJson(user.getAuthenticationModel());
					writeToSocket(socket, json + "\npull\nclose\n");
					readSocket(socket);
					closeSocket(socket);
				}

			}
		}).start();
	}

	public void getAllContactsReq() {
		new Thread(new Runnable() {

			public void run() {

				Socket socket = createSocket();
				if (socket != null) {
					User user = User.getInstance();
					String json = gson.toJson(user.getAuthenticationModel());
					writeToSocket(socket, json + "\ngetAllContacts\nclose\n");
					readSocket(socket);
					closeSocket(socket);
				}
			}
		}).start();
	}

	private void readSocket(Socket socket) {
		try {
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
					Contact contact = gson.fromJson(inputString, Contact.class);
					setChanged();
					notifyObservers(contact);
				} else if (inputString
						.contains("\"databaseRepresentation\":\"authentication\"")) {
					setChanged();
					AuthenticationModel authenticationModel = gson.fromJson(
							inputString, AuthenticationModel.class);
					notifyObservers(authenticationModel);
				} else {
					System.out.println("Did not recognize model: "
							+ inputString);
				}
			}
			bufferedReader.close();
			setChanged();
			notifyObservers(null);
		} catch (JsonSyntaxException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void writeToSocket(Socket socket, String string) {
		try {
			BufferedWriter bufferedWriter = new BufferedWriter(
					new OutputStreamWriter(socket.getOutputStream()));
			bufferedWriter.write(string);
			bufferedWriter.flush();
			System.out.println("Lyckades skriva till server");
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private Socket createSocket() {
		SSLSocket socket = null;
		char keystorepass[] = "password".toCharArray();
		char trustpassword[] = "password".toCharArray();
		do {
			if (ContextIsReady == true) {
				try {
					System.out.println("här kommer krypto");
					KeyStore ts = KeyStore.getInstance("BKS");
					InputStream trustin = this.context.getAssets().open(
							"clienttruststore.bks");
					ts.load(trustin, trustpassword);

					KeyStore ks = KeyStore.getInstance("BKS");
					InputStream keyin = this.context.getAssets().open(
							"client.bks");
					ks.load(keyin, keystorepass);
					System.out.println("keystore klar");
					TrustManagerFactory tmf = TrustManagerFactory
							.getInstance(TrustManagerFactory
									.getDefaultAlgorithm());
					tmf.init(ts);

					KeyManagerFactory kmf = KeyManagerFactory
							.getInstance(KeyManagerFactory
									.getDefaultAlgorithm());
					kmf.init(ks, keystorepass);
					System.out.println("alla masters klara");
					SSLContext sslCtx = SSLContext.getInstance("TLS");
					sslCtx.init(kmf.getKeyManagers(), tmf.getTrustManagers(),
							new SecureRandom());
					System.out.println("sslcontext klart");
					SSLSocketFactory socketFactory = sslCtx.getSocketFactory();
					System.out.println("trying to connect, ip is: " + ip
							+ " port is: " + port);

					socket = (SSLSocket) socketFactory.createSocket(ip, port);
					socket.startHandshake();
					System.out.println("Socketen lyckades ansluta");
					failedToConnect = true;
					
					// Sov en halv sekund för att avlasta appen
					synchronizedWait(this);
				} catch (UnknownHostException e) {
					loadNextServer();

				} catch (IOException e) {
					loadNextServer();
				} catch (KeyStoreException e) {
					e.printStackTrace();
				} catch (NoSuchAlgorithmException e) {
					e.printStackTrace();
				} catch (CertificateException e) {
					e.printStackTrace();
				} catch (UnrecoverableKeyException e) {
					e.printStackTrace();
				} catch (KeyManagementException e) {
					e.printStackTrace();
				}
			}
		} while (socket == null && !failedToConnect);
		return socket;
	}
	
	private synchronized void synchronizedWait (SocketConnection socketConnection) {
		try {
			socketConnection.wait(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void closeSocket(Socket socket) {
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void logout() {
		new Thread(new Runnable() {
			public void run() {
				Socket socket = createSocket();
				if (socket != null) {
					User user = User.getInstance();
					String json = gson.toJson(user.getAuthenticationModel());
					writeToSocket(socket, json + "\n" + "logout\nclose\n");
					closeSocket(socket);
					user.setLoggedIn(false);
				}

			}
		}).start();
	}

	/**
	 * Hämtar ApplicationContext från aktiviteten som startar SocketContection
	 * 
	 * @param context
	 */
	public synchronized void setContext(Context context) {
		this.context = context;
		ContextIsReady = true;

	}

}
