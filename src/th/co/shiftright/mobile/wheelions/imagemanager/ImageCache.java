package th.co.shiftright.mobile.wheelions.imagemanager;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Locale;

import th.co.shiftright.mobile.wheelions.WheelionsApplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;

public class ImageCache {

	private static final String CACHE_DIR_PATH = "/wheelions_cache";

	private static String getCacheDirPath(Context context) {
		String cacheDirPath = WheelionsApplication.getApplicationExternalPath(context) + CACHE_DIR_PATH;
		return cacheDirPath;
	}

	private static String getFileCacheDirPath(Context context, IImageItem imageItem) {
		String cacheDirPath = String.format(Locale.US, "%s/%s", getCacheDirPath(context), 
				ImageCategory.toString(imageItem.getImageCategory()));
		return cacheDirPath;
	}

	private static void deleteFiles(File file, boolean deleteDir) {
		if (file != null && file.exists()) {
			if (file.isDirectory()) {
				String[] children = file.list();
				for (int i = 0; i < children.length; i++) {
					File currentFile = new File(file, children[i]);
					if (currentFile.isDirectory()) {
						deleteFiles(currentFile, true);
					} else {
						currentFile.delete();	
					}
				}
				if (deleteDir) {
					file.delete();
				}
			} else {
				file.delete();
			}
		}
	}

	public static void clearAllCache(Context context) {
		String cacheDirPath = getCacheDirPath(context);
		File cacheDir = new File(cacheDirPath);
		cacheDir.mkdirs();
		if (cacheDir != null && cacheDir.exists()) {
			if (cacheDir.isDirectory()) {
				deleteFiles(cacheDir, false);
			}
		}
	}

	public static synchronized void cacheImage(Context context, int size, IImageItem imageItem, Bitmap photo) {
		String id = imageItem.getImageID();
		if (photo != null && WheelionsApplication.ifStringNotNullOrEmpty(id)) {
			try {
				String path = String.format(Locale.US, "%s/%d", getFileCacheDirPath(context, imageItem), size);
				File dir = new File(path);
				dir.mkdirs();
				String filePath = String.format(Locale.US, "%s/%s", path, id);
				File pic = new File(filePath);
				FileOutputStream fileOutput = new FileOutputStream(pic);
				BufferedOutputStream bos = new BufferedOutputStream(fileOutput);
				photo.compress(CompressFormat.PNG, 100, bos);
				bos.flush();
				bos.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static synchronized Bitmap getCacheImage(Context context, int size, IImageItem imageItem) {
		String id = imageItem.getImageID();
		if (WheelionsApplication.ifStringNotNullOrEmpty(id)) {
			try {
				String filePath = String.format(Locale.US, "%s/%d/%s", getFileCacheDirPath(context, imageItem), size, id);
				File pic = new File(filePath);
				if (pic != null && pic.exists()) {
					Bitmap bookImage = BitmapFactory.decodeFile(pic.getAbsolutePath());
					return bookImage;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

}
