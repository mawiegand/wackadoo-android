package com.wackadoo.wackadoo_client.activites;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import com.wackadoo.wackadoo_client.R;
import com.wackadoo.wackadoo_client.helper.GameWebViewClient;
import com.wackadoo.wackadoo_client.helper.StaticHelper;
import com.wackadoo.wackadoo_client.javascriptinterfaces.LoginJavaScriptHandler;

public class WackadooWebviewActivity extends Activity {	

	private static final String TAG = WackadooWebviewActivity.class.getSimpleName();
	private static final int UPDATE_CONNECTION_TIMER = 10000;
	
	private WebView webView;
	private int width;
	private Handler mConnectionHandler;
	
    @SuppressLint({ "NewApi", "JavascriptInterface" })
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wackadoowebview);
        
        webView = (WebView) findViewById(R.id.main_webView);
        if (savedInstanceState != null) {
            webView.restoreState(savedInstanceState);
        }
        
        webView.setWebViewClient(new GameWebViewClient(this));
        webView.setWebChromeClient(new WebChromeClient());
        
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setDomStorageEnabled(true);	
        
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        
        Bundle b = getIntent().getExtras();
        final LoginJavaScriptHandler loginHandler = new LoginJavaScriptHandler(this, b.getString("accessToken"), b.getString("expiration"), b.getString("userId"), b.getString("hostname"));
        webView.addJavascriptInterface(loginHandler, "LoginHandler");
        
        webView.loadUrl("file:///android_asset/index.html");
    }
    
    protected void onSaveInstanceState(Bundle outState) {
    	webView.saveState(outState);
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	// start automatic connection test in background
    	mConnectionHandler = new android.os.Handler();
    	mConnectionHandler.postDelayed(checkConnectionThread, UPDATE_CONNECTION_TIMER);
    }
    
    // log touch events 
    @Override
    public boolean dispatchTouchEvent(MotionEvent evt) {
        float x = evt.getAxisValue(MotionEvent.AXIS_X);
        float y = evt.getAxisValue(MotionEvent.AXIS_Y);
        Log.i("TOUCH", "---->" + evt.getActionMasked() + ": " + x + "|" + y);
        // 0 = ACTION_DOWN, 1 = ACTION_UP, 2 = ACTION_MOVE, 
        // 5 = ACTION_POINTER_1_DOWN, 6 = ACTION_POINTER_1_UP
        return super.dispatchTouchEvent(evt);

    }
    
    @Override
    protected void onPause() {
    	super.onPause();
        mConnectionHandler.removeCallbacks(checkConnectionThread);
    }
    
    // use device backbutton to go back one page in webview
    @Override
    public void onBackPressed() {
		if (webView.canGoBack()) {
			webView.goBack();
			
		} else {
			AlertDialog.Builder builder = new AlertDialog.Builder(WackadooWebviewActivity.this);
			builder.setMessage(getString(R.string.dialog_return_startscreen_text))
				   .setPositiveButton(R.string.alert_quit_yes, new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							finish();									
						}
					})
				   .setNegativeButton(R.string.alert_quit_no, null);
			AlertDialog dialog = builder.create();
		    dialog.show();
		    StaticHelper.styleDialog(WackadooWebviewActivity.this, dialog);
		}
    }
    
    // runs the scale animation repeatedly
 	private Runnable checkConnectionThread = new Runnable() {
 		public void run() {		
 			// warning if no internet connection
 			if (!StaticHelper.isOnline(WackadooWebviewActivity.this)) {
 				Toast.makeText(WackadooWebviewActivity.this, getResources().getString(R.string.no_connection), Toast.LENGTH_LONG)
 					 .show();
 			}
 			mConnectionHandler.postDelayed(this, UPDATE_CONNECTION_TIMER);
 		}
 	}; 
}
