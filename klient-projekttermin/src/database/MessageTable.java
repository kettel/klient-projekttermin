package database;

import android.net.Uri;
import android.provider.BaseColumns;

public class MessageTable {
	public MessageTable() {
	}

	public static final class Messages implements BaseColumns {
		private Messages() {
		}

		public static final Uri CONTENT_URI = Uri.parse("content://"
				+ MessagesContentProvider.AUTHORITY + "/messages");

		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/";

		public static final String MESSAGE_ID = "_id";
		
		public static final String TABLE_NAME = "messages";
		
		// Kolumner
		public static final String CONTENT = "content";
		public static final String RECEIVER = "receiver";
		public static final String SENDER = "sender";
		public static final String TIMESTAMP = "timestamp";
		public static final String ISREAD = "isRead";
		public static final String STATUS = "status";
	}
}
