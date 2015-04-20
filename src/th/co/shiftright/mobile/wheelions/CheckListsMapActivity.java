package th.co.shiftright.mobile.wheelions;

import java.util.ArrayList;

import th.co.shiftright.mobile.wheelions.models.CheckPoint;
import th.co.shiftright.mobile.wheelions.models.ILocation;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import GoogleMaps.GoogleMapsMapView;
import GoogleMaps.MapViewListener;

public class CheckListsMapActivity extends MapBasedActivity {

	public static final String CHECK_POINTS = "checkPoints";
	private ArrayList<CheckPoint> allCheckPoints;
	private GoogleMapsMapView mapView = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		allCheckPoints = getIntent().getParcelableArrayListExtra(CHECK_POINTS);
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void initializePage() {
		setContentView(R.layout.activity_checklists_map);
	}

	@Override
	protected GoogleMapsMapView getGoogleMapsView() {
		if (mapView == null) {
			GoogleMap map = ((MapFragment) getFragmentManager().findFragmentById(R.id.googleMapsView)).getMap();
			if (map != null) {
				mapView = new GoogleMapsMapView(this, map);
			}
		}
		return mapView;
	}

	@Override
	protected void onGoogleMapLoaded() {
		googleMaps.setMapViewListener(new MapViewListener() {
			@Override
			public void onZoom(CameraPosition cameraPosition) {}
			@Override
			public void onPlaceSelected(ILocation selectedPlace) {
				CheckPoint checkPoint = (CheckPoint) selectedPlace;
				if (!checkPoint.isCompleted()) {
					Intent intent = new Intent(CheckListsMapActivity.this, ReportStatusActivity.class);
					intent.putExtra(ReportStatusActivity.CHECK_POINT, checkPoint);
					startActivityForResult(intent, TaskDetailActivity.REPORT_CHECKPOINT);
				} else {
					Intent intent = new Intent(CheckListsMapActivity.this, CheckPointDetailActivity.class);
					intent.putExtra(CheckPointDetailActivity.CHECK_POINT, checkPoint);
					startActivityForResult(intent, TaskDetailActivity.REPORT_CHECKPOINT);
				}
			}
			@Override
			public void onPlaceDeSelected() {}
			@Override
			public void onPan(CameraPosition cameraPosition) {}
			@Override
			public void onMapRendered() {}
			@Override
			public void onClick(LatLng clickedPoint) {}
		});
		if (allCheckPoints != null && allCheckPoints.size() > 0) {
			for (CheckPoint checkPoint : allCheckPoints) {
				mapView.addPlace(checkPoint);
			}
		}
	}

	@Override
	protected void onFailedToLoadGoogleMap() {
		showToastMessage("Unable to load google maps.");
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case TaskDetailActivity.REPORT_CHECKPOINT: {
			if (resultCode == RESULT_OK) {
				setResult(RESULT_OK);
			}
			break;
		}
		default:
			break;
		}
	}

	@Override
	public void onBackPressed() {
		finish();
	}

}
