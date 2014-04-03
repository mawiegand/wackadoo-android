package com.wackadoo.wackadoo_client;

import com.example.wackadoo_webview.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WackadooWebviewActivity extends Activity {	

		private WebView webView;
		
	    @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_wackadoowebview);
	        webView = (WebView) findViewById(R.id.main_webView);
	        if (savedInstanceState != null) {
	            webView.restoreState(savedInstanceState);
	        }
	        
	        WebSettings webSettings = webView.getSettings();
	        webSettings.setJavaScriptEnabled(true);
	        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
	       
	        //webSettings.setUseWideViewPort(true);
	       // webSettings.setLoadWithOverviewMode(true);
	        
	        webView.setWebChromeClient(new WebChromeClient());
	        webView.setWebViewClient(new WebViewClient());
	        
	        webSettings.setAllowFileAccess(true);
	        webView.loadUrl("file:///android_asset/index.html");
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

}



