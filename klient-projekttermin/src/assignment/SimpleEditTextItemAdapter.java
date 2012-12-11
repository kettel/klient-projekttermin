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
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
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
		default:
			return R.layout.textfield_item;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		convertView = null;

		final View v = super.getView(position, convertView, parent);
		editText = (EditText) v.findViewById(R.id.text_item);
		if (position == 7) {
			convertView = inflater.inflate(getItemViewType(position), null);
			b7 = (Button) convertView.findViewById(R.id.button_item);
			b7.setHint(((HashMap<String, String>) this.getItem(position))
					.get("line1"));
			if (itemStrings.get(position) != null
					&& !itemStrings.get(position).equals("")) {
				b7.setText(itemStrings.get(position));
			}
			b7.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					agentsAlternatives(b7);
				}

			});

		} else if (position == 3) {
			convertView = inflater.inflate(getItemViewType(position), null);
			b3 = (Button) convertView.findViewById(R.id.button_item);
			b3.setHint(((HashMap<String, String>) this.getItem(position))
					.get("line1"));
			if (itemStrings.get(position) != null
					&& !itemStrings.get(position).equals("")) {
				b3.setText(itemStrings.get(position));
			}
			b3.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					priorityAlternatives(b3);
				}

			});
		} else if (position == 5) {
			convertView = inflater.inflate(getItemViewType(position), null);
			b5 = (Button) convertView.findViewById(R.id.button_item);
			b5.setHint(((HashMap<String, String>) this.getItem(position))
					.get("line1"));
			if (itemStrings.get(position) != null
					&& !itemStrings.get(position).equals("")) {
				b5.setText(itemStrings.get(position));
			}
			b5.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					pictureAlternatives();
				}

			});
		} else if (position == 1) {
			convertView = inflater.inflate(getItemViewType(position), null);
			b1 = (Button) convertView.findViewById(R.id.button_item);
			b1.setHint(((HashMap<String, String>) this.getItem(position))
					.get("line1"));
			if (itemStrings.get(position) != null
					&& !itemStrings.get(position).equals("")) {
				b1.setText("Hämtade koordinater");
			}
			b1.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					coordinateField(b1);
				}

			});
		} else if (editText != null) {
			if (itemStrings.get(position) != null) {
				editText.setText(itemStrings.get(position));
			} else {
				editText.setText(null);
			}
			editText.setHint(((HashMap<String, String>) this.getItem(position))
					.get("line1"));
			editText.setId(position);
			editText.setOnFocusChangeListener(this);
			editText.setOnKeyListener(new OnKeyListener() {

				public boolean onKey(View v, int keyCode, KeyEvent event) {
					if (event.getAction() == KeyEvent.ACTION_DOWN) {
						switch (keyCode) {
						case KeyEvent.KEYCODE_DPAD_CENTER:
						case KeyEvent.KEYCODE_ENTER:
							switch (position) {
							case 0:
								coordinateField(b1);
								break;
							case 4:
								pictureAlternatives();
								break;
							case 2:
								priorityAlternatives(b3);
								break;
							case 6:
								agentsAlternatives(b7);
								break;
							default:
								break;
							}
							return true;
						default:
							break;
						}
					}
					return false;
				}
			});
		}
		if (position == 7 || position == 3 || position == 5 || position == 1) {
			return convertView;
		} else
			return v;

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
		intent.putExtra("calling-activity",
				ActivityConstants.ADD_AGENTS);
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

	public void setAgents(List<Contact> l){
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
