package th.co.shiftright.mobile.wheelions.api;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import com.google.android.gms.maps.model.LatLng;

import th.co.shiftright.mobile.wheelions.WheelionsApplication;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Build;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;

public class APIRequest {

	private APIAsyncParam asyncParam = null;
	private APIAsyncBackendCall asyncBackend;
	private Locale defaultLocale = Locale.US;
	private HashMap<String, String> header;

	public APIRequest(APIAsyncParam param) {
		this.asyncParam = param;
		header = new HashMap<String, String>();
		header.put("Accept", "application/json");
	}

	public void setDefaultLocale(Locale defaultLocale) {
		this.defaultLocale = defaultLocale;
	}

	public void CancelRequest() {
		if (asyncBackend != null) {
			asyncBackend.cancelTask();
		}
	}

	private void RunRequest(String url, HttpMethod method) {
		CancelRequest();
		try {
			if (asyncParam != null) {
				HashMap<String, Object> parameters = getRequiredParameters();
				APIRequestParam requestParam;
				if (parameters == null) {
					requestParam = new APIRequestParam(url, method, asyncParam, header);
				} else {
					requestParam = new APIRequestParam(url, method, parameters, asyncParam, header);
				}
				asyncBackend = new APIAsyncBackendCall(requestParam);
				WheelionsApplication.executeAsyncTask(asyncBackend);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void RunFormRequest(String url, HttpMethod method, HashMap<String, Object> file) {
		CancelRequest();
		try {
			if (asyncParam != null) {
				HashMap<String, Object> parameters = getRequiredParameters();
				APIRequestParam requestParam;
				if (file == null && parameters == null) {
					requestParam = new APIRequestParam(url, method, asyncParam, header);
				} else if (file == null && parameters != null) {
					requestParam = new APIRequestParam(url, method, parameters, asyncParam, header);
				} else {
					if (parameters != null) {
						file.putAll(parameters);
					}
					requestParam = new APIRequestParam(url, method, file, asyncParam, header);
				}
				asyncBackend = new APIAsyncBackendCall(requestParam);
				WheelionsApplication.executeAsyncTask(asyncBackend);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private HashMap<String, Object> getRequiredParameters() {
		return null;
	}

	public String getDeviceUDID() {
		TelephonyManager tm = (TelephonyManager) asyncParam.getActivity().getSystemService(Context.TELEPHONY_SERVICE);
		String deviceID = tm.getDeviceId();
		if (deviceID != null) {
			return deviceID;
		} else {
			String androidID = Secure.getString(asyncParam.getActivity().getContentResolver(), Secure.ANDROID_ID);
			if (androidID != null) {
				return androidID;
			}
		}
		return "unknown";
	}

	public String getDeviceName() {
		String manufacturer = Build.MANUFACTURER;
		String model = Build.MODEL;
		if (model.startsWith(manufacturer)) {
			return capitalize(model);
		} else {
			return capitalize(manufacturer) + " " + model;
		}
	}

	private String capitalize(String s) {
		if (s == null || s.length() == 0) {
			return "";
		}
		char first = s.charAt(0);
		if (Character.isUpperCase(first)) {
			return s;
		} else {
			return Character.toUpperCase(first) + s.substring(1);
		}
	}

	public String getAndroidVersion() {
		return Build.VERSION.RELEASE;
	}

	private PackageInfo getPackageInfo() {
		PackageInfo pi = null;
		try {
			if (asyncParam.getActivity() != null) {
				pi = asyncParam.getActivity().getPackageManager().getPackageInfo(asyncParam.getActivity().getPackageName(), PackageManager.GET_ACTIVITIES);
			}
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		return pi;
	}

	public String getAppVersion() {
		PackageInfo versionInfo = getPackageInfo();
		return versionInfo.versionName;
	}

	public String getProviderName() {
		TelephonyManager manager = (TelephonyManager) asyncParam.getActivity().getSystemService(Context.TELEPHONY_SERVICE);
		String carrierName = manager.getNetworkOperatorName();
		if (carrierName != null && !carrierName.equals("")) {
			return carrierName;
		}
		return "unknown";
	}

	public String getApplicationID() {
		if (asyncParam.getActivity() != null) {
			return asyncParam.getActivity().getPackageName();
		}
		return "unknown";
	}

	private String getBackendUrl() {
		return WheelionsApplication.BACKEND_URL;
	}

	private String addGetParameter(String url, String key, String value, boolean firstParameter) {
		try {
			value = URLEncoder.encode(value, "utf-8");
			if (firstParameter) {
				url = String.format(defaultLocale, "%s?%s=%s", url, key, value);
			} else {
				url = String.format(defaultLocale, "%s&%s=%s", url, key, value);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return url;
	}

	private String addGetParameter(String url, String key, ArrayList<String> value, boolean firstParameter) {
		for (String currentValue : value) {
			try {
				currentValue = URLEncoder.encode(currentValue, "utf-8");
				if (firstParameter) {
					firstParameter = false;
					url = String.format(defaultLocale, "%s?%s[]=%s", url, key, currentValue);
				} else {
					url = String.format(defaultLocale, "%s&%s[]=%s", url, key, currentValue);
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return url;
	}

	public void login(String driverNo, String password) {
		String url = String.format(defaultLocale, "%s/login", getBackendUrl());
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("driver_license_no", driverNo);
		parameters.put("password", password);
		RunFormRequest(url, HttpMethod.POST, parameters);
	}

	public void getDriverJobs(String driverID) {
		String url = String.format(defaultLocale, "%s/drivers/%s/jobs", getBackendUrl(), driverID);
		RunRequest(url, HttpMethod.GET);
	}

	public void getJobDetail(String driverID, String jobID) {
		String url = String.format(defaultLocale, "%s/drivers/%s/jobs/%s/job_logs", getBackendUrl(), driverID, jobID);
		RunRequest(url, HttpMethod.GET);
	}

	public void reportTaskLog(String driverID, String jobID, LatLng location, String note, String code) {
		String url = String.format(defaultLocale, "%s/drivers/%s/jobs/%s/job_logs", getBackendUrl(), driverID, jobID);
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		if (location != null) {
			parameters.put("latitude", location.latitude);
			parameters.put("longitude", location.longitude);
		}
		parameters.put("note", note);
		parameters.put("job_status_code", code);
		RunFormRequest(url, HttpMethod.POST, parameters);
	}

	public void sendPhotoLog(String driverID, String jobID, List<Bitmap> photos, LatLng location, String note, String code) {
		if (photos != null) {
			String url = String.format(defaultLocale, "%s/drivers/%s/jobs/%s/job_logs", getBackendUrl(), driverID, jobID);
			HashMap<String, Object> parameters = new HashMap<String, Object>();
			int index = 0;
			for (Bitmap photo : photos) {
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				photo.compress(CompressFormat.JPEG, 100, bos);
				byte[] imgData = bos.toByteArray();
				parameters.put(String.format(Locale.US, "images[%d]", index) , imgData);
				index++;
			}
			if (location != null) {
				parameters.put("latitude", location.latitude);
				parameters.put("longitude", location.longitude);
			}
			parameters.put("note", note);
			parameters.put("job_status_code", code);
			RunFormRequest(url, HttpMethod.POST, parameters);
		}
	}

	public void getCheckList(String driverID, String jobID) {
		String url = String.format(defaultLocale, "%s/drivers/%s/jobs/%s/check_lists", getBackendUrl(), driverID, jobID);
		RunRequest(url, HttpMethod.GET);
	}

}


