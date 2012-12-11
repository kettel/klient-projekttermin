package map;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class CustomAdapter extends ArrayAdapter<String> {
	private boolean enabled = true;

	public CustomAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);
		// TODO Auto-generated constructor stub
	}

	public CustomAdapter(Context context, int resource, int textViewResourceId,
			List<String> objects) {
		super(context, resource, textViewResourceId, objects);
		// TODO Auto-generated constructor stub
	}

	public CustomAdapter(Context context, int resource, int textViewResourceId,
			String[] objects) {
		super(context, resource, textViewResourceId, objects);
		// TODO Auto-generated constructor stub
	}

	public CustomAdapter(Context context, int resource, int textViewResourceId) {
		super(context, resource, textViewResourceId);
		// TODO Auto-generated constructor stub
	}

	public CustomAdapter(Context context, int textViewResourceId,
			List<String> objects) {
		super(context, textViewResourceId, objects);
		// TODO Auto-generated constructor stub
	}

	public CustomAdapter(Context context, int textViewResourceId,
			String[] objects) {
		super(context, textViewResourceId, objects);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean areAllItemsEnabled() {
		// TODO Auto-generated method stub
		return isEnabled(0);
	}

	@Override
	public boolean isEnabled(int position) {
		if (position==0) {
			return enabled;
		}
		return super.isEnabled(position);
	}

	public void navigationToggle() {
		enabled = !enabled;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		System.out.println(enabled);
		if (position==0&&!enabled&&convertView!=null) {
			convertView.setAlpha((float)0.5);
		}
		return super.getView(position, convertView, parent);
	}
	

}
