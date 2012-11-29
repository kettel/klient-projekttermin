package com.klient_projekttermin;

import java.util.Observable;
import java.util.Observer;

import models.Assignment;
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

public class PullRequestHandler implements Observer {
	
	private Context context;
	public PullRequestHandler(Context context) {
		super();
		this.context=context;
	}

	public void update(Observable observable, Object data) {
		System.out.println("notification");
		String message="";
		if (data instanceof Contact) {
			message="Ny kontakt";
		}else if (data instanceof Assignment) {
			message="Nytt uppdrag";
		}else if (data instanceof MessageModel) {
			message="Nytt meddelande";
		}
		int icon = R.drawable.ic_launcher;
		long when = System.currentTimeMillis(); // can change this to a future
												// time if desired
		String title = context.getString(R.string.app_name);
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);

		Intent notificationIntent = new Intent(context, MainActivity.class);

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
