package com.wackadoo.wackadoo_client.activites;

import com.adjust.sdk.Adjust;
import com.adjust.sdk.OnFinishedListener;
import com.adjust.sdk.ResponseData;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.wackadoo.wackadoo_client.R;
import com.wackadoo.wackadoo_client.analytics.SampleHelper;
import com.wackadoo.wackadoo_client.helper.SoundManager;
import com.wackadoo.wackadoo_client.helper.StaticHelper;
import com.wackadoo.wackadoo_client.model.AdjustProperties;
import com.wackadoo.wackadoo_client.model.UserCredentials;

/**
 * Base custom activity subclass, to handle background music
 * Common tasks that are shared by all Activities should be implemented here
 * @author Kevin Steinle
 *
 */
public class WackadooActivity extends Activity {

	protected SoundManager soundManager;
	
	private UserCredentials userCredentials;
	private SampleHelper sample;
	
	protected void onCreate(Bundle savedInstanceState, int layoutId) {
		super.onCreate(savedInstanceState);
		setContentView(layoutId);
		StaticHelper.overrideFonts(this, findViewById(R.id.activityContainer));
		
	    // create and set up player for background music
		soundManager = SoundManager.getInstance(this);
		
		userCredentials = new UserCredentials(getApplicationContext());
	}
	
	@Override
	public void onBackPressed() {
		soundManager.setContinueMusic(true);
		finish();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		// adjust tracking
		Adjust.onResume(this);
		
		sample = SampleHelper.getInstance(getApplicationContext());
        final IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        registerReceiver(sample, filter);
        
        String userId = userCredentials.getIdentifier();
        String fbId = userCredentials.getFbPlayerId();
        
        if (sample.isTrackingStoped()) {
     		sample.startTracking();
     		sample.setUserId(userId);
     		sample.setFacebookId(fbId);
     		sample.track("session_start", "session");
         	sample.startAutoPing();
         	
 			if (userId.isEmpty() == false) {
 				AdjustProperties adjustProps = AdjustProperties
 						.getInstance(getApplicationContext());
 				int logins = adjustProps.loginsForUser(userId);
 				if (logins < 2) {
 					adjustProps.incrementLoginCountForUser(userId);
 					if (adjustProps.loginsForUser(userId) >= 2) {
 						Adjust.trackEvent("ikc6km");
 					}
 				}
 			}
         }
		
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
		unregisterReceiver(sample);
		// stop music, if not another activity is started
		if (soundManager.shouldPlayerStop()) {
			soundManager.pause();
		}
	}
	
}
