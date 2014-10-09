package com.wackadoo.wackadoo_client.helper;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.wackadoo.wackadoo_client.R;
import com.wackadoo.wackadoo_client.adapter.ShopRowItem;

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
		StaticHelper.backgroundMusicPlayer.start();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		if (!StaticHelper.continueMusic) {
			StaticHelper.backgroundMusicPlayer.stop();
		}
	}
	
}
