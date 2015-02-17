package com.wackadoo.wackadoo_client.tasks;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.util.Log;

import com.wackadoo.wackadoo_client.R;
import com.wackadoo.wackadoo_client.helper.StaticHelper;
import com.wackadoo.wackadoo_client.interfaces.CreateAccountCallbackInterface;
import com.wackadoo.wackadoo_client.model.DeviceInformation;

public class CreateAccountAsyncTask extends AsyncTask<String, Integer, Boolean> {
	
	private static final String TAG = CreateAccountAsyncTask.class.getSimpleName();
	
    private CreateAccountCallbackInterface listener;
    private JSONObject jsonResponse;
    
    public CreateAccountAsyncTask(CreateAccountCallbackInterface callback) {
    	this.listener = callback;
    }
	
	@Override
	protected Boolean doInBackground(String... params) {
		Activity parent = (Activity) listener;
		DeviceInformation deviceInformation = new DeviceInformation(parent);
		String completeURL = StaticHelper.generateUrlForTask(parent, true, parent.getString(R.string.createAccountPath), null);
	    StringBuilder sb = new StringBuilder();
	    
	    List <NameValuePair> nameValuePairs = new ArrayList <NameValuePair>();		
		nameValuePairs.add(new BasicNameValuePair("client_id", "WACKADOO-ANDROID"));
		nameValuePairs.add(new BasicNameValuePair("client_password", "5d"));
		nameValuePairs.add(new BasicNameValuePair("generic_password", "1"));
		nameValuePairs.add(new BasicNameValuePair("nickname_base", "WackyUser"));
		nameValuePairs.add(new BasicNameValuePair("password", "egjzdsgt"));
		nameValuePairs.add(new BasicNameValuePair("password_confirmation", "egjzdsgt"));
		
		// tracking data
		nameValuePairs.add(new BasicNameValuePair("[device_information][operating_system]", deviceInformation.getOs()));					// e.g. Android 4.4.4	
		nameValuePairs.add(new BasicNameValuePair("[device_information][app_token]", deviceInformation.getUniqueTrackingToken()));		 
		nameValuePairs.add(new BasicNameValuePair("[device_information][hardware_string]", deviceInformation.getHardware()));				// e.g. LGE Nexus 4
		nameValuePairs.add(new BasicNameValuePair("[device_information][hardware_token]", String.valueOf(									// hash encoded MAC adress of device
				(((WifiManager) parent.getSystemService(Context.WIFI_SERVICE)).getConnectionInfo().getMacAddress().hashCode()))));			
		try {
			nameValuePairs.add(new BasicNameValuePair("[device_information][version]", 														// game version
					parent.getPackageManager().getPackageInfo(parent.getPackageName(), 0).versionName));		
		} catch (NameNotFoundException e) {
			e.printStackTrace(); 
		} 
						
		nameValuePairs.add(new BasicNameValuePair("[device_information][vendor_token]", deviceInformation.getUniqueTrackingToken())); 
		nameValuePairs.add(new BasicNameValuePair("[device_information][advertiser_token]", deviceInformation.getUniqueTrackingToken())); 	// adjust trackerToken
		
		if (StaticHelper.debugEnabled) {
			for (NameValuePair nvp : nameValuePairs) {
				Log.d(TAG, nvp.getName() + ": " + nvp.getValue());
			}
		}
		
		try {
			HttpResponse response = StaticHelper.executeRequest(HttpPost.METHOD_NAME, completeURL, nameValuePairs, null);

			InputStream in = response.getEntity().getContent();
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
	    
			String line = null;
			while((line = reader.readLine()) != null) {
				sb.append(line);
			}
			
			jsonResponse = new JSONObject(sb.toString());
			
			if (jsonResponse.has("error")) {
				System.out.println(jsonResponse.toString());
				return false;
			}
			return true;
			
    	} catch (Exception e) {
    		if (sb != null) Log.e(TAG, sb.toString());
    		e.printStackTrace();
    	}		
		
		return false;
    }
	
	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);
		String identifier = null; 
		String username = null;
		String accountId = null;
		String email = null;
		
		if (result) {
			try {
				identifier = jsonResponse.getString("identifier");
				username = jsonResponse.getString("nickname");
				accountId = jsonResponse.getString("id");
				email = jsonResponse.getString("email");
			} catch (Exception e) {
				e.printStackTrace();
			}
		} 
		listener.onRegistrationCompleted(result, identifier, username, accountId, email);
	}
}
