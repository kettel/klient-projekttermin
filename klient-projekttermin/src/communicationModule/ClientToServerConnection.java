package communicationModule;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import com.google.gson.Gson;
import com.nutiteq.wrappers.List;

import models.Assignment;
import models.Contact;
import models.MessageModel;
import models.ModelInterface;

import database.Database;
import android.content.Context;
import android.util.Log;

/**
 * En klass som sköter sickandet och motagndet av data mellan klienten och serven.
 * @author Eric
 *
 */

public class ClientToServerConnection extends Thread  {

	private String ServerIP = "94.254.72.38";
	private int ServerPort = 17234;
	private Socket requestSocet =  null;
	private String transmisson = null;
	private PrintWriter  output = null;
	private BufferedReader input = null;
	private String inputString = null;
	private Database database = new Database();
	private Gson gson = new Gson();
	private boolean sendData = false;
	private boolean connected = false;
	private boolean ContextIsReady = false; 
	private Context context = null;
	private int waitTime = 0;
	
	/**
	 * en tom konstruktor
	 */
	public ClientToServerConnection(){
		
	}
	/**
	 * Används för att förhindra att data sickas 
	 * @param enabel
	 */
	private synchronized void sendData(boolean enabel){
		sendData = enabel;
	}
	/**
	 * används av CommunicationService för att sicka Gson strängar.
	 * @param transmisson en Gson sträng
	 */
	public synchronized void sendTransmisson(String transmisson){
		this.transmisson = transmisson;
		sendData = true;
	}
	/**
	 * Om en kontakt med serven kan etablras sätts denna till true. 
	 * @param enabel 
	 */
	private synchronized void setConnetion(boolean enabel){
		connected = enabel;
	}
	/**
	 * jag tror ni fattar
	 * @return connected.
	 */
	public synchronized boolean isConnection(){
		return connected;
	}
	/**
	 * För att kunna använda databasen behövs applikationens context. 
	 * @param context  använd getApplicationContext()
	 */
	public synchronized void setContext(Context context){
		this.context = context;
		ContextIsReady = true;
	}
	
	public void run() {
		
		while(true){
			//etaberar kontakt
			try {
				requestSocet = new Socket(ServerIP,ServerPort);
				input = new BufferedReader(new InputStreamReader(requestSocet.getInputStream()));
				output = new PrintWriter(requestSocet.getOutputStream(), true);
				setConnetion(true);
			} catch (Exception e) {
				setConnetion(false);
				Log.e("Connection", ("Error: " + e.toString()));
				try {
					waitTime = waitTime+1;
					this.wait(waitTime);
				} catch (Exception e2) {
					Log.e("Thread", "Wating error: " + e2.toString());
				}
				
			}
			
			while(isConnection()){
				// inkommande data.
				try {
					if(input.ready() && ContextIsReady){
						inputString = input.readLine();
						Log.i("incomeing", inputString);
						if (inputString.contains("\"databaseRepresentation\":\"message\"")) {
							MessageModel message = gson.fromJson(inputString, MessageModel.class);
							database.addToDB(message, this.context);
						}else if (inputString.contains("\"databasetRepresentation\":\"assignment\"")) {
							Assignment assignment = gson.fromJson(inputString, Assignment.class);
							database.addToDB(assignment, this.context);
						}else if (inputString.contains("\"databasetRepresentation\":\"contact\"")) {
							Contact contact = gson.fromJson(inputString, Contact.class);
							database.addToDB(contact, context);
						}else {
							Log.e("Database input problem","Did not recognise inputtype.");
						}
					}
				} catch (Exception e) {
					Log.e("Crash in input", "inputString: " + e.toString());
				}
				// sicka data
				if(sendData && isConnection()){
					output.println(transmisson);
					// Checks and handels errors in the stream
					if(output.checkError()){
						Log.i("output", "Transmisson failed");
						setConnetion(false);
					}
					Log.i("output", "sending Transmisson");
					sendData(false);
				}
			}
		}
	}
}