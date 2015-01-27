package th.co.shiftright.mobile.wheelions;

import th.co.shiftright.mobile.wheelions.models.ConfirmationDialogListener;

import com.google.android.gms.common.ConnectionResult;

import GoogleMaps.GoogleMapsMapView;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;

public abstract class MapBasedActivity extends LocationBasedActivity {

	protected GoogleMapsMapView googleMaps;
	private ProgressDialog locationLoading;
	private boolean gotLocation = false;
	private Runnable waitTask;
	private final int waitInterval = 10000;
	private Handler handler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		locationLoading = WheelionsApplication.getLoadingDialog(this);
		locationLoading.setCancelable(false);
		initializePage();
		googleMaps = getGoogleMapsView();
		if (googleMaps == null) {
			onFailedToLoadGoogleMap();
		}
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		handler.postDelayed(waitTask, waitInterval);
		super.onConnected(connectionHint);
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		super.onConnectionFailed(result);
		locationLoading.cancel();
		showConfirmationRequest();
	}

	private void showConfirmationRequest() {
		String message = "Couldn't get user location. Continue anyway?";
		String okText = "Yes";
		String cancelText = "No";
		WheelionsApplication.showConfirmationDialog(this, message, okText, cancelText, new ConfirmationDialogListener() {
			@Override
			public void onUserCancel() {}
			@Override
			public void onUserAgree() {
				if (googleMaps != null) {
					googleMaps.initializeData(R.drawable.pin_me, getCurrentLocation());
					onGoogleMapLoaded();
				}
			}
		});
	}

	@Override
	protected void onLocationChanged() {
		if (waitTask != null) {
			handler.removeCallbacks(waitTask);
			waitTask = null;
		}
		if (!gotLocation) {
			gotLocation = true;
			locationLoading.cancel();
			if (googleMaps != null) {
				googleMaps.initializeData(R.drawable.pin_me, getCurrentLocation());
				onGoogleMapLoaded();
			}
		}
		googleMaps.setCurrentLocation(getCurrentLocation());
	}

	protected abstract void initializePage();
	protected abstract GoogleMapsMapView getGoogleMapsView();
	protected abstract void onGoogleMapLoaded();
	protected abstract void onFailedToLoadGoogleMap();

}
