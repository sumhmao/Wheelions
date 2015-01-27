package th.co.shiftright.mobile.wheelions;

import th.co.shiftright.mobile.wheelions.models.WheelionsData;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashActivity extends Activity {

	private final int SPLASH_DISPLAY_LENGHT = 2000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				if (WheelionsData.instance(SplashActivity.this).isUserLoggedIn()) {
					Intent intent = new Intent(SplashActivity.this, TaskListActivity.class);
					SplashActivity.this.startActivity(intent);
					finish();
				} else {
					Intent intent = new Intent(SplashActivity.this, MainActivity.class);
					SplashActivity.this.startActivity(intent);
					finish();
				}
			}
		}, SPLASH_DISPLAY_LENGHT);
	}

}
