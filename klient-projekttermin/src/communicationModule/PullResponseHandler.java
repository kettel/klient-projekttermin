package communicationModule;

import java.util.Observable;
import java.util.Observer;

import qosManager.QoSManager;

import login.User;
import messageFunction.Inbox;
import models.Assignment;
import models.AssignmentPriority;
import models.AssignmentStatus;
import models.AuthenticationModel;
import models.Contact;
import models.MessageModel;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import assignment.AssignmentOverview;

import com.klient_projekttermin.GCMIntentService;
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
	private QoSManager qosManager;

	public PullResponseHandler(Context context) {
		super();
		this.context = context;
		db = Database.getInstance(context);
		qosManager = QoSManager.getInstance();
	}

	public void update(Observable observable, Object data) {

		if (qosManager.readyToAdjustCM()) {
			qosManager.changeConnectivityMarkerStatus(true);
		}

		if (notificationIntent == null) {
			notificationIntent = new Intent(context, MainActivity.class);
		}
		if (data == null && hasChanged) {
			showNotification();
		} else {
			if (data instanceof Contact) {
				System.out.println("Ny kontakt");
				message = "Ny kontakt";
				/**
				 * Om update blir en etta har uppdrag uppdateras och således
				 * behöver det inte läggas till som nytt. Annars lägger man till
				 * det som ett nytt uppdrag.
				 */
				int update = db.updateModel((Contact) data,
						context.getContentResolver());
				if (update == 0) {
					db.addToDB((Contact) data, context.getContentResolver());
				}
				notificationIntent = new Intent(context,
						ContactsBookActivity.class);
				hasChanged = true;
			} else if (data instanceof Assignment) {
				System.out.println("Nytt uppdrag");
				message = "Nytt uppdrag";

				Assignment incAssignment = (Assignment) data;
				
				/**
				 * Har det inkommande uppdraget "FINISHED" som status ska den raderas från databasen.
				 */
				if (incAssignment.getAssignmentStatus() == AssignmentStatus.FINISHED) {
//					db.deleteFromDB(incAssignment, context.getContentResolver());
					db.updateModel(incAssignment, context.getContentResolver());
				} else {
					/**
					 * Om update blir en etta har uppdrag uppdateras och således
					 * behöver det inte läggas till som nytt. Annars lägger man
					 * till det som ett nytt uppdrag.
					 */
					int update = db.updateModel(incAssignment,
							context.getContentResolver());
					if (update == 0) {
						incAssignment.setAssignmentStatus(AssignmentStatus.NEED_HELP);
						db.addToDB(incAssignment, context.getContentResolver());
					}
				}
				notificationIntent = new Intent(context,
						AssignmentOverview.class);
				hasChanged = true;
			} else if (data instanceof MessageModel) {
				System.out.println("Nytt meddelande");
				message = "Nytt meddelande";
				db.addToDB((MessageModel) data, context.getContentResolver());
				notificationIntent = new Intent(context, Inbox.class);
				hasChanged = true;
				GCMIntentService.sendMessage(context, "message");
			} else if (data instanceof AuthenticationModel) {

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
