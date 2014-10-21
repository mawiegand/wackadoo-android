package com.wackadoo.wackadoo_client.helper;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Toast;

import com.wackadoo.wackadoo_client.R;
import com.wackadoo.wackadoo_client.activites.MainActivity;

// custom activity subclass, to handle background music
public class WackadooActivity extends Activity {

	protected void onCreate(Bundle savedInstanceState, int layoutId) {
		super.onCreate(savedInstanceState);
		setContentView(layoutId);
		StaticHelper.overrideFonts(this, findViewById(R.id.activityContainer));
	}
	
	@Override
	public void onBackPressed() {
		StaticHelper.continueMusic = true;
		finish();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		// continue music, if its not currently playing
		StaticHelper.continueMusic = false;
		if (!StaticHelper.backgroundMusicPlayer.isPlaying() && StaticHelper.soundOn) {
			StaticHelper.setUpPlayer(this);
			StaticHelper.backgroundMusicPlayer.start();
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
		
		// stop music, if not another activity is started
		if (!StaticHelper.continueMusic && StaticHelper.backgroundMusicPlayer.isPlaying()) {
			StaticHelper.backgroundMusicPlayer.stop();
		}
	}
	
}
