package communicationModule;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import models.Assignment;
import models.Contact;
import models.MessageModel;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import database.Database;

/**
 * Tråden för en enskild anslutning till servern
 * 
 * @author kristoffer & nikola
 * 
 */
public class MultiServerThread extends Thread {

	private Socket socket = null;
	private BufferedReader input = null;
	private String inputLine;

	/**
	 * Konstruktorn, tar emot en socket för porten vi lyssnar på
	 * 
	 * @param socket Den socket som anslutningen sker genom
	 */
	
	public MultiServerThread(Socket socket) {
		super("MultiServerThread");
		this.socket = socket;
		System.out.println("multiconstruct!");
	}

	/**
	 * Koden för den tråd som skapas för en ny anslutning.
	 */
	public void run() {
		System.out.println("IN RUN");

		try {
			// Buffrar ihop flera tecken från InputStreamen till en sträng
			input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			// Läser den buffrade strängen
			inputLine = input.readLine();

			// Trace: Ett meddelande/assingment/kontakt har tagits emot.
			System.out.println(socket.getInetAddress() + ": " + inputLine);

			// Bestämmer vilken typ av input som kommer in. När det avgjorts
			// sparas och/eller skickas input:en vidare.
			handleTypeOfInput(inputLine);

			// Stänger buffern
			input.close();
			// Stänger anslutningen
			socket.close();

		} catch (IOException e) {
			System.out.println(e);
		}
	}

	/**
	 * Bestämmer vilken typ av objekt som kommer in och hanterar den efter
	 * preferenser den anger.
	 * 
	 * @param br
	 *            Den buffrade strängen.
	 */
	private void handleTypeOfInput(String input) {
		System.out.println("TEEEEEEEEEEEEEEST" + input);
		
		//
//		//String inputType = input.substring(27, 34);
//
//		Database db = new Database();
//
//		if (input.contains("\"databaseRepresentation\":\"message\"")) {
//			System.out
//					.println("Sending message to database and/or forwarding it.");
//			
//			// Gson konverterar json-strängen till MessageModel-objektet igen
//			// och handleMsg skickar och sparar meddelandet.
//			MessageModel msg = (new Gson()).fromJson(input, MessageModel.class);
//			// Lägger in meddelandet i databasen
////			db.addToDB(msg );
//
//		} else if (input.contains("\"databasetRepresentation\":\"assignment\"")) {
//			// Spara och/eller skicka vidare uppdraget.
//
//			// Gson konverterar json-strängen till Assignment-objektet igen.
//			Assignment assignmentFromJson = (new Gson()).fromJson(input,
//					Assignment.class);
//			// Lägger in kontakten i databasen
////			db.addToDB(assignmentFromJson);
//
//			// *****Skicka vidare till enhet och databas!***********
//		} else if (input.contains("\"databasetRepresentation\":\"contact\"")) {
//			// Spara och/eller skicka vidare uppdraget.
//
//			// Gson konverterar json-strängen till MessageModel-objektet igen.
//			Contact contactFromJson = (new Gson()).fromJson(input,
//					Contact.class);
//			// Lägger in uppdraget i databasen
////			db.addToDB(contactFromJson);
//
//			// *****Skicka vidare till enhet och databas!***********
//		} else
//			System.out.println("Did not recognise inputtype.");
	}

}