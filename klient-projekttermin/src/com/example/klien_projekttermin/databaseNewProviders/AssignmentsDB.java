package com.example.klien_projekttermin.databaseNewProviders;


import java.io.ByteArrayOutputStream;
import java.util.List;

import com.example.klien_projekttermin.databaseNewProviders.AssignmentTable.Assignments;

import models.Assignment;
import models.AssignmentStatus;
import models.Contact;
import models.ModelInterface;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.graphics.Bitmap;

public class AssignmentsDB {
	private AssignmentsDB(){}
	private static final AssignmentsDB instance = new AssignmentsDB();
	public static AssignmentsDB getInstance(){
		return instance;
	}
	
	public void addAssignment(ContentResolver contentResolver, Assignment assignment){
		ContentValues contentValue = new ContentValues();
        contentValue.put(Assignments.NAME, assignment.getName());
        contentValue.put(Assignments.LAT, assignment.getLat());
        contentValue.put(Assignments.LON, assignment.getLon());
        contentValue.put(Assignments.REGION, assignment.getRegion());
        contentValue.put(Assignments.SENDER, assignment.getSender());
        contentValue.put(Assignments.AGENTS, listToString(assignment.getAgents()));
        contentValue.put(Assignments.EXTERNAL_MISSION, assignment.isExternalMission());
        contentValue.put(Assignments.DESCRIPTION, assignment.getAssignmentDescription());
        contentValue.put(Assignments.TIMESPAN, assignment.getTimeSpan());
        contentValue.put(Assignments.STATUS, assignment.getAssignmentStatus().toString());
        contentValue.put(Assignments.CAMERAIMAGE, bitmapToByteArray(assignment.getCameraImage()));
        contentValue.put(Assignments.STREETNAME, assignment.getStreetName());
        contentValue.put(Assignments.SITENAME, assignment.getSiteName());
        contentValue.put(Assignments.TIMESTAMP, assignment.getTimeStamp());
        contentResolver.insert(Assignments.CONTENT_URI, contentValue);
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
	
	private String listToString(List<Contact> list){
		String ret = new String();
		for (Contact m : list) {
			ret += ret + m.getContactName() + "/";
		}
		return ret;
	}
}
