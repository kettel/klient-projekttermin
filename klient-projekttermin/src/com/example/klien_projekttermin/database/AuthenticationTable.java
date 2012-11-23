package com.example.klien_projekttermin.database;

import android.net.Uri;
import android.provider.BaseColumns;

public class AuthenticationTable {
	public AuthenticationTable() {
	}

	public static final class Authentications implements BaseColumns {
		private Authentications() {
		}

		public static final Uri CONTENT_URI = Uri.parse("content://"+ AuthenticationContentProvider.AUTHORITY + "/authentications");

		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/";

		public static final String AUTHENTICATION_ID = "_id";

		public static final String TABLE_NAME = "authentications";

		// Kolumner
		public static final String USERNAME = "userName";
		public static final String PASSWORD = "password";

	}
}
