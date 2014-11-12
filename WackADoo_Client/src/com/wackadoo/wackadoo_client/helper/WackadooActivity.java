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
		SoundManager.continueMusic = true;
		finish();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		// continue music, if its not currently playing
		SoundManager.continueMusic = false;
		if (!SoundManager.backgroundMusicPlayer.isPlaying() && SoundManager.soundOn) {
			SoundManager.setUpPlayer(this);
			SoundManager.backgroundMusicPlayer.start();
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
		if (!SoundManager.continueMusic && SoundManager.backgroundMusicPlayer.isPlaying()) {
			SoundManager.backgroundMusicPlayer.stop();
		}
	}
	
}
