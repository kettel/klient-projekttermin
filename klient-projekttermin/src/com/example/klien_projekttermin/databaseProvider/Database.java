package com.example.klien_projekttermin.databaseProvider;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import models.Assignment;
import models.AssignmentStatus;
import models.Contact;
import models.MessageModel;
import models.ModelInterface;
import net.sqlcipher.database.SQLiteDatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

/**
 * En klass med metoder för Create, Remove, Update, Delete (CRUD) operationer på
 * databasen.
 * 
 * @author kettel
 * 
 */
public class Database {
	// Icke-använd URI för senast tillagda meddelande...
	private Uri messUri;

	// Programkontexten som databasen ska köras i
	private static Context context = null;

	// Databasen som klassen ska använda
	private static SQLiteDatabase database = null;

	// Lösenord för databasen
	public static String PASSWORD = "password";

	// ID-sträng för alla databaser.
	protected static String KEY_ID = "_id";

	// Ifall SQLCipher har laddat in sina externa .so-bibliotek
	private static boolean isLibraryLoaded = false;

	private Database() {
	}

	private static Database instance = new Database();

	/**
	 * Hämta instans av Databasen.
	 * 
	 * @param c
	 *            Context Applikationskontexten.
	 * @return Database Instans av databasen.
	 */
	public static Database getInstance(Context c) {
		// Om kontexten redan är skapad (dvs om instansen hämtas efter att den
		// är skapad)
		if (context == null) {
			context = c;
		}

		// Filen var från början en klassvariabel, kändes dock inte om att det
		// behövs
		File dbFile = context.getDatabasePath("tddd36.db");

		// Om databasfilen inte existerar, skapa den
		if (!dbFile.exists()) {
			dbFile.mkdirs();
			dbFile.delete();
		}

		// Ladda vid behov in SQLCipher-bibliotek filer
		if (!isLibraryLoaded) {
			SQLiteDatabase.loadLibs(context);
			isLibraryLoaded = true;
		}

		// Initiera databasen för denna singleton
		database = SQLiteDatabase.openOrCreateDatabase(dbFile, PASSWORD, null);
		return instance;
	}

	/**
	 * Lägg till ett uppdrag/kontakt/meddelande till rätt databas
	 * 
	 * @param m
	 *            ModellInterface av objekt som ska läggas till
	 * @param context
	 *            Aktivitetens kontext så data läggs i rätt databas
	 */
	public void addToDB(ModelInterface m, Context c) {
		String dbRep = m.getDatabaseRepresentation();
		if (dbRep.equalsIgnoreCase("assignment")) {
			Assignment ass = (Assignment) m;

			ContentValues values = new ContentValues();
			
			String agents = new String();
			List<Contact> receivers = ass.getAgents();
			for (Contact contact : receivers) {
				agents.concat(contact.getContactName() + "/");
			}
			
			values.put(AssignmentTable.COLUMN_ASSIGNMENTDESCRIPTION,
					ass.getAssignmentDescription());
			values.put(AssignmentTable.COLUMN_ASSIGNMENTSTATUS,
					ass.getAssignmentStatus().toString());
			values.put(AssignmentTable.COLUMN_CAMERAIMAGE,
					bitmapToByteArray(ass.getCameraImage()));
			values.put(AssignmentTable.COLUMN_LAT, Double.toString(ass.getLat()));
			values.put(AssignmentTable.COLUMN_LON, Double.toString(ass.getLon()));
			values.put(AssignmentTable.COLUMN_NAME, ass.getName());
			values.put(AssignmentTable.COLUMN_SENDER, ass.getSender());
			values.put(AssignmentTable.COLUMN_AGENTS, agents);
			values.put(AssignmentTable.COLUMN_SITENAME, ass.getSiteName());
			values.put(AssignmentTable.COLUMN_STREETNAME, ass.getStreetName());
			values.put(AssignmentTable.COLUMN_TIMESPAN, ass.getTimeSpan());
			Uri assUri = context.getContentResolver().insert(
					DatabaseContentProviderAssignments.CONTENT_URI, values);
			Log.d("DB", "AssignmentURI: " + assUri);
		} else if (dbRep.equalsIgnoreCase("contact")) {
			Contact contact = (Contact) m;
			ContentValues values = new ContentValues();
			values.put(ContactsTable.COLUMN_CONTACT_NAME,
					contact.getContactName());

			Uri contactUri = context.getContentResolver().insert(
					com.example.klien_projekttermin.databaseProvider.Contact.Contacts.CONTENT_URI, values);
			Log.d("DB", "ContactURI: " + contactUri);
		} else if (dbRep.equalsIgnoreCase("message")) {
			MessageModel mess = (MessageModel) m;
			String content = mess.getMessageContent().toString();
			String receiver = mess.getReciever().toString();
			String timestamp = Long.toString(Calendar.getInstance()
					.getTimeInMillis());

			ContentValues values = new ContentValues();
			values.put(MessageTable.COLUMN_CONTENT, content);
			values.put(MessageTable.COLUMN_RECEIVER, receiver);
			values.put(MessageTable.COLUMN_TIMESTAMP, timestamp);
			values.put(MessageTable.COLUMN_ISREAD, "FALSE");

			// Nytt meddelande
			// messUri borde väl egentligen kunna returneras för enkel åtkomst?
			messUri = context.getContentResolver().insert(
					DatabaseContentProviderMessages.CONTENT_URI, values);
			Log.d("DB", "MeddelandeURI: " + messUri);
		}

	}

