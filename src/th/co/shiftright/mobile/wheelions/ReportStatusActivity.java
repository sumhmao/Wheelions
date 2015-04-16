package th.co.shiftright.mobile.wheelions;

import java.util.ArrayList;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;

import th.co.shiftright.mobile.wheelions.adapters.StatusSpinnerAdapter;
import th.co.shiftright.mobile.wheelions.api.APIAsyncParam;
import th.co.shiftright.mobile.wheelions.api.APIRequest;
import th.co.shiftright.mobile.wheelions.api.APIRequestListener;
import th.co.shiftright.mobile.wheelions.api.APIRequestResult;
import th.co.shiftright.mobile.wheelions.custom_controls.CirclePageIndicator;
import th.co.shiftright.mobile.wheelions.custom_controls.CustomProgressDialog;
import th.co.shiftright.mobile.wheelions.custom_controls.ImagePreviewItem;
import th.co.shiftright.mobile.wheelions.custom_controls.ImagePreviewItemListener;
import th.co.shiftright.mobile.wheelions.custom_controls.TakePhotoButton;
import th.co.shiftright.mobile.wheelions.custom_controls.TakePhotoButtonListener;
import th.co.shiftright.mobile.wheelions.models.CheckPoint;
import th.co.shiftright.mobile.wheelions.models.ConfirmationDialogListener;
import th.co.shiftright.mobile.wheelions.models.TaskData;
import th.co.shiftright.mobile.wheelions.models.TaskStatus;
import GoogleMaps.GoogleMapsMapView;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class ReportStatusActivity extends MapBasedActivity {

	public static final String TASK = "task";
	public static final String CHECK_POINT = "checkpoint";
	private TaskData currentTask;
	private CheckPoint checkPoint;
	private GoogleMapsMapView mapView = null;
	private LinearLayout checkPointDataPanel;
	private TextView lblCheckPointTitle;
	private TextView lblCheckPointDetail;
	private Spinner snStatusSpinner;
	private ArrayList<TaskStatus> allStatus;
	private StatusSpinnerAdapter adapter;
	private TakePhotoButton btnAddPhoto;
	private ViewPager photoPreview;
	private PreviewPagerAdapter previewAdapter;
	private CirclePageIndicator pageIndicator;
	private Button btnCancel;
	private Button btnReport;
	private APIRequest statusRequest;
	private CustomProgressDialog loading;
	private ArrayList<Uri> allPhotos;
	private ImagePreviewItemListener previewListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		currentTask = getIntent().getParcelableExtra(TASK);
		checkPoint = getIntent().getParcelableExtra(CHECK_POINT);
		super.onCreate(savedInstanceState);
		allPhotos = new ArrayList<Uri>();
		if (currentTask != null) {
			allStatus = new ArrayList<TaskStatus>();
			allStatus.add(new TaskStatus("02", "รับของ"));
			allStatus.add(new TaskStatus("03", "รายงานตำแหน่ง"));
			allStatus.add(new TaskStatus("04", "ถึงปลายทาง"));
			allStatus.add(new TaskStatus("99", "ปิดงาน"));
		}
		previewListener = new ImagePreviewItemListener() {
			@Override
			public void onImageRemove(Uri currentImage) {
				allPhotos.remove(currentImage);
				refreshPhotosPreview();
			}
		};
		initializeComponents();
		initializeRequests();
	}

	private void initializeComponents() {
		snStatusSpinner = (Spinner) findViewById(R.id.snStatusSpinner);
		if (currentTask != null) {
			adapter = new StatusSpinnerAdapter(this, allStatus);
			snStatusSpinner.setAdapter(adapter);
		} else {
			snStatusSpinner.setVisibility(View.GONE);
			if (checkPoint != null) {
				checkPointDataPanel = (LinearLayout) findViewById(R.id.checkPointDataPanel);
				checkPointDataPanel.setVisibility(View.VISIBLE);
				lblCheckPointTitle = (TextView) findViewById(R.id.lblCheckPointTitle);
				lblCheckPointDetail = (TextView) findViewById(R.id.lblCheckPointDetail);
				lblCheckPointTitle.setText(checkPoint.getTitle());
				lblCheckPointDetail.setText(checkPoint.getDescription());
			}
		}
		btnAddPhoto = (TakePhotoButton) findViewById(R.id.btnAddPhoto);
		btnAddPhoto.initializeData(this);
		btnAddPhoto.setTakePhotoButtonListener(new TakePhotoButtonListener() {
			@Override
			public void onImageSelected(Uri imageUri) {
				if (imageUri != null) {
					allPhotos.add(imageUri);
					refreshPhotosPreview();
				}
			}
		});
		photoPreview = (ViewPager) findViewById(R.id.imagePreview);
		previewAdapter = new PreviewPagerAdapter();
		photoPreview.setAdapter(previewAdapter);
		pageIndicator = (CirclePageIndicator) findViewById(R.id.pageIndicator);
		pageIndicator.setPageColor(Color.GRAY);
		pageIndicator.setFillColor(getResources().getColor(R.color.blue));
		pageIndicator.setViewPager(photoPreview);

		btnCancel = (Button) findViewById(R.id.btnCancel);
		btnCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		btnReport = (Button) findViewById(R.id.btnReport);
		btnReport.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showConfirmationDialog("Upload this status ?", "Yes", "Cancel", new ConfirmationDialogListener() {
					@Override
					public void onUserAgree() {
						doSendData();
					}
					@Override
					public void onUserCancel() {}
				});
			}
		});
	}

	private void initializeRequests() {
		loading = WheelionsApplication.getLoadingDialog(this);
		statusRequest = new APIRequest(new APIAsyncParam(this, new APIRequestListener() {
			@Override
			public void onRequestFinish(APIRequestResult result) {
				if (result.isStatusInRange(200, 200)) {
					setResult(RESULT_OK);
					finish();
				} else {
					showToastMessage("Failed to send log.");
				}
			}
			@Override
			public void onNoInternetOrLocation(Activity activity) {}
			@Override
			public void onConnectionTimeout(Activity activity) {
				showToastMessage("Failed to send log.");
			}
			@Override
			public void doInBackground(APIRequestResult result) {}
		}, loading, true));
	}

	private void refreshPhotosPreview() {
		if (allPhotos.size() > 0) {
			previewAdapter.clearItems();
			for (Uri image : allPhotos) {
				ImagePreviewItem item = new ImagePreviewItem(this);
				item.setData(this, image);
				item.setItemListener(previewListener);
				previewAdapter.addNewItem(item);
			}
			previewAdapter.notifyDataSetChanged();
			pageIndicator.notifyDataSetChanged();
			photoPreview.setVisibility(View.VISIBLE);
			pageIndicator.setVisibility(View.VISIBLE);
		} else {
			previewAdapter.clearItems();
			previewAdapter.notifyDataSetChanged();
			pageIndicator.notifyDataSetChanged();
			photoPreview.setVisibility(View.GONE);
			pageIndicator.setVisibility(View.GONE);
		}
	}

	@Override
	protected void onDestroy() {
		btnAddPhoto.clearAllData();
		super.onDestroy();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		btnAddPhoto.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void initializePage() {
		setContentView(R.layout.activity_report_status);
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
	protected void onGoogleMapLoaded() {}

	@Override
	protected void onFailedToLoadGoogleMap() {
		showToastMessage("Unable to load google maps.");
	}

	private void doSendData() {
		if (currentTask != null) {
			TaskStatus status = (TaskStatus) snStatusSpinner.getSelectedItem();
			if (allPhotos.size() == 0) {
				statusRequest.reportTaskLog(WheelionsApplication.getCurrentUserID(ReportStatusActivity.this),
						currentTask.getId(), getCurrentLocation(), status.getTitle(), status.getCode());
			} else {
				ArrayList<Bitmap> photos = new ArrayList<Bitmap>();
				for (Uri uri : allPhotos) {
					Bitmap photo = WheelionsApplication.getBitmapFromUri(ReportStatusActivity.this, uri);
					photos.add(photo);
				}
				statusRequest.sendPhotoLog(WheelionsApplication.getCurrentUserID(ReportStatusActivity.this),
						currentTask.getId(), photos, getCurrentLocation(), status.getTitle(), status.getCode());
			}
		} else if (checkPoint != null) {
			if (allPhotos.size() > 0) {
				ArrayList<Bitmap> photos = new ArrayList<Bitmap>();
				for (Uri uri : allPhotos) {
					Bitmap photo = WheelionsApplication.getBitmapFromUri(ReportStatusActivity.this, uri);
					photos.add(photo);
				}
				statusRequest.sendCheckPointLog(WheelionsApplication.getCurrentUserID(ReportStatusActivity.this),
						checkPoint.getJobId(), checkPoint.getId(), photos, getCurrentLocation(), checkPoint.getTitle());
			} else {
				statusRequest.sendCheckPointLog(WheelionsApplication.getCurrentUserID(ReportStatusActivity.this),
						checkPoint.getJobId(), checkPoint.getId(), null, getCurrentLocation(), checkPoint.getTitle());	
			}
		}
	}

	@Override
	public void onBackPressed() {
		if (allPhotos.size() > 0) {
			showConfirmationDialog("All data will be lost ?", "Yes", "Cancel", new ConfirmationDialogListener() {
				@Override
				public void onUserAgree() {
					ReportStatusActivity.super.onBackPressed();
				}
				@Override
				public void onUserCancel() {}
			});
		} else {
			super.onBackPressed();
		}
	}
}

class PreviewPagerAdapter extends PagerAdapter {
	private ArrayList<ImagePreviewItem> galleryImages;

	public PreviewPagerAdapter() {
		galleryImages = new ArrayList<ImagePreviewItem>();
	}

	public void clearItems() {
		galleryImages.clear();
	}

	public void addNewItem(ImagePreviewItem item) {
		galleryImages.add(item);
	}

	@Override
	public void destroyItem(View arg0, int arg1, Object arg2) {
		if (arg2 != null) {
			ImagePreviewItem currentItem = (ImagePreviewItem) arg2;
			((ViewPager) arg0).removeView(currentItem);
		}
	}

	@Override
	public void finishUpdate(View arg0) {}

	@Override
	public int getCount() {
		return galleryImages.size();
	}

	@Override
	public Object instantiateItem(View arg0, int arg1) {
		ViewGroup.LayoutParams param = new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		ImagePreviewItem currentItem = galleryImages.get(arg1);
		if (currentItem != null) {
			((ViewPager)arg0).addView(currentItem, param);
		}
		return currentItem;
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	@Override
	public void restoreState(Parcelable arg0, ClassLoader arg1) {}

	@Override
	public Parcelable saveState() {
		return null;
	}

	@Override
	public void startUpdate(View arg0) {}

	@Override
	public int getItemPosition(Object object) {
		return POSITION_NONE;
	}
}
