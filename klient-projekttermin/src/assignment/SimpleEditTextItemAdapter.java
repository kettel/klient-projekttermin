package assignment;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.SimpleAdapter;

import com.example.klien_projekttermin.R;

public class SimpleEditTextItemAdapter extends SimpleAdapter{

	private List<? extends Map<String, ?>> data;
	private String[] from;

	public SimpleEditTextItemAdapter(Context context,
			List<? extends Map<String, ?>> data, int resource, String[] from,
			int[] to) {
		super(context, data, resource, from, to);
		this.data=data;
		this.from=from;
		// TODO Auto-generated constructor stub
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View v=super.getView(position, convertView, parent);
		EditText editText=(EditText)v.findViewById(R.id.editText1);
		editText.setText(null);
		editText.setHint((CharSequence) data.get(position).get(from[0]));
		return v;
	}

}
