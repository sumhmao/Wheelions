package th.co.shiftright.mobile.wheelions;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import th.co.shiftright.mobile.wheelions.adapters.TaskLogAdapter;
import th.co.shiftright.mobile.wheelions.api.APIAsyncParam;
import th.co.shiftright.mobile.wheelions.api.APIRequest;
import th.co.shiftright.mobile.wheelions.api.APIRequestListener;
import th.co.shiftright.mobile.wheelions.api.APIRequestResult;
import th.co.shiftright.mobile.wheelions.custom_controls.CustomProgressDialog;
import th.co.shiftright.mobile.wheelions.custom_controls.ReportStatusDialog;
import th.co.shiftright.mobile.wheelions.custom_controls.ReportStatusDialogListener;
import th.co.shiftright.mobile.wheelions.models.TaskData;
import th.co.shiftright.mobile.wheelions.models.TaskLogData;
import th.co.shiftright.mobile.wheelions.models.TaskStatus;
import th.co.shiftright.mobile.wheelions.models.WheelionsData;
import th.co.shiftright.mobile.wheelions.pulltorefresh.PullToRefreshBase;
import th.co.shiftright.mobile.wheelions.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import th.co.shiftright.mobile.wheelions.pulltorefresh.PullToRefreshListView;
import android.app.Activity;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class TaskDetailActivity extends LocationBasedActivity {

	public static final String TASK = "task";

	private TaskData currentTask;
	private TextView lblTaskDescription;
	private TextView lblTaskNo;
	private TextView lblFrom;
	private TextView lblTo;
	private Button btnReportStatus;
	private Button btnAddPhoto;
	private ArrayList<TaskLogData> allLogs;
	private TaskLogAdapter adapter;
	private PullToRefreshListView lsvTaskLog;

	private APIRequest request;
	private APIRequest statusRequest;
	private APIRequest photoRequest;
	private CustomProgressDialog loading;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		currentTask = getIntent().getParcelableExtra(TASK);
		setContentView(R.layout.activity_task_detail);
		allLogs = new ArrayList<TaskLogData>();
		initializeComponents();
		initializeRequests();
		if (currentTask != null) {
			loadTaskData();
			refreshData();
		} else {
			showToastMessage("Failed to load job data.");
			finish();
		}
	}

	private void initializeComponents() {
		lblTaskDescription = (TextView) findViewById(R.id.lblTaskDescription);
		lblTaskNo = (TextView) findViewById(R.id.lblTaskNo);
		lblFrom = (TextView) findViewById(R.id.lblFrom);
		lblTo = (TextView) findViewById(R.id.lblTo);
		btnReportStatus = (Button) findViewById(R.id.btnReportStatus);
		btnReportStatus.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ReportStatusDialog dialog = new ReportStatusDialog(TaskDetailActivity.this);
				dialog.setReportStatusDialogListener(new ReportStatusDialogListener() {
					@Override
					public void onStatusReported(TaskStatus status) {
						if (status != null) {
							statusRequest.reportTaskLog(WheelionsApplication.getCurrentUserID(TaskDetailActivity.this),
									currentTask.getId(), getCurrentLocation(), status.getTitle(), status.getCode());
						}
					}
				});
				dialog.show();
			}
		});
		btnAddPhoto = (Button) findViewById(R.id.btnAddPhoto);
		btnAddPhoto.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				TaskStatus photoStatus = new TaskStatus("03", "เพิ่มรูป");
				photoRequest.sendPhotoLog(WheelionsApplication.getCurrentUserID(TaskDetailActivity.this),
						currentTask.getId(), BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher),
						getCurrentLocation(), photoStatus.getTitle(), photoStatus.getCode());
			}
		});
		adapter = new TaskLogAdapter(this, allLogs);
		lsvTaskLog = (PullToRefreshListView) findViewById(R.id.lsvTaskLog);
		lsvTaskLog.setShowIndicator(false);
		lsvTaskLog.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				refreshData();
			}
		});
		lsvTaskLog.setAdapter(adapter);
	}

	private void initializeRequests() {
		loading = WheelionsApplication.getLoadingDialog(this);
		request = new APIRequest(new APIAsyncParam(this, new APIRequestListener() {
			boolean succeed = false;
			@Override
			public void onRequestFinish(APIRequestResult result) {
				if (succeed && result.isStatusInRange(200, 200)) {
					onRefreshFinish();
				} else {
					showToastMessage("Failed to load log.");
				}
				succeed = false;
			}
			@Override
			public void onNoInternetOrLocation(Activity activity) {}
			@Override
			public void onConnectionTimeout(Activity activity) {
				showToastMessage("Failed to load log.");
			}
			@Override
			public void doInBackground(APIRequestResult result) {
				if (result.isStatusInRange(200, 200)) {
					try {
						JSONObject object = new JSONObject(result.GetData());
						JSONArray array = object.getJSONArray("data");
						for (int i = 0; i < array.length(); i++) {
							TaskLogData log = new TaskLogData(array.getJSONObject(i));
							allLogs.add(log);
						}
						succeed = true;
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
		}, loading));

		statusRequest = new APIRequest(new APIAsyncParam(this, new APIRequestListener() {
			@Override
			public void onRequestFinish(APIRequestResult result) {
				if (result.isStatusInRange(200, 200)) {
					refreshData();
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
		}, loading));

		photoRequest = new APIRequest(new APIAsyncParam(this, new APIRequestListener() {
			@Override
			public void onRequestFinish(APIRequestResult result) {
				if (result.isStatusInRange(200, 200)) {
					refreshData();
				} else {
					showToastMessage("Failed to send photo.");
				}
			}
			@Override
			public void onNoInternetOrLocation(Activity activity) {}
			@Override
			public void onConnectionTimeout(Activity activity) {
				showToastMessage("Failed to send photo.");
			}
			@Override
			public void doInBackground(APIRequestResult result) {}
		}, loading, true));
	}

	private void loadTaskData() {
		lblTaskDescription.setText(currentTask.getDescription());
		lblTaskNo.setText(currentTask.getTaskNo());
		lblFrom.setText(currentTask.getFrom());
		lblTo.setText(currentTask.getTo());
	}

	private void refreshData() {
		allLogs.clear();
		adapter.notifyDataSetChanged();
		request.getJobDetail(WheelionsData.instance(this).getUserID(), currentTask.getId());
	}

	private void onRefreshFinish() {
		if (lsvTaskLog.isRefreshing()) {
			lsvTaskLog.onRefreshComplete();
		}
		adapter.notifyDataSetChanged();
	}

	@Override
	protected void onLocationChanged() {}

}
