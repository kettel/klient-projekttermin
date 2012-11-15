package assignment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import map.MapActivity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.SimpleAdapter;

import com.example.klien_projekttermin.R;

public class SimpleEditTextItemAdapter extends SimpleAdapter implements
		android.view.View.OnFocusChangeListener {

	private HashMap<Integer, String> itemStrings = new HashMap<Integer, String>();
	private Context context;

	public SimpleEditTextItemAdapter(Context context,
			List<? extends Map<String, ?>> data, int resource, String[] from,
			int[] to) {
		super(context, data, resource, from, to);
		this.context = context;
		// TODO Auto-generated constructor stub
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View v = super.getView(position, convertView, parent);
		EditText editText = (EditText) v.findViewById(R.id.editText1);
		if (itemStrings.get(position) != null) {
			editText.setText(itemStrings.get(position));
		} else {
			editText.setText(null);
		}
		editText.setHint(((HashMap<String, String>) this.getItem(position))
				.get("line1"));
		;
		editText.setId(position);
		editText.setOnFocusChangeListener(this);
		return v;
	}

	public HashMap<Integer, String> getItemStrings() {
		return itemStrings;
	}



	public void setItemStrings(HashMap<Integer, String> itemStrings) {
		this.itemStrings = itemStrings;
	}

	public void textToItem(int position, String s) {
		itemStrings.put(position, s);
	}

	public void onFocusChange(View v, boolean hasFocus) {
		if (!hasFocus) {
			final int position = v.getId();
			final EditText Caption = (EditText) v;
			String s = Caption.getText().toString();
			if (!s.isEmpty()) {
				itemStrings.put(position, s);
			}
		}
		if (hasFocus && v.getId() == 1) {
			final EditText Caption = (EditText) v;
			String s = Caption.getText().toString();
			if (s.isEmpty()) {
				coordinateField();
			}
		}
	}

	public void coordinateField() {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("Koordinater");
		builder.setMessage("Vill du hämta koordinater från kartan?");
		builder.setPositiveButton("ok", new OnClickListener() {
			public void onClick(DialogInterface dialog, int arg1) {
				dialog.dismiss();

				Intent intent = new Intent(context, MapActivity.class);
				intent.putExtra("calling-activity",
						ActivityConstants.ADD_COORDINATES_TO_ASSIGNMENT);
				context.startActivity(intent);
				((AddAssignment)context).finish();
			}
		});
		builder.setNegativeButton("cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		builder.setCancelable(false);
		builder.create().show();
	}
}
