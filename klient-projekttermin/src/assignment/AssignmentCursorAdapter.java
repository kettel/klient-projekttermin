package assignment;

import models.AssignmentStatus;
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
		final int columnIndexName = cursor
				.getColumnIndexOrThrow(AssignmentTable.Assignments.NAME);
		final int columnIndexPrio = cursor
				.getColumnIndexOrThrow(AssignmentTable.Assignments.PRIORITY);
		final String str = cursor.getString(columnIndexName) + "  "
				+ getPriorityToString(cursor.getString(columnIndexPrio));
		return str;
	}

	public String getPriorityToString(String assignmentPrio) {

		final String PRIO_HIGH = "PRIO_HIGH";
		final String PRIO_NORMAL = "PRIO_NORMAL";
		final String PRIO_LOW = "PRIO_LOW";
		final String PRIO_NONEEXISTANT = "Ej satt";

		if (assignmentPrio == null) {
			return PRIO_NONEEXISTANT;
		} else if (assignmentPrio.equals(PRIO_HIGH)) {
			return "Hög prioritering";
		} else if (assignmentPrio.equals(PRIO_NORMAL)) {
			return "Normal prioritering";
		} else if (assignmentPrio.equals(PRIO_LOW)) {
			return "Låg prioritering";
		} else {
			return PRIO_NONEEXISTANT;
		}

	}

	@Override
	public void bindView(View arg0, Context arg1, Cursor arg2) {
		final String text = convertToString(arg2);
		int i = arg2.getColumnIndex(AssignmentTable.Assignments.STATUS);
		if(arg2.getString(i).equals(AssignmentStatus.NEED_HELP.toString())){
			((TextView) arg0).setBackgroundColor(Color.LTGRAY);
		} else {
			((TextView) arg0).setBackgroundColor(Color.TRANSPARENT);
		}
		((TextView) arg0).setText(text);
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
		if (getFilterQueryProvider() != null) {
			return getFilterQueryProvider().runQuery(constraint);
		}

		return contentResolver.query(AssignmentTable.Assignments.CONTENT_URI,
				null, AssignmentTable.Assignments.NAME + " like '%"
						+ constraint.toString() + "%'", null,
				AssignmentTable.Assignments.NAME);
	}

}
