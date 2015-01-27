package GoogleMaps;

import com.google.android.gms.maps.model.LatLng;

import android.location.Location;

public class HelperFunctions {

	public static LatLng convertToGeoPoint(Location location) {
		return new LatLng(location.getLatitude(), location.getLongitude());
	}

}
