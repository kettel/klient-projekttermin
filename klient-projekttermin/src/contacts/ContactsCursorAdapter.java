package contacts;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class ContactsCursorAdapter extends CursorAdapter {

	public ContactsCursorAdapter(Context context, Cursor c, boolean autoRequery) {
		super(context, c, autoRequery);
	}

	public ContactsCursorAdapter(Context context, Cursor c, int flags) {
		super(context, c, flags);
		// TODO Auto-generated constructor stub
	}


	@Override
	public String convertToString(Cursor cursor) {
		System.out.println("convert");
		final int columnIndex = cursor.getColumnIndexOrThrow("state");
		final String str = cursor.getString(columnIndex);
		cursor.close();
		return str;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		System.out.println("bind");
		final String text = convertToString(cursor);
		((TextView) view).setText(text);
		cursor.close();
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		System.out.println("new");
		final LayoutInflater inflater = LayoutInflater.from(context);
		final View view = inflater.inflate(
				android.R.layout.simple_dropdown_item_1line, parent, false);
		cursor.close();

		return view;
	}

}
