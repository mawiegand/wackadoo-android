package com.wackadoo.wackadoo_client.tasks;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import com.wackadoo.wackadoo_client.R;
import com.wackadoo.wackadoo_client.helper.StaticHelper;
import com.wackadoo.wackadoo_client.interfaces.GetAccountCallbackInterface;
import com.wackadoo.wackadoo_client.model.UserCredentials;

public class GetAccountAsyncTask extends AsyncTask<String, Integer, Boolean> {
	
	private final static String TAG = GetAccountAsyncTask.class.getSimpleName();
	
    private GetAccountCallbackInterface listener;
    private JSONObject jsonResponse;

	private String accessToken;

	private boolean restoreAccount;
    
    public GetAccountAsyncTask(GetAccountCallbackInterface callback, UserCredentials userCredentials, boolean restoreAccount) {
    	this.listener = callback;
    	this.restoreAccount = restoreAccount;
    	this.accessToken = userCredentials.getAccessToken().getToken();
    }
	
	@Override
	protected Boolean doInBackground(String... params) {
		Activity parent = (Activity) listener;
		String completeURL = StaticHelper.generateUrlForTask(parent, true, parent.getString(R.string.getAccountPath), null);
		
		StringBuilder sb = new StringBuilder();
		
		try {	    
		    Log.d(TAG, "Get Account Request");
		    HttpResponse response = StaticHelper.executeRequest(HttpGet.METHOD_NAME, completeURL, null, accessToken);
		
		    InputStream in = response.getEntity().getContent();
		    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		    String line = null;
		    while((line = reader.readLine()) != null){
		        sb.append(line);
		    }
		    
		    jsonResponse = new JSONObject(sb.toString());
		    Log.d(TAG, "Get Account Response:" + jsonResponse);
		    return true;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);
		
		if (result) {
			try {
				String identifier = jsonResponse.getString("identifier");
				String username = jsonResponse.getString("nickname");
				String accountId = jsonResponse.getString("id");
				listener.getAccountCallback(identifier, username, accountId, restoreAccount);
				
			} catch(Exception e) {
				e.printStackTrace();
			}
			
		} else {
		}
	}
}
