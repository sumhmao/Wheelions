package th.co.shiftright.mobile.wheelions.api;

import android.app.Activity;

public interface APIRequestListener {

	public abstract void doInBackground(final APIRequestResult result);
	public abstract void onRequestFinish(final APIRequestResult result);
	public abstract void onConnectionTimeout(final Activity activity);
	public abstract void onNoInternetOrLocation(final Activity activity);

}
