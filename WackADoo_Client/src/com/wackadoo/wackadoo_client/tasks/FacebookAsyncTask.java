package com.wackadoo.wackadoo_client.tasks;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.wackadoo.wackadoo_client.helper.UtilityHelper;
import com.wackadoo.wackadoo_client.model.UserCredentials;

public class FacebookAsyncTask extends AsyncTask<String, Integer, Boolean>{

	private static final String TAG = FacebookAsyncTask.class.getSimpleName();
	
	private Context context;
	private UserCredentials userCredentials;
	private String type;
	    
	public FacebookAsyncTask(Context context, UserCredentials userCredentials, String type) {
	    this.context = context;
	    this.userCredentials = userCredentials;
	    this.type = type;
	}
		
	@Override
	protected Boolean doInBackground(String... params) {
	    try {
	    	HttpRequestBase request = null;
	    	String url = "";
	    	
	    	// check if facebook id is already used
	    	if (type.equals("facebook_id")) {
	    		url = UtilityHelper.generateUrlForTask(context, true, type) + userCredentials.getFbPlayerId();
	    		request = new HttpGet(url);
	    		
	    	// connect facebook id and character
	    	} else if(type.equals("facebook_connect")) {
	    		url = UtilityHelper.generateUrlForTask(context, false, type);
	    		request = new HttpPut(url);
	    		
	    		// generate entity of name+value pairs
	    		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
	    		nameValuePairs.add(new BasicNameValuePair("fb_player_id", userCredentials.getFbPlayerId()));
	    		nameValuePairs.add(new BasicNameValuePair("fb_access_token", userCredentials.getFbAccessToken()));
	    		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(nameValuePairs);
	    		entity.setContentType("application/x-www-form-urlencoded;charset=UTF-8");
	    		
				request.setHeader("Authorization", "Bearer " + userCredentials.getAccessToken().getToken());
				((HttpPut) request).setEntity(entity);
	    	}
			
			// set up post request
			request.getParams().setParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, Boolean.FALSE);
			request.setHeader("Accept", "application/json");
			
			// set up http client
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpConnectionParams.setSoTimeout(httpClient.getParams(), 10*1000); 
			HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), 10*1000); 
		
			Log.d(TAG, "---> Complete url " + url);
			Log.d(TAG, "---> Facebook request for " + userCredentials.getEmail() + " | " + userCredentials.getFbPlayerId() + " | " + userCredentials.getFbAccessToken());
			HttpResponse response = httpClient.execute(request); 
			
			// validate response
			InputStream in = response.getEntity().getContent();
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ( (line = reader.readLine()) != null) {
				sb.append(line);
			}
			
			Log.d(TAG, "---> Facebook responseline: " + response.getStatusLine());
			try {
				JSONObject jsonResponse = new JSONObject(sb.toString());
				Log.d(TAG, "---> json response: " + jsonResponse);
			} catch (JSONException e) {
				Log.d(TAG, "---> Keine json response");
			}
			return true;
			
    	} catch (Exception e) {
    		e.printStackTrace();
    	}		
		return false;
		
//		Session loginSession = Session.getActiveSession();
//		if (loginSession == null || loginSession.getState().isClosed()) {
//			String appId = context.getResources().getString(R.string.facebookAppId);
//			loginSession = new Session.Builder(context).setApplicationId(appId).build();
//		}
	}
		
	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);
		// 201 = created, 200 = success
		// facebook ids return code 200/304 found!
		// facebook ids return code 404 free to use
		
		if (result) {
//			((FacebookLoginCallbackInterface) context).onFacebookRegistrationCompleted(loginSession);
		}
	}
}
