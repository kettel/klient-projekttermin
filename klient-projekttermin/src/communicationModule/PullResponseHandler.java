package communicationModule;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import messageFunction.Inbox;
import models.Assignment;
import models.Contact;
import models.MessageModel;
import models.ModelInterface;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import assignment.AssignmentOverview;

import com.klient_projekttermin.MainActivity;
import com.klient_projekttermin.R;

import contacts.ContactsBookActivity;
import database.Database;

public class PullResponseHandler implements Observer {

	private Context context;
	private Database db;
	private Intent notificationIntent;
	private boolean hasChanged = false;
	private String message = "";

	public PullResponseHandler(Context context) {
		super();
		this.context = context;
		db = Database.getInstance(context);
	}

	public void update(Observable observable, Object data) {
		System.out.println("notification");

		notificationIntent = new Intent(context, MainActivity.class);
		if (data == null && hasChanged) {
			System.out.println("klar med pull");
			showNotification();
		} else {
			if (data instanceof Contact) {
				message = "Ny kontakt";
				db.addToDB((Contact) data, context.getContentResolver());
				notificationIntent = new Intent(context,
						ContactsBookActivity.class);
				hasChanged = true;
			} else if (data instanceof Assignment) {
				message = "Nytt uppdrag";
				eraseTempAssignmentInDB((Assignment) data);
				db.addToDB((Assignment) data, context.getContentResolver());
				notificationIntent = new Intent(context,
						AssignmentOverview.class);
				hasChanged = true;
			} else if (data instanceof MessageModel) {
				message = "Nytt meddelande";
				db.addToDB((MessageModel) data, context.getContentResolver());
				notificationIntent = new Intent(context, Inbox.class);
				hasChanged = true;
			}
		}

	}

	/**
	 * Om uppdraget som kommer in är det som sidosparades (globalID -1) så ska
	 * den ersätta globalID -1 -uppdraget.
	 * 
	 * @param assignment
	 */
	private void eraseTempAssignmentInDB(Assignment assignment) {

		List<ModelInterface> list = db
				.getAllFromDB((ModelInterface) new Assignment(),
						context.getContentResolver());
		
		for (ModelInterface modelInterface : list) {
			if (((Assignment) modelInterface).getGlobalID() == -1
					&& ((Assignment) modelInterface).getName().equals(
							assignment.getName())) {
				db.deleteFromDB((ModelInterface) new Assignment(-1),
						context.getContentResolver());
			}
		}
	}

	private void showNotification() {
		String title = context.getString(R.string.app_name);
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		int icon = R.drawable.loggakrona;

		// set intent so it does not start a new activity
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent intent = PendingIntent.getActivity(context, 0,
				notificationIntent, 0);
		Uri defaultSound = RingtoneManager
				.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

		Notification notification = new NotificationCompat.Builder(context)
				.setContentTitle(title).setContentText(message)
				.setContentIntent(intent).setSmallIcon(icon)
				.setLights(Color.YELLOW, 1, 2).setAutoCancel(true)
				.setSound(defaultSound).build();

		notificationManager.notify(0, notification);
	}

}
