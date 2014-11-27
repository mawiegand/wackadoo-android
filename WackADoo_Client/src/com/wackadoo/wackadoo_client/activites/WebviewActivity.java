package com.wackadoo.wackadoo_client.activites;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.Toast;

import com.wackadoo.wackadoo_client.R;
import com.wackadoo.wackadoo_client.analytics.SampleHelper;
import com.wackadoo.wackadoo_client.helper.GameWebViewClient;
import com.wackadoo.wackadoo_client.helper.StaticHelper;
import com.wackadoo.wackadoo_client.helper.WackadooActivity;
import com.wackadoo.wackadoo_client.javascriptinterfaces.LoginJavaScriptHandler;

public class WebviewActivity extends WackadooActivity {	

	private static final String TAG = WebviewActivity.class.getSimpleName();
	private static final int UPDATE_CONNECTION_TIMER = 10000;
	
	private WebView webView;
	private Handler mConnectionHandler;
	private ImageView reloadBtn;
	
    @SuppressLint({ "NewApi", "JavascriptInterface" })
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_webview);
        
        setUpReloadBtn();
        setUpWebview();
        setUpJavaScriptInterface(getIntent().getExtras());
      
        if (savedInstanceState != null) {
        	webView.restoreState(savedInstanceState);
        }
     
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
    
    @Override
    protected void onPause() {
    	super.onPause();
        mConnectionHandler.removeCallbacks(checkConnectionThread);
    }
	
    // set up the webview
    private void setUpWebview() {
    	  webView = (WebView) findViewById(R.id.main_webView);
    	  
    	  webView.setWebViewClient(new GameWebViewClient(this));
          webView.setWebChromeClient(new WebChromeClient());
          
          WebSettings webSettings = webView.getSettings();
          webSettings.setJavaScriptEnabled(true);
          webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
          webSettings.setLoadWithOverviewMode(true);
          webSettings.setUseWideViewPort(true);
          webSettings.setDomStorageEnabled(true);	
          
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
              WebView.setWebContentsDebuggingEnabled(true);
          }
    }
    
    private void setUpJavaScriptInterface(Bundle b) {
    	// get display size
    	Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        
        // create javascript interface with necessary values
        String psioriSessionToken = SampleHelper.getInstance().getSessionToken();
        String psioriInstallToken = SampleHelper.getInstance().getInstallToken();
        
        final LoginJavaScriptHandler loginHandler = new LoginJavaScriptHandler(this, 
        		b.getString("accessToken"), b.getString("expiration"), b.getString("userId"), b.getString("hostname"), size.x,size.y, psioriSessionToken, psioriInstallToken);
           
        webView.addJavascriptInterface(loginHandler, "LoginHandler");
    }
    
    // set up native reloadBtn
	private void setUpReloadBtn() {
		reloadBtn = (ImageView) findViewById(R.id.reloadBtn);
		reloadBtn.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent evt) {
				int action = evt.getActionMasked();
				
				if (action == MotionEvent.ACTION_DOWN) {
					reloadBtn.setColorFilter(Color.parseColor("#50000000"));
					
				} else if (action == MotionEvent.ACTION_UP) {
					reloadBtn.setColorFilter(null);
//					webView.reload();
					webView.loadUrl("javascript:WACKADOO.reload()");
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
}
