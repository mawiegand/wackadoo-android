package com.wackadoo.wackadoo_client.helper;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;

import com.wackadoo.wackadoo_client.R;

// custom activity subclass, to handle background music
public class WackadooActivity extends Activity {

	protected void onCreate(Bundle savedInstanceState, int layoutId) {
		super.onCreate(savedInstanceState);
		setContentView(layoutId);
		StaticHelper.overrideFonts(this, findViewById(R.id.activityContainer));
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			StaticHelper.continueMusic = true;
			finish();
	        return true;
	    }
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		StaticHelper.continueMusic = false;
		if (!StaticHelper.backgroundMusicPlayer.isPlaying()) {
			StaticHelper.setUpPlayer(this);
			StaticHelper.backgroundMusicPlayer.start();
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		if (!StaticHelper.continueMusic && StaticHelper.backgroundMusicPlayer.isPlaying()) {
			StaticHelper.backgroundMusicPlayer.stop();
		}
	}
	
}
