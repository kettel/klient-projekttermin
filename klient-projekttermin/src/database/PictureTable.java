package database;

import android.net.Uri;
import android.provider.BaseColumns;

public class PictureTable {

	public static final class Pictures implements BaseColumns {
		private Pictures() {
		}

		public static final Uri CONTENT_URI = Uri.parse("content://"+ PictureContentProvider.AUTHORITY + "/pictures");

		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/";

		public static final String PICTURE_ID = "_id";

		public static final String PICTURE = "picture";
	}
}
