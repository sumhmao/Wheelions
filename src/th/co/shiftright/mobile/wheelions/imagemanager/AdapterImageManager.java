package th.co.shiftright.mobile.wheelions.imagemanager;

import th.co.shiftright.mobile.wheelions.WheelionsApplication;
import th.co.shiftright.mobile.wheelions.models.AsyncTaskQueue;
import th.co.shiftright.mobile.wheelions.models.AsyncTaskQueueItem;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.widget.ArrayAdapter;

public class AdapterImageManager {

	private Context mContext;
	private LruCache<String, Bitmap> cache;
	private int downloadCount = 0;
	private AsyncTaskQueue downloadTasks = new AsyncTaskQueue();
	private ArrayAdapter<?> adapter;
	private ImageSize imageSize;

	private int notifyLimit = 5;
	public void setNotifyLimit(int notifyLimit) {
		this.notifyLimit = notifyLimit;
	}

	private ImageDownloadEventListener customDownloadListener = null;
	public void setCustomImageDownloadListener(ImageDownloadEventListener customDownloadListener) {
		this.customDownloadListener = customDownloadListener;
	}

	public AdapterImageManager(Context context, ArrayAdapter<?> adapter, ImageSize imageSize) {
		this.mContext = context;
		this.adapter = adapter;
		this.imageSize = imageSize;
		final int memClass = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
		final int cacheSize = (1024 * 1024 * ((memClass * WheelionsApplication.IMAGE_CACHE_PERCENTAGE) / 100));
		cache = new LruCache<String, Bitmap>(cacheSize){
			protected int sizeOf(String key, Bitmap bitmap) {
				return bitmap.getRowBytes() * bitmap.getHeight(); 
			}
		};
	}

	public AdapterImageManager(Context context, ArrayAdapter<?> adapter, ImageSize imageSize, int cachePercent) {
		this.mContext = context;
		this.adapter = adapter;
		this.imageSize = imageSize;
		final int memClass = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
		final int cacheSize = (1024 * 1024 * ((memClass * cachePercent) / 100));
		cache = new LruCache<String, Bitmap>(cacheSize){
			protected int sizeOf(String key, Bitmap bitmap) {
				return bitmap.getRowBytes() * bitmap.getHeight(); 
			}
		};
	}

	public static AsyncTaskQueueItem getImageDownloadTask(final ImageDownloadEventListener listener, final ImageSize size, final int viewSize, 
			final IImageItem imageItem, AsyncTaskQueue queue) {
		final String url = imageItem.getImageUrl(size);
		if (url != null) {
			AsyncTaskQueueItem downloadPhotoAsync = new AsyncTaskQueueItem(queue){
				private String id;
				private Bitmap photo;
				@Override
				protected void onItemPreExecute() {
					id = imageItem.getImageID();
				}
				@Override
				protected void onItemDoInBackground() {
					try {
						photo = WheelionsApplication.decodeSampledBitmapFromURL(url, viewSize);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				@Override
				protected void onItemPostExecute() {
					if (listener != null) {
						listener.onDownloadImageFinish(id, photo);
					}
				};
			};
			return downloadPhotoAsync;
		} 
		return null;
	}

	public Bitmap getImage(final IImageItem imageItem, final int size) {
		if (imageItem != null) {
			Bitmap image = cache.get(imageItem.getImageID());
			if (image == null) {
				Bitmap temp = ImageCache.getCacheImage(mContext, size, imageItem);
				if (temp != null) {
					cache.put(imageItem.getImageID(), temp);
					image = cache.get(imageItem.getImageID());
				} else if (!downloadTasks.containsKey(imageItem.getImageID())) {
					ImageDownloadEventListener listener = new ImageDownloadEventListener() {
						@Override
						public void onDownloadImageFinish(String itemID, Bitmap photo) {
							if (photo != null) {
								ImageCache.cacheImage(mContext, size, imageItem, photo);
								downloadCount++;
								cache.put(itemID, photo);
								if (customDownloadListener != null) {
									customDownloadListener.onDownloadImageFinish(itemID, photo);
								}
							}
							if (adapter != null && (downloadCount % notifyLimit == 0 || downloadTasks.size() == 0)) {
								adapter.notifyDataSetChanged();
							}
						}
					};
					AsyncTaskQueueItem task = AdapterImageManager.getImageDownloadTask(listener, imageSize, size, imageItem, downloadTasks);
					if (task != null) {
						downloadTasks.put(imageItem.getImageID(), task);
					}
				}
			}
			return image;
		} 
		return null;
	}

}
