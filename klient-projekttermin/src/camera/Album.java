package camera;

import java.util.ArrayList;
import java.util.List;

import map.CustomAdapter;
import models.ModelInterface;
import models.PictureModel;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Debug;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.ListView;
import assignment.AddAssignment;

import com.klient_projekttermin.ActivityConstants;
import com.klient_projekttermin.R;

import database.Database;

public class Album extends Activity implements OnItemClickListener {
	public boolean Visibility = true;

	public List<ModelInterface> imagesFromDB;
	public static String pic = "";
	private int callingActivity;
	private String[] pictureAlts = { "Skapa uppdrag med foto" };
	private int currentPictureId;
	private List<Bitmap> images = new ArrayList<Bitmap>();
	private List<Bitmap> imagesToAssignment = new ArrayList<Bitmap>();
	private Bitmap bitmap;
	private int currentPic = 0;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photo_gallery);
		callingActivity = getIntent().getIntExtra("calling-activity", 0);
		Gallery g = (Gallery) findViewById(R.id.Gallery);
		Database db = Database.getInstance(getApplicationContext());	
		imagesFromDB = db
				.getAllFromDB(new PictureModel(), getContentResolver());
			for (ModelInterface temp : imagesFromDB) {
				PictureModel p = (PictureModel) temp;
				BitmapFactory.Options ops = new BitmapFactory.Options();
				ops.inSampleSize = 2;
				
				bitmap = BitmapFactory.decodeByteArray(p.getPicture(),
						0, p.getPicture().length, ops);
				images.add(bitmap);
				ops.inSampleSize = 8;
				Bitmap b = BitmapFactory.decodeByteArray(p.getPicture(),
						0, p.getPicture().length, ops);
				imagesToAssignment.add(b);
			}
			
			g.setAdapter(new ImageAdapter(this));
			g.setSpacing(10);
			g.setOnItemClickListener(this);
	}
	
	private void setPictureId(int i){
		currentPic = i;
	}

	public class ImageAdapter extends BaseAdapter {
		private Context myContext;

		public ImageAdapter(Context c) {
			this.myContext = c;
		}

		public int getCount() {
			return images.size();
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView i = new ImageView(this.myContext);
			i.setBackgroundColor(Color.BLACK);
			i.setImageBitmap(images.get(position));
			/* Image should be scaled as width/height are set. */
			i.setScaleType(ImageView.ScaleType.FIT_XY);
			/* Set the Width/Height of the ImageView. */
			i.setLayoutParams(new Gallery.LayoutParams(700, 400));

			return i;
		}

		public float getScale(boolean focused, int offset) {
			/* Formula: 1 / (2 ^ offset) */
			return Math.max(0, 1.0f / (float) Math.pow(2, Math.abs(offset)));
		}
	}

	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
	}

	public class Gallery1 extends Gallery {
		public Gallery1(Context context) {
			super(context);
		}

		public Gallery1(Context context, AttributeSet attrs, int defStyle) {
			super(context, attrs, defStyle);
		}

		public Gallery1(Context context, AttributeSet attrs) {
			super(context, attrs);
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			return false;
		}

	}

	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		currentPictureId = arg2;
		switch (callingActivity) {
		case ActivityConstants.CAMERA:
			showPictureAlts();
			break;
		case ActivityConstants.ADD_PICTURE_TO_ASSIGNMENT:
			Intent intent = new Intent(Album.this, AddAssignment.class);
			intent.putExtra("calling-activity",
					ActivityConstants.ADD_PICTURE_TO_ASSIGNMENT);
			intent.putExtra(pic, imagesToAssignment.get(currentPictureId));
			setResult(ActivityConstants.RESULT_FROM_CAMERA, intent);
			finish();
		default:
			break;
		}
	}

	private void showPictureAlts() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Foto val");
		ListView modeList = new ListView(this);
		CustomAdapter modeAdapter = new CustomAdapter(this,
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
					createAssignmentFromPicture();
					finish();
					break;
				default:
					break;
				}
			}
		});
		dialog.show();
	}

	private void createAssignmentFromPicture() {
		Intent i = new Intent(Album.this, AddAssignment.class);
		i.putExtra(pic, currentPictureId);
		i.putExtra("calling-activity",
				ActivityConstants.ADD_PICTURE_TO_ASSIGNMENT);
		Album.this.startActivity(i);
	}
}