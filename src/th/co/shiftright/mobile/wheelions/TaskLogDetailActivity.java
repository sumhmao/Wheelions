package th.co.shiftright.mobile.wheelions;

import java.util.Locale;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;

import th.co.shiftright.mobile.wheelions.imagemanager.ImageDownloadEventListener;
import th.co.shiftright.mobile.wheelions.models.AsyncTaskQueueItem;
import th.co.shiftright.mobile.wheelions.models.TaskLogData;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import GoogleMaps.GoogleMapsMapView;
import GoogleMaps.PlacePin;

public class TaskLogDetailActivity extends MapBasedActivity {

	public static final String TASK_LOG = "tasklog";
	private TaskLogData currentTaskLog = null;

	private TextView lblLogNote;
	private TextView lblLogTime;
	private GoogleMapsMapView mapView = null;
	private ImageView imgPhoto;
	private ProgressBar loading;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		currentTaskLog = getIntent().getParcelableExtra(TASK_LOG);
		super.onCreate(savedInstanceState);
		initializeComponents();
		if (currentTaskLog == null) {
			showToastMessage("Failed to load log");
			onBackPressed();
		}
	}

	private void initializeComponents() {
		lblLogNote = (TextView) findViewById(R.id.lblLogNote);
		lblLogTime = (TextView) findViewById(R.id.lblLogTime);
		loading = (ProgressBar) findViewById(R.id.loadingProgressBar);
		imgPhoto = (ImageView) findViewById(R.id.imgPhoto);

		if (currentTaskLog != null) {
			lblLogNote.setText(String.format(Locale.US, getString(R.string.log_note_format), currentTaskLog.getName()));
			lblLogTime.setText(String.format(Locale.US, getString(R.string.log_time_format), currentTaskLog.getFullTimeString()));
			if (currentTaskLog.getFirstPhoto() != null) {
				imgPhoto.setVisibility(View.VISIBLE);
			}
			AsyncTaskQueueItem task = currentTaskLog.getImageTask(new ImageDownloadEventListener() {
				@Override
				public void onDownloadImageFinish(String itemID, Bitmap photo) {
					loading.setVisibility(View.GONE);
					if (photo != null) {
						imgPhoto.setImageBitmap(photo);
					}
				}
			}, WheelionsApplication.getScreenWidth(TaskLogDetailActivity.this));
			if (task != null) {
				loading.setVisibility(View.VISIBLE);
				WheelionsApplication.executeAsyncTask(task);
			}
		}
	}

	@Override
	protected void initializePage() {
		setContentView(R.layout.activity_task_log_detail);
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
		if (currentTaskLog != null) {
			googleMaps.setSingleMode(true);
			PlacePin pin = mapView.addSelectedPlace(currentTaskLog);
			mapView.selectMarker(pin);
			mapView.goToPlace(currentTaskLog);
		}
	}

	@Override
	protected void onFailedToLoadGoogleMap() {
		Toast.makeText(this, "Unable to load google maps.", Toast.LENGTH_LONG).show();
		onBackPressed();
	}

}
