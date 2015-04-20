package th.co.shiftright.mobile.wheelions.models;

import com.google.android.gms.maps.model.LatLng;

public interface ILocation {

	public abstract String getId();	
	public abstract LatLng getLocation();

}
