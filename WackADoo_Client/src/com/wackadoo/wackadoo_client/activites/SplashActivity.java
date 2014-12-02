package com.wackadoo.wackadoo_client.activites;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.crashlytics.android.Crashlytics;
import com.wackadoo.wackadoo_client.R;

public class SplashActivity extends Activity {
	
	// timer for splash screen in ms
	private static int SPLASH_TIMER = 500;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Crashlytics.start(this);
		setContentView(R.layout.activity_splash);
		
		// start MainActivity after time of SPLASH_TIMER
		new Handler().postDelayed(new Runnable() {
			public void run() {
				Intent i = new Intent(SplashActivity.this, MainActivity.class);
				startActivity(i);
				finish();
			}
		}, SPLASH_TIMER);
	}

}
