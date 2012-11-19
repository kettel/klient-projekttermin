package com.example.klien_projekttermin.databaseProvider;

import android.net.Uri;
import android.provider.BaseColumns;


public class Contact {

	public Contact() {
	}

	public static final class Contacts implements BaseColumns {
		private Contacts() {
		}

		public static final Uri CONTENT_URI = Uri.parse("content://"
				+ DatabaseContentProviderContacts.AUTHORITY + "/contacts");

		// Komplett?
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/";

		// Database table
		public static final String TABLE_CONTACTS = "contact";
		public static final String COLUMN_ID = "_id";
		public static final String COLUMN_CONTACT_NAME = "contact_name";
	}

}

