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

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.util.Log;

import com.adjust.sdk.ActivityKind;
import com.adjust.sdk.Adjust;
import com.adjust.sdk.ResponseData;
import com.wackadoo.wackadoo_client.R;
import com.wackadoo.wackadoo_client.helper.StaticHelper;
import com.wackadoo.wackadoo_client.interfaces.GameLoginCallbackInterface;
import com.wackadoo.wackadoo_client.model.DeviceInformation;
import com.wackadoo.wackadoo_client.model.UserCredentials;

public class GameLoginAsyncTask extends AsyncTask<String, Integer, Boolean> {
	
	private static final String TAG = GameLoginAsyncTask.class.getSimpleName();
	
    private Context context;
	private UserCredentials userCredentials;
	private JSONObject jsonResponse;
	private boolean refresh, restoreAccount;
    
    public GameLoginAsyncTask(Context context, UserCredentials userCredentials, boolean restoreAccount, boolean refresh) {
    	this.context = context;
    	this.refresh = refresh;
    	this.userCredentials = userCredentials;
    	this.restoreAccount = restoreAccount;
    }
    
    @Override
	protected Boolean doInBackground(String... params) {
	    DeviceInformation deviceInformation = new DeviceInformation(context);
		String completeURL = StaticHelper.generateUrlForTask(context, true, context.getString(R.string.loginPath), null);
	    StringBuilder sb = new StringBuilder();
	
	    String username, password;
	    if (userCredentials.getEmail().length() > 0) {
	    	username = userCredentials.getEmail();	    	
	    } else {
	    	if (userCredentials.getUsername().length() > 0) {
	    		username = userCredentials.getUsername();
	    	} else {
	    		username = userCredentials.getIdentifier();	    	
	    	}
	    }
	    if (userCredentials.getPassword().length() > 0) {
	    	password = userCredentials.getPassword();
	    } else {
	    	password = "egjzdsgt";
	    }
	    
	    List <NameValuePair> nameValuePairs = new ArrayList <NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("client_id", "WACKADOO-ANDROID"));
		nameValuePairs.add(new BasicNameValuePair("client_password", "5d"));
		nameValuePairs.add(new BasicNameValuePair("grant_type", "password"));
		nameValuePairs.add(new BasicNameValuePair("scope", "5dentity payment"));
		
		if (!restoreAccount) {
			nameValuePairs.add(new BasicNameValuePair("username", username));
		} else {
			nameValuePairs.add(new BasicNameValuePair("restore_with_device_token", "true"));
		}
		nameValuePairs.add(new BasicNameValuePair("password", password));
		
		// tracking data
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
			
			if (StaticHelper.debugEnabled) {
				Log.d(TAG, "request for " + username + " and pw " + password);
				Log.d(TAG, "response:" + jsonResponse);
			}
			
			if (jsonResponse.has("error")) {
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
		
		try {
			if (result) {
					String accessToken = jsonResponse.getString("access_token");
					String expiresIn = jsonResponse.getString("expires_in");
					String identifier = jsonResponse.getString("user_identifer");
					((GameLoginCallbackInterface) context).loginCallback(result.booleanValue(), accessToken, expiresIn, identifier, restoreAccount, refresh);
				
			} else {
				if (jsonResponse.has("error")) {
					((GameLoginCallbackInterface) context).loginCallbackError(jsonResponse.getString("error"), restoreAccount, refresh);
				} else {
					((GameLoginCallbackInterface) context).loginCallbackError(null, restoreAccount, refresh);
				}
			}
			
		} catch (Exception e) {
			((GameLoginCallbackInterface) context).loginCallbackError(null, restoreAccount, refresh);
			e.printStackTrace();
		}
	}
}
