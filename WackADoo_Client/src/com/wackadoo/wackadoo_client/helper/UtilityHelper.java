package com.wackadoo.wackadoo_client.helper;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Locale;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;

import com.wackadoo.wackadoo_client.R;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ListAdapter;
import android.widget.ListView;

public class UtilityHelper {

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

	// prints key hash for facebook app
	public static void printKeyHash(Context context){
	    try {
	        PackageInfo info = context.getPackageManager().getPackageInfo(
	        		"com.wackadoo.wackadoo_client", PackageManager.GET_SIGNATURES);
	        for(Signature signature : info.signatures) {
	            MessageDigest md = MessageDigest.getInstance("SHA");
	            md.update(signature.toByteArray());
	            Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
	        }
	    } catch (NameNotFoundException e) {
	        Log.d("KeyHash:", e.toString());
	    } catch (NoSuchAlgorithmException e) {
	        Log.d("KeyHash:", e.toString());
	    }
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
			// FacebookAsyncTask:login
			if (type.equals("facebook_id")) {
				urlForRequest = context.getString(R.string.facebookIdPath);
			}
		
		// -----  gs06  -----
		} else {
			baseUrl = context.getString(R.string.baseGameServerPath);
			// FacebookAsyncTask:connect
			if (type.equals("facebook_connect")) {
				urlForRequest = context.getString(R.string.facebookConnectPath);
			}
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
}