	/**
	 * Räkna antal poster i vald databas
	 * 
	 * @param m
	 *            datatypen för den databas som ska räknas samman
	 * @param context
	 *            programkontexten så rätt databas kan väljas
	 * @return
	 */
	public int getDBCount(ModelInterface m, Context c) {
		String dbRep = m.getDatabaseRepresentation();
		int returnCount = 0;
		if (dbRep.equalsIgnoreCase("assignment")) {
			// SELECT * WHERE _id IS NOT null
			Cursor cursor = context.getContentResolver().query(
					DatabaseContentProviderAssignments.CONTENT_URI, null,
					Database.KEY_ID + " IS NOT null", null, null);
			returnCount = cursor.getCount();
			cursor.close();
		} else if (dbRep.equalsIgnoreCase("contact")) {
			// SELECT * WHERE _id IS NOT null
			Cursor cursor = context.getContentResolver().query(
					com.example.klien_projekttermin.databaseProvider.Contact.Contacts.CONTENT_URI, null,
					Database.KEY_ID + " IS NOT null", null, null);
			returnCount = cursor.getCount();
			cursor.close();
		} else if (dbRep.equalsIgnoreCase("message")) {
			// SELECT * WHERE _id IS NOT null
			Cursor cursor = context.getContentResolver().query(
					DatabaseContentProviderMessages.CONTENT_URI, null,
					Database.KEY_ID + " IS NOT null", null, null);
			returnCount = cursor.getCount();
			cursor.close();
		}
		return returnCount;
	}

	/**
	 * Ta bort ett objekt från databasen
	 * 
	 * @param m
	 *            ModelInterface Det objekt som önskas tas bort
	 * @param context
	 */
	public void deleteFromDB(ModelInterface m, Context context) {
		String dbRep = m.getDatabaseRepresentation();
		int deleted = 0;
		if (dbRep.equalsIgnoreCase("assignment")) {
			deleted = context.getContentResolver().delete(
					DatabaseContentProviderAssignments.CONTENT_URI,
					Database.KEY_ID + " = " + Long.toString(m.getId()), null);
			// Ta bort alla uppdrag.. -> null, null *host*
			// deleted =
			// context.getContentResolver().delete(DatabaseContentProviderAssignments.CONTENT_URI,
			// null, null);
		} else if (dbRep.equalsIgnoreCase("contact")) {
			deleted = context.getContentResolver().delete(
					com.example.klien_projekttermin.databaseProvider.Contact.Contacts.CONTENT_URI,
					Database.KEY_ID + " = " + Long.toString(m.getId()), null);
		} else if (dbRep.equalsIgnoreCase("message")) {
			deleted = context.getContentResolver().delete(
					DatabaseContentProviderMessages.CONTENT_URI,
					Database.KEY_ID + " = " + Long.toString(m.getId()), null);
		}
		// Logga antal borttagna element?
		// Eller varna/logga när man tagit bort mer än 1 element?
		// Log.d("DB", "Deleted: " + deleted + " posts from " +
		// m.getDatabaseRepresentation());
	}

