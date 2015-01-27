package th.co.shiftright.mobile.wheelions;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import th.co.shiftright.mobile.wheelions.adapters.TaskListAdapter;
import th.co.shiftright.mobile.wheelions.api.APIAsyncParam;
import th.co.shiftright.mobile.wheelions.api.APIRequest;
import th.co.shiftright.mobile.wheelions.api.APIRequestListener;
import th.co.shiftright.mobile.wheelions.api.APIRequestResult;
import th.co.shiftright.mobile.wheelions.custom_controls.CustomProgressDialog;
import th.co.shiftright.mobile.wheelions.imagemanager.ImageDownloadEventListener;
import th.co.shiftright.mobile.wheelions.models.AsyncTaskQueueItem;
import th.co.shiftright.mobile.wheelions.models.ConfirmationDialogListener;
import th.co.shiftright.mobile.wheelions.models.TaskData;
import th.co.shiftright.mobile.wheelions.models.User;
import th.co.shiftright.mobile.wheelions.models.WheelionsData;
import th.co.shiftright.mobile.wheelions.pulltorefresh.PullToRefreshBase;
import th.co.shiftright.mobile.wheelions.pulltorefresh.PullToRefreshListView;
import th.co.shiftright.mobile.wheelions.pulltorefresh.PullToRefreshBase.OnRefreshListener;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class TaskListActivity extends BasedActivity {

	private ArrayList<TaskData> allTasks;
	private TaskListAdapter adapter;

	private User currentUser;
	private ImageView imgUserProfile;
	private TextView lblUsername;
	private PullToRefreshListView lsvTaskList;
	private ImageButton btnLogout;

	private APIRequest request;
	private CustomProgressDialog loading;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_list);
		allTasks = new ArrayList<TaskData>();
		initializeComponents();
		initializeRequests();
		currentUser = WheelionsData.instance(this).getUser();
		if (currentUser != null) {
			loadUserData();	
		} else {
			showToastMessage("Failed to load user data.");
			doLogout();
		}
	}

	private void initializeComponents() {
		imgUserProfile = (ImageView) findViewById(R.id.imgUserProfile);
		lblUsername = (TextView) findViewById(R.id.lblUsername);
		adapter = new TaskListAdapter(this, allTasks);
		lsvTaskList = (PullToRefreshListView) findViewById(R.id.lsvTaskList);
		lsvTaskList.setShowIndicator(false);
		lsvTaskList.setAdapter(adapter);
		lsvTaskList.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				refreshData();
			}
		});
		lsvTaskList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				TaskData data = (TaskData) parent.getItemAtPosition(position);
				if (data != null) {
					onTaskSelected(data);
				}
			}
		});
		btnLogout = (ImageButton) findViewById(R.id.btnLogout);
		btnLogout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showConfirmationDialog("Logout ?", "Yes", "No", new ConfirmationDialogListener() {
					@Override
					public void onUserCancel() {}
					@Override
					public void onUserAgree() {
						doLogout();	
					}
				});
			}
		});
	}

	private void initializeRequests() {
		loading = WheelionsApplication.getLoadingDialog(this);
		request = new APIRequest(new APIAsyncParam(this, new APIRequestListener() {
			boolean succeed = false;
			@Override
			public void onRequestFinish(APIRequestResult result) {
				if (succeed && result.isStatusInRange(200, 200)) {
					onRefreshSucceed();
				} else {
					showToastMessage("Failed to load job.");
				}
				succeed = false;
			}
			@Override
			public void onNoInternetOrLocation(Activity activity) {}
			@Override
			public void onConnectionTimeout(Activity activity) {
				showToastMessage("Failed to load job.");
			}
			@Override
			public void doInBackground(APIRequestResult result) {
				if (result.isStatusInRange(200, 200)) {
					try {
						JSONObject object = new JSONObject(result.GetData());
						JSONArray array = object.getJSONArray("data");
						for (int i = 0; i < array.length(); i++) {
							TaskData task = new TaskData(array.getJSONObject(i));
							allTasks.add(task);
						}
						succeed = true;
					} catch (JSONException e) {
						allTasks.clear();
						e.printStackTrace();
					}
				}
			}
		}, loading));
	}

	private void onTaskSelected(TaskData selectedData) {
		Intent intent = new Intent(this, TaskDetailActivity.class);
		intent.putExtra(TaskDetailActivity.TASK, selectedData);
		startActivity(intent);
	}

	private void loadUserData() {
		lblUsername.setText(currentUser.getFullName());
		Bitmap picture = WheelionsData.instance(this).getUserPicture();
		if (picture != null) {
			picture = WheelionsApplication.maskImageCircle(this, picture);
			imgUserProfile.setImageBitmap(picture);
		} else {
			int size = WheelionsApplication.getDPFromPixel(this, 60);
			AsyncTaskQueueItem task = currentUser.getImageTask(new ImageDownloadEventListener() {
				@Override
				public void onDownloadImageFinish(String itemID, Bitmap photo) {
					if (photo != null) {
						WheelionsData.instance(TaskListActivity.this).saveUserImage(photo);
						photo = WheelionsApplication.maskImageCircle(TaskListActivity.this, photo);
						imgUserProfile.setImageBitmap(photo);
					}
				}
			}, size);
			if (task != null) {
				WheelionsApplication.executeAsyncTask(task);
			}
		}
		refreshData();
	}

	private void refreshData() {
		allTasks.clear();
		adapter.notifyDataSetChanged();
		request.getDriverJobs(currentUser.getID());
	}

	private void onRefreshSucceed() {
		if (lsvTaskList.isRefreshing()) {
			lsvTaskList.onRefreshComplete();
		}
		adapter.notifyDataSetChanged();
	}

}
