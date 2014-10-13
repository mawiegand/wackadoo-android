package com.wackadoo.wackadoo_client.tasks;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import com.wackadoo.wackadoo_client.R;
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
		String urlForRequest = parent.getString(R.string.getAccountPath);
		String baseURL = parent.getString(R.string.basePath);
		String completeURL = baseURL + String.format(urlForRequest, Locale.getDefault().getCountry().toLowerCase());
		
		HttpGet request = new HttpGet(completeURL);
		StringBuilder sb = new StringBuilder();
		

		try {	    
		    request.getParams().setParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, Boolean.FALSE);
		    request.setHeader("Authorization", "Bearer " + accessToken);
		    request.setHeader("Accept", "application/json");
		    
		    HttpResponse response = null;
		    DefaultHttpClient httpClient = new DefaultHttpClient();
		    HttpConnectionParams.setSoTimeout(httpClient.getParams(), 10*1000); 
		    HttpConnectionParams.setConnectionTimeout(httpClient.getParams(),10*1000); 

		    Log.d(TAG, "Get Account Request");
		    response = httpClient.execute(request); 
		
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
