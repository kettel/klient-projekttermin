package assignment;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import map.CustomAdapter;
import map.MapActivity;
import models.Contact;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import camera.Album;
import camera.Cam;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.klient_projekttermin.ActivityConstants;
import com.klient_projekttermin.R;
import com.nutiteq.components.WgsPoint;

import contacts.ContactsBookActivity;

public class SimpleEditTextItemAdapter extends SimpleAdapter implements
		android.view.View.OnFocusChangeListener {

	@SuppressLint({ "UseSparseArrays", "UseSparseArrays" })
	private HashMap<Integer, String> itemStrings = new HashMap<Integer, String>();
	private Context context;
	private Button b1;
	private Button b5;
	private Button b3;
	private Button b7;
	public static String items;
	private String temp = "Agenter: ";

	private static String[] priorityAlts = { "Hög", "Normal", "Låg" };
	private EditText editText;
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
		switch (position) {
		case 1:
			return R.layout.button_item;
		case 5:
			return R.layout.button_item;
		case 3:
			return R.layout.button_item;
		case 7:
			return R.layout.button_item;
		case 8:
			return android.R.layout.simple_list_item_checked;
		default:
			return R.layout.textfield_item;
		}
	}
	

	@SuppressWarnings("unchecked")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (convertView == null) {
			/**
			 * Finns ingen vy som vi kan återanvända, skapar en ny!
			 */
			convertView = inflater.inflate(getItemViewType(position), parent,
					false);
			/**
			 * I brist på förståelse för koden samt tid skapar vi världens
			 * fulaste switch-case som ger alla items funktionalitet beroende på
			 * plats.
			 */
			switch (position) {
			case 1:
				b1 = (Button) convertView.findViewById(R.id.button_item);
				b1.setText(((HashMap<String, String>) this.getItem(position))
						.get("line1"));
				b1.setOnClickListener(new OnClickListener() {

					public void onClick(View v) {
						coordinateField(b1);
					}
				});
				break;
			case 3:
				b3 = (Button) convertView.findViewById(R.id.button_item);
				b3.setText(((HashMap<String, String>) this.getItem(position))
						.get("line1"));
				b3.setOnClickListener(new OnClickListener() {

					public void onClick(View v) {
						priorityAlternatives(b3);
					}
				});
				break;
			case 5:
				b5 = (Button) convertView.findViewById(R.id.button_item);
				b5.setText(((HashMap<String, String>) this.getItem(position))
						.get("line1"));
				b5.setOnClickListener(new OnClickListener() {

					public void onClick(View v) {
						pictureAlternatives();
					}
				});
				break;
			case 7:
				b7 = (Button) convertView.findViewById(R.id.button_item);
				b7.setText(((HashMap<String, String>) this.getItem(position))
						.get("line1"));
				b7.setOnClickListener(new OnClickListener() {

					public void onClick(View v) {
						agentsAlternatives(b7);
					}
				});
				break;
			case 8:
				TextView textView = (TextView) convertView
						.findViewById(android.R.id.text1);
				textView.setText(((HashMap<String, String>) this
						.getItem(position)).get("line1"));
				break;

			default:
				editText = (EditText) convertView.findViewById(R.id.text_item);
				if (itemStrings.get(position) != null) {
					editText.setText(itemStrings.get(position));
				} else {
					editText.setText(null);
					editText.setHint(((HashMap<String, String>) this
							.getItem(position)).get("line1"));
				}
				editText.setId(position);
				break;
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
				itemStrings.put(v.getId(), s.toString());
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void afterTextChanged(Editable s) {
			}
		});
	}

	private void agentsAlternatives(Button b) {
		Intent intent = new Intent(context, ContactsBookActivity.class);
		intent.putExtra("calling-activity", ActivityConstants.ADD_AGENTS);
		((AddAssignment) context).startActivityForResult(intent, 1);
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
					Intent intent = new Intent(context, Album.class);
					intent.putExtra("calling-activity",
							ActivityConstants.ADD_PICTURE_TO_ASSIGNMENT);
					((AddAssignment) context).startActivityForResult(intent, 1);
					break;
				case 1:
					Intent intent2 = new Intent(context, Cam.class);
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

	private void coordinateField(final Button b) {
		LocationManager manager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
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
		modeList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				dialog.dismiss();
				switch (arg2) {
				case 0:
					dialog.dismiss();
					Intent intent = new Intent(context, MapActivity.class);
					intent.putExtra("calling-activity",
							ActivityConstants.ADD_COORDINATES_TO_ASSIGNMENT);
					((AddAssignment) context).startActivityForResult(intent, 0);
					break;
				case 1:
					itemStrings.put(1, pos);
					b.setText("Hämtade koordinater");
					break;
				default:
					break;
				}
			}
		});
		dialog.show();
	}
	
	private void priorityAlternatives(final Button b) {
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
				switch (arg2) {
				case 0:
					itemStrings.put(3, "Hög prioritet");
					b.setText(itemStrings.get(3));
					// v.setText("Hög prioritet");
					break;
				case 1:
					itemStrings.put(3, "Normal prioritet");
					b.setText(itemStrings.get(3));
					// v.setText("Normal prioritet");
					break;
				case 2:
					itemStrings.put(3, "Låg prioritet");
					b.setText(itemStrings.get(3));
					// v.setText("Låg prioritet");
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

	public void setAgents(List<Contact> l) {
		StringBuilder sb = new StringBuilder();
		for (Contact contact : l) {
			sb.append(contact.getContactName() + ", ");
		}
		itemStrings.put(7, sb.toString());
		b7.setText(sb.toString());
	}

	public void setItemStrings(HashMap<Integer, String> itemStrings) {
		this.itemStrings = itemStrings;
	}
}
