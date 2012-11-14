package assignment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.LauncherActivity.ListItem;
import android.content.Context;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.SimpleAdapter;

import com.example.klien_projekttermin.R;
public class SimpleEditTextItemAdapter extends SimpleAdapter{
	private HashMap<Integer,String> itemStrings=new HashMap<Integer, String>();


	public SimpleEditTextItemAdapter(Context context,
			List<? extends Map<String, ?>> data, int resource, String[] from,
			int[] to) {
		super(context, data, resource, from, to);
		// TODO Auto-generated constructor stub
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View v=super.getView(position, convertView, parent);
		EditText editText=(EditText)v.findViewById(R.id.editText1);
		editText.setText(null);
		editText.setHint(((HashMap<String, String>) this.getItem(position)).get("line1"));
		editText.setId(position);
		editText.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if (!hasFocus) {
					
					final int position = v.getId();
					final EditText Caption = (EditText) v;
					String s=Caption.getText().toString();
					if (!s.isEmpty()) {
						itemStrings.put(position,s);
					}
				}
			}
		});
		return v;
	}

	public HashMap<Integer, String> getItemStrings() {
		return itemStrings;
	}

	public void setItemStrings(HashMap<Integer, String> itemStrings) {
		this.itemStrings = itemStrings;
	}
	
}
