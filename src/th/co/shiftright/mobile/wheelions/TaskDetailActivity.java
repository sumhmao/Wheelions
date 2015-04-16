package th.co.shiftright.mobile.wheelions;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import th.co.shiftright.mobile.wheelions.adapters.CheckListAdapter;
import th.co.shiftright.mobile.wheelions.api.APIAsyncParam;
import th.co.shiftright.mobile.wheelions.api.APIRequest;
import th.co.shiftright.mobile.wheelions.api.APIRequestListener;
import th.co.shiftright.mobile.wheelions.api.APIRequestResult;
import th.co.shiftright.mobile.wheelions.custom_controls.CustomProgressDialog;
import th.co.shiftright.mobile.wheelions.models.CheckPoint;
import th.co.shiftright.mobile.wheelions.models.TaskData;
import th.co.shiftright.mobile.wheelions.models.WheelionsData;
import th.co.shiftright.mobile.wheelions.pulltorefresh.PullToRefreshBase;
import th.co.shiftright.mobile.wheelions.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import th.co.shiftright.mobile.wheelions.pulltorefresh.PullToRefreshListView;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class TaskDetailActivity extends LocationBasedActivity {

	public static final String TASK = "task";
	public static final int REPORT_CHECKPOINT = 1234;

	private TaskData currentTask;
	private TextView lblTaskDescription;
	private TextView lblTaskNo;
	private TextView lblFrom;
	private TextView lblTo;
	private ArrayList<CheckPoint> allCheckPoints;
	private PullToRefreshListView lsvCheckList;
	private CheckListAdapter adapter;
	private APIRequest request;
	private CustomProgressDialog loading;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		currentTask = getIntent().getParcelableExtra(TASK);
		setContentView(R.layout.activity_task_detail);
		allCheckPoints = new ArrayList<CheckPoint>();
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
		lsvCheckList = (PullToRefreshListView) findViewById(R.id.lsvCheckList);
		lsvCheckList.setShowIndicator(false);
		lsvCheckList.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				refreshData();
			}
		});
		lsvCheckList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				CheckPoint checkPoint = (CheckPoint) parent.getItemAtPosition(position);
				if (!checkPoint.isCompleted()) {
					Intent intent = new Intent(TaskDetailActivity.this, ReportStatusActivity.class);
					intent.putExtra(ReportStatusActivity.CHECK_POINT, checkPoint);
					startActivityForResult(intent, REPORT_CHECKPOINT);
				} else {
					Intent intent = new Intent(TaskDetailActivity.this, CheckPointDetailActivity.class);
					intent.putExtra(CheckPointDetailActivity.CHECK_POINT, checkPoint);
					startActivityForResult(intent, REPORT_CHECKPOINT);
				}
			}
		});
		adapter = new CheckListAdapter(this, allCheckPoints);
		lsvCheckList.setAdapter(adapter);
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
					showToastMessage("Failed to load checklist.");
				}
				succeed = false;
			}
			@Override
			public void onNoInternetOrLocation(Activity activity) {}
			@Override
			public void onConnectionTimeout(Activity activity) {
				showToastMessage("Failed to load checklist.");
			}
			@Override
			public void doInBackground(APIRequestResult result) {
				if (result.isStatusInRange(200, 200)) {
					try {
						JSONObject object = new JSONObject(result.GetData());
						JSONArray array = object.getJSONArray("data");
						for (int i = 0; i < array.length(); i++) {
							CheckPoint checkPoint = new CheckPoint(array.getJSONObject(i));
							allCheckPoints.add(checkPoint);
						}
						succeed = true;
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
		}, loading));
	}

	private void loadTaskData() {
		lblTaskDescription.setText(currentTask.getDescription());
		lblTaskNo.setText(currentTask.getTaskNo());
		lblFrom.setText(currentTask.getFrom());
		lblTo.setText(currentTask.getTo());
	}

	private void refreshData() {
		allCheckPoints.clear();
		adapter.notifyDataSetChanged();
		request.getCheckList(WheelionsData.instance(this).getUserID(), currentTask.getId());
	}

	private void onRefreshFinish() {
		if (lsvCheckList.isRefreshing()) {
			lsvCheckList.onRefreshComplete();
		}
		adapter.notifyDataSetChanged();
	}

	@Override
	protected void onLocationChanged() {}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case REPORT_CHECKPOINT: {
			if (resultCode == RESULT_OK) {
				refreshData();
			}
			break;
		}
		default:
			break;
		}
	}

}
