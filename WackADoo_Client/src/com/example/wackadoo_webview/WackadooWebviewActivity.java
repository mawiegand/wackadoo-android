package com.example.wackadoo_webview;

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
	        setContentView(R.layout.wackadoo_webview);
	        webView = (WebView) findViewById(R.id.main_webView);
	        if (savedInstanceState != null) {
	            webView.restoreState(savedInstanceState);
	        }
	        
	        WebSettings webSettings = webView.getSettings();
	        webSettings.setJavaScriptEnabled(true);
	        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
	       
	        //webSettings.setUseWideViewPort(true);
	       // webSettings.setLoadWithOverviewMode(true);
	        
	    /*    
	        webView.setWebViewClient(new WebViewClient() {
	        	@Override
	            public boolean shouldOverrideUrlLoading(WebView view, String url) {
	        		System.out.println("asdf: webview called"); 
	                view.loadUrl(url);
	                return true;
	            }
	        });

	        webView.setWebChromeClient(new WebChromeClient() {
	            
	      	  public boolean onCreateWindow (WebView view, boolean dialog, boolean userGesture, Message resultMsg) {
	      		  System.out.println("asdf: it's called");
	      		  return false;
	      	  }
	      });

	        webView.loadUrl("file:///android_asset/index.html");
	        */
	        webView.setWebChromeClient(new WebChromeClient());
	        webView.setWebViewClient(new WebViewClient());
	        
	        String urlToLoad = "Javascript:( function() { window.name = JSON.stringify({ \"accessToken\" : \"eyJ0b2tlbiI6eyJpZGVudGlmaWVyIjoiQmxBd0FUaFNWRHR3RHVWRiIsInNjb3BlIjpbInBheW1lbnQiLCI1ZGVudGl0eSIsIndhY2thZG9vLXJvdW5kNSJdLCJ0aW1lc3RhbXAiOiIyMDE0LTA0LTAyVDExOjU3OjI5KzAyOjAwIn0sInNpZ25hdHVyZSI6ImE4ZDc3M2UxNjc0MDViNDFjMGM2MDJiMDJiOGNiMmExYjc5Y2YzYTMifQ==\", \"client_id\" : \"WACKADOOHTML5-ROUND5\", \"expiration\" : \"28800\", \"locale\" : \"de_DE\"  }) ;  window.open(\"https://gs05.wack-a-doo.com/client?t=28704894\");})()";
	        webView.loadUrl(urlToLoad);
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



