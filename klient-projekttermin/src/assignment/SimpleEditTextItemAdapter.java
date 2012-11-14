package assignment;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import com.example.klien_projekttermin.R;

public class SimpleEditTextItemAdapter extends ArrayAdapter<String>{
	public SimpleEditTextItemAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);
		// TODO Auto-generated constructor stub
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View v=super.getView(position, convertView, parent);
		EditText editText=(EditText)v.findViewById(R.id.editText1);
		editText.setText(null);
		editText.setHint(this.getItem(position));
		return v;
	}

}
