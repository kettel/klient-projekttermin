package database;

import java.util.ArrayList;
import java.util.List;

import database.AssignmentTable.Assignments;

import models.Assignment;
import models.AssignmentPriority;
import models.AssignmentStatus;
import models.Contact;
import models.ModelInterface;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;

public class AssignmentsDB {

	private AssignmentsDB() {
	}

	private static final AssignmentsDB instance = new AssignmentsDB();

	public static AssignmentsDB getInstance() {
		return instance;
	}

	public void addAssignment(ContentResolver contentResolver,
			Assignment assignment) {
		ContentValues values = new ContentValues();
		values.put(Assignments.GLOBAL_ASSIGNMENT_ID, assignment.getGlobalID());
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
				assignment.getCameraImage());
		values.put(Assignments.STREETNAME, assignment.getStreetName());
		values.put(Assignments.SITENAME, assignment.getSiteName());
		values.put(Assignments.TIMESTAMP, assignment.getTimeStamp());
		values.put(Assignments.PRIORITY, assignment.getAssignmentPriority()
				.toString());
		values.put(Assignments.PRIORITY_INT, assignment.getPrio_int());
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
				String globalid = "";
				Long timestamp = Long.valueOf("0");
				double lon = 0, lat = 0;
				boolean externalMission = false;
				long priorityInt = 0;
				byte[] image = null;
				AssignmentStatus status = AssignmentStatus.NOT_STARTED;
				AssignmentPriority priority = AssignmentPriority.PRIO_NORMAL;
				List<Contact> agents = new ArrayList<Contact>();

				for (int i = 0; i < cursor.getColumnCount(); i++) {
					String currentCol = cursor.getColumnName(i);
					if (currentCol.equalsIgnoreCase(Assignments.ASSIGNMENT_ID)) {
						id = cursor.getInt(i);
					}else if (currentCol.equalsIgnoreCase(Assignments.GLOBAL_ASSIGNMENT_ID)) {
						globalid = cursor.getString(i);
					}else if (currentCol.equalsIgnoreCase(Assignments.NAME)) {
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
						image = cursor.getBlob(i);
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
					} else if (currentCol
							.equalsIgnoreCase(Assignments.PRIORITY_INT)) {
						priorityInt = cursor.getInt(i);
					}
				}
				Assignment assignment = new Assignment(id, // id från DB
						globalid, // global id
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
						priority, 
						priorityInt); // priority

				assignmentList.add(assignment);

			} while (cursor.moveToNext());
		}
		cursor.close();
		return assignmentList;
	}


	private String listToString(List<Contact> list) {
		String ret = new String();
		for (Contact m : list) {
			ret = ret + m.getContactName() + "/";
		}
		return ret;
	}

	public void delete(ContentResolver contentResolver, Assignment assignment) {
		contentResolver.delete(
				Assignments.CONTENT_URI,
				Assignments.GLOBAL_ASSIGNMENT_ID + " = "
						+ "\"" + assignment.getGlobalID() + "\"", null);

	}

	public int updateAssignment(ContentResolver contentResolver,
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
				assignment.getCameraImage());
		values.put(Assignments.STREETNAME, assignment.getStreetName());
		values.put(Assignments.SITENAME, assignment.getSiteName());
		values.put(Assignments.TIMESTAMP, assignment.getTimeStamp());
		values.put(Assignments.PRIORITY, assignment.getAssignmentPriority()
				.toString());
		values.put(Assignments.PRIORITY_INT, assignment.getPrio_int());
		int updated = contentResolver.update(
				Assignments.CONTENT_URI,
				values,
				Assignments.GLOBAL_ASSIGNMENT_ID + " = "
						+ "\"" + assignment.getGlobalID() + "\"", null);
//				Assignments.ASSIGNMENT_ID + " = "
//						+  assignment.getId() , null);

		return updated;
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
