package th.co.shiftright.mobile.wheelions;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

import th.co.shiftright.mobile.wheelions.custom_controls.GalleryItemFullscreenItem;
import th.co.shiftright.mobile.wheelions.models.ImageItem;

import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

public class GalleryPreviewActivity extends BasedActivity {

	public static final String START_INDEX = "startIndex";
	public static final String ITEMS = "items";
	private ViewPager imageContainer;
	private int startIndex = 0;
	private ArrayList<ImageItem> images = null;
	private ArrayList<String> cachePaths = null;
	private File tempDir;
	private int itemHeight;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gallery_preview);
		imageContainer = (ViewPager) findViewById(R.id.fullscreenImageContainer);
		if (savedInstanceState != null) {
			if (savedInstanceState.containsKey(ITEMS)) {
				this.images = savedInstanceState.getParcelableArrayList(ITEMS);
			}
		}
		if (images == null) {
			this.images = getIntent().getParcelableArrayListExtra(ITEMS);
			this.startIndex = getIntent().getIntExtra(START_INDEX, 0);
		}
		String tempDirPath = Environment.getExternalStorageDirectory() + "/wheelionstempcache";
		tempDir = new File(tempDirPath);
		tempDir.mkdirs();
		cachePaths = new ArrayList<String>();
		int count = 0;
		for (ImageItem item : images) {
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
		itemHeight = WheelionsApplication.getScreenMaxSize(this);
		initializeComponent();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putParcelableArrayList(ITEMS, images);
		super.onSaveInstanceState(outState);
	}

	private void initializeComponent() {
		final ArrayList<GalleryItemFullscreenItem> galleryItems = new ArrayList<GalleryItemFullscreenItem>();
		for (int idx = 0; idx < images.size(); idx++) {
			String cacheFilePath = cachePaths.get(idx);
			if (images.get(idx) != null) {
				GalleryItemFullscreenItem item = new GalleryItemFullscreenItem(this);
				item.setImageData(this, images.get(idx), cacheFilePath);
				galleryItems.add(item);
			}
		}
		if (images.size() > 1) {
			galleryItems.add(0, null);
			galleryItems.add(null);
			startIndex++;
		}
		GalleryItemFullscreenPagerAdapter adapter = new GalleryItemFullscreenPagerAdapter(galleryItems);
		imageContainer.setAdapter(adapter);
		imageContainer.setCurrentItem(startIndex);
		galleryItems.get(startIndex).getImageContent(itemHeight);
		imageContainer.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageSelected(int arg0) {
				GalleryItemFullscreenItem item = galleryItems.get(arg0);
				if (item == null) {
					if (arg0 == 0) {
						imageContainer.setCurrentItem(images.size(), false);
					} else {
						imageContainer.setCurrentItem(1, false);
					}
				} else {
					item.getImageContent(itemHeight);
				}
			}
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {}
			@Override
			public void onPageScrollStateChanged(int arg0) {}
		});
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

class GalleryItemFullscreenPagerAdapter extends PagerAdapter
{
	private ArrayList<GalleryItemFullscreenItem> galleryImages;

	public GalleryItemFullscreenPagerAdapter(ArrayList<GalleryItemFullscreenItem> galleryImages) {
		this.galleryImages = galleryImages;
	}

	@Override
	public void destroyItem(View arg0, int arg1, Object arg2) {
		if (arg2 != null) {
			GalleryItemFullscreenItem currentItem = (GalleryItemFullscreenItem) arg2;
			((ViewPager) arg0).removeView(currentItem);
			currentItem.cancelTask();
			currentItem.clearBitmap();
		}
	}

	@Override
	public void finishUpdate(View arg0) {

	}

	@Override
	public int getCount() {
		return galleryImages.size();
	}

	@Override
	public Object instantiateItem(View arg0, int arg1) {
		GalleryItemFullscreenItem currentItem = galleryImages.get(arg1);
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
	public void restoreState(Parcelable arg0, ClassLoader arg1) {

	}

	@Override
	public Parcelable saveState() {
		return null;
	}

	@Override
	public void startUpdate(View arg0) {

	}

}
