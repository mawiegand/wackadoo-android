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
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.wackadoo.wackadoo_client.R;
import com.wackadoo.wackadoo_client.helper.StaticHelper;
import com.wackadoo.wackadoo_client.interfaces.CreateAccountCallbackInterface;

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
		String completeURL = StaticHelper.generateUrlForTask(parent, true, parent.getString(R.string.createAccountPath), null);
		
		StringBuilder sb = new StringBuilder();
		
		List <NameValuePair> nameValuePairs = new ArrayList <NameValuePair>(6);
		nameValuePairs.add(new BasicNameValuePair("client_id", "WACKADOO-IOS"));
		nameValuePairs.add(new BasicNameValuePair("client_password", "5d"));
		nameValuePairs.add(new BasicNameValuePair("generic_password", "1"));
		nameValuePairs.add(new BasicNameValuePair("nickname_base", "WackyUser"));
		nameValuePairs.add(new BasicNameValuePair("password", "egjzdsgt"));
		nameValuePairs.add(new BasicNameValuePair("password_confirmation", "egjzdsgt"));

		try {
			HttpResponse response = StaticHelper.executeRequest(HttpPost.METHOD_NAME, completeURL, nameValuePairs, null);
		
		    InputStream in = response.getEntity().getContent();
		    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		    String line = null;
		    while((line = reader.readLine()) != null){
		        sb.append(line);
		    }
		    
		    jsonResponse = new JSONObject(sb.toString());
		    
		    if (StaticHelper.debugEnabled) {
		    	Log.d(TAG, "response:" + jsonResponse);
		    }
		    return true;
			
		} catch (Exception e) {
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
