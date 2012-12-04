package logger;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.Context;
import android.text.format.Time;
import android.util.Log;
import android.widget.Toast;
/**
 * En Loggarklass som skriver och läser logginlägg till en textfil
 * @author Eric Karlsson
 *
 */
public class logger {
	private String FILENAME = "";
	FileOutputStream outputStream = null;
	Context context;
	Time timeStamp = new Time();
	
	/**
	 * Loggerns konstruktor, när objektet skapas öppnas en  ”outputstream” till filen.
	 * @param context Android contexten som används för att skapa filer.
	 * @param logName Namnent på logfilen som kommer att användas, om en fil med detta namn inte finns kommer den att skapas.
	 */
	public logger(Context context,String logName){
		this.context = context;
		this.FILENAME = logName;
		try {
			outputStream = context.openFileOutput(FILENAME, Context.MODE_APPEND);
		} catch (FileNotFoundException e) {
			// Detta bör aldrig hända.
		}
	}
	/**
	 * 
	 * @param userTag Användarens eller enhetens namn, om logginlägget inte ska stämplas med en användarstämpel skicka in NULL 
	 * @param entry Logginlägget
	 */
	public void writeToLog(String userTag,String entry) {
		// Tiden vid inlägget, sparas som en läsbar string stämpel.
		timeStamp.setToNow();
		String stamp = "[" + String.valueOf(timeStamp.hour) + ":" 
		+ String.valueOf(timeStamp.minute)+ ":" + String.valueOf(timeStamp.second) +"]";
		// om användarstämpel används.
		if(userTag != null){
			stamp = stamp + "[" + userTag + "]";	
		}
		
		entry = (stamp + entry + "\n");
		try{
		outputStream.write(entry.getBytes());
		}catch(IOException e){
			Log.d("writeToLog","Entry failed: "+ entry + "due to" + e.toString());
		}
		
	}
	/**
	 * Läser loggen
	 * @return Retunerar loggen som en sträng
	 */
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
					read = read + "\n"+ line;
				}
				inputStream.close();
				return read;
			}
		} catch (Exception e) {
			Toast.makeText(context.getApplicationContext(),e.toString() +   e.getMessage(),Toast.LENGTH_SHORT).show();
		}
		return null;
	}
	/**
	 * Rensar loggen och börjar lägga in logginlägg i toppen av loggen. 
	 * 
	 */
	public void clearLog(){
		try {
			outputStream.close();
			context.deleteFile(FILENAME);
			outputStream = context.openFileOutput(FILENAME, Context.MODE_APPEND);
		} catch (Exception e) {
		}	
	}
	
}