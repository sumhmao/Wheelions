package th.co.shiftright.mobile.wheelions;

import org.json.JSONException;
import org.json.JSONObject;

import th.co.shiftright.mobile.wheelions.api.APIAsyncParam;
import th.co.shiftright.mobile.wheelions.api.APIRequest;
import th.co.shiftright.mobile.wheelions.api.APIRequestListener;
import th.co.shiftright.mobile.wheelions.api.APIRequestResult;
import th.co.shiftright.mobile.wheelions.custom_controls.CustomProgressDialog;
import th.co.shiftright.mobile.wheelions.models.User;
import th.co.shiftright.mobile.wheelions.models.WheelionsData;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends BasedActivity {

	private EditText txtDrivingNo;
	private EditText txtPassword;
	private Button btnLogin;
	private APIRequest request;
	private CustomProgressDialog loading;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initializeComponents();
		initializeRequests();

		// test data
		txtDrivingNo.setText("3408844458953");
		txtPassword.setText("password");
	}

	private void initializeComponents() {
		txtDrivingNo = (EditText) findViewById(R.id.txtDrivingNo);
		txtPassword = (EditText) findViewById(R.id.txtPassword);
		btnLogin = (Button) findViewById(R.id.btnLogin);
		btnLogin.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (checkData()) {
					verifyCredential();
				} else {
					showToastMessage("Please fill-in all required information.");
				}
			}
		});
	}

	private void initializeRequests() {
		loading = WheelionsApplication.getLoadingDialog(this);
		request = new APIRequest(new APIAsyncParam(this, new APIRequestListener() {
			@Override
			public void onRequestFinish(APIRequestResult result) {
				if (isUserLoggedIn() && result.isStatusInRange(200, 200)) {
					toMainPage();
				} else {
					showToastMessage("Login failed.");
				}
			}
			@Override
			public void onNoInternetOrLocation(Activity activity) {}
			@Override
			public void onConnectionTimeout(Activity activity) {
				showToastMessage("Login failed.");
			}
			@Override
			public void doInBackground(APIRequestResult result) {
				if (result.isStatusInRange(200, 200)) {
					try {
						JSONObject object = new JSONObject(result.GetData());
						JSONObject data = object.getJSONArray("data").getJSONObject(0);
						String accessToken = object.getString("authentication_token");
						User user = new User(data);
						WheelionsData.instance(MainActivity.this).saveData(user, accessToken);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
		}, loading));
	}

	private boolean checkData() {
		return !(WheelionsApplication.checkIfEditTextIsNullOrEmpty(txtDrivingNo) ||
				WheelionsApplication.checkIfEditTextIsNullOrEmpty(txtPassword));
	}

	private void verifyCredential() {
		String drivingNo = WheelionsApplication.getEditTextValue(txtDrivingNo);
		String password = WheelionsApplication.getEditTextValue(txtPassword);
		request.login(drivingNo, password);
	}

	private void toMainPage() {
		Intent intent = new Intent(this, TaskListActivity.class);
		startActivity(intent);
		finish();
	}

}
