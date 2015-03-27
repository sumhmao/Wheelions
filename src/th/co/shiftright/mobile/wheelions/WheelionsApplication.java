package th.co.shiftright.mobile.wheelions;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import th.co.shiftright.mobile.wheelions.custom_controls.CustomProgressDialog;
import th.co.shiftright.mobile.wheelions.models.ConfirmationDialogListener;
import th.co.shiftright.mobile.wheelions.models.WheelionsData;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Bitmap.Config;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.util.TypedValue;
import android.view.Display;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class WheelionsApplication extends Application {

	public static final String BACKEND_URL = "http://testing.pugping.com/api";
	public static final String RESOURCE_URL = "http://testing.pugping.com/";

	public static final int CONNECTION_TIMEOUT = 45000;
	public static final int SOCKET_TIMEOUT = 45000;
	public static final int IMAGE_CACHE_PERCENTAGE = 20;

	public static final int IMAGE_MAX_SIZE = 800;

	@Override
	public void onCreate() {
		super.onCreate();
	}

	public static int getDPFromPixel(Context context, int pixel) {
		int dp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, pixel, context.getResources().getDisplayMetrics());
		return dp;
	}

	public static CustomProgressDialog getLoadingDialog(Context context) {
		CustomProgressDialog dialog = new CustomProgressDialog(context);
		dialog.setCancelable(true);
		return dialog;
	}

	public static CustomProgressDialog getLoadingDialog(Context context, String title, String message) {
		CustomProgressDialog dialog = new CustomProgressDialog(context);
		dialog.setHeader(title);
		dialog.setDetail(message);
		dialog.setCancelable(true);
		return dialog;
	}

	public static void hideKeyboard(Activity activity) {
		if (activity.getCurrentFocus() != null) {
			InputMethodManager inputManager =
					(InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
			inputManager.hideSoftInputFromWindow(
					activity.getCurrentFocus().getWindowToken(),
					InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	public static void showKeyboard(Activity activity) {
		InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (inputMethodManager != null) {
			inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
		}
	}

	public static File getApplicationExternalPath(Context context) {
		if (context != null) {
			return context.getApplicationContext().getExternalFilesDir(null);
		} else {
			return Environment.getExternalStorageDirectory();
		}
	}

	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	public static void removeGlobalLayoutListener(ViewTreeObserver vto, ViewTreeObserver.OnGlobalLayoutListener listener) {
		if (vto != null && listener != null) {
			if (vto.isAlive()) {
				if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
					vto.removeGlobalOnLayoutListener(listener);
				} else {
					vto.removeOnGlobalLayoutListener(listener);
				}
			}
		}
	}

	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	public static int getScreenWidth(Context context) {
		WindowManager wm = null;
		if (context instanceof Activity) {
			wm = ((Activity) context).getWindowManager();
		} else {
			wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		}
		int Measuredwidth = 0;
		Point size = new Point();
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2){
			wm.getDefaultDisplay().getSize(size);
			Measuredwidth = size.x;
		}else{
			Display d = wm.getDefaultDisplay();
			Measuredwidth = d.getWidth();
		}
		return Measuredwidth;
	}

	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	public static int getScreenHeight(Context context) {
		WindowManager wm = null;
		if (context instanceof Activity) {
			wm = ((Activity) context).getWindowManager();
		} else {
			wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		}
		int Measuredheight = 0;
		Point size = new Point();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			wm.getDefaultDisplay().getSize(size);
			Measuredheight = size.y;
		} else {
			Display d = wm.getDefaultDisplay();
			Measuredheight = d.getHeight();
		}
		return Measuredheight;
	}

	public static int getScreenMaxSize(Context context) {
		int width = WheelionsApplication.getScreenWidth(context);
		int height = WheelionsApplication.getScreenHeight(context);
		return Math.max(width, height);
	}

	public static boolean API_11() {
		return Build.VERSION.SDK_INT >= 11;
	}

	@SuppressLint("NewApi")
	public static void executeAsyncTask(AsyncTask<Void, Void, Void> task) {
		if (WheelionsApplication.API_11()) {
			task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		} else {
			task.execute();
		}
	}

	public static boolean ifStringNotNullOrEmpty(String string) {
		if (string != null && !string.trim().equals("")) {
			return true;
		}
		return false;
	}

	public static boolean checkJSONObjectForKey(String key, JSONObject object) {
		if (key != null && !key.equals("") && object != null && object != JSONObject.NULL) {
			try {
				Object value = object.get(key);
				if (value != null && value != JSONObject.NULL) {
					if (value instanceof JSONObject) {
						if (((JSONObject) value).length() > 0) {
							return true;
						} else {
							return false;
						}
					}
					return true;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	public static boolean getBoolValueFromResponse(Object object) {
		if (object instanceof String) {
			String string = (String)object;
			if (string.equalsIgnoreCase("yes") || string.equalsIgnoreCase("true") || string.equalsIgnoreCase("available"))
				return true;
			else
				return false;
		} else if (object instanceof Boolean) {
			return ((Boolean)object).booleanValue();
		} else return Boolean.getBoolean(object.toString());
	}

	public static boolean checkIfEditTextIsNullOrEmpty(EditText editText) {
		String text = editText.getText().toString();
		if (text.equals("")) {
			return true;
		}
		return false;
	}

	public static String getEditTextValue(EditText editText) {
		String text = editText.getText().toString();
		return text;
	}

	public static int calculateInSampleSize(BitmapFactory.Options options, int maxSize) {
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > maxSize || width > maxSize) {
			final int halfHeight = height / 2;
			final int halfWidth = width / 2;

			while ((halfHeight / inSampleSize) > maxSize || (halfWidth / inSampleSize) > maxSize) {
				inSampleSize *= 2;
			}
		}

		return inSampleSize;
	}

	public static Bitmap decodeSampledBitmapFromURL(String urlPath, int maxSize) {
		try {
			final BitmapFactory.Options options = new BitmapFactory.Options();
			options.inPreferredConfig = Bitmap.Config.RGB_565;
			options.inJustDecodeBounds = true;
			URL url = new URL(urlPath);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoInput(true);
			connection.connect();
			InputStream input = connection.getInputStream();
			BitmapFactory.decodeStream(input, null, options);
			input.close();
			url = new URL(urlPath);
			connection = (HttpURLConnection) url.openConnection();
			connection.setDoInput(true);
			connection.connect();
			input = connection.getInputStream();
			options.inSampleSize = calculateInSampleSize(options, maxSize);
			options.inPreferredConfig = Bitmap.Config.RGB_565;
			options.inJustDecodeBounds = false;
			Bitmap output = BitmapFactory.decodeStream(input, null, options);
			input.close();
			return output;
		} catch (Exception e) {
			return null;
		}
	}

	public static Bitmap decodeSampledBitmapFromPath(String pathName,
			int maxSize) {
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = Bitmap.Config.RGB_565;
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(pathName, options);
		options.inSampleSize = calculateInSampleSize(options, maxSize);
		options.inPreferredConfig = Bitmap.Config.RGB_565;
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(pathName, options);
	}

	public static Bitmap getBitmapFromUri(Activity activity, Uri imageUri) {
		try {
			ContentResolver cr = activity.getContentResolver();
			cr.notifyChange(imageUri, null);
			Bitmap currentImage = android.provider.MediaStore.Images.Media.getBitmap(cr, imageUri);
			return currentImage;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Bitmap maskImage(Bitmap original, Bitmap mask) {
		int size = Math.min(original.getWidth(), original.getHeight());
		mask = Bitmap.createScaledBitmap(mask, size, size, true);
		int startX = (original.getWidth() - size) / 2;
		int startY = (original.getHeight() - size) / 2;
		original = Bitmap.createBitmap(original, startX, startY, size, size);
		Bitmap result = Bitmap.createBitmap(mask.getWidth(), mask.getHeight(), Config.ARGB_8888);
		Canvas mCanvas = new Canvas(result);
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
		paint.setDither(true);
		mCanvas.drawBitmap(original, 0, 0, null);
		mCanvas.drawBitmap(mask, 0, 0, paint);
		paint.setXfermode(null);
		return result;
	}

	public static Bitmap maskImageCircle(Context context, Bitmap original) {
		Bitmap mask = BitmapFactory.decodeResource(context.getResources(), R.drawable.round_mask);
		return WheelionsApplication.maskImage(original, mask);
	}

	public static Date getDateFromString(String dateString) {
		// 2014-05-01 00:00:00
		if (!WheelionsApplication.ifStringNotNullOrEmpty(dateString)) {
			return null;
		}
		try {
			dateString = dateString.replace("T", " ");
			dateString = dateString.replace("Z", "+00:00");
			SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy'-'MM'-'dd' 'HH':'mm':'ss", Locale.getDefault());
			if (dateString.length() > 19) {
				String prefix = dateString.substring(0, 19);
				String suffix = dateString.substring(19);
				if (suffix.startsWith(".")) {
					for (int i = 1; i < suffix.length(); i++) {
						if (!Character.isDigit(suffix.charAt(i))) {
							suffix = suffix.substring(i);
							break;
						}
					}
				}
				dateString = prefix + suffix;
			}
			return dateFormatter.parse(dateString);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String formatTimeToString(Date eventDate) {
		Locale locale = Locale.US;
		String timeString = null;
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd' 'MMM' 'yyyy", Locale.getDefault());
		timeString = String.format(locale, "%s", dateFormat.format(eventDate));
		return timeString;
	}

	public static String formatDateToStandardString(Date eventDate) {
		Locale locale = Locale.US;
		String timeString = null;
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy'-'MM'-'dd'T'HH':'mm':'ssZ", Locale.getDefault());
		timeString = String.format(locale, "%s", dateFormat.format(eventDate));
		return timeString;
	}

	public static String formatShortDateString(Date eventDate) {
		Locale locale = Locale.US;
		String timeString = null;
		SimpleDateFormat dateFormat = new SimpleDateFormat("d/M HH:mm", Locale.getDefault());
		timeString = String.format(locale, "%s", dateFormat.format(eventDate));
		return timeString;
	}

	public static String formatLongDateString(Date eventDate) {
		Locale locale = Locale.US;
		String timeString = null;
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd' 'MMM' 'yyyy HH:mm", Locale.getDefault());
		timeString = String.format(locale, "%s", dateFormat.format(eventDate));
		return timeString;
	}

	public static String calculateTimePastString(Date eventDate) {
		Locale locale = Locale.US;
		String timePastString;
		Calendar currentDate = Calendar.getInstance();
		double interval =  currentDate.getTime().getTime() - eventDate.getTime();
		int minutes = (int)(interval / 60000);
		int hours = (int)(minutes / 60);
		int days = (int)(hours / 24);
		Calendar eventCalendar = Calendar.getInstance();
		eventCalendar.setTime(eventDate);
		if (days > 1 && eventCalendar.get(Calendar.YEAR) != currentDate.get(Calendar.YEAR)) {
			DateFormat dateFormat = SimpleDateFormat.getDateInstance(DateFormat.SHORT);
			timePastString = String.format(locale, "%s", dateFormat.format(eventDate));
		} else if (days > 1) {
			DateFormat dateFormat = new SimpleDateFormat("d/M HH:mm", Locale.getDefault());
			timePastString = String.format(locale, "%s", dateFormat.format(eventDate));
		} else if (days == 1) {
			DateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
			timePastString = String.format("Yesterday %s", dateFormat.format(eventDate));
		} else if (hours >= 1) {
			if (hours == 1) {
				timePastString = String.format(locale, "An hour ago");
			} else {
				timePastString = String.format(locale, "%d hours ago", hours);
			}
		} else if (minutes > 1) {
			timePastString = String.format(locale, "%d minutes ago", minutes);
		} else if (minutes == 1) {
			timePastString = String.format(locale, "%d minute ago", minutes);
		} else {
			timePastString = String.format(locale, "%d seconds ago", (int)(interval / 1000));
		}
		return timePastString;
	}

	public static String getCurrentUserAuthToken(Context context) {
		if (WheelionsData.instance(context).isUserLoggedIn()) {
			return WheelionsData.instance(context).getXAccessToken();
		} else {
			return "";
		}
	}

	public static String getCurrentUserID(Context context) {
		if (WheelionsData.instance(context).isUserLoggedIn()) {
			return WheelionsData.instance(context).getUserID();
		} else {
			return "";
		}
	}

	public static void showConfirmationDialog(Activity activity, String message,
			String okText, String cancelText,
			final ConfirmationDialogListener listener) {
		if (listener != null) {
			AlertDialog.Builder builder = new AlertDialog.Builder(activity)
			.setMessage(message)
			.setPositiveButton(okText, new Dialog.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialogInterface, int i) {
					listener.onUserAgree();
				}
			})
			.setNegativeButton(cancelText, new Dialog.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					listener.onUserCancel();
				}
			});
			builder.create().show();
		}
	}

	public static String getResourceUrl(String url) {
		String rootUrl = WheelionsApplication.RESOURCE_URL;
		if (url != null && !url.equals("") && rootUrl != null && !rootUrl.equals("")) {
			if (rootUrl.endsWith("/")) {
				rootUrl = rootUrl.substring(0, rootUrl.length() - 1);
			}
			url = String.format("%s/%s", rootUrl, url);
			return url;
		}
		return "";
	}

}
