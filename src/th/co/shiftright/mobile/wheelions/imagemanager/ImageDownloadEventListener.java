package th.co.shiftright.mobile.wheelions.imagemanager;

import android.graphics.Bitmap;

public interface ImageDownloadEventListener {

	public abstract void onDownloadImageFinish(String itemID, Bitmap photo);

}
