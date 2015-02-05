package th.co.shiftright.mobile.wheelions.custom_controls;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Calendar;
import java.util.Locale;

import th.co.shiftright.mobile.wheelions.R;
import th.co.shiftright.mobile.wheelions.WheelionsApplication;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.util.AttributeSet;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class TakePhotoButton extends Button implements OnClickListener {

	public static final int TAKE_PICTURE = 1;
	public static final int CHOOSE_PICTURE = 2;
	private Activity activity;
	private boolean isCopyImage = false;
	public void setCopyImage(boolean isCopyImage) {
		this.isCopyImage = isCopyImage;
	}
	private Uri imageUri = null;
	public Uri getImageUri() {
		return imageUri;
	}
	private TakePhotoButtonListener listener = null;
	public void setTakePhotoButtonListener(TakePhotoButtonListener listener) {
		this.listener = listener;
	}

	public TakePhotoButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public TakePhotoButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public TakePhotoButton(Context context) {
		super(context);
		init(context);
	}

	private void init(Context context) {
		setOnClickListener(this);
	}

	public void initializeData(Activity activity) {
		this.activity = activity;
	}

	@Override
	public void onClick(View v) {
		String fileName = Long.toString(Calendar.getInstance().getTime().getTime());
		File photoDir = new File(WheelionsApplication.getApplicationExternalPath(activity) + "/tempphotos");
		photoDir.mkdirs();
		File photo = new File(photoDir, fileName);
		imageUri = Uri.fromFile(photo);
		SelectImageSourceDialog dialog = new SelectImageSourceDialog(getContext());
		dialog.initializeData(activity, imageUri);
		dialog.show();
	}

	private int getImageRotate(ExifInterface exif) {
		int rotate = 0;
		int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
		switch(orientation) {
		case ExifInterface.ORIENTATION_ROTATE_270:
			rotate = 270;
			break;
		case ExifInterface.ORIENTATION_ROTATE_180:
			rotate = 180;
			break;
		case ExifInterface.ORIENTATION_ROTATE_90:
			rotate = 90;
			break;
		}
		return rotate;
	}

	private Uri scaleImageAndReturnURI(Bitmap originalImg, File imageFile, int rotate) {
		try {
			if (originalImg.getWidth() > originalImg.getHeight()) {
				originalImg = Bitmap.createScaledBitmap(originalImg, WheelionsApplication.IMAGE_MAX_SIZE,
						(originalImg.getHeight() * WheelionsApplication.IMAGE_MAX_SIZE) / originalImg.getWidth(), true);
			} else {
				originalImg = Bitmap.createScaledBitmap(originalImg, (originalImg.getWidth() * WheelionsApplication.IMAGE_MAX_SIZE) / originalImg.getHeight(),
						WheelionsApplication.IMAGE_MAX_SIZE, true);
			}
			if (rotate != 0) {
				Matrix mtx = new Matrix();
				mtx.postRotate(rotate);
				originalImg = Bitmap.createBitmap(originalImg, 0, 0,
						originalImg.getWidth(), originalImg.getHeight(), mtx, true);
			}
			FileOutputStream fileOutput = new FileOutputStream(imageFile);
			BufferedOutputStream bos = new BufferedOutputStream(fileOutput);
			originalImg.compress(CompressFormat.JPEG, 100, bos);
			bos.flush();
			bos.close();
			Uri currentUri = Uri.fromFile(imageFile);
			originalImg.recycle();
			return currentUri;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private void copy(String srcPath) {
		try {
			FileInputStream in = new FileInputStream(srcPath);
			String fileName = String.format("%s.jpg", Long.toString(Calendar.getInstance().getTime().getTime()));
			String destPath = Environment.getExternalStorageDirectory() + "/Wheelions";
			File outputDir = new File(destPath);
			outputDir.mkdirs();
			File output = new File(outputDir, fileName);
			FileOutputStream out = new FileOutputStream(output);
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			saveMediaEntry(output.getAbsolutePath(), fileName);
			in.close();
			out.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	private void saveMediaEntry(String imagePath, String title) {
		try {
			ExifInterface exif = new ExifInterface(imagePath);
			ContentValues v = new ContentValues();
			v.put(Images.Media.TITLE, title);
			v.put(Images.Media.DISPLAY_NAME, "SuperBowl Image");
			v.put(Images.Media.DESCRIPTION, "Taken by SuperBowl");
			long currentDate = Calendar.getInstance().getTime().getTime();
			v.put(Images.Media.DATE_ADDED, currentDate);
			v.put(Images.Media.DATE_TAKEN, currentDate);
			v.put(Images.Media.DATE_MODIFIED, currentDate) ;
			v.put(Images.Media.MIME_TYPE, "image/jpeg");
			v.put(Images.Media.ORIENTATION, getImageRotate(exif));

			File f = new File(imagePath) ;
			File parent = f.getParentFile() ;
			String path = parent.toString().toLowerCase(Locale.US);
			String name = parent.getName().toLowerCase(Locale.US);
			v.put(Images.ImageColumns.BUCKET_ID, path.hashCode());
			v.put(Images.ImageColumns.BUCKET_DISPLAY_NAME, name);
			v.put(Images.Media.SIZE, f.length()) ;
			f = null ;

			float [] location = new float[2];
			if( exif.getLatLong(location) ) {
				v.put(Images.Media.LATITUDE, location[0]);
				v.put(Images.Media.LONGITUDE, location[1]);
			}
			v.put("_data", imagePath) ;
			ContentResolver c = activity.getContentResolver() ;
			c.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, v);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case TAKE_PICTURE: {
			if (resultCode == Activity.RESULT_OK) {
				try {
					File imageFile = new File(new URI(imageUri.toString()));
					if (isCopyImage) {
						copy(imageFile.getAbsolutePath());
					}
					Bitmap originalImg = WheelionsApplication.decodeSampledBitmapFromPath(imageFile.getAbsolutePath(), 
							WheelionsApplication.IMAGE_MAX_SIZE);
					ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
					int rotate = getImageRotate(exif);
					Uri currentImageUri = scaleImageAndReturnURI(originalImg, imageFile, rotate);
					originalImg.recycle();
					if (currentImageUri != null) {
						imageUri = currentImageUri;
						imageDidSelect();
					}
				} catch (Exception e) {
					e.printStackTrace();
					Toast.makeText(activity, "Failed to load image", Toast.LENGTH_LONG).show();
				}
			}
			break;
		}
		case CHOOSE_PICTURE: {
			if (resultCode == Activity.RESULT_OK) {
				try {
					Uri selectedImage = data.getData();
					String[] filePathColumn = {MediaStore.Images.Media.DATA};
					Cursor cursor = activity.getContentResolver().query(selectedImage, filePathColumn, null, null, null);
					cursor.moveToFirst();
					int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
					String filePath = cursor.getString(columnIndex);
					cursor.close();
					Bitmap selectedImageImg = WheelionsApplication.decodeSampledBitmapFromPath(filePath, 
							WheelionsApplication.IMAGE_MAX_SIZE);
					ExifInterface exif = new ExifInterface(filePath);
					int rotate = getImageRotate(exif);
					String fileName = Long.toString(Calendar.getInstance().getTime().getTime());
					File photoDir = new File(WheelionsApplication.getApplicationExternalPath(activity) + "/tempphotos");
					photoDir.mkdirs();
					File photo = new File(photoDir, fileName);
					Uri currentImageUri = scaleImageAndReturnURI(selectedImageImg, photo, rotate);
					selectedImageImg.recycle();
					if (currentImageUri != null) {
						imageUri = currentImageUri;
						imageDidSelect();
					}
				} catch (Exception e) {
					Toast.makeText(activity, "Failed to load image", Toast.LENGTH_LONG).show();
				}
			}
			break;
		}
		}
	}

	private void imageDidSelect() {
		if (listener != null) {
			listener.onImageSelected(imageUri);
		}
	}

	private void deleteFileByUri(Uri fileUri) {
		try {
			File imageFile = new File(new URI(fileUri.toString()));
			imageFile.delete();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void clearAllData() {
		if (imageUri != null) {
			deleteFileByUri(imageUri);
			imageUri = null;
		}
	}

}

class SelectImageSourceDialog extends AlertDialog {

	private Activity activity;
	private Uri imageUri;

	public SelectImageSourceDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
	}

	public SelectImageSourceDialog(Context context, int theme) {
		super(context, theme);
	}

	public SelectImageSourceDialog(Context context) {
		super(context);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.select_image_source_dialog);
		getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
				| WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
		initializeComponents();
	}

	private void initializeComponents() {
		Button btnChoosePhoto = (Button) findViewById(R.id.btnChoosePhoto);
		btnChoosePhoto.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				openPhotoLibrary();
			}
		});
		Button btnTakePhoto = (Button) findViewById(R.id.btnTakePhoto);
		btnTakePhoto.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				openCamera();
			}
		});
		Button btnCancel = (Button) findViewById(R.id.btnCancel);
		btnCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
	}

	private void openCamera() {
		Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
		intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
		activity.startActivityForResult(intent, TakePhotoButton.TAKE_PICTURE);
		dismiss();
	}

	private void openPhotoLibrary() {
		Intent intent = new Intent(Intent.ACTION_PICK);
		intent.setType("image/*");
		activity.startActivityForResult(intent, TakePhotoButton.CHOOSE_PICTURE);
		dismiss();
	}

	public void initializeData(Activity activity, Uri imageUri) {
		this.activity = activity;
		this.imageUri = imageUri;
	}

}
