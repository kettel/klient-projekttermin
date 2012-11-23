package com.klient_projekttermin;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import com.google.gson.Gson;

import database.Database;

import models.Assignment;
import models.Contact;
import models.MessageModel;

import android.content.Context;
import android.util.Log;

public class DataReceiver extends Thread{
	private Context context;
	private Socket socket;
	private String ServerIP = "94.254.72.38";
	private int ServerPort = 17234;
	private BufferedReader input = null;
	private PrintWriter  output = null;
	private String inputString = null;
	private Database database;
	private Gson gson = new Gson();
	private boolean connecton = false;
	
	public DataReceiver(Context context){
		System.out.println("DataReceiver is konstructing");
		this.context = context;
		database = Database.getInstance(this.context);
		try {
			this.socket = new Socket(ServerIP,ServerPort);
		} catch (Exception e) {
			Log.e("DataReciver", "Error in setting up socket due to " + e.toString());
		}
	}
	
	public void run(){
		try {
			input = new BufferedReader(new InputStreamReader(socket.getInputStream()));	
			output = new PrintWriter(socket.getOutputStream(), true);
			connecton = true;
		} catch (Exception e) {
			Log.e("DataReciver", "Error in setting streams due to " + e.toString());
		}
		while(connecton){
			System.out.println("connection");
			try {
				if(input.ready()){
					inputString = input.readLine();
					Log.e("DataReceiver", "icomeing data");
					if (inputString.contains("\"databaseRepresentation\":\"message\"")) {
						MessageModel message = gson.fromJson(inputString, MessageModel.class);
						database.addToDB(message, this.context.getContentResolver());
					}else if (inputString.contains("\"databaseRepresentation\":\"assignment\"")) {
						Assignment assignment = gson.fromJson(inputString, Assignment.class);
						assignment.getCameraImage();
						database.addToDB(assignment, this.context.getContentResolver());
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
			if(output.checkError()){
				Log.i("output", "Stream is down");
			}
		}
	}
	
}
