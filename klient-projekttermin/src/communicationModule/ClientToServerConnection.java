package communicationModule;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

import com.example.klien_projekttermin.database.Database;
import com.google.gson.Gson;
import com.nutiteq.wrappers.List;

import models.Assignment;
import models.Contact;
import models.MessageModel;
import models.ModelInterface;

import android.content.Context;
import android.text.format.Time;
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
	private Queue <String> transmissonQueue = new LinkedList<String>();
	private PrintWriter  output = null;
	private BufferedReader input = null;
	private String inputString = null;
	private Database database;
	private Gson gson = new Gson();
	private boolean sendData = false;
	private boolean connected = false;
	private boolean ContextIsReady = false; 
	private Context context = null;
	private int waitTime = 1;
	private long heartbeatTime = 0;
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
		this.transmissonQueue.add(transmisson);
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
		database = Database.getInstance(this.context);
		ContextIsReady = true;
	}
	/**
	 * väntar till reconnect, samt ökar väntetiden kontiueligt upp till en minut. 
	 */
	private synchronized void timeToWait(){
		if(waitTime < 60000){
			waitTime = waitTime+50;
		}
		try {
			this.wait(waitTime);	
		} catch (Exception e) {
			Log.e("Thread", "Wating error: " + e.toString());
		}
		
	}
	/**
	 * nollställer väntetiden till en reconnect
	 */
	private void resetTimeToWait(){
		waitTime = 1;
	}
	private boolean timeToheartbeat(long currentTime){
		
		if(heartbeatTime == 0){
			heartbeatTime = System.currentTimeMillis();
		}
		
		if(currentTime >= heartbeatTime+30000){
			heartbeatTime = System.currentTimeMillis();
			return true;
		}else {
			return false;
		}
	}
	
	public void run() {
		while(true){
			//etablerar kontakt
			try {
				requestSocet = new Socket(ServerIP,ServerPort);
				input = new BufferedReader(new InputStreamReader(requestSocet.getInputStream()));
				output = new PrintWriter(requestSocet.getOutputStream(), true);
				setConnetion(true);
			} catch (Exception e) {
				setConnetion(false);
				Log.e("Connection", ("Connection failed: " + "Time is " + Integer.toString(waitTime)));
				timeToWait();
			}
			
			while(isConnection()){
				resetTimeToWait();
				// inkommande data.
				try {
					if(input.ready() && ContextIsReady){
						inputString = input.readLine();
						Log.e("incomeing", "icomeing data");
						if (inputString.contains("\"databaseRepresentation\":\"message\"")) {
							MessageModel message = gson.fromJson(inputString, MessageModel.class);
							database.addToDB(message, this.context.getContentResolver());
//							if(CommunicationService != null){
//								CommunicationService.handelIncomeingMessage();
//							}
						}else if (inputString.contains("\"databaseRepresentation\":\"assignment\"")) {
							System.out.println(inputString);
							Assignment assignment = gson.fromJson(inputString, Assignment.class);
							System.out.println("geson here: " + assignment.getName() );
							assignment.getCameraImage();
							database.addToDB(assignment, this.context.getContentResolver());
							System.out.println("After database add");
//							if(CommunicationService != null){
//								CommunicationService.handelIncomeingAssignment();
//							}
						}else if (inputString.contains("\"databaseRepresentation\":\"contact\"")) {
							Contact contact = gson.fromJson(inputString, Contact.class);
							database.addToDB(contact, context.getContentResolver());
						}else {
							Log.e("Database input problem","Did not recognise inputtype.");
						}
					}
				} catch (Exception e) {
					Log.e("Crash in input", "inputString: " + e.toString());
				}
				// sicka data
				if(sendData && isConnection()){
					if(!this.transmissonQueue.isEmpty()){
						for (int i = 0; i < this.transmissonQueue.size(); i++) {
							output.println(this.transmissonQueue.poll());
						}
					};
					// kollar och kontrolerar fel i streamen
					if(output.checkError()){
						Log.i("output", "Transmisson failed");
						setConnetion(false);
					}
					Log.i("output", "sending Transmisson");
					sendData(false);
				}
				//Fixar heartbeat
				if(timeToheartbeat(System.currentTimeMillis()) && isConnection()){
					output.println("Heart");
					// kollar och kontrolerar fel i streamen
					if(output.checkError()){
						Log.i("output", "Hearbeat failed");
						setConnetion(false);
					}
					Log.i("output", "seading heartbeat");
				}
			}
		}
	}
}
