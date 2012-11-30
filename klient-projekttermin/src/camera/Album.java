package camera;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;

import map.CustomAdapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.util.AttributeSet;
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

import com.google.gson.Gson;
import com.klient_projekttermin.ActivityConstants;
import com.klient_projekttermin.R;

public class Album extends Activity implements OnItemClickListener {
	public boolean Visibility = true;

	public ArrayList<Bitmap> images;
	public static String pic;
	private int callingActivity;
	private String[] pictureAlts = { "Skapa uppdrag med foto" };
	private int currentPictureId;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photo_gallery);
		callingActivity = getIntent().getIntExtra("calling-activity", 0);
		Gallery g = (Gallery) findViewById(R.id.Gallery);
		images = getArrayOfPictures();
		g.setAdapter(new ImageAdapter(this));
		g.setSpacing(10);
		g.setOnItemClickListener(this);
	}

	private ArrayList<Bitmap> getArrayOfPictures() {
		File file = new File(Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/Pictures/Album/");
		File imageList[] = file.listFiles();
		images = new ArrayList<Bitmap>();
		BitmapFactory.Options bitop = new BitmapFactory.Options();
		bitop.inSampleSize = 16;
		for (int i = 0; i < imageList.length; i++) {
			Bitmap b = BitmapFactory.decodeFile(imageList[i].getAbsolutePath(),
					bitop);
			images.add(b);
		}
		return images;
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
		System.out.println("HDFKJFNKDSMFKLA " + arg2);
		switch (callingActivity) {
		case ActivityConstants.CAMERA:
			showPictureAlts();
			System.out.println("SKICKA BILD TILL ASS");
			break;
		case ActivityConstants.ADD_PICTURE_TO_ASSIGNMENT:
			Intent intent = new Intent(Album.this, AddAssignment.class);
			intent.putExtra("calling-activity",
					ActivityConstants.ADD_PICTURE_TO_ASSIGNMENT);
			System.out.println(images.get(currentPictureId));
			intent.putExtra(pic, images.get(currentPictureId));
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
					System.out.println("I SHOW SEARCH ASLTS");
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