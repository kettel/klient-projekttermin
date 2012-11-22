package assignment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import map.CustomAdapter;
import map.MapActivity;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import camera.Camera;

import com.example.klien_projekttermin.ActivityConstants;
import com.example.klien_projekttermin.R;

public class SimpleEditTextItemAdapter extends SimpleAdapter implements
		android.view.View.OnFocusChangeListener {

	@SuppressLint("UseSparseArrays")
	private HashMap<Integer, String> itemStrings = new HashMap<Integer, String>();
	private Context context;
	private String items;
	private static String[] pictureAlts = { "Bifoga bild", "Ta bild" };

	public SimpleEditTextItemAdapter(Context context,
			List<? extends Map<String, ?>> data, int resource, String[] from,
			int[] to) {
		super(context, data, resource, from, to);
		this.context = context;
	}

	@SuppressWarnings("unchecked")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = super.getView(position, convertView, parent);

		EditText editText = (EditText) v.findViewById(R.id.text_item);
		if (editText!=null) {
			if (itemStrings.get(position) != null) {
				editText.setText(itemStrings.get(position));
			} else {
				editText.setText(null);
			}
			editText.setHint(((HashMap<String, String>) this.getItem(position))
					.get("line1"));
			editText.setId(position);
			editText.setOnFocusChangeListener(this);
		}
		
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
		if (hasFocus && v.getId() == 6) {
			final EditText Caption = (EditText) v;
			String s = Caption.getText().toString();
			if (s.isEmpty()) {
				pictureAlternatives();
			}
		}
	}

	private void pictureAlternatives() {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("Bild alternativ");
		ListView modeList = new ListView(context);
		CustomAdapter modeAdapter = new CustomAdapter(context,
				android.R.layout.simple_list_item_1, android.R.id.text1,
				pictureAlts);
		modeList.setAdapter(modeAdapter);
		builder.setView(modeList);
		final Dialog dialog = builder.create();
		modeList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				dialog.dismiss();
				switch (arg2) {
				case 0:
					Intent intent = createIntent(ActivityConstants.ADD_PICTURE_TO_ASSIGNMENT);
					context.startActivity(intent);
					((AddAssignment) context).finish();
					break;
				case 1:
					Intent intent2 = createIntent(ActivityConstants.TAKE_PICTURE_FOR_ASSIGNMENT);
					context.startActivity(intent2);
					((AddAssignment) context).finish();
					break;
				default:
					break;
				}
			}
		});
		dialog.show();
	}
	
	private Intent createIntent(int id){
		Intent intent = new Intent(context, Camera.class);
		intent.putExtra("calling-activity",id);
		intent.putExtra(items, itemStrings);
		return intent;
	}

	private void coordinateField() {
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
				((AddAssignment) context).finish();
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
