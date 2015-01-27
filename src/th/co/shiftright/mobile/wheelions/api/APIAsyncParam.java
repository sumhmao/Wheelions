package th.co.shiftright.mobile.wheelions.api;

import android.app.Activity;
import android.app.ProgressDialog;

public class APIAsyncParam {

	private APIRequestListener listener = null;
	private ProgressDialog progressDialog;
	private Activity activity;
	private boolean isMultipart = false;
	
	public Activity getActivity() {
		return activity;
	}
	public APIRequestListener getListener() {
		return listener;
	}
	public ProgressDialog getProgressDialog() {
		return progressDialog;
	}
	public boolean isMultipart() {
		return isMultipart;
	}
	
	public APIAsyncParam(Activity activity, APIRequestListener listener, ProgressDialog dialog) {
		this.activity = activity;
		this.listener = listener;
		this.progressDialog = dialog;
	}
	
	public APIAsyncParam(Activity activity, APIRequestListener listener, ProgressDialog dialog, boolean isMultipart) {
		this.activity = activity;
		this.listener = listener;
		this.progressDialog = dialog;
		this.isMultipart = isMultipart;
	}
}
