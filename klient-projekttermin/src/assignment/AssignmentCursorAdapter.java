package assignment;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import database.AssignmentTable;

public class AssignmentCursorAdapter extends CursorAdapter {

	private ContentResolver contentResolver;

	public AssignmentCursorAdapter(Context context, Cursor c,
			boolean autoRequery) {
		super(context, c, autoRequery);
	}

	public AssignmentCursorAdapter(Context context, Cursor c, int flags) {
		super(context, c, flags);
		contentResolver = context.getContentResolver();
	}

	@Override
	public String convertToString(Cursor cursor) {
		final int columnIndex=cursor.getColumnIndexOrThrow(AssignmentTable.Assignments.NAME);
		final String str=cursor.getString(columnIndex);
		return str;
	}

	@Override
	public void bindView(View arg0, Context arg1, Cursor arg2) {
		final String text=convertToString(arg2);
		((TextView)arg0).setText(text);
		((TextView)arg0).setTextColor(Color.BLACK);
	}

	@Override
	public View newView(Context arg0, Cursor arg1, ViewGroup arg2) {
		final LayoutInflater inflater = LayoutInflater.from(arg0);
		final View view = inflater.inflate(
		android.R.layout.simple_dropdown_item_1line, arg2, false);
		return view;
	}

	@Override
	public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
		System.out.println("DETTA KOMMER ALDRIG ATT SYNAS");
		if (getFilterQueryProvider() != null) { return getFilterQueryProvider().runQuery(constraint); }

	    return contentResolver.query(
				AssignmentTable.Assignments.CONTENT_URI, null, AssignmentTable.Assignments.NAME+" like '%"+constraint.toString()+"%'",
				null, AssignmentTable.Assignments.NAME);
	}

}
