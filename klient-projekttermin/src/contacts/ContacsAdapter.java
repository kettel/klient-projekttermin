package contacts;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class ContacsAdapter extends ArrayAdapter<String>{

	private Activity activity;
	
	public ContacsAdapter(Activity activity, int textViewResourceId,
			String[] objects) {
		super(activity, textViewResourceId, objects);
		this.activity = activity;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = super.getView(position, convertView, parent);
		v.setId(position);
		v.setClickable(true);
		v.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				((ContactsBookActivity)activity).contactOptions(v);
			}
		});
		return v;
	}
}
