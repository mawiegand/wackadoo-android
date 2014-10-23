package com.wackadoo.wackadoo_client.activites;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.wackadoo.wackadoo_client.R;
import com.wackadoo.wackadoo_client.helper.StaticHelper;
import com.wackadoo.wackadoo_client.javascriptinterfaces.LoginJavaScriptHandler;

public class WackadooWebviewActivity extends Activity {	

	private static final String TAG = WackadooWebviewActivity.class.getSimpleName();
	
	private WebView webView;
	private int width;
	
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
        
        // test: scale webview to fit device window
        WindowManager manager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
	    DisplayMetrics metrics = new DisplayMetrics();
	    manager.getDefaultDisplay().getMetrics(metrics);
	    metrics.widthPixels /= metrics.density;
        width = metrics.widthPixels;
        
        // test: rescale window after page is loaded
        webView.setWebViewClient(new WebViewClient() {
        	@Override
        	public void onPageFinished(WebView view, String url) {
        		super.onPageFinished(view, url);
        	    webView.loadUrl("javascript:var scale = " + width + " / document.body.scrollWidth; document.body.style.zoom = scale;");
        	}
        	
        	public void onScaleChanged(WebView view, float oldScale, float newScale) {
        		super.onScaleChanged(view, oldScale, newScale);
        		Log.d(TAG, "Scale changed!");
        	}
        	
        	@Override
        	public void onReceivedSslError(WebView view,
        			SslErrorHandler handler, SslError error) {
        		handler.proceed();
        	}
        	
        });
        
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
    }
    
    // use device backbutton to go back one page in webview
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
	    	if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK) {
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
    			return true;
	    	}
	    	return super.onKeyDown(keyCode, event);
	    }
}
