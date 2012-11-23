package camera;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import loginFunction.InactivityListener;
import map.CustomAdapter;
import messageFunction.CreateMessage;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.ListView;
import assignment.AddAssignment;
import assignment.SimpleEditTextItemAdapter;

import com.google.gson.Gson;
import com.klient_projekttermin.ActivityConstants;
import com.klient_projekttermin.R;

public class PhotoGallery extends InactivityListener implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2109014410227191853L;
	private ImageView image;
	private ArrayList<Bitmap> images;
	private Gallery ga;
	private int currentPictureId;
	private int callingActivity;
	private String[] pictureAlts = { "Skicka meddelande med foto", "Skapa uppdrag med foto" };
	public static String picture;
	@SuppressWarnings("unused")
	private HashMap<Integer, String> content;
	public static String contents;
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photo_gallery);

		final ArrayList<Bitmap> map = getArrayOfPictures();
		ga = (Gallery) findViewById(R.id.Gallery);
		ga.setAdapter(new ImageAdapter(this));

		image = (ImageView) findViewById(R.id.ImageView);
		ga.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				currentPictureId = arg2;
				image.setImageBitmap(map.get(arg2));
			}
		});
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_photo_gallery, menu);
		return true;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		callingActivity = getIntent().getIntExtra("calling-activity", 0);
		content = (HashMap<Integer, String>) getIntent().getSerializableExtra(SimpleEditTextItemAdapter.items);
		switch (callingActivity) {
		case ActivityConstants.CAMERA:
			showPictureAlts(item);
			break;
		case ActivityConstants.ADD_PICTURE_TO_ASSIGNMENT:
			Intent intent = new Intent(PhotoGallery.this, AddAssignment.class);
			intent.putExtra("calling-activity", ActivityConstants.ADD_PICTURE_TO_ASSIGNMENT);
			System.out.println(images.get(currentPictureId));
			intent.putExtra(picture, images.get(currentPictureId));
			setResult(ActivityConstants.RESULT_FROM_CAMERA, intent);
			finish();
		default:
			break;
		}
		return true;
	}

	private void showPictureAlts(MenuItem item){
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
					sendPictureWithMessage();
					break;
				case 1:
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
	
	private void sendPictureWithMessage(){
		Gson gson = new Gson();
		Intent i = new Intent(PhotoGallery.this, CreateMessage.class);
		i.putExtra(picture, gson.toJson(images.get(currentPictureId)));
		i.putExtra("calling-activity", ActivityConstants.ADD_PICTURE_TO_MESSAGE);
		PhotoGallery.this.startActivity(i);
	}
	
	private void createAssignmentFromPicture(){
		Gson gson = new Gson();
		Intent i = new Intent(PhotoGallery.this, AddAssignment.class);
		i.putExtra(picture, gson.toJson(images.get(currentPictureId)));
		i.putExtra("calling-activity", ActivityConstants.ADD_PICTURE_TO_ASSIGNMENT);
		PhotoGallery.this.startActivity(i);
	}
	
	public class ImageAdapter extends BaseAdapter {

		private Context ctx;
		private int imageBackground;

		public ImageAdapter(Context c) {
			ctx = c;
			TypedArray ta = obtainStyledAttributes(R.styleable.Gallery1);
			imageBackground = ta.getResourceId(
					R.styleable.Gallery1_android_galleryItemBackground, 1);
			ta.recycle();
		}

		public int getCount() {
			return images.size();
		}

		public Object getItem(int arg0) {
			return arg0;
		}

		public long getItemId(int position) {
			return 0;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView iv = new ImageView(ctx);
			iv.setImageBitmap(images.get(position));
			iv.setScaleType(ImageView.ScaleType.FIT_XY);
			iv.setLayoutParams(new Gallery.LayoutParams(150, 120));
			iv.setBackgroundResource(imageBackground);
			return iv;
		}
	}
}
