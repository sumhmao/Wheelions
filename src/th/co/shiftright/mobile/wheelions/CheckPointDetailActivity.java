package th.co.shiftright.mobile.wheelions;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import th.co.shiftright.mobile.wheelions.adapters.CheckPointLogAdapter;
import th.co.shiftright.mobile.wheelions.api.APIAsyncParam;
import th.co.shiftright.mobile.wheelions.api.APIRequest;
import th.co.shiftright.mobile.wheelions.api.APIRequestListener;
import th.co.shiftright.mobile.wheelions.api.APIRequestResult;
import th.co.shiftright.mobile.wheelions.custom_controls.CustomProgressDialog;
import th.co.shiftright.mobile.wheelions.models.CheckPoint;
import th.co.shiftright.mobile.wheelions.models.TaskLogData;
import th.co.shiftright.mobile.wheelions.models.WheelionsData;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class CheckPointDetailActivity extends BasedActivity {

	public static final String CHECK_POINT = "checkPoint";
	private APIRequest request;
	private CustomProgressDialog loading;
	private CheckPoint currentData;
	private TextView lblCheckPointTitle;
	private TextView lblCheckPointDetail;
	private Button btnReport;
	private ListView lsvLog;
	private CheckPointLogAdapter adapter;
	private ArrayList<TaskLogData> allLogs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		currentData = getIntent().getParcelableExtra(CHECK_POINT);
		setContentView(R.layout.activity_check_point_detail);
		allLogs = new ArrayList<TaskLogData>();
		initializeRequest();
		initializeComponents();
		request.getCheckPoint(WheelionsData.instance(this).getUserID(), currentData.getJobId(), currentData.getId());
	}

	private void initializeComponents() {
		lsvLog = (ListView) findViewById(R.id.lsvLog);
		adapter = new CheckPointLogAdapter(this, allLogs, WheelionsApplication.getScreenWidth(this));
		lsvLog.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				TaskLogData selectedLog = (TaskLogData) parent.getItemAtPosition(position);
				if (selectedLog.getAllPhotos().size() > 0) {
					Intent intent = new Intent(CheckPointDetailActivity.this, GalleryPreviewActivity.class);
					intent.putExtra(GalleryPreviewActivity.ITEMS, selectedLog.getAllPhotos());
					startActivity(intent);
				}
			}
		});
		lsvLog.setAdapter(adapter);
		lblCheckPointTitle = (TextView) findViewById(R.id.lblCheckPointTitle);
		lblCheckPointDetail = (TextView) findViewById(R.id.lblCheckPointDetail);
		lblCheckPointTitle.setText(currentData.getTitle());
		lblCheckPointDetail.setText(currentData.getDescription());
		btnReport = (Button) findViewById(R.id.btnReport);
		btnReport.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(CheckPointDetailActivity.this, ReportStatusActivity.class);
				intent.putExtra(ReportStatusActivity.CHECK_POINT, currentData);
				startActivityForResult(intent, TaskDetailActivity.REPORT_CHECKPOINT);
			}
		});
	}

	private void initializeRequest() {
		loading = WheelionsApplication.getLoadingDialog(this);
		request = new APIRequest(new APIAsyncParam(this, new APIRequestListener() {
			@Override
			public void onRequestFinish(APIRequestResult result) {
				if (currentData != null && result.isStatusInRange(200, 200)) {
					allLogs.addAll(currentData.getAllLogs());
					adapter.notifyDataSetChanged();
				} else {
					showToastMessage("Failed to load data.");
					finish();
				}
			}
			@Override
			public void onNoInternetOrLocation(Activity activity) {}
			@Override
			public void onConnectionTimeout(Activity activity) {
				showToastMessage("Failed to load data.");
				finish();
			}
			@Override
			public void doInBackground(APIRequestResult result) {
				if (result.isStatusInRange(200, 200)) {
					try {
						JSONObject object = new JSONObject(result.GetData());
						JSONArray dataArray = object.getJSONArray("data");
						if (dataArray.length() > 0) {
							currentData = new CheckPoint(dataArray.getJSONObject(0));
						}
					} catch (JSONException e) {
						e.printStackTrace();
						currentData = null;
					}
				}
			}
		}, loading));
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case TaskDetailActivity.REPORT_CHECKPOINT: {
			if (resultCode == RESULT_OK) {
				setResult(RESULT_OK);
				finish();
			}
			break;
		}
		default:
			break;
		}
	}

}
