package communicationModule;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import login.User;
import models.Assignment;
import models.AssignmentStatus;
import models.AuthenticationModel;
import models.Contact;
import models.MessageModel;
import models.MessageStatus;
import models.ModelInterface;
import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.klient_projekttermin.CommonUtilities;

import database.Database;

/**
 * VAR VÄNLIG RÖR EJ! Vid användande av denna klass bör du sätta en
 * PullResponseHandler som observer innan du utför en anslutning. Var även
 * försiktig vid användandet av denna klass då varje ny anslutning tar väldigt
 * mycket kraft.
 * 
 * @author lundmark
 * 
 */

public class SocketConnection extends Observable {
	private Gson gson = new Gson();
	private String ip = "94.254.72.38";
	private int port = 17234;
	private ArrayList<String[]> servers = new ArrayList<String[]>();
	Iterator<String[]> iterator;
	private int tries = 0;
	private boolean failedToConnect = false;
	private Context context = null;
	private Database db;

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
		/**
		 * För att lägga till en server, skapa en String array med föjande
		 * utseende: [0]=ip,[1]=serverport,[2]=jettyport
		 * 
		 * Nedan följer servrarna:
		 */
		String[] i = { "94.254.72.38", "17234", "16783" };
		servers.add(i);
		String[] j = { "94.254.72.38", "18234", "17783" };
		servers.add(j);
		/**
		 * --Slut på serverlistan--
		 */
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
				if (failedToConnect) {
					addToQue(model);
				} else {
					String que = getQue();
					if (!que.equals("")) {
						System.out.println("QUE not empty : ");
						sendJSON(que);
					} else {
						System.out.println("Que is empty");
					}
				}
			}
		}).start();
	}

	private String getQue() {
		db = Database.getInstance(context);
		StringBuilder sb = new StringBuilder();
		List<ModelInterface> assignments = db.getAllFromDB(new Assignment(),
				context.getContentResolver());
		for (ModelInterface m : assignments) {
			if (((Assignment) m).getAssignmentStatus() == AssignmentStatus.QUE) {
				((Assignment) m)
						.setAssignmentStatus(AssignmentStatus.NOT_STARTED);
				db.updateModel(m, context.getContentResolver());
				sb.append(gson.toJson(m) + "\n");
			}
		}
		List<ModelInterface> messages = db.getAllFromDB(new MessageModel(),
				context.getContentResolver());
		for (ModelInterface m : messages) {
			System.out.println("STATUS " + ((MessageModel) m).getStatus());
			if (((MessageModel) m).getStatus() == MessageStatus.QUE) {
				((MessageModel) m).setStatus(MessageStatus.SENT);
				db.updateModel(m, context.getContentResolver());
				sb.append(gson.toJson(m) + "\n");
			}
		}
		return sb.toString();
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

	/**
	 * Skapar en ny socketanslutning och skriver en sträng på denna
	 * 
	 * @param json
	 *            - En sträng med det som ska skickas
	 */
	private void sendJSON(String json) {
		SSLSocket socket = createSocket();
		System.out.println("Skickar detta " + json);
		if (socket != null) {
			writeToSocket(socket, json + "\n");
			closeSocket(socket);
		}

	}

	/**
	 * Laddar in nästa server. Server addressen samt portar finnsi arrayen. Ip
	 * på index 0, port på index 1 och jettyport på index 2
	 */
	private void loadNextServer() {
		if (iterator.hasNext()) {
			String[] server = iterator.next();
			// System.out.println("byter port: " + server[1]);
			ip = server[0];
			port = Integer.parseInt(server[1]);
			CommonUtilities.SERVER_URL = "http://" + server[0] + ":"
					+ server[2];
		} else {
			/**
			 * Nått slutet på listan så laddar om dessa igen.
			 */
			try {
				/**
				 * När man laddat om listan 5 gånger och inte lyckats inser man
				 * sig besegrad o lägger ner.
				 */
				if (tries < 5) {
					// System.out.println("reload servers");
					tries++;
					Thread.sleep(100);
					iterator = servers.iterator();
					// System.out.println(iterator.hasNext());
				} else {
					/**
					 * 5 försök gjorda, lägger ner.
					 */
					failedToConnect = true;
					setChanged();
					notifyObservers("failed");
				}
			} catch (InterruptedException e) {
				// System.out
				// .println("Omladdning av serverlistan i loadNextServer i SocketConnection sket sig");
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
		SSLSocket socket = createSocket();
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
		// System.out.println("Sending pullrequest");
		new Thread(new Runnable() {

			public void run() {
				SSLSocket socket = createSocket();
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

				SSLSocket socket = createSocket();
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

	private void readSocket(SSLSocket socket) {
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
					// System.out.println("Did not recognize model: "
					// + inputString);
				}
			}
			bufferedReader.close();
			/**
			 * Meddelar pullRequestHandler, om satt, att hämtningen är klar.
			 */
			setChanged();
			notifyObservers(null);
		} catch (JsonSyntaxException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void writeToSocket(SSLSocket socket, String string) {
		try {
			BufferedWriter bufferedWriter = new BufferedWriter(
					new OutputStreamWriter(socket.getOutputStream()));
			bufferedWriter.write(string);
			bufferedWriter.flush();
			// System.out.println("Lyckades skriva till server");
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private SSLSocket createSocket() {
		SSLSocket socket = null;
		SSLSocketFactory socketFactory = null;
		char keystorepass[] = "password".toCharArray();
		char trustpassword[] = "password".toCharArray();
		if (context != null) {
			// System.out.println("Börjar med krypteringsdelen");
			KeyStore keyStore = null;
			try {
				keyStore = KeyStore.getInstance("BKS");
				InputStream trustin = this.context.getAssets().open(
						"clienttruststore.bks");
				keyStore.load(trustin, trustpassword);

				KeyStore ks = KeyStore.getInstance("BKS");
				InputStream keyin = this.context.getAssets().open("client.bks");
				ks.load(keyin, keystorepass);
				// System.out.println("keystore klar");
				TrustManagerFactory tmf = TrustManagerFactory
						.getInstance(TrustManagerFactory.getDefaultAlgorithm());
				tmf.init(keyStore);

				KeyManagerFactory kmf = KeyManagerFactory
						.getInstance(KeyManagerFactory.getDefaultAlgorithm());
				kmf.init(ks, keystorepass);
				// System.out.println("alla masters klara");
				SSLContext sslCtx = SSLContext.getInstance("TLS");
				sslCtx.init(kmf.getKeyManagers(), tmf.getTrustManagers(),
						new SecureRandom());
				// System.out.println("sslcontext klart");
				socketFactory = sslCtx.getSocketFactory();
				System.out.println("trying to connect, ip is: " + ip
						+ " port is: " + port);
				do {
					try {
						socket = (SSLSocket) socketFactory.createSocket(ip,
								port);
						socket.startHandshake();
						// System.out.println("Socketen lyckades ansluta");
					} catch (UnknownHostException e) {
						loadNextServer();
					} catch (IOException e) {
						loadNextServer();
					}

				} while (socket == null && !failedToConnect);
			} catch (KeyStoreException e) {
				e.printStackTrace();
			} catch (IOException e) {
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
		} else {
			System.out
					.println("Saknar context till krypteringen, du bör verkligen undersöka varför inget context är satt eller varför detta är null!!");
		}
		return socket;
	}

	private void addToQue(ModelInterface model) {
		System.out.println("Add to Que");
		db = Database.getInstance(context);
		if (model.getDatabaseRepresentation().equalsIgnoreCase("assignment")) {
			System.out.println("Add assignment to que");
			((Assignment) model).setAssignmentStatus(AssignmentStatus.QUE);
			db.updateModel(model, context.getContentResolver());
		} else if (model.getDatabaseRepresentation()
				.equalsIgnoreCase("message")) {
			System.out.println("Add message to que");
			((MessageModel) model).setStatus(MessageStatus.QUE);
			db.updateModel(model, context.getContentResolver());
		}
	}

	private void closeSocket(SSLSocket socket) {
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void logout() {
		new Thread(new Runnable() {
			public void run() {
				SSLSocket socket = createSocket();
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
	 * Setter för context som krävs för varje anslutning.
	 * 
	 * @param context
	 */
	public synchronized void setContext(Context context) {
		this.context = context;
	}

}
