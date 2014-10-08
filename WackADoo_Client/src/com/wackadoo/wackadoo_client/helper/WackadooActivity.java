package com.wackadoo.wackadoo_client.helper;

import android.app.Activity;
import android.view.KeyEvent;

public class WackadooActivity extends Activity {

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
//	        moveTaskToBack(true);
			StaticHelper.continueMusic = true;
//	        return true;
	    }
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		if (!StaticHelper.continueMusic) {
			StaticHelper.backgroundMusicPlayer.stop();
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		StaticHelper.continueMusic = false;
		StaticHelper.backgroundMusicPlayer.start();
	}
	
}
