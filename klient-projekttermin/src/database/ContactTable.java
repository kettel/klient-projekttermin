package database;

import android.net.Uri;
import android.provider.BaseColumns;

public class ContactTable {

	public ContactTable() {
	}

	public static final class Contacts implements BaseColumns {
		private Contacts() {
		}

		public static final Uri CONTENT_URI = Uri.parse("content://"+ ContactsContentProvider.AUTHORITY + "/contacts");

		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/";

		public static final String CONTACT_ID = "_id";

		public static final String NAME = "name";
	}

}

