package com.example.wackadoo_webview;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.os.Build;

public class MainActivity extends ActionBarActivity {

	private WebView webView;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        webView = (WebView) findViewById(R.id.main_webView);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        
        //Remember to use actual AccessToken & (Client)-ID
        String urlToLoad = "Javascript:( function() { window.name = JSON.stringify({ \"accessToken\" : \"eyJ0b2tlbiI6eyJpZGVudGlmaWVyIjoiQmxBd0FUaFNWRHR3RHVWRiIsInNjb3BlIjpbInBheW1lbnQiLCI1ZGVudGl0eSIsIndhY2thZG9vLXJvdW5kNSJdLCJ0aW1lc3RhbXAiOiIyMDE0LTAzLTI3VDE0OjM2OjE0KzAxOjAwIn0sInNpZ25hdHVyZSI6ImE0N2M2NGVmMzdjMjNkYTgxNDhiODUwOTc4MDRhNjhkNjBjZTJmZDgifQ\", \"client_id\" : \"WACKADOOHTML5-ROUND5\", \"expiration\" : \"28000\", \"locale\" : \"de_DE\"  }) ;  window.open(\"https://gs05.wack-a-doo.com/client?t=9921196\");})()";
      //  String urlToLoad = "Javascript:( function() { window.open(\"http://www.google.de\")})()"; //For testing only
        webView.loadUrl(urlToLoad);
        
        //// Should use index.html and Javascript to window.open. Need to overwrite a method of the WebChromeClient.
        /*
        webView.getSettings().setSupportMultipleWindows(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        
        WebChromeClient chromeClient = new WebChromeClient();
        webView.setWebChromeClient(chromeClient);
        
        webView.loadUrl("file:///android_asset/index.html"); */
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }

}
