package th.co.shiftright.mobile.wheelions;

import th.co.shiftright.mobile.wheelions.models.ConfirmationDialogListener;
import th.co.shiftright.mobile.wheelions.models.WheelionsData;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.Toast;

public class BasedActivity extends Activity {

	private BroadcastReceiver logoutReceiver = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		IntentFilter logoutFilter = new IntentFilter();
		logoutFilter.addAction(WheelionsData.ACTION_LOGOUT);
		logoutReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				finish();
			}
		};
		registerReceiver(logoutReceiver, logoutFilter);
	}

	@Override
	protected void onDestroy() {
		if (logoutReceiver != null) {
			unregisterReceiver(logoutReceiver);
		}
		super.onDestroy();
	}

	protected boolean isUserLoggedIn() {
		return WheelionsData.instance(this).isUserLoggedIn();
	}

	protected void showToastMessage(String message) {
		Toast.makeText(this, message, Toast.LENGTH_LONG).show();
	}

	public void doLogout() {
		WheelionsData.instance(this).logout();
		Intent intent = new Intent(getApplicationContext(), MainActivity.class);
		super.startActivity(intent);
	}

	protected void showConfirmationDialog(String message, String okText, String cancelText, 
			ConfirmationDialogListener listener) {
		WheelionsApplication.showConfirmationDialog(this, message, okText, cancelText, listener);
	}

}
