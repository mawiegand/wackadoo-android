package com.wackadoo.wackadoo_client.helper;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.util.List;
import java.util.Locale;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.wackadoo.wackadoo_client.R;

public class StaticHelper {
	
	public static final String FB_ID_TASK = "facebook_id_task";
	public static final String FB_CONNECT_TASK = "facebook_connect_task";
	public static final String FB_LOGIN_TASK = "facebook_login_task";

	// static variables for background music
	public static MediaPlayer backgroundMusicPlayer;
	public static boolean continueMusic;

	// Workaround for dynamic height of the ListView. Fixes issue of not showing every item in listviews when in a scrollview 
	public static void setListViewHeightBasedOnChildren(ListView listView) {
	    ListAdapter listAdapter = listView.getAdapter();
	    if (listAdapter == null)
	        return;
	
	    int desiredWidth = MeasureSpec.makeMeasureSpec(listView.getWidth(), MeasureSpec.UNSPECIFIED);
	    int totalHeight = 0;
	    View view = null;
	    for (int i = 0; i < listAdapter.getCount(); i++) {
	        view = listAdapter.getView(i, view, listView);
	        if (i == 0) {
	        	view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, LayoutParams.WRAP_CONTENT));
	        }
	        view.measure(desiredWidth, MeasureSpec.UNSPECIFIED);
	        totalHeight += view.getMeasuredHeight();
	    }
	    ViewGroup.LayoutParams params = listView.getLayoutParams();
	    params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
	    listView.setLayoutParams(params);
	    listView.requestLayout();
	}

	// checks if String is valid mail adress
	public static boolean isValidMail(String email) {
		boolean result = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
		return result;
	}

	// checks if the device is connected to the internet
	public static boolean isOnline(Activity activity) {
	    ConnectivityManager cm =
	        (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo netInfo = cm.getActiveNetworkInfo();
	    if (netInfo != null && netInfo.isConnected()) {
	        return true;
	    }
	    return false;
	}
	
	public static boolean isHostAvailable(String hostname, Activity activity) {
		try {
			InetAddress.getAllByName(hostname);
			return true;
		} catch(Exception e) {
			Log.d("Server", "Host " + hostname + " is not available");
			e.printStackTrace();
			return false;
		}		
	}
	
	// generates a httpPost object for given type of asynctask
	public static String generateUrlForTask(Context context, boolean basePath, String type) {
		String baseUrl = "", urlForRequest = "", completeUrl = ""; 
		
		// -----  www  -----
		if (basePath) {
			baseUrl = context.getString(R.string.basePath);
			// FacebookAsyncTask:check id
			if (type.equals(StaticHelper.FB_ID_TASK)) {
				urlForRequest = context.getString(R.string.facebookIdPath);
			
			// FacebookAsyncTask:connect
			} else if (type.equals(StaticHelper.FB_CONNECT_TASK)) {
				urlForRequest = context.getString(R.string.facebookConnectPath);
		
			// FacebookAsyncTask:login
			} else if (type.equals(StaticHelper.FB_LOGIN_TASK)) {
				urlForRequest = context.getString(R.string.facebookLoginPath);
			}
		
		// -----  gs06  -----
		} else {
			baseUrl = context.getString(R.string.baseGameServerPath);
		}
		completeUrl = baseUrl + String.format(urlForRequest, Locale.getDefault().getCountry().toLowerCase());
		return completeUrl;
	}
	
	// set up the entity for an async task
	public static UrlEncodedFormEntity setUpEntity(List<NameValuePair> nameValuePairs) throws UnsupportedEncodingException  {
		UrlEncodedFormEntity entity = null;
		entity = new UrlEncodedFormEntity(nameValuePairs);
		entity.setContentType("application/x-www-form-urlencoded;charset=UTF-8");
		return entity;
	}
	
	// set up the given httpRequest & httpClient for an async task
	public static void setUpHttpObjects(DefaultHttpClient httpClient) {
		HttpConnectionParams.setSoTimeout(httpClient.getParams(), 10*1000); 
		HttpConnectionParams.setConnectionTimeout(httpClient.getParams(),10*1000); 
	}

	// static method to play a click sound, typically called when button is clicked
	public static void playClickSound(Context context) {
		MediaPlayer clickPlayer = MediaPlayer.create(context, R.raw.click);
		clickPlayer.start();
	}
}
