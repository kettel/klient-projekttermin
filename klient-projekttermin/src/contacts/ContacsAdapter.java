package contacts;

import java.util.HashMap;
import java.util.List;

import com.klient_projekttermin.ActivityConstants;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class ContacsAdapter extends ArrayAdapter<String> {

	private Activity activity;
	private int callingActivity;
	private HashMap<Integer, Boolean> selected =  new HashMap<Integer, Boolean>();

	public ContacsAdapter(Activity activity, int textViewResourceId,
			String[] objects, int callingActivity) {
		super(activity, textViewResourceId, objects);
		this.activity = activity;
		this.callingActivity = callingActivity;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = super.getView(position, convertView, parent);
		v.setId(position);
		v.setClickable(true);
		v.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (callingActivity == ActivityConstants.ADD_AGENTS) {
					if (v.getBackground().equals(Color.rgb(0x80, 0x80, 0xFF))) {
						v.setBackgroundColor(Color.TRANSPARENT);
					} else {
						setSelected(v.getId(), true);
						v.setBackgroundColor(Color.rgb(0x80, 0x80, 0xFF));
					}
				}
				((ContactsBookActivity) activity).contactOptions(v);
			}
		});
		return v;
	}

	public void setSelected(int id, boolean selected) {
		this.selected.put(id, selected);
	}

	public HashMap<Integer, Boolean> getSelected() {
		return selected;
	}
}
