package com.wackadoo.wackadoo_client.tasks;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.provider.Settings.Secure;
import android.util.Log;

import com.wackadoo.wackadoo_client.R;
import com.wackadoo.wackadoo_client.helper.StaticHelper;
import com.wackadoo.wackadoo_client.interfaces.FacebookTaskCallbackInterface;
import com.wackadoo.wackadoo_client.model.DeviceInformation;
import com.wackadoo.wackadoo_client.model.ResponseResult;
import com.wackadoo.wackadoo_client.model.UserCredentials;

public class FacebookLoginAsyncTask extends AsyncTask<String, Integer, ResponseResult>{

	private static final String TAG = FacebookLoginAsyncTask.class.getSimpleName();
	
	private Context context;
	private UserCredentials userCredentials;
	    
	public FacebookLoginAsyncTask(Context context, UserCredentials userCredentials) {
	    this.context = context;
	    this.userCredentials = userCredentials;
	}
		
	@Override
	protected ResponseResult doInBackground(String... params) {
		HttpRequestBase request = null;
		String url = "";
		String statusLine = "";

		try {
    		url = StaticHelper.generateUrlForTask(context, true, context.getString(R.string.facebookLoginPath), null);
    		
    		List <NameValuePair> nameValuePairs = new ArrayList <NameValuePair>();
    		nameValuePairs.add(new BasicNameValuePair("fb_player_id", userCredentials.getFbPlayerId()));
    		nameValuePairs.add(new BasicNameValuePair("client_id", "WACKADOO-ANDROID"));
    		nameValuePairs.add(new BasicNameValuePair("client_password", "5d"));
    		nameValuePairs.add(new BasicNameValuePair("grant_type", "fb-player-id"));
    		nameValuePairs.add(new BasicNameValuePair("scope", "5dentity"));
    		
    		// tracking data
    		DeviceInformation deviceInformation = new DeviceInformation(context);
    		nameValuePairs.add(new BasicNameValuePair("[device_information][operating_system]", deviceInformation.getOs()));					// e.g. Android 4.4.4	
    		nameValuePairs.add(new BasicNameValuePair("[device_information][app_token]", deviceInformation.getUniqueTrackingToken()));		 
    		nameValuePairs.add(new BasicNameValuePair("[device_information][hardware_string]", deviceInformation.getHardware()));				// e.g. LGE Nexus 4
    		nameValuePairs.add(new BasicNameValuePair("[device_information][hardware_token]", String.valueOf(									// hash encoded MAC adress of device
    				(((WifiManager) context.getSystemService(Context.WIFI_SERVICE)).getConnectionInfo().getMacAddress().hashCode()))));			
    		try {
    			nameValuePairs.add(new BasicNameValuePair("[device_information][version]", 														// game version
    					context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName));		
    		} catch (NameNotFoundException e) {
    			e.printStackTrace(); 
    		} 
    						
    		nameValuePairs.add(new BasicNameValuePair("[device_information][vendor_token]", deviceInformation.getUniqueTrackingToken())); 
    		nameValuePairs.add(new BasicNameValuePair("[device_information][advertiser_token]", Secure.getString(context.getContentResolver(), Secure.ANDROID_ID)));
    		
			HttpResponse response = StaticHelper.executeRequest(HttpPost.METHOD_NAME, url, nameValuePairs, null);
			
			// validate response
			InputStream in = response.getEntity().getContent();
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ( (line = reader.readLine()) != null) {
				sb.append(line);
			}
			
			statusLine = String.valueOf(response.getStatusLine());
			
			if (StaticHelper.debugEnabled) {
				Log.d(TAG, "request for " + userCredentials.getEmail() + " | " + userCredentials.getFbPlayerId());
				Log.d(TAG, "responseline: " + response.getStatusLine());
			}
			return new ResponseResult(StaticHelper.FB_LOGIN_TASK, true, statusLine, sb.toString());
				
    	} catch (Exception e) {
    		e.printStackTrace();
    	}		
		return new ResponseResult(StaticHelper.FB_LOGIN_TASK, false, statusLine);
	}
		
	@Override
	protected void onPostExecute(ResponseResult responseResult) {
		super.onPostExecute(responseResult);
		
		if (responseResult.isSuccess()) {
			((FacebookTaskCallbackInterface) context).onFacebookTaskFinished(responseResult);
		}
	}
}
