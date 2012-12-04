package contacts;

import database.ContactTable;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;


public class ContactsCursorAdapter extends CursorAdapter {
	ContentResolver contentResolver;
	public ContactsCursorAdapter(Context context, Cursor c, boolean autoRequery) {
		super(context, c, autoRequery);
		contentResolver=context.getContentResolver();
		runQueryOnBackgroundThread("");
	}

	public ContactsCursorAdapter(Context context, Cursor c, int flags) {
		super(context, c, flags);
		contentResolver=context.getContentResolver();
	}
	@Override
	public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
	    // this is how you query for suggestions
	    // notice it is just a StringBuilder building the WHERE clause of a cursor which is the used to query for results
	    if (getFilterQueryProvider() != null) { return getFilterQueryProvider().runQuery(constraint); }

	    return contentResolver.query(
				ContactTable.Contacts.CONTENT_URI, null, ContactTable.Contacts.NAME+" like '%"+constraint.toString()+"%'",
				null, ContactTable.Contacts.NAME);
	}

	@Override
	public String convertToString(Cursor cursor) {
		final int columnIndex = cursor.getColumnIndexOrThrow(ContactTable.Contacts.NAME);
		final String str = cursor.getString(columnIndex);
		return str;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		final String text = convertToString(cursor);
		((TextView) view).setText(text);
		((TextView) view).setTextColor(Color.BLACK);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		final LayoutInflater inflater = LayoutInflater.from(context);
		final View view = inflater.inflate(
				android.R.layout.simple_dropdown_item_1line, parent, false);
		return view;
	}

}
