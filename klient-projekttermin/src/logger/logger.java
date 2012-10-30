package logger;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.widget.Toast;

public class logger {
	private final String FILENAME = "log.txt";
	FileOutputStream outputStream = null;
	Context context;
	Time timeStamp = new Time();
	
	public logger(Context context){
		this.context = context;
		try {
			outputStream = context.openFileOutput(FILENAME, context.MODE_PRIVATE);
		} catch (FileNotFoundException e) {
		}
	}
	
	
	public void writeToLog(String entry) throws FileNotFoundException{
		timeStamp.setToNow();
		String stamp = "[" + String.valueOf(timeStamp.hour) + ":" + String.valueOf(timeStamp.minute)+ ":" + String.valueOf(timeStamp.second) +"]";
		entry = (stamp + entry);
		try{
		outputStream.write(entry.getBytes());
		Log.d("writeToLog","Entry was successfull: " + entry);
		}catch(IOException e){
			Log.d("writeToLog","Entry failed: "+ entry + "due to" + e.toString());
		}
	}
	
	public String readFromLog(){
		String line = null;
		String read = null;
		try {
			InputStream inputStream = context.openFileInput(FILENAME);
			if(inputStream != null ){
				InputStreamReader input = new InputStreamReader(inputStream);
				BufferedReader bufferdReader = new BufferedReader(input);
				read = " ";
				while((line = bufferdReader.readLine()) != null){
					read = read + line;
				}
				inputStream.close();
				Log.d("readFromLog",read);
				return read;
			}
		} catch (Exception e) {
			Toast.makeText(context.getApplicationContext(),e.toString() +   e.getMessage(),Toast.LENGTH_SHORT).show();
		}
		return null;
	}
	
}