package com.wackadoo.wackadoo_client.helper;

import com.adjust.sdk.Adjust;
import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Toast;

import com.wackadoo.wackadoo_client.R;
import com.wackadoo.wackadoo_client.activites.MainActivity;

// custom activity subclass, to handle background music
public class WackadooActivity extends Activity {

	protected SoundManager soundManager;
	
	protected void onCreate(Bundle savedInstanceState, int layoutId) {
		super.onCreate(savedInstanceState);
		setContentView(layoutId);
		StaticHelper.overrideFonts(this, findViewById(R.id.activityContainer));
		
	    // create and set up player for background music
		soundManager = SoundManager.getInstance(this);
	}
	
	@Override
	public void onBackPressed() {
		soundManager.setContinueMusic(true);
		finish();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		Adjust.onResume(this);
		
		// continue music, if its not currently playing
		soundManager.setContinueMusic(false);
		if (soundManager.shouldPlayerStart()) {
			soundManager.start();
		}
		
		// warning if no internet connection
		if (!StaticHelper.isOnline(this)) {
			Toast.makeText(this, getResources().getString(R.string.no_connection), Toast.LENGTH_SHORT)
				 .show();
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		Adjust.onPause();
		
		// stop music, if not another activity is started
		if (soundManager.shouldPlayerStop()) {
			soundManager.stop();
		}
	}
	
}