	/**
	 * Hämta alla poster i databasen för inskickad modell.
	 * 
	 * @param m
	 *            ModelInterface Modellen styr från vilken databas data hämtas
	 * @param context
	 * @return List<ModelInterface> Alla objekt från vald databas
	 */
	public List<ModelInterface> getAllFromDB(ModelInterface m, Context context) {
		String dbRep = m.getDatabaseRepresentation();
		List<ModelInterface> returnList = new ArrayList<ModelInterface>();
		if (dbRep.equalsIgnoreCase("assignment")) {
			Cursor cursor = context.getContentResolver().query(
					DatabaseContentProviderAssignments.CONTENT_URI, null,
					Database.KEY_ID + " IS NOT null", null, null);
			// Loopa igenom alla rader och lägg till dem i listan
			if (cursor.moveToFirst()) {
				do {
					// Konvertera BLOB -> Bitmap
					// Skriv metod... Eller hjälpklass?
					byte[] image = cursor.getBlob(9);
					ByteArrayInputStream imageStream = new ByteArrayInputStream(
							image);
					Bitmap theImage = BitmapFactory.decodeStream(imageStream);
					// Gör om strängar med agenter på uppdrag till en lista
					List <Contact> agents = new ArrayList<Contact>();
					String[] agentArray = cursor.getString(5).split("/");
					for (String agent : agentArray) {
						agents.add(new Contact(agent));
					}
					
					Assignment assignment = new Assignment(Long.valueOf(cursor
							.getString(0)), // id från DB
							cursor.getString(1), // name
							Long.parseLong(cursor.getString(2)), // lat
							Long.parseLong(cursor.getString(3)), // lon
							cursor.getString(4),// region
							agents, // agents
							cursor.getString(6), // sender
							Boolean.parseBoolean(cursor.getString(7)), // isExternalMission
							cursor.getString(8), // assDesc
							cursor.getString(9), // timeSpan
							AssignmentStatus.valueOf(cursor.getString(10)), // assStatus
							theImage, // cameraImage
							cursor.getString(12), // streetName
							cursor.getString(13), // siteName
							Long.valueOf(cursor.getString(14))); // timeStamp

					returnList.add(assignment);
				} while (cursor.moveToNext());
			}
			cursor.close();
		} else if (dbRep.equalsIgnoreCase("contact")) {
			Cursor cursor = context.getContentResolver().query(
					com.example.klien_projekttermin.databaseProvider.Contact.Contacts.CONTENT_URI, null,
					Database.KEY_ID + " IS NOT null", null, null);
			if (cursor.moveToFirst()) {
				do {
					Contact contact = new Contact(
	                		Long.valueOf(cursor.getString(0)),
	                		cursor.getString(1));

					returnList.add(contact);
				} while (cursor.moveToNext());
			}
			cursor.close();
		} else if (dbRep.equalsIgnoreCase("message")) {
			Cursor cursor = context.getContentResolver().query(
					DatabaseContentProviderMessages.CONTENT_URI, null,
					Database.KEY_ID + " IS NOT null", null, null);
			// Loopa igenom alla rader och lägg till dem i listan
			if (cursor.moveToFirst()) {
				do {
					MessageModel message = new MessageModel(Long.valueOf(cursor.getString(0)),
							cursor.getString(1),
							cursor.getString(2),
							cursor.getString(3),
							Long.valueOf(cursor.getString(4)),
							Boolean.valueOf(cursor.getString(5)));

					returnList.add(message);
				} while (cursor.moveToNext());
			}
			cursor.close();
		}
		return returnList;
	}

