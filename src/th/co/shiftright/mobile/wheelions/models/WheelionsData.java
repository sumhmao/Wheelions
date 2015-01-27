package th.co.shiftright.mobile.wheelions.models;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Locale;

import org.json.JSONObject;

import com.google.android.gms.maps.model.LatLng;

import th.co.shiftright.mobile.wheelions.WheelionsApplication;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;

public class WheelionsData {

	private Context applicationContext;
	private User user;
	private String XAccessToken = null;
	private static final String KEY = "wheelions-data";
	private static final String USER_DATA = "user-json";
	private static final String TOKEN = "x-access-token";
	private static final String LATITUDE = "instaloei-latitude";
	private static final String LONGITUDE = "instaloei-longitude";
	private static WheelionsData instance = null;

	private static final String USER_DIR_PATH = "/wheelions_userinfo";
	private static final String USER_PIC_NAME = "wheelions_user_picture";
	private String userDirPath;
	private File userDir;
	private File userPic;

	private LatLng latestLocation = null;
	public LatLng getLatestLocation() {
		return latestLocation;
	}

	public static final String ACTION_LOGOUT = "user-logout-action";

	public static synchronized WheelionsData instance(Context context) {
		if (instance == null) {
			instance = new WheelionsData();
			instance.applicationContext = context.getApplicationContext();
			instance.restoreData();
		}
		return instance;
	}

	public WheelionsData() {
		userDirPath = WheelionsApplication.getApplicationExternalPath(applicationContext) + USER_DIR_PATH;
		userDir = new File(userDirPath);
		userDir.mkdirs();
		String filePath = String.format(Locale.US, "%s/%s", userDirPath, USER_PIC_NAME);
		userPic = new File(filePath);
	}

	private void restoreData() {
		try {
			SharedPreferences savedSession = applicationContext.getSharedPreferences(KEY, Context.MODE_PRIVATE);
			if (savedSession.contains(LATITUDE) && savedSession.contains(LONGITUDE)) {
				double latitude = Double.longBitsToDouble(savedSession.getLong(LATITUDE, 0));
				double longitude = Double.longBitsToDouble(savedSession.getLong(LONGITUDE, 0));
				latestLocation = new LatLng(latitude, longitude);
			}
			String jsonData = savedSession.getString(USER_DATA, null);
			if (jsonData != null) {
				user = new User(new JSONObject(jsonData));
			}
			XAccessToken = savedSession.getString(TOKEN, "");
		} catch (Exception e) {
			e.printStackTrace();
			XAccessToken = null;
		}
	}

	public void saveData(User user, String xAccessToken) {
		if (user != null && WheelionsApplication.ifStringNotNullOrEmpty(xAccessToken)) {
			this.XAccessToken = xAccessToken;
			this.user = user;
			SharedPreferences.Editor editor = applicationContext.getSharedPreferences(KEY, Context.MODE_PRIVATE).edit();
			editor.putString(USER_DATA, user.toJSON());
			editor.putString(TOKEN, xAccessToken);
			editor.commit();
		}
	}

	public String getUserID() {
		return user.getID();
	}

	public User getUser() {
		return user;
	}

	public String getXAccessToken() {
		return XAccessToken;
	}

	public boolean isUserLoggedIn() {
		return (user != null && WheelionsApplication.ifStringNotNullOrEmpty(XAccessToken));
	}

	private void notifyLogout() {
		if (applicationContext != null) {
			Intent broadcastIntent = new Intent();
			broadcastIntent.setAction(WheelionsData.ACTION_LOGOUT);
			applicationContext.sendBroadcast(broadcastIntent);
		}
	}

	public void saveUserImage(Bitmap image) {
		try {
			FileOutputStream fileOutput = new FileOutputStream(userPic);
			BufferedOutputStream bos = new BufferedOutputStream(fileOutput);
			image.compress(CompressFormat.PNG, 100, bos);
			bos.flush();
			bos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Bitmap getUserPicture() {
		if (userPic != null && userPic.exists()) {
			Bitmap userImage = BitmapFactory.decodeFile(userPic.getAbsolutePath());
			return userImage;
		} else {
			return null;
		}
	}

	public void saveLocation(LatLng location) {
		if (location != null) {
			latestLocation = location;
			Editor editor = applicationContext.getSharedPreferences(KEY, Context.MODE_PRIVATE).edit();
			editor.putLong(LATITUDE, Double.doubleToRawLongBits(location.latitude));
			editor.putLong(LONGITUDE, Double.doubleToRawLongBits(location.longitude));
			editor.commit();
		}
	}

	public void logout() {
		user = null;
		XAccessToken = null;
		SharedPreferences.Editor editor = applicationContext.getSharedPreferences(KEY, Context.MODE_PRIVATE).edit();
		editor.clear();
		editor.commit();
		if (userPic != null && userPic.exists()) {
			userPic.delete();
		}
		notifyLogout();
	}

}
