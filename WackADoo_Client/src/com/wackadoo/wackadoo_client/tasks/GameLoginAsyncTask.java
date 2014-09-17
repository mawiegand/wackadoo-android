package com.wackadoo.wackadoo_client.tasks;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.JsonArray;
import com.wackadoo.wackadoo_client.R;
import com.wackadoo.wackadoo_client.interfaces.GameLoginCallbackInterface;
import com.wackadoo.wackadoo_client.model.DeviceInformation;
import com.wackadoo.wackadoo_client.model.UserCredentials;

public class GameLoginAsyncTask extends AsyncTask<String, Integer, Boolean> {
	
	private static final String TAG = GameLoginAsyncTask.class.getSimpleName();
	
    private GameLoginCallbackInterface listener;
    private Context context;
	private UserCredentials userCredentials;
	private JSONObject jsonResponse;
	private ProgressDialog progressDialog;
    
    public GameLoginAsyncTask(GameLoginCallbackInterface callback, Context context, UserCredentials userCredentials, ProgressDialog progressDialog) {
    	this.listener = callback;
    	this.context = context;
    	this.userCredentials = userCredentials;
    	this.progressDialog = progressDialog;
    }
    
    @Override
	protected Boolean doInBackground(String... params) {
	    DeviceInformation deviceInformation = new DeviceInformation(context);
	    Activity parent = (Activity) this.listener;
	    
		String urlForRequest = parent.getString(R.string.loginURL);
		String baseURL = parent.getString(R.string.baseURL);
		String completeURL = baseURL + String.format(urlForRequest, Locale.getDefault().getCountry().toLowerCase());
	    HttpPost request = new HttpPost(completeURL);
	    StringBuilder sb = new StringBuilder();
	
	    String username, password;
	    if(userCredentials.getEmail().length() > 0 && userCredentials.getPassword().length() > 0) {
	    	username = userCredentials.getEmail();
	    	password = userCredentials.getPassword();
	    } else {
	    	username = userCredentials.getIdentifier();
	    	password = "egjzdsgt";
	    }
	    
	    List <NameValuePair> nameValuePairs = new ArrayList <NameValuePair> (7);
		nameValuePairs.add(new BasicNameValuePair("client_id", "WACKADOO-IOS"));
		nameValuePairs.add(new BasicNameValuePair("client_password", "5d"));
		nameValuePairs.add(new BasicNameValuePair("username", username));
		nameValuePairs.add(new BasicNameValuePair("password", password));
		nameValuePairs.add(new BasicNameValuePair("grant_type", "password"));
		nameValuePairs.add(new BasicNameValuePair("scope", ""));
		nameValuePairs.add(new BasicNameValuePair("operating_system", deviceInformation.getOs()));
		nameValuePairs.add(new BasicNameValuePair("app_token", deviceInformation.getUniqueTrackingToken()));
		nameValuePairs.add(new BasicNameValuePair("hardware_string", deviceInformation.getHardware()));
		nameValuePairs.add(new BasicNameValuePair("hardware_token", deviceInformation.getBundleBuild()));
		nameValuePairs.add(new BasicNameValuePair("version", deviceInformation.getBundleVersion()));

		try {
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(nameValuePairs);
			entity.setContentType("application/x-www-form-urlencoded;charset=UTF-8");
			
			request.getParams().setParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, Boolean.FALSE);
			request.setHeader("Accept", "application/json");
			request.setEntity(entity);  
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpConnectionParams.setSoTimeout(httpClient.getParams(), 10*1000); 
			HttpConnectionParams.setConnectionTimeout(httpClient.getParams(),10*1000); 
			
			HttpResponse response = null;
			
			Log.d(TAG, "Login Request");
			response = httpClient.execute(request); 

			InputStream in = response.getEntity().getContent();
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
	    
			String line = null;
			while((line = reader.readLine()) != null) {
				sb.append(line);
			}
			
			jsonResponse = new JSONObject(sb.toString());
			Log.d(TAG, "Login Response:" + jsonResponse);
			return true;
			
    	} catch (Exception e) {
    		e.printStackTrace();
    	}		
		return false;
    }
	
	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);

		if(progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
		
		try {
			if(result) {
				if(jsonResponse.has("error")) {
					listener.loginCallbackError(jsonResponse.getString("error"));
				
				} else {
					String accessToken = jsonResponse.getString("access_token");
					String expiresIn = jsonResponse.getString("expires_in");
					String identifier = jsonResponse.getString("user_identifer");
					listener.loginCallback(accessToken, expiresIn, identifier);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
