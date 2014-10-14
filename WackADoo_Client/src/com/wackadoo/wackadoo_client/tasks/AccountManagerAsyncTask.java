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
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.AbstractHttpMessage;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import com.wackadoo.wackadoo_client.R;
import com.wackadoo.wackadoo_client.helper.StaticHelper;
import com.wackadoo.wackadoo_client.interfaces.AccountManagerCallbackInterface;
import com.wackadoo.wackadoo_client.model.UserCredentials;

public class AccountManagerAsyncTask extends AsyncTask<String, Integer, Integer>{

	private static final String TAG = AccountManagerAsyncTask.class.getSimpleName();
	
    private AccountManagerCallbackInterface listener;
    private ProgressDialog progressDialog;
    private String value, type;
    private UserCredentials userCredentials;
    private JSONObject jsonResponse;
    
    public AccountManagerAsyncTask(AccountManagerCallbackInterface listener, ProgressDialog progressDialog, UserCredentials userCredentials, String type, String value) {
    	this.listener = listener;
    	this.progressDialog = progressDialog;
    	this.userCredentials = userCredentials;
    	this.value = value;
    	this.type = type;
    }
	
	@Override
	protected Integer doInBackground(String... params) {
		Activity parent = (Activity) listener;
		String urlForRequest, completeURL;
		HttpRequestBase request;
		String method;
		List<NameValuePair> nameValuePairs = new ArrayList <NameValuePair>();
		
		// change email
		if (type.equals("mail")) {
			urlForRequest = 
			completeURL = StaticHelper.generateUrlForTask(parent, true, parent.getString(R.string.changeEmailPath) + userCredentials.getAccountId());
			method = HttpPut.METHOD_NAME;
			nameValuePairs.add(new BasicNameValuePair("identity[email]", value)); 
			
		// change password
		} else {
			urlForRequest = parent.getString(R.string.changePasswordPath);
			completeURL = userCredentials.getHostname() + String.format(urlForRequest, Locale.getDefault().getCountry().toLowerCase());
			method = HttpPost.METHOD_NAME;
			Log.d(TAG, "***** change password from '" + userCredentials.getPassword() + "' to '" + value + "'");
			nameValuePairs.add(new BasicNameValuePair("character[password]", value));	
		}

		StringBuilder sb = new StringBuilder();

		try {
			HttpResponse response = StaticHelper.executeRequest(method, completeURL, nameValuePairs, userCredentials.getAccessToken().getToken());
		
		    String responseLine = response.getStatusLine().toString();
		    Log.d(TAG, "response line: " + responseLine);
		    
		    if (responseLine.contains("200 OK")) {
		    	InputStream in = response.getEntity().getContent();
		    	BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		    	String line = null;
		    	while((line = reader.readLine()) != null){
		    		sb.append(line);
		    	}
		    	jsonResponse = new JSONObject(sb.toString());
		    	Log.d(TAG, "Account Manager Response: " + jsonResponse);
		    	return 200;
		    
		    } else if (responseLine.contains("403 Forbidden")) {
		    	return 403;
			} else if (responseLine.contains("409 Conflict")) {
				return 409;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 500;
	}
	
	@Override
	protected void onPostExecute(Integer result) {
		super.onPostExecute(result);
		if (result == 200) {
			result = !jsonResponse.has("error_description") ? 200 : 500;
		}
		listener.accountManagerCallback(type, result, value);
	}
}
