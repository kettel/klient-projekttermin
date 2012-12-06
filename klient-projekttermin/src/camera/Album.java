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
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import assignment.AddAssignment;

import com.klient_projekttermin.ActivityConstants;
import com.klient_projekttermin.R;

import database.Database;

public class Album extends Activity implements OnItemClickListener {
	public boolean Visibility = true;

	private List<ModelInterface> imagesFromDB;
	private int callingActivity;
	private String[] pictureAlts = { "Skapa uppdrag med foto" };
	private int currentPictureId;
	private List<Bitmap> images = new ArrayList<Bitmap>();
	private Bitmap bitmap;
	private Database db;
	private ImageView selectedImage;
	private ImageView stepLeftImage;
	private ImageView stepRightImage;
	Gallery g;
	private int changePosition = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photo_gallery);
		callingActivity = getIntent().getIntExtra("calling-activity", 0);

		// När man markerat en bild i galerisnurran ska den man valt visas i en
		// större imageView.
		selectedImage = (ImageView) findViewById(R.id.imageView_selectedPic);
		stepLeftImage = (ImageView) findViewById(R.id.imageView_stepLeft);
		stepRightImage = (ImageView) findViewById(R.id.imageView_stepRight);
		scaleUpView(stepLeftImage);
		scaleUpView(stepRightImage);

		setSelectedImageOnClickListener();
		setStepLeftImageOnClickListener();
		setStepRightImageOnClickListener();

		db = Database.getInstance(getApplicationContext());
		imagesFromDB = db
				.getAllFromDB(new PictureModel(), getContentResolver());
		for (ModelInterface temp : imagesFromDB) {
			PictureModel p = (PictureModel) temp;
			BitmapFactory.Options ops = new BitmapFactory.Options();
			ops.inSampleSize = 2;
			bitmap = BitmapFactory.decodeByteArray(p.getPicture(), 0,
					p.getPicture().length, ops);
			images.add(bitmap);
		}

		g = (Gallery) findViewById(R.id.Gallery);
		g.setAdapter(new ImageAdapter(this));
		g.setSpacing(10);
		g.setOnItemClickListener(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	public void scaleUpView(ImageView image) {
		image.setScaleX((float) 1.8);
		image.setScaleY((float) 1.8);
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
			if (!imagesFromDB.isEmpty()) {
				i.setImageBitmap(images.get(changePosition));
			}
			
			/* Image should be scaled as width/height are set. */
			i.setScaleType(ImageView.ScaleType.FIT_XY);
			/* Set the Width/Height of the ImageView. */
			i.setLayoutParams(new Gallery.LayoutParams(200, 200));
			//changePosition = position;
			return i;
		}

		public float getScale(boolean focused, int offset) {
			/* Formula: 1 / (2 ^ offset) */
			return Math.max(0, 1.0f / (float) Math.pow(2, Math.abs(offset)));
		}
	}

	private void setSelectedImageOnClickListener() {
		selectedImage.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// currentPictureId = arg2;
				switch (callingActivity) {
				case ActivityConstants.CAMERA:
					showPictureAlts();
					break;
				case ActivityConstants.ADD_PICTURE_TO_ASSIGNMENT:
					addPicToAss();
					break;
				default:
					break;
				}
			}
		});
	}

	private void setStepLeftImageOnClickListener() {
		stepLeftImage.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (changePosition != 0) {
					changePosition--;
				}
				selectedImage.setImageBitmap(getPic(changePosition));
				g.setSelection(changePosition, true);
			}
		});
	}

	private void setStepRightImageOnClickListener() {
		stepRightImage.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (changePosition != g.getCount() - 1) {
					changePosition++;
				}
				selectedImage.setImageBitmap(getPic(changePosition));
				g.setSelection(changePosition, true);
			}
		});
	}

	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		currentPictureId = arg2;
		selectedImage.setImageBitmap(getPic(currentPictureId));
	}

	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		currentPictureId = arg2;
		selectedImage.setImageBitmap(getPic(currentPictureId));
	}

	private void addPicToAss() {
		Intent intent = new Intent(Album.this, AddAssignment.class);
		intent.putExtra("pic", currentPictureId);
		setResult(ActivityConstants.RESULT_FROM_CAMERA, intent);
		finish();
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
		i.putExtra("pic", currentPictureId);
		i.putExtra("calling-activity",
				ActivityConstants.ADD_PICTURE_TO_ASSIGNMENT);
		Album.this.startActivity(i);
	}

	private Bitmap getPic(int id) {
		db = Database.getInstance(getApplicationContext());
		List<ModelInterface> pics = db.getAllFromDB(new PictureModel(),
				getContentResolver());
		PictureModel p = (PictureModel) pics.get(id);
		BitmapFactory.Options ops = new BitmapFactory.Options();
		ops.inSampleSize = 2;
		Bitmap bitmap = BitmapFactory.decodeByteArray(p.getPicture(), 0,
				p.getPicture().length, ops);
		return bitmap;
	}
}