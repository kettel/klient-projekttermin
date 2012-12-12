package assignment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.Contact;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.klient_projekttermin.R;

public class SimpleEditTextItemAdapter extends SimpleAdapter implements
		android.view.View.OnFocusChangeListener {

	@SuppressLint({ "UseSparseArrays", "UseSparseArrays" })
	private HashMap<Integer, String> itemStrings = new HashMap<Integer, String>();
	private Context context;
	public static String items;

	public SimpleEditTextItemAdapter(Context context,
			List<? extends Map<String, ?>> data, int resource, String[] from,
			int[] to) {
		super(context, data, resource, from, to);
		this.context = context;
	}

	@Override
	public int getItemViewType(int position) {
		switch (position) {
		case 0:
		case 1:
		case 2:
		case 3:
			return R.layout.textfield_item;
		case 8:
			return android.R.layout.simple_list_item_checked;
		default:
			return android.R.layout.simple_list_item_2;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (convertView == null||position==8) {
			/**
			 * Finns ingen vy som vi kan återanvända, skapar en ny!
			 */
			convertView = inflater.inflate(getItemViewType(position), parent,
					false);
			if (getItemViewType(position) == android.R.layout.simple_list_item_2) {
				TextView textView = (TextView) convertView
						.findViewById(android.R.id.text1);
				textView.setText(((HashMap<String, String>) this
						.getItem(position)).get("line1"));
				textView.setTextColor(Color.GRAY);
			} else if (getItemViewType(position) == R.layout.textfield_item) {
				EditText editText = (EditText) convertView
						.findViewById(R.id.text_item);
				editText.setHint(((HashMap<String, String>) this
						.getItem(position)).get("line1"));
				editText.setOnFocusChangeListener(this);
				editText.setId(position);
			} else if (getItemViewType(position) == android.R.layout.simple_list_item_checked) {
				TextView textView = (TextView) convertView
						.findViewById(android.R.id.text1);
				textView.setText(((HashMap<String, String>) this
						.getItem(position)).get("line1"));
			}
		}

		// Skapandet av ny vy klar
		if (this.itemStrings.containsKey(position)) {
			if (convertView.findViewById(R.id.text_item) != null) {
				EditText editText = (EditText) convertView
						.findViewById(R.id.text_item);
				editText.setText(this.itemStrings.get(position));
				editText.setId(position);
			} else if (convertView.findViewById(android.R.id.text1) != null
					&& getItemViewType(position) != android.R.layout.simple_list_item_checked) {
				TextView textView = (TextView) convertView
						.findViewById(android.R.id.text1);
				textView.setText(this.itemStrings.get(position));
				textView.setTextColor(Color.WHITE);
			}
		}
		if (convertView.findViewById(android.R.id.text2) != null) {
			TextView textView = (TextView) convertView
					.findViewById(android.R.id.text2);
			textView.setText(((HashMap<String, String>) this.getItem(position))
					.get("line1"));
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

	public HashMap<Integer, String> getItemStrings() {
		return itemStrings;
	}

	public void setItemStrings(HashMap<Integer, String> itemStrings) {
		this.itemStrings = itemStrings;
	}
}
