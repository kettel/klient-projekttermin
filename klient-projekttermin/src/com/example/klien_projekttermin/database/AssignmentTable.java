package com.example.klien_projekttermin.database;

import android.net.Uri;
import android.provider.BaseColumns;

public class AssignmentTable {
	
	public AssignmentTable(){
	}
	
	public static final class Assignments implements BaseColumns{
		// Default-innehåll för URI, typ och ID
		public static final Uri CONTENT_URI = Uri.parse("content://"
				+ AssignmentsContentProvider.AUTHORITY + "/assignments");

		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/";

		public static final String ASSIGNMENT_ID = "_id";
		
		public static final String TABLE_NAME = "assignments";
		
		// Kolumnnamn i databasen
		public static final String NAME = "name";
		public static final String LAT = "lat";
		public static final String LON = "lon";
		public static final String REGION = "region";
		public static final String SENDER = "sender";
		public static final String AGENTS = "agents";
		public static final String EXTERNAL_MISSION = "external_mission";
		public static final String DESCRIPTION = "description";
		public static final String TIMESPAN = "timespan";
		public static final String STATUS = "assignment_status";
		public static final String CAMERAIMAGE = "camera_image";
		public static final String STREETNAME = "streetname";
		public static final String SITENAME = "sitename";
		public static final String TIMESTAMP = "timestamp";
		//public static final String PRIORITY = "priority";
		
	}
}
