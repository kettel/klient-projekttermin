package com.example.klien_projekttermin.database;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import com.example.klien_projekttermin.database.AssignmentTable.Assignments;

import models.Assignment;
import models.AssignmentPriority;
import models.AssignmentStatus;
import models.Contact;
import models.ModelInterface;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class AssignmentsDB {
	private AssignmentsDB() {
	}

	private static final AssignmentsDB instance = new AssignmentsDB();

	public static AssignmentsDB getInstance() {
		return instance;
	}

	public void addAssignment(ContentResolver contentResolver,
			Assignment assignment) {
		Log.d("DB"," Vad hernder bre: "+ assignment.getAssignmentPriority().toString());
		
		ContentValues values = new ContentValues();
		values.put(Assignments.NAME, assignment.getName());
		values.put(Assignments.LAT, assignment.getLat());
		values.put(Assignments.LON, assignment.getLon());
		values.put(Assignments.REGION, assignment.getRegion());
		values.put(Assignments.SENDER, assignment.getSender());
		values.put(Assignments.AGENTS, listToString(assignment.getAgents()));
		values.put(Assignments.EXTERNAL_MISSION, assignment.isExternalMission());
		values.put(Assignments.DESCRIPTION,
				assignment.getAssignmentDescription());
		values.put(Assignments.TIMESPAN, assignment.getTimeSpan());
		values.put(Assignments.STATUS, assignment.getAssignmentStatus()
				.toString());
		values.put(Assignments.CAMERAIMAGE,
				bitmapToByteArray(assignment.getCameraImage()));
		values.put(Assignments.STREETNAME, assignment.getStreetName());
		values.put(Assignments.SITENAME, assignment.getSiteName());
		values.put(Assignments.TIMESTAMP, assignment.getTimeStamp());
		values.put(Assignments.PRIORITY, assignment.getAssignmentPriority()
				.toString());
		contentResolver.insert(Assignments.CONTENT_URI, values);
	}

	public List<ModelInterface> getAllAssignments(
			ContentResolver contentResolver) {
		List<ModelInterface> assignmentList = new ArrayList<ModelInterface>();
		Cursor cursor = contentResolver.query(Assignments.CONTENT_URI, null,
				Assignments.ASSIGNMENT_ID + " IS NOT null", null, null);
		if (cursor.moveToFirst()) {
			do {
				String name = new String(), region = new String(), sender = new String(), description = new String(), timespan = new String(), streetname = new String(), sitename = new String();
				long id = 0;
				Long timestamp = Long.valueOf("0");
				double lon = 0, lat = 0;
				boolean externalMission = false;
				Bitmap image = null;
				AssignmentStatus status = AssignmentStatus.NOT_STARTED;
				AssignmentPriority priority = AssignmentPriority.PRIO_NORMAL;
				List<Contact> agents = new ArrayList<Contact>();

				for (int i = 0; i < cursor.getColumnCount(); i++) {
					String currentCol = cursor.getColumnName(i);
					if (currentCol.equalsIgnoreCase(Assignments.ASSIGNMENT_ID)) {
						id = cursor.getInt(i);
					} else if (currentCol.equalsIgnoreCase(Assignments.NAME)) {
						name = cursor.getString(i);
					} else if (currentCol.equalsIgnoreCase(Assignments.LAT)) {
						lat = Double.valueOf(cursor.getString(i));
					} else if (currentCol.equalsIgnoreCase(Assignments.LON)) {
						lon = Double.valueOf(cursor.getString(i));
					} else if (currentCol.equalsIgnoreCase(Assignments.REGION)) {
						region = cursor.getString(i);
					} else if (currentCol.equalsIgnoreCase(Assignments.SENDER)) {
						sender = cursor.getString(i);
					} else if (currentCol.equalsIgnoreCase(Assignments.AGENTS)) {
						String[] agentArray = cursor.getString(i).split("/");
						for (String agent : agentArray) {
							if (!agent.equals("")) {
								agents.add(new Contact(agent));
							}
						}
					} else if (currentCol
							.equalsIgnoreCase(Assignments.EXTERNAL_MISSION)) {
						externalMission = Boolean.getBoolean(cursor
								.getString(i));
					} else if (currentCol
							.equalsIgnoreCase(Assignments.DESCRIPTION)) {
						description = cursor.getString(i);
					} else if (currentCol
							.equalsIgnoreCase(Assignments.TIMESPAN)) {
						timespan = cursor.getString(i);
					} else if (currentCol.equalsIgnoreCase(Assignments.STATUS)) {
						status = AssignmentStatus.valueOf(cursor.getString(i));
					} else if (currentCol
							.equalsIgnoreCase(Assignments.CAMERAIMAGE)) {
						// Konvertera BLOB -> Bitmap
						byte[] byteImage = cursor.getBlob(i);
						ByteArrayInputStream imageStream = new ByteArrayInputStream(
								byteImage);
						image = BitmapFactory.decodeStream(imageStream);
					} else if (currentCol
							.equalsIgnoreCase(Assignments.STREETNAME)) {
						streetname = cursor.getString(i);
					} else if (currentCol
							.equalsIgnoreCase(Assignments.SITENAME)) {
						sitename = cursor.getString(i);
					} else if (currentCol
							.equalsIgnoreCase(Assignments.TIMESTAMP)) {
						timestamp = Long.valueOf(cursor.getString(i));
					} else if (currentCol
							.equalsIgnoreCase(Assignments.PRIORITY)) {
						priority = AssignmentPriority.valueOf(cursor
								.getString(i));
					}
				}
				Assignment assignment = new Assignment(id, // id från DB
						name, // name
						lat, // lat
						lon, // lon
						region,// region
						agents, // agents
						sender, // sender
						externalMission, // isExternalMission
						description, // assDesc
						timespan, // timeSpan
						status, // assStatus
						image, // cameraImage
						streetname, // streetName
						sitename, // siteName
						timestamp,// timeStamp
						priority); // priority

				assignmentList.add(assignment);

			} while (cursor.moveToNext());
		}
		cursor.close();
		return assignmentList;
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

	private String listToString(List<Contact> list) {
		String ret = new String();
		for (Contact m : list) {
			ret += ret + m.getContactName() + "/";
		}
		return ret;
	}

	public void delete(ContentResolver contentResolver, Assignment assignment) {
		contentResolver.delete(
				Assignments.CONTENT_URI,
				Assignments.ASSIGNMENT_ID + " = "
						+ Long.toString(assignment.getId()), null);

	}

	public void updateAssignment(ContentResolver contentResolver,
			Assignment assignment) {
		ContentValues values = new ContentValues();
		values.put(Assignments.NAME, assignment.getName());
		values.put(Assignments.LAT, assignment.getLat());
		values.put(Assignments.LON, assignment.getLon());
		values.put(Assignments.REGION, assignment.getRegion());
		values.put(Assignments.SENDER, assignment.getSender());
		values.put(Assignments.AGENTS, listToString(assignment.getAgents()));
		values.put(Assignments.EXTERNAL_MISSION, assignment.isExternalMission());
		values.put(Assignments.DESCRIPTION,
				assignment.getAssignmentDescription());
		values.put(Assignments.TIMESPAN, assignment.getTimeSpan());
		values.put(Assignments.STATUS, assignment.getAssignmentStatus()
				.toString());
		values.put(Assignments.CAMERAIMAGE,
				bitmapToByteArray(assignment.getCameraImage()));
		values.put(Assignments.STREETNAME, assignment.getStreetName());
		values.put(Assignments.SITENAME, assignment.getSiteName());
		values.put(Assignments.TIMESTAMP, assignment.getTimeStamp());
		values.put(Assignments.PRIORITY, assignment.getAssignmentPriority()
				.toString());
		int updated = contentResolver.update(
				Assignments.CONTENT_URI,
				values,
				Assignments.ASSIGNMENT_ID + " = "
						+ Long.toString(assignment.getId()), null);
		Log.d("DB", "Uppdaterade " + updated + " assignments.");
	}

	public int getCount(ContentResolver contentResolver) {
		int returnCount = 0;
		Cursor cursor = contentResolver.query(Assignments.CONTENT_URI, null,
				Assignments.ASSIGNMENT_ID + " IS NOT null", null, null);
		returnCount = cursor.getCount();
		cursor.close();
		return returnCount;
	}
}
