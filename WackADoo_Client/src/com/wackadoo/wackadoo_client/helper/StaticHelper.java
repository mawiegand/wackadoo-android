package com.wackadoo.wackadoo_client.helper;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpMessage;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestFactory;
import org.apache.http.HttpResponse;
import org.apache.http.MethodNotSupportedException;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.DefaultHttpRequestFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.HttpMethod;
import com.wackadoo.wackadoo_client.R;
import com.wackadoo.wackadoo_client.model.UserCredentials;
import com.wackadoo.wackadoo_client.tasks.GetCharacterAsyncTask;

public class StaticHelper {
	
	public static final String FB_ID_TASK = "facebook_id_task";
	public static final String FB_CONNECT_TASK = "facebook_connect_task";
	public static final String FB_LOGIN_TASK = "facebook_login_task";
	private static final String TAG = StaticHelper.class.getSimpleName();

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
	public static String generateUrlForTask(Context context, boolean basePath, String urlForRequest) {
		String baseUrl = "", completeUrl = ""; 
		
		// -----  www  -----
		if (basePath) {
			baseUrl = context.getString(R.string.basePath);		
		// -----  gs06  -----
		} else {
			baseUrl = context.getString(R.string.baseGameServerPath);
		}
		completeUrl = baseUrl + String.format(urlForRequest, Locale.getDefault().getCountry().toLowerCase());
		return completeUrl;
	}
	
	public static HttpResponse executeRequest(String method, String url, List<NameValuePair> values, String accessToken) throws ClientProtocolException, IOException {
		Log.d(TAG, "completeURL: " + url);
		HttpResponse response = null;
		HttpRequestBase request = null;
		if (method.equals(HttpGet.METHOD_NAME)) {
			request = new HttpGet(url);
		}
		if (method.equals(HttpPost.METHOD_NAME)) {
			request = new HttpPost(url);
		}
		if (method.equals(HttpPut.METHOD_NAME)) {
			request = new HttpPut(url);
		}
		request.getParams().setParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, Boolean.FALSE);
		if (accessToken != null) {
			request.setHeader("Authorization", "Bearer " + accessToken);
		}
		request.setHeader("Accept", "application/json");

		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpConnectionParams.setSoTimeout(httpClient.getParams(), 10*1000); 
		HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), 10*1000); 

		if (request instanceof HttpEntityEnclosingRequestBase) {
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(values);
			entity.setContentType("application/x-www-form-urlencoded;charset=UTF-8");
			((HttpEntityEnclosingRequestBase) request).setEntity(entity);  
		}

		response = httpClient.execute(request);		
		return response;
	}
	
	// set up the given httpRequest & httpClient for an async task
	public static void setUpHttpObjects(DefaultHttpClient httpClient) {
		HttpConnectionParams.setSoTimeout(httpClient.getParams(), 10*1000); 
		HttpConnectionParams.setConnectionTimeout(httpClient.getParams(),10*1000); 
	}

	// set up backgroundMusicPlayer for background music 
	public static void setUpPlayer(Context context) {
		backgroundMusicPlayer = MediaPlayer.create(context, R.raw.themesong);
		backgroundMusicPlayer.setLooping(true);
		backgroundMusicPlayer.setVolume(100, 100);
	}
	
	// static method to play a click sound, typically called when button is clicked
	public static void playClickSound(Context context) {
		MediaPlayer clickPlayer = MediaPlayer.create(context, R.raw.click);
		clickPlayer.start();
	}

	// overrides font with custom font in assets/fonts
	public static void overrideFonts(final Context context, final View v) {
		Typeface tf = Typeface.createFromAsset(context.getAssets(), "fonts/Montserrat-Regular.ttf");
		
		try {
	        if (v instanceof ViewGroup) {
	            ViewGroup vg = (ViewGroup) v;
	            for (int i = 0; i<vg.getChildCount(); i++) {
	                View child = vg.getChildAt(i);
	                overrideFonts(context, child);
	            }
	        } else if (v instanceof TextView) {
	            ((TextView)v).setTypeface(tf);
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}

	public static void styleDialog(Context context, Dialog dialog) {
		 // Set title divider color
	    int id = context.getResources().getIdentifier("titleDivider", "id", "android");
	    View titleDivider = dialog.findViewById(id);
	    if (titleDivider != null) {
	    	titleDivider.setBackgroundColor(context.getResources().getColor(R.color.textbox_orange));
	    }
	    
	    // change font of dialog texts
	    Typeface tf = Typeface.createFromAsset(context.getAssets(), "fonts/Montserrat-Regular.ttf");

	    // title
	    id = context.getResources().getIdentifier("alertTitle", "id", "android");
	    if (dialog.findViewById(id) != null) {
	    	((TextView) dialog.findViewById(id)).setTypeface(tf);
	    }
	    // title
	    id = context.getResources().getIdentifier("progressTitle", "id", "android");
	    if (dialog.findViewById(id) != null) {
	    	((TextView) dialog.findViewById(id)).setTypeface(tf);
	    }
	    // message
	    id = context.getResources().getIdentifier("message", "id", "android");
	    if (dialog.findViewById(id) != null) {
	    	((TextView) dialog.findViewById(id)).setTypeface(tf);
	    }
	    
	    // positive button
	    id = context.getResources().getIdentifier("button1", "id", "android");
	    if (dialog.findViewById(id) != null) {
	    	((TextView) dialog.findViewById(id)).setTypeface(tf);
	    }
	    
	    // negative button
	    id = context.getResources().getIdentifier("button2", "id", "android");
	    if (dialog.findViewById(id) != null) {
	    	((TextView) dialog.findViewById(id)).setTypeface(tf);
	    }
	}
}
