package th.co.shiftright.mobile.wheelions;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;

import th.co.shiftright.mobile.wheelions.custom_controls.CirclePageIndicator;
import th.co.shiftright.mobile.wheelions.models.LogImagePreviewItem;
import th.co.shiftright.mobile.wheelions.models.ImageItem;
import th.co.shiftright.mobile.wheelions.models.TaskLogData;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import GoogleMaps.GoogleMapsMapView;
import GoogleMaps.PlacePin;

public class TaskLogDetailActivity extends MapBasedActivity {

	public static final String TASK_LOG = "tasklog";
	private TaskLogData currentTaskLog = null;

	private TextView lblLogNote;
	private TextView lblLogTime;
	private GoogleMapsMapView mapView = null;
	private ViewPager photoPreview;
	private CirclePageIndicator pageIndicator;
	private ArrayList<String> cachePaths = null;
	private File tempDir;
	private int itemHeight;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		currentTaskLog = getIntent().getParcelableExtra(TASK_LOG);
		super.onCreate(savedInstanceState);
		initializeComponents();
		if (currentTaskLog == null) {
			showToastMessage("Failed to load log");
			onBackPressed();
		}
	}

	private void initializeComponents() {
		lblLogNote = (TextView) findViewById(R.id.lblLogNote);
		lblLogTime = (TextView) findViewById(R.id.lblLogTime);

		String tempDirPath = Environment.getExternalStorageDirectory() + "/wheelions_tempcache";
		tempDir = new File(tempDirPath);
		tempDir.mkdirs();
		cachePaths = new ArrayList<String>();
		int count = 0;
		for (ImageItem item : currentTaskLog.getAllPhotos()) {
			if (item != null && item.getOriginal() != null) {
				String filePath;
				if (WheelionsApplication.ifStringNotNullOrEmpty(item.getId())) {
					filePath = String.format(Locale.US, "%s/%s", tempDirPath, item.getId());
				} else {
					filePath = String.format(Locale.US, "%s/fullscreen%d", tempDirPath, count);
				}
				cachePaths.add(filePath);
			} else {
				cachePaths.add(null);
			}
			count++;
		}
		itemHeight = WheelionsApplication.getDPFromPixel(this, 250);

		final ArrayList<LogImagePreviewItem> galleryItems = new ArrayList<LogImagePreviewItem>();
		for (int idx = 0; idx < currentTaskLog.getAllPhotos().size(); idx++) {
			String cacheFilePath = cachePaths.get(idx);
			if (currentTaskLog.getAllPhotos().get(idx) != null) {
				LogImagePreviewItem item = new LogImagePreviewItem(this);
				item.setImageData(this, currentTaskLog.getAllPhotos().get(idx), cacheFilePath);
				galleryItems.add(item);
			}
		}
		LogImagePreviewPagerAdapter adapter = new LogImagePreviewPagerAdapter(galleryItems);

		photoPreview = (ViewPager) findViewById(R.id.imagePreview);
		photoPreview.setAdapter(adapter);
		pageIndicator = (CirclePageIndicator) findViewById(R.id.pageIndicator);
		pageIndicator.setPageColor(Color.GRAY);
		pageIndicator.setFillColor(getResources().getColor(R.color.blue));
		pageIndicator.setViewPager(photoPreview);
		galleryItems.get(0).getImageContent(itemHeight);
		pageIndicator.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int arg0) {
				galleryItems.get(arg0).getImageContent(itemHeight);
			}
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {}
			@Override
			public void onPageScrollStateChanged(int arg0) {}
		});

		if (currentTaskLog != null) {
			lblLogNote.setText(String.format(Locale.US, getString(R.string.log_note_format), currentTaskLog.getName()));
			lblLogTime.setText(String.format(Locale.US, getString(R.string.log_time_format), currentTaskLog.getFullTimeString()));
			if (currentTaskLog.getAllPhotos().size() > 0) {
				photoPreview.setVisibility(View.VISIBLE);
				pageIndicator.setVisibility(View.VISIBLE);
			}
		}
	}

	@Override
	protected void initializePage() {
		setContentView(R.layout.activity_task_log_detail);
	}

	@Override
	protected GoogleMapsMapView getGoogleMapsView() {
		if (mapView == null) {
			GoogleMap map = ((MapFragment) getFragmentManager().findFragmentById(R.id.googleMapsView)).getMap();
			if (map != null) {
				mapView = new GoogleMapsMapView(this, map);
			}
		}
		return mapView;
	}

	@Override
	protected void onGoogleMapLoaded() {
		if (currentTaskLog != null) {
			googleMaps.setSingleMode(true);
			PlacePin pin = mapView.addSelectedPlace(currentTaskLog);
			mapView.selectMarker(pin);
			mapView.goToPlace(currentTaskLog);
		}
	}

	@Override
	protected void onFailedToLoadGoogleMap() {
		Toast.makeText(this, "Unable to load google maps.", Toast.LENGTH_LONG).show();
		onBackPressed();
	}

	@Override
	public void onBackPressed() {
		if (tempDir != null && tempDir.exists()) {
			if (tempDir.isDirectory()) {
				String[] children = tempDir.list();
				for (int i = 0; i < children.length; i++) {
					new File(tempDir, children[i]).delete();
				}
			} 
			tempDir.delete();
		}
		super.onBackPressed();
	}

}

class LogImagePreviewPagerAdapter extends PagerAdapter
{
	private ArrayList<LogImagePreviewItem> galleryImages;

	public LogImagePreviewPagerAdapter(ArrayList<LogImagePreviewItem> galleryImages) {
		this.galleryImages = galleryImages;
	}

	@Override
	public void destroyItem(View arg0, int arg1, Object arg2) {
		if (arg2 != null) {
			LogImagePreviewItem currentItem = (LogImagePreviewItem) arg2;
			((ViewPager) arg0).removeView(currentItem);
			currentItem.cancelTask();
			currentItem.clearBitmap();
		}
	}

	@Override
	public void finishUpdate(View arg0) {}

	@Override
	public int getCount() {
		return galleryImages.size();
	}

	@Override
	public Object instantiateItem(View arg0, int arg1) {
		LogImagePreviewItem currentItem = galleryImages.get(arg1);
		if (currentItem != null) {
			((ViewPager)arg0).addView(currentItem);
		}
		return currentItem;
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	@Override
	public void restoreState(Parcelable arg0, ClassLoader arg1) {}

	@Override
	public Parcelable saveState() {
		return null;
	}

	@Override
	public void startUpdate(View arg0) {}

}
