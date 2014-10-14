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
import android.os.AsyncTask;
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
    		url = StaticHelper.generateUrlForTask(context, true, context.getString(R.string.facebookLoginPath));
    		request = new HttpPost(url);
    		
    		List <NameValuePair> nameValuePairs = new ArrayList <NameValuePair>();
    		nameValuePairs.add(new BasicNameValuePair("fb_player_id", userCredentials.getFbPlayerId()));
    		nameValuePairs.add(new BasicNameValuePair("client_id", "WACKADOO-FBCANVAS"));
    		nameValuePairs.add(new BasicNameValuePair("client_password", "5d"));
    		nameValuePairs.add(new BasicNameValuePair("grant_type", "fb-player-id"));
    		nameValuePairs.add(new BasicNameValuePair("scope", "5dentity"));
    		
    		// tracking data
    		DeviceInformation deviceInformation = new DeviceInformation(context);
    		nameValuePairs.add(new BasicNameValuePair("operating_system", deviceInformation.getOs()));
    		nameValuePairs.add(new BasicNameValuePair("app_token", deviceInformation.getUniqueTrackingToken()));
    		nameValuePairs.add(new BasicNameValuePair("hardware_string", deviceInformation.getHardware()));
    		nameValuePairs.add(new BasicNameValuePair("hardware_token", deviceInformation.getBundleBuild()));
    		nameValuePairs.add(new BasicNameValuePair("version", deviceInformation.getBundleVersion()));
    		nameValuePairs.add(new BasicNameValuePair("[device_information][device_token]", deviceInformation.getUniqueTrackingToken()));
    		nameValuePairs.add(new BasicNameValuePair("vendor_token", deviceInformation.getUniqueTrackingToken()));
    		
    		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(nameValuePairs);
    		entity.setContentType("application/x-www-form-urlencoded;charset=UTF-8");
    		((HttpPost) request).setEntity(entity);
    	
			// set up request
			request.getParams().setParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, Boolean.FALSE);
			request.setHeader("Accept", "application/json");
			
			// set up http client
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpConnectionParams.setSoTimeout(httpClient.getParams(), 10*1000); 
			HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), 10*1000); 
		
			Log.d(TAG, "---> Complete url " + url);
			Log.d(TAG, "---> Facebook request for " + userCredentials.getEmail() + " | " + userCredentials.getFbPlayerId());
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
			Log.d(TAG, "---> json response: " + sb.toString());
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
