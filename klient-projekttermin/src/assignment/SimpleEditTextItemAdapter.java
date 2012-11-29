package assignment;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import map.CustomAdapter;
import map.MapActivity;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import camera.Album;
import camera.Camera;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.klient_projekttermin.ActivityConstants;
import com.klient_projekttermin.R;
import com.nutiteq.components.WgsPoint;

public class SimpleEditTextItemAdapter extends SimpleAdapter implements
		android.view.View.OnFocusChangeListener {

	@SuppressLint({ "UseSparseArrays", "UseSparseArrays" })
	private HashMap<Integer, String> itemStrings = new HashMap<Integer, String>();
	private Context context;
	private boolean isCreatingDialog = false;
	private boolean isCreatingCoordDialog = false;
	private static String[] priorityAlts = { "Hög", "Normal", "Låg" };
	private EditText editText;
	private boolean isCreatingPrioDialog = false;
	private static String[] pictureAlts = { "Bifoga bild", "Ta bild" , "Ingen bild"};
	private static String[] coordsAlts = { "Bifoga koordinater från karta" , "Använd GPS position" , "Inga koordinater" };


	public SimpleEditTextItemAdapter(Context context,
			List<? extends Map<String, ?>> data, int resource, String[] from,
			int[] to) {
		super(context, data, resource, from, to);
		this.context = context;
	}

	@SuppressWarnings("unchecked")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		convertView = null;

		final View v = super.getView(position, convertView, parent);
		editText = (EditText) v.findViewById(R.id.text_item);

		if (editText != null) {
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

	public void textToItem(int position, String s) {
		itemStrings.put(position, s);
	}

	public void onFocusChange(final View v, boolean hasFocus) {
		((EditText) v).addTextChangedListener(new TextWatcher() {

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (v.getId() != 1 && v.getId() != 6) {
					itemStrings.put(v.getId(), s.toString());
				}
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void afterTextChanged(Editable s) {
			}
		});
		if (hasFocus && v.getId() == 1 && itemStrings.get(v.getId()) == null) {
			if (!isCreatingCoordDialog) {
				isCreatingCoordDialog = true;
				coordinateField(v);
			}
		}
		if (hasFocus && v.getId() == 6 && itemStrings.get(v.getId()) == null) {
			if (!isCreatingDialog) {
				isCreatingDialog = true;
				pictureAlternatives();
			}
		}
		if (hasFocus && v.getId() == 7 && itemStrings.get(v.getId()) == null) {
			if (!isCreatingPrioDialog ) {
				isCreatingPrioDialog = true;
				priorityAlternatives((EditText)v);
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
		dialog.setCancelable(false);
		modeList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				dialog.dismiss();
				isCreatingDialog = false;
				switch (arg2) {
				case 0:
					Intent intent = new Intent(context, Album.class);
					intent.putExtra("calling-activity",
							ActivityConstants.ADD_PICTURE_TO_ASSIGNMENT);
					((AddAssignment) context).startActivityForResult(intent, 1);
					break;
				case 1:
					Intent intent2 = new Intent(context, Camera.class);
					intent2.putExtra("calling-activity",
							ActivityConstants.TAKE_PICTURE_FOR_ASSIGNMENT);
					((AddAssignment) context)
							.startActivityForResult(intent2, 2);
					break;
				default:
					break;
				}
			}
		});
		dialog.show();
	}

	private void coordinateField(final View v) {
		final EditText ed1 = (EditText) v;
		LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		String provider = manager.getBestProvider(new Criteria(), true);
		Location location = manager.getLastKnownLocation(provider);
		Gson gson = new Gson();
		final Type type = new TypeToken<WgsPoint[]>() {
		}.getType();
		WgsPoint[] wgs = new WgsPoint[1];
		wgs[0] = new WgsPoint(location.getLatitude(), location.getLongitude());
		
		final String pos = gson.toJson(wgs, type);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("Koordinater");
		ListView modeList = new ListView(context);
		CustomAdapter modeAdapter = new CustomAdapter(context,
				android.R.layout.simple_list_item_1, android.R.id.text1,
				coordsAlts);
		modeList.setAdapter(modeAdapter);
		builder.setView(modeList);
		
		final Dialog dialog = builder.create();
		dialog.setCancelable(false);
		modeList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				dialog.dismiss();
				switch (arg2) {
				case 0:
					isCreatingCoordDialog = false;
					dialog.dismiss();
					Intent intent = new Intent(context, MapActivity.class);
					intent.putExtra("calling-activity",
							ActivityConstants.ADD_COORDINATES_TO_ASSIGNMENT);
					((AddAssignment) context).startActivityForResult(intent, 0);
					break;
				case 1:
					isCreatingCoordDialog = false;
					itemStrings.put(v.getId(), pos);
					ed1.setText(pos);
					break;
				default:
					isCreatingCoordDialog = false;
					break;
				}
			}
		});
		dialog.show();
	}

	private void priorityAlternatives(final EditText v) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("Välj uppdragets prioritet");
		ListView modeList = new ListView(context);
		CustomAdapter modeAdapter = new CustomAdapter(context,
				android.R.layout.simple_list_item_1, android.R.id.text1,
				priorityAlts);
		modeList.setAdapter(modeAdapter);
		builder.setView(modeList);
		final Dialog dialog = builder.create();
		modeList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				dialog.dismiss();
				isCreatingPrioDialog = false;
				switch (arg2) {
				case 0:
					v.setText("Hög prioritet");
					break;
				case 1:
					v.setText("Normal prioritet");
					break;
				case 2:
					v.setText("Låg prioritet");
				default:
					break;
				}
			}
		});
		dialog.show();
	}

	public HashMap<Integer, String> getItemStrings() {
		return itemStrings;
	}

	public void setItemStrings(HashMap<Integer, String> itemStrings) {
		this.itemStrings = itemStrings;
	}
}
