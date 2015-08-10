package com.wackadoo.wackadoo_client.activites;

import org.xwalk.core.XWalkPreferences;
import org.xwalk.core.XWalkView;
import org.xwalk.core.internal.XWalkSettings;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.Toast;

import com.adjust.sdk.Adjust;
import com.wackadoo.wackadoo_client.R;
import com.wackadoo.wackadoo_client.analytics.SampleHelper;
import com.wackadoo.wackadoo_client.helper.AndroidBug5497Workaround;
import com.wackadoo.wackadoo_client.helper.StaticHelper;
import com.wackadoo.wackadoo_client.javascriptinterfaces.JavaScriptHandler;
import com.wackadoo.wackadoo_client.model.AdjustProperties;
public class WebviewActivity extends WackadooActivity {	

	private static final String TAG = WebviewActivity.class.getSimpleName();
	private static final int UPDATE_CONNECTION_TIMER = 10000;
	private static final int UPDATE_PLAYTIME_TIME = 30000;
	
	private static long currentPlayTime = 0;
	
	private XWalkView xWalkView;
	private Handler mConnectionHandler;
	private ImageView reloadBtn;
	
	private SampleHelper sample;
	private Handler playtimeHandler;
	private AdjustProperties adjustProperties;
	
	private String userId;
	
    @SuppressLint({ "NewApi", "JavascriptInterface" })
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_webview);
        
        Bundle bundle = getIntent().getExtras();
        this.userId = bundle.getString("userId");
        
        setUpReloadBtn();
        setUpWebview();
        setUpJavaScriptInterface(bundle);
        
        if (savedInstanceState != null) {
        	xWalkView.restoreState(savedInstanceState);        	
        }
        
    	xWalkView.clearCache(true);
     
        xWalkView.load("file:///android_asset/index.html", null);
        AndroidBug5497Workaround.assistActivity(this);
        this.adjustProperties = AdjustProperties.getInstance(getApplicationContext());
    }
    
    protected void onSaveInstanceState(Bundle outState) {
    	super.onSaveInstanceState(outState);
    	xWalkView.saveState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
    	super.onRestoreInstanceState(savedInstanceState);
    	xWalkView.restoreState(savedInstanceState);
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	
    	// start automatic connection test in background
    	mConnectionHandler = new android.os.Handler();
    	mConnectionHandler.postDelayed(checkConnectionThread, UPDATE_CONNECTION_TIMER);
    	
    	if (adjustProperties.IsUserFiveMinutesIngame(userId) == false) {
    		currentPlayTime = adjustProperties.getPlayTimeForUser(userId);
        	playtimeHandler = new android.os.Handler();
        	playtimeHandler.postDelayed(playtimeThread, UPDATE_CONNECTION_TIMER);
    	}
    }
    
    @Override
    protected void onPause() {
    	super.onPause();
    	
        mConnectionHandler.removeCallbacks(checkConnectionThread);
        playtimeHandler.removeCallbacks(playtimeThread);
        
        if (adjustProperties.IsUserFiveMinutesIngame(userId) == false) {
        	adjustProperties.setPlayTimeForUser(userId, currentPlayTime);
        }
    }
	
    // set up the webview

    private void setUpWebview() {
    	xWalkView = (XWalkView) findViewById(R.id.main_webView);
        XWalkPreferences.setValue(XWalkPreferences.JAVASCRIPT_CAN_OPEN_WINDOW, true);     
    }
    
    private void setUpJavaScriptInterface(Bundle b) {
    	// get display size
    	Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        
        // create javascript interface with necessary values
        String psioriSessionToken = SampleHelper.getInstance(getApplicationContext()).getSessionToken();
        String psioriInstallToken = SampleHelper.getInstance(getApplicationContext()).getInstallToken();
        
        final JavaScriptHandler jsHandler = new JavaScriptHandler(this, 
        		b.getString("accessToken"), b.getString("expiration"), 
        		b.getString("userId"), b.getString("hostname"), size.x,size.y,
        		psioriSessionToken, psioriInstallToken, getApplicationContext());
           
        xWalkView.addJavascriptInterface(jsHandler, "AndroidDelegate");
        
    }
    
    // set up native reloadBtn
	private void setUpReloadBtn() {
		reloadBtn = (ImageView) findViewById(R.id.reloadBtn);
		reloadBtn.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent evt) {
				int action = evt.getActionMasked();
				
				if (action == MotionEvent.ACTION_DOWN) {
					reloadBtn.setColorFilter(Color.parseColor("#748073"));
					
				} else if (action == MotionEvent.ACTION_UP) {
					reloadBtn.setColorFilter(null);
					xWalkView.load("javascript:WACKADOO.reload()", null);
					Toast.makeText(WebviewActivity.this, "Reload", Toast.LENGTH_SHORT)
						 .show();
				}
				
				return true;
			}
		});
	}
    
    // use device backbutton to leave game if user wants to
    @Override
    public void onBackPressed() {
		AlertDialog.Builder builder = new AlertDialog.Builder(WebviewActivity.this);
		builder.setMessage(getString(R.string.dialog_return_startscreen_text))
			   .setPositiveButton(R.string.alert_quit_yes, new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						soundManager.setContinueMusic(true);
						startActivity(new Intent(WebviewActivity.this, MainActivity.class));
						finish();									
					}
				})
			   .setNegativeButton(R.string.alert_quit_no, null);
		AlertDialog dialog = builder.create();
	    dialog.show();
	    StaticHelper.styleDialog(WebviewActivity.this, dialog);
    }
    
    // runs the scale animation repeatedly
 	private Runnable checkConnectionThread = new Runnable() {
 		public void run() {		
 			// warning if no internet connection
 			if (!StaticHelper.isOnline(WebviewActivity.this)) {
 				Toast.makeText(WebviewActivity.this, getResources().getString(R.string.no_connection), Toast.LENGTH_LONG)
 					 .show();
 			}
 			mConnectionHandler.postDelayed(this, UPDATE_CONNECTION_TIMER);
 		}
 	}; 
 	
 	 // compute play time
 	private Runnable playtimeThread = new Runnable() {
 		public void run() {		
 			if (currentPlayTime < AdjustProperties.FIVE_MINUTES) {
 	 			currentPlayTime += UPDATE_PLAYTIME_TIME;		
 			} else if (StaticHelper.isOnline(WebviewActivity.this) && adjustProperties.IsUserFiveMinutesIngame(userId) == false) {
 	 			adjustProperties.setPlayTimeForUser(userId, currentPlayTime);
 				adjustProperties.setUserPlayedForFiveMinutes(userId);
 				Adjust.trackEvent("2n21b7");
 				playtimeHandler.removeCallbacks(playtimeThread);
 			}
 		}
 	}; 
}