	/**
	 * Uppdatera värden för ett objekt i databasen
	 * 
	 * @param m
	 *            ModelInterface Det uppdaterade objektet (OBS! Måste ha samma
	 *            Id-nummer som det objekt det ska ersätta)
	 * @param context
	 */
	public void updateModel(ModelInterface m, Context context) {
		String dbRep = m.getDatabaseRepresentation();
		// Logga antalet uppdaterade poster om de är mer än 1?
		int updated = 0;
		if (dbRep.equalsIgnoreCase("assignment")) {
			Assignment ass = (Assignment) m;
			ContentValues values = new ContentValues();

			values.put(AssignmentTable.COLUMN_ASSIGNMENTDESCRIPTION,
					ass.getAssignmentDescription());
			values.put(AssignmentTable.COLUMN_ASSIGNMENTSTATUS,
					ass.getAssignmentStatus().toString());
			values.put(AssignmentTable.COLUMN_CAMERAIMAGE,
					bitmapToByteArray(ass.getCameraImage()));
			values.put(AssignmentTable.COLUMN_LAT, Double.toString(ass.getLat()));
			values.put(AssignmentTable.COLUMN_LON, Double.toString(ass.getLon()));
			values.put(AssignmentTable.COLUMN_NAME, ass.getName());
			values.put(AssignmentTable.COLUMN_SENDER, ass.getSender());
			values.put(AssignmentTable.COLUMN_SITENAME, ass.getSiteName());
			values.put(AssignmentTable.COLUMN_STREETNAME, ass.getStreetName());
			values.put(AssignmentTable.COLUMN_TIMESPAN, ass.getTimeSpan());

			updated = context.getContentResolver().update(
					DatabaseContentProviderAssignments.CONTENT_URI, values,
					Database.KEY_ID + " = " + Long.toString(m.getId()), null);
		} else if (dbRep.equalsIgnoreCase("contact")) {
			Contact contact = (Contact) m;
			ContentValues values = new ContentValues();
			values.put(ContactsTable.COLUMN_CONTACT_NAME, contact.getContactName());

			updated = context.getContentResolver().update(
					com.example.klien_projekttermin.databaseProvider.Contact.Contacts.CONTENT_URI, values,
					Database.KEY_ID + " = " + Long.toString(m.getId()), null);

		} else if (dbRep.equalsIgnoreCase("message")) {
			MessageModel mess = (MessageModel) m;

			ContentValues values = new ContentValues();
			values.put(MessageTable.COLUMN_CONTENT, mess.getMessageContent()
					.toString());
			values.put(MessageTable.COLUMN_RECEIVER, mess.getReciever()
					.toString());
			values.put(MessageTable.COLUMN_TIMESTAMP,
					Long.toString(mess.getMessageTimeStamp()));
			values.put(MessageTable.COLUMN_ISREAD, (mess.isRead() ? "TRUE"
					: "FALSE"));

			updated = context.getContentResolver().update(
					DatabaseContentProviderMessages.CONTENT_URI, values,
					Database.KEY_ID + " = " + Long.toString(m.getId()), null);
		}
	}

	/**
	 * Konverterar en Android bitmap till en ByteArray
	 * 
	 * @param cameraImage
	 *            Bitmap
	 * @return byte[] BLOB
	 */
	private byte[] bitmapToByteArray(Bitmap cameraImage) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Bitmap bmp = cameraImage;
		bmp.compress(Bitmap.CompressFormat.PNG, 100, baos);
		byte[] photo = baos.toByteArray();
		return photo;
	}

	/**
	 * Stäng databasen som singleton-databasen skapar när den instansieras.
	 */
	public void destroy() {
		// Om databasen inte är null samt om den är öppen
		if (database != null || database.isOpen()) {
			database.close();
		}
	}
}
