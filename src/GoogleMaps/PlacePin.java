package GoogleMaps;

import th.co.shiftright.mobile.wheelions.R;
import th.co.shiftright.mobile.wheelions.models.TaskLogData;

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

	private TaskLogData currentLog;
	public TaskLogData getLog() {
		return currentLog;
	}

	public PlacePin(TaskLogData place) {
		this.currentLog = place;
	}

	@Override
	public LatLng getPosition() {
		return currentLog.getLocation();
	}

	public int getMarkerResId() {
		return R.drawable.ic_launcher;
	}

	public int getSelectedMarkerResId() {
		return R.drawable.ic_launcher;
	}
}
