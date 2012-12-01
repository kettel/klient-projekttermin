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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import camera.PhotoGallery;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.klient_projekttermin.ActivityConstants;
import com.klient_projekttermin.R;
import com.nutiteq.components.WgsPoint;
import com.nutiteq.location.providers.AndroidGPSProvider;

import contacts.ContactsCursorAdapter;

public class SimpleEditTextItemAdapter extends SimpleAdapter implements
		android.view.View.OnFocusChangeListener {

	@SuppressLint({ "UseSparseArrays", "UseSparseArrays" })
	private HashMap<Integer, String> itemStrings = new HashMap<Integer, String>();
	private Context context;
	private boolean isCreatingDialog = false;
	private boolean isCreatingCoordDialog = false;
	public static String items;
	private String temp = "Agenter: ";

	private static String[] priorityAlts = { "Hög", "Normal", "Låg" };
	private EditText editText;
	private boolean isCreatingPrioDialog = false;
	private boolean isCreatingAgentDialog = false;
	private static String[] pictureAlts = { "Bifoga bild", "Ta bild",
			"Ingen bild" };
	private static String[] coordsAlts = { "Bifoga koordinater från karta",
			"Använd GPS position", "Inga koordinater" };

	public SimpleEditTextItemAdapter(Context context,
			List<? extends Map<String, ?>> data, int resource, String[] from,
			int[] to) {
		super(context, data, resource, from, to);
		this.context = context;
	}

	@Override
	public int getItemViewType(int position) {
		// TODO Auto-generated method stub
		return position == 8 ? R.layout.autocomp_item : R.layout.textfield_item;
	}

	@SuppressWarnings("unchecked")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (convertView == null) {
			convertView = inflater.inflate(getItemViewType(position), null);
			if (position == 8) {
				
				final AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView) convertView
						.findViewById(R.id.autoText_item);
				
				
				
				autoCompleteTextView.setAdapter(new ContactsCursorAdapter(
						context, null, true));

					autoCompleteTextView.setHint(((HashMap<String, String>) this
							.getItem(position)).get("line1"));
					
					//Snygghax.. för att få tag i auto-vyns text.
					autoCompleteTextView.setOnItemClickListener(new OnItemClickListener() {

						public void onItemClick(AdapterView<?> arg0, View arg1,
								int arg2, long arg3) {
							TextView e = (TextView)arg1;
							Log.e("FEL", e.getText().toString());
							
							temp = temp + e.getText().toString()+ ", ";
							
							itemStrings.put(8, temp);
							autoCompleteTextView.setHint(temp);
							autoCompleteTextView.setText("");
							//itemStrings.put(8, e.getText().toString());
						}
					});

			} else {
				System.out.println("else");
				editText = (EditText) convertView.findViewById(R.id.text_item);

				if (editText != null) {
					if (itemStrings.get(position) != null) {
						editText.setText(itemStrings.get(position));
					} else {
						editText.setText(null);
					}
					editText.setHint(((HashMap<String, String>) this
							.getItem(position)).get("line1"));
					editText.setId(position);
					editText.setOnFocusChangeListener(this);
				}
			}
		}
		return convertView;

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
		
		if (hasFocus && v.getId() == 1) {
			if (!isCreatingCoordDialog) {
				isCreatingCoordDialog = true;
				coordinateField(v);
			}
		}
		if (hasFocus && v.getId() == 6) {
			if (!isCreatingDialog) {
				isCreatingDialog = true;
				pictureAlternatives();
			}
		}
		if (hasFocus && v.getId() == 7) {
			if (!isCreatingPrioDialog) {
				isCreatingPrioDialog = true;
				priorityAlternatives((EditText) v);
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
					Intent intent = new Intent(context, PhotoGallery.class);
					intent.putExtra("calling-activity",
							ActivityConstants.ADD_PICTURE_TO_ASSIGNMENT);
					((AddAssignment) context).startActivityForResult(intent, 1);
					break;
				case 1:
					Intent intent2 = new Intent(context, PhotoGallery.class);
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
		// LocationManager manager = (LocationManager) context
		// .getSystemService(Context.LOCATION_SERVICE);
		// String provider = manager.getBestProvider(new Criteria(), true);
		// Location location = manager.getLastKnownLocation(provider);
		// System.out.println(location + " LOCATION");
		// LocationManager manager = (LocationManager)
		// context.getSystemService(Context.LOCATION_SERVICE);
		// AndroidGPSProvider locationSource = new AndroidGPSProvider(manager,
		// 1000L);
		// WgsPoint location = locationSource.getLocation();
		// Gson gson = new Gson();
		// final Type type = new TypeToken<WgsPoint[]>() {
		// }.getType();
		// WgsPoint[] wgs = new WgsPoint[1];
		// wgs[0] = location;
		// // System.out.println(new WgsPoint(location.getLatitude(), location
		// // .getLongitude()));
		// final String pos = gson.toJson(wgs, type);

		// System.out.println("POS: " + pos);
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

				isCreatingDialog = false;
				switch (arg2) {
				case 0:
					isCreatingCoordDialog = false;
					dialog.dismiss();
					Intent intent = new Intent(context, MapActivity.class);
					intent.putExtra("calling-activity",
							ActivityConstants.ADD_COORDINATES_TO_ASSIGNMENT);
					((AddAssignment) context).startActivityForResult(intent, 0);
				case 1:
					Intent intent2 = new Intent(context, MapActivity.class);
					intent2.putExtra("calling-activity",
							ActivityConstants.GET_GPS_LOCATION);
					((AddAssignment) context)
							.startActivityForResult(intent2, 0);
					isCreatingCoordDialog = false;
					// itemStrings.put(v.getId(), pos);
					// ed1.setText(pos);
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

	public void agentAlternatives() {

	}

	public HashMap<Integer, String> getItemStrings() {
		return itemStrings;
	}

	public void setItemStrings(HashMap<Integer, String> itemStrings) {
		this.itemStrings = itemStrings;
	}
}
