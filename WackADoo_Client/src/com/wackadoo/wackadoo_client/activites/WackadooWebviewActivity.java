package com.wackadoo.wackadoo_client.activites;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.wackadoo.wackadoo_client.R;
import com.wackadoo.wackadoo_client.javascriptinterfaces.LoginJavaScriptHandler;

public class WackadooWebviewActivity extends Activity {	

		private WebView webView;
		
	    @SuppressLint({ "NewApi", "JavascriptInterface" })
		@Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        requestWindowFeature(Window.FEATURE_NO_TITLE);
	        setContentView(R.layout.activity_wackadoowebview);
	        webView = (WebView) findViewById(R.id.main_webView);
	        if (savedInstanceState != null) {
	            webView.restoreState(savedInstanceState);
	        }

	        webView.setWebChromeClient(new WebChromeClient());
	        webView.setWebViewClient(new WebViewClient());
	        
	        WebSettings webSettings = webView.getSettings();
	        webSettings.setJavaScriptEnabled(true);
	        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
//	        webSettings.setAllowFileAccess(true);
//	        webSettings.setLoadsImagesAutomatically(true);
	        Bundle b = getIntent().getExtras();
	        
	        // get display size
//	        Display display = getWindowManager().getDefaultDisplay();
//	        Point size = new Point(); 
//	        display.getSize(new Point());
//	        int screenSizeX = size.x;
//	        int screenSizeY = size.y;
	        
	        final LoginJavaScriptHandler loginHandler = new LoginJavaScriptHandler(b.getString("accessToken"), b.getString("expiration"), b.getString("userId"));
//	        final LoginJavaScriptHandler loginHandler = new LoginJavaScriptHandler(b.getString("accessToken"), b.getString("expiration"), b.getString("userId"), screenSizeX, screenSizeY);
	        webView.addJavascriptInterface(loginHandler, "LoginHandler");
	        
	        // dont show scrollbars
//	        webView.setVerticalScrollBarEnabled(false);
//	        webView.setHorizontalScrollBarEnabled(false);
//	        
	        webView.setInitialScale(175);
//	        webView.getSettings().setDomStorageEnabled(true);
//	        webView.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
	        
//	        webView.setScrollbarFadingEnabled(false);
//	        webView.getSettings().setLoadWithOverviewMode(true);
//	        webView.getSettings().setUseWideViewPort(true);
	        webView.loadUrl("file:///android_asset/index.html");
//	        webView.loadUrl("javascript:(function(){" + js + "})()"); // js is javascript to be executed
	    
	        webView.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					int action = event.getActionMasked();
					
					if(action == MotionEvent.ACTION_UP) {
						int x = webView.getScrollX();
						int y = webView.getScrollY();
//						webView.scrollTo(x, y + 50);
						
					}
					return false;
				}
			});
	    }

		@Override
	    public boolean onCreateOptionsMenu(Menu menu) {
	        getMenuInflater().inflate(R.menu.main, menu);
	        return true;
	    }

	    @Override
	    public boolean onOptionsItemSelected(MenuItem item) {
	        int id = item.getItemId();
	        if (id == R.id.action_settings) {
	            return true;
	        }
	        return super.onOptionsItemSelected(item);
	    }
	    
	    protected void onSaveInstanceState(Bundle outState) {
	    	webView.saveState(outState);
	    }

	    @Override
	    protected void onResume() {
	    	super.onResume();
	    }
	    
	    // use device backbutton to go back one page in webview
	    @Override
	    public boolean onKeyDown(int keyCode, KeyEvent event) {
	    	if(event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK) {
    			if(webView.canGoBack()) {
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
    					   .setNegativeButton(R.string.alert_quit_no, null)
    					   .show();
    			}
    			return true;
	    	}
	    	return super.onKeyDown(keyCode, event);
	    }
}



