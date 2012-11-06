package map;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.AutoCompleteTextView;

public class CustomACTV extends AutoCompleteTextView {

	public CustomACTV(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	public CustomACTV(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public CustomACTV(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

//	@Override
//    public boolean enoughToFilter() {
//        return true;
//    }
	@Override
    protected void onFocusChanged(boolean focused, int direction,
            Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        if (focused) {
            //performFiltering("", 0);
        }
    }

}
