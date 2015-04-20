package GoogleMaps;

import th.co.shiftright.mobile.wheelions.R;
import th.co.shiftright.mobile.wheelions.models.ILocation;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class PlacePin implements ClusterItem {

	private boolean startWithSelected = false;
	public void setStartWithSelected(boolean startWithSelected) {
		this.startWithSelected = startWithSelected;
	}
	public boolean startWithSelected() {
		return startWithSelected;
	}

	private ILocation currentLog;
	public ILocation getLog() {
		return currentLog;
	}

	public PlacePin(ILocation place) {
		this.currentLog = place;
	}

	@Override
	public LatLng getPosition() {
		return currentLog.getLocation();
	}

	public int getMarkerResId() {
		return R.drawable.pin;
	}

	public int getSelectedMarkerResId() {
		return R.drawable.pin;
	}
}
