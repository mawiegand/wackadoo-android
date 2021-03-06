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

import com.wackadoo.wackadoo_client.R;
import com.wackadoo.wackadoo_client.helper.StaticHelper;
import com.wackadoo.wackadoo_client.interfaces.FacebookTaskCallbackInterface;
import com.wackadoo.wackadoo_client.model.ResponseResult;
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
		String method = null;
		String url = "";
		String statusLine = "";
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

		try {
	    	//**********************************************//	
			//*****  check if fb id is already used  *****//
	    	if (type.equals(StaticHelper.FB_ID_TASK)) {
	    		url = StaticHelper.generateUrlForTask(context, true, context.getString(R.string.facebookIdPath), null) + userCredentials.getFbPlayerId();
	    		method = HttpGet.METHOD_NAME;
	    		
	    	//*****************************************//	
			//*****  connect fb id and character  *****//
	    	} else if (type.equals(StaticHelper.FB_CONNECT_TASK)) {
	    		url = StaticHelper.generateUrlForTask(context, true, context.getString(R.string.facebookConnectPath), null) + userCredentials.getFbPlayerId();
	    		method = HttpPut.METHOD_NAME;
	    		
	    		// generate entity of name+value pairs	    		
	    		nameValuePairs.add(new BasicNameValuePair("id", userCredentials.getFbPlayerId()));
	    		nameValuePairs.add(new BasicNameValuePair("facebook[access_token]", userCredentials.getAccessToken().getFbToken()));
	    	}
			
			HttpResponse response = StaticHelper.executeRequest(method, url, nameValuePairs, userCredentials.getAccessToken().getToken());
			
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
				Log.d(TAG, "responseline: " + response.getStatusLine());
			}
			
			try {
				JSONObject jsonResponse = new JSONObject(sb.toString());
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
