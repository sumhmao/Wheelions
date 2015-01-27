package th.co.shiftright.mobile.wheelions;

import th.co.shiftright.mobile.wheelions.models.WheelionsData;
import GoogleMaps.HelperFunctions;
import android.location.Location;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

public abstract class LocationBasedActivity extends BasedActivity implements 
GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

	protected GoogleApiClient locationClient;
	protected LocationRequest locationRequest;
	private LatLng currentLocation;
	protected LatLng getCurrentLocation() {
		if (currentLocation == null) {
			if (locationClient.isConnected() && getLastLocation() != null) {
				currentLocation = HelperFunctions.convertToGeoPoint(getLastLocation());
			} else {
				return WheelionsData.instance(this).getLatestLocation();
			}
		}
		return currentLocation;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		locationClient = initLocationServiceConnection();
		currentLocation = null;
	}

	protected GoogleApiClient initLocationServiceConnection() {
		return new GoogleApiClient.Builder(this)
		.addApi(LocationServices.API)
		.addConnectionCallbacks(this)
		.addOnConnectionFailedListener(this)
		.build();
	}

	@Override
	protected void onStart() {
		super.onStart();
		locationClient.connect();
	}

	@Override
	protected void onStop() {
		locationClient.disconnect();
		super.onStop();
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {}

	@Override
	public void onConnectionSuspended(int cause) {}

	@Override
	public void onConnected(Bundle connectionHint) {
		locationRequest = LocationRequest.create();
		locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		locationRequest.setInterval(1000);
		LocationServices.FusedLocationApi.requestLocationUpdates(
				locationClient, locationRequest, this);
	}

	@Override
	public void onLocationChanged(Location location) {
		currentLocation = HelperFunctions.convertToGeoPoint(location);
		WheelionsData.instance(LocationBasedActivity.this).saveLocation(currentLocation);
		LocationBasedActivity.this.onLocationChanged();
	}

	private Location getLastLocation() {
		return LocationServices.FusedLocationApi.getLastLocation(locationClient);
	}

	protected abstract void onLocationChanged();

}
