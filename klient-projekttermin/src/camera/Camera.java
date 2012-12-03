package camera;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import models.PictureModel;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.klient_projekttermin.ActivityConstants;
import com.klient_projekttermin.SecureActivity;
import com.klient_projekttermin.R;

import database.Database;

public class Camera extends SecureActivity {

	private static final String BITMAP_STORAGE_KEY = "viewbitmap";
	private static final String IMAGEVIEW_VISIBILITY_STORAGE_KEY = "imageviewvisibility";
	// private ImageView mImageView;
	private Bitmap mImageBitmap;

	private static final int CAMERA_REQUEST = 1337;

	private String[] from = { "line1", "line2" };
	private int[] to = { android.R.id.text1, android.R.id.text2 };

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_camera_menu);
		// mImageView = (ImageView) findViewById(R.id.imageView1);
		mImageBitmap = null;
		ListView lv = (ListView) findViewById(android.R.id.list);
		lv.setAdapter(new SimpleAdapter(this, generateMenuContent(),
				android.R.layout.simple_list_item_2, from, to));
		lv.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				switch (arg2) {
				case 0:
					dispatchTakePictureIntent();
					break;
				case 1:
					Intent myIntent2 = new Intent(Camera.this, Album.class);
					myIntent2.putExtra("calling-activity",
							ActivityConstants.CAMERA);
					Camera.this.startActivity(myIntent2);
					break;
				default:
					break;
				}
			}
		});
	}

	private List<HashMap<String, String>> generateMenuContent() {
		List<HashMap<String, String>> content = new ArrayList<HashMap<String, String>>();
		String[] menuItems = { "Kamera", "Album" };
		String[] menuSubtitle = { "Ta bilder", "Visa upp bilder" };
		for (int i = 0; i < menuItems.length; i++) {
			HashMap<String, String> hashMap = new HashMap<String, String>();
			hashMap.put("line1", menuItems[i]);
			hashMap.put("line2", menuSubtitle[i]);
			content.add(hashMap);
		}
		return content;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_camera_menu, menu);
		return true;
	}

	private void dispatchTakePictureIntent() {
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(takePictureIntent, CAMERA_REQUEST);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data != null) {
			if (data.getExtras() == null) {
				dispatchTakePictureIntent();
			} else if (requestCode == 1337) {
				System.out.println("Data " + data.getExtras());
				Bitmap bm = (Bitmap) data.getExtras().get("data");
				Database db = Database.getInstance(getApplicationContext());
				db.addToDB(new PictureModel(getByteArray(bm)),
						getContentResolver());
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private byte[] getByteArray(Bitmap bitmap) {
		if (bitmap != null) {
			ByteArrayOutputStream byteArrayBitmapStream = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.PNG, 100,
					byteArrayBitmapStream);
			byte[] b = byteArrayBitmapStream.toByteArray();
			return b;
		} else {
			return new byte[2];
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putParcelable(BITMAP_STORAGE_KEY, mImageBitmap);
		outState.putBoolean(IMAGEVIEW_VISIBILITY_STORAGE_KEY,
				(mImageBitmap != null));
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		mImageBitmap = savedInstanceState.getParcelable(BITMAP_STORAGE_KEY);
	}
}
