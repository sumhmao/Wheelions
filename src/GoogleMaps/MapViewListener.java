package GoogleMaps;

import th.co.shiftright.mobile.wheelions.models.ILocation;

import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

public interface MapViewListener {
	public abstract void onMapRendered();
	public abstract void onPan(CameraPosition cameraPosition);
	public abstract void onZoom(CameraPosition cameraPosition);
	public abstract void onClick(LatLng clickedPoint);
	public abstract void onPlaceSelected(ILocation selectedPlace);
	public abstract void onPlaceDeSelected();
}
