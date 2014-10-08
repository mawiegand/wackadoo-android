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
import org.apache.http.client.methods.HttpPost;
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

import com.wackadoo.wackadoo_client.helper.ResponseResult;
import com.wackadoo.wackadoo_client.helper.StaticHelper;
import com.wackadoo.wackadoo_client.interfaces.FacebookTaskCallbackInterface;
import com.wackadoo.wackadoo_client.model.DeviceInformation;
import com.wackadoo.wackadoo_client.model.UserCredentials;

public class FacebookAccountAsyncTask extends AsyncTask<String, Integer, ResponseResult>{

	private static final String TAG = FacebookAccountAsyncTask.class.getSimpleName();
	
	private Context context;
	private UserCredentials userCredentials;
	private String type;
	    
	public FacebookAccountAsyncTask(Context context, UserCredentials userCredentials, String type) {
	    this.context = context;
	    this.userCredentials = userCredentials;
	    this.type = type;
	}
		
	@Override
	protected ResponseResult doInBackground(String... params) {
		HttpRequestBase request = null;
		String url = "";
		String statusLine = "";

		try {
	    	//**********************************************//	
			//*****  check if fb id is already used  *****//
	    	if (type.equals(StaticHelper.FB_ID_TASK)) {
	    		url = StaticHelper.generateUrlForTask(context, true, type) + userCredentials.getFbPlayerId();
	    		request = new HttpGet(url);
	    		
	    		
	    	//*****************************************//	
			//*****  connect fb id and character  *****//
	    	} else if (type.equals(StaticHelper.FB_CONNECT_TASK)) {
	    		url = StaticHelper.generateUrlForTask(context, true, type) + userCredentials.getFbPlayerId();
	    		request = new HttpPut(url);
	    		
	    		// generate entity of name+value pairs
	    		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
	    		nameValuePairs.add(new BasicNameValuePair("id", userCredentials.getFbPlayerId()));
	    		nameValuePairs.add(new BasicNameValuePair("facebook[access_token]", userCredentials.getFbAccessToken()));
	    		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(nameValuePairs);
	    		entity.setContentType("application/x-www-form-urlencoded;charset=UTF-8");
	    		
				request.setHeader("Authorization", "Bearer " + userCredentials.getAccessToken().getToken());
				((HttpPut) request).setEntity(entity);
	    	}
			
			// set up request
			request.getParams().setParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, Boolean.FALSE);
			request.setHeader("Accept", "application/json");
			request.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
			
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
			
			statusLine = String.valueOf(response.getStatusLine());
			Log.d(TAG, "---> Facebook responseline: " + response.getStatusLine());
			try {
				JSONObject jsonResponse = new JSONObject(sb.toString());
				Log.d(TAG, "---> json response: " + jsonResponse);
			} catch (JSONException e) {}
			return new ResponseResult(type, true, statusLine);
			
    	} catch (Exception e) {
    		e.printStackTrace();
    	}		
		return new ResponseResult(type, false, statusLine);
	}
		
	@Override
	protected void onPostExecute(ResponseResult responseResult) {
		super.onPostExecute(responseResult);
		
		if (responseResult.isSuccess()) {
			((FacebookTaskCallbackInterface) context).onFacebookTaskFinished(responseResult);
		}
	}
}
