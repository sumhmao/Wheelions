package th.co.shiftright.mobile.wheelions.custom_controls;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import th.co.shiftright.mobile.wheelions.R;
import th.co.shiftright.mobile.wheelions.WheelionsApplication;
import th.co.shiftright.mobile.wheelions.imagemanager.ImageCache;
import th.co.shiftright.mobile.wheelions.imagemanager.ImageSize;
import th.co.shiftright.mobile.wheelions.models.ImageItem;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class GalleryItemFullscreenItem extends RelativeLayout {

	private RelativeLayout parentView;
	private ImageView imageView;
	private ProgressBar progressBar;
	private Bitmap currentImage = null;
	private AsyncTask<Void, Void, Void> downloadTask = null;
	private Activity activity;
	private ImageItem currentItem;
	private File cacheFilePath = null;
	public void setImageData(Activity activity, ImageItem item, String cacheFilePath) {
		this.activity = activity;
		this.currentItem = item;
		if (cacheFilePath != null) {
			this.cacheFilePath = new File(cacheFilePath);
		}
	}
	private int itemSize;

	public GalleryItemFullscreenItem(Context context) {
		super(context);
		init(context);
	}

	public GalleryItemFullscreenItem(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public GalleryItemFullscreenItem(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	private void init(Context context) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);
		parentView = (RelativeLayout) inflater.inflate(R.layout.galleryitemfullscreenitem, this, true);
		imageView = (ImageView) parentView.findViewById(R.id.imageDisplay);
		progressBar = (ProgressBar) parentView.findViewById(R.id.loadingProgressBar);
	}

	public void getImageContent(int size) {
		itemSize = size;
		clearBitmap();
		progressBar.setVisibility(View.VISIBLE);
		if (cacheFilePath.exists()) {
			currentImage = BitmapFactory.decodeFile(cacheFilePath.getAbsolutePath());
			showCurrentImage();
		} else {
			Bitmap cacheImage = ImageCache.getCacheImage(getContext(), size, currentItem);
			if (cacheImage != null) {
				saveCacheFile(cacheImage);
				currentImage = BitmapFactory.decodeFile(cacheFilePath.getAbsolutePath());
				showCurrentImage();
			} else {
				cancelTask();
				downloadImage();
			}
		}
	}

	private void downloadImage() {
		if (currentItem != null) {
			downloadTask = new AsyncTask<Void, Void, Void>() {
				@Override
				protected void onPreExecute() {
					if (!isOnline()) {
						Toast.makeText(activity, "No internet connection", Toast.LENGTH_LONG).show();
						this.cancel(true);
					}
					super.onPreExecute();
				}
				@Override
				protected Void doInBackground(Void... params) {
					currentImage = WheelionsApplication.decodeSampledBitmapFromURL(currentItem.getImageUrl(ImageSize.Original), itemSize);
					if (currentImage != null) {
						ImageCache.cacheImage(getContext(), itemSize, currentItem, currentImage);
						saveCacheFile(currentImage);
					}
					return null;
				}
				@Override
				protected void onPostExecute(Void result) {
					showCurrentImage();
					super.onPostExecute(result);
				}
			};
			WheelionsApplication.executeAsyncTask(downloadTask);
		}
	}

	private void saveCacheFile(Bitmap image) {
		try {
			if (image != null) {
				FileOutputStream fileOutput = new FileOutputStream(cacheFilePath);
				BufferedOutputStream bos = new BufferedOutputStream(fileOutput);
				image.compress(CompressFormat.JPEG, 100, bos);
				bos.flush();
				bos.close();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void showCurrentImage() {
		progressBar.setVisibility(View.INVISIBLE);
		if (currentImage != null) {
			imageView.setImageBitmap(currentImage);
			imageView.setVisibility(View.VISIBLE);
		} else {
			imageView.setVisibility(View.INVISIBLE);
		}
	}

	public void cancelTask() {
		if (downloadTask != null) {
			downloadTask.cancel(true);
		}
	}

	public void clearBitmap() {
		imageView.setImageBitmap(null);
		if (currentImage != null) {
			currentImage.recycle();
			currentImage = null;
		}
	}

	private boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) activity
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnected()) {
			return true;
		}
		return false;
	}

}
