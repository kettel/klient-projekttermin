package camera;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;

import map.CustomAdapter;
import messageFunction.CreateMessage;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
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

import com.example.klien_projekttermin.ActivityConstants;
import com.example.klien_projekttermin.R;
import com.google.gson.Gson;

public class PhotoGallery extends Activity {

	private ImageView image;
	private ArrayList<Bitmap> images;
	private Gallery ga;
	private int currentPictureId;
	private int callingActivity;
	private String[] pictureAlts = { "Skicka meddelande med foto", "Skapa uppdrag med foto" };
	public static String picture;

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
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		callingActivity = getIntent().getIntExtra("calling-activity", 0);
		Gson gson = new Gson();
		switch (callingActivity) {
		case ActivityConstants.CAMERA:
			showPictureAlts(item);
			break;
		case ActivityConstants.ADD_PICTURE_TO_ASSIGNMENT:
			Intent intent = new Intent(PhotoGallery.this, AddAssignment.class);
			String encodedImage = getStringFromBitmap(images.get(currentPictureId));
			JSONObject jsonObj = null;
			try {
				jsonObj = new JSONObject("{\"image\":\" + encodedImage + \"}");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			intent.putExtra("calling-activity", ActivityConstants.ADD_PICTURE_TO_ASSIGNMENT);
			intent.putExtra(picture, jsonObj.toString());
			this.startActivity(intent);
		default:
			break;
		}
		return true;
	}
	
	private String getStringFromBitmap(Bitmap bitmapPicture) {
		 /*
		 * This functions converts Bitmap picture to a string which can be
		 * JSONified.
		 * */
		 final int COMPRESSION_QUALITY = 100;
		 String encodedImage;
		 ByteArrayOutputStream byteArrayBitmapStream = new ByteArrayOutputStream();
		 bitmapPicture.compress(Bitmap.CompressFormat.PNG, COMPRESSION_QUALITY,
		 byteArrayBitmapStream);
		 byte[] b = byteArrayBitmapStream.toByteArray();
		 encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
		 return encodedImage;
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
		finish();
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
