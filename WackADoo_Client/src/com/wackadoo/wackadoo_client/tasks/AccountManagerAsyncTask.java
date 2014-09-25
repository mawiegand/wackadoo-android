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
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.AbstractHttpMessage;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import com.wackadoo.wackadoo_client.R;
import com.wackadoo.wackadoo_client.interfaces.AccountManagerCallbackInterface;
import com.wackadoo.wackadoo_client.model.UserCredentials;

public class AccountManagerAsyncTask extends AsyncTask<String, Integer, Boolean>{

	private final static String TAG = AccountManagerAsyncTask.class.getSimpleName();
	
    private AccountManagerCallbackInterface listener;
    private ProgressDialog progressDialog;
    private String value, type;
    private UserCredentials userCredentials;
    private JSONObject jsonResponse;
    
    public AccountManagerAsyncTask(AccountManagerCallbackInterface callback, ProgressDialog progressDialog, UserCredentials userCredentials, String type, String value) {
    	this.listener = callback;
    	this.progressDialog = progressDialog;
    	this.userCredentials = userCredentials;
    	this.value = value;
    	this.type = type;
    }
	
	@Override
	protected Boolean doInBackground(String... params) {
		Activity parent = (Activity) listener;
		String urlForRequest;

		String completeURL;
		AbstractHttpMessage request;
		List <NameValuePair> nameValuePairs = new ArrayList <NameValuePair>();
		
		// change email
		if(type.equals("mail")) {
			urlForRequest = parent.getString(R.string.changeEmailURL) + userCredentials.getClientID();
			completeURL = parent.getString(R.string.baseURL) + String.format(urlForRequest, Locale.getDefault().getCountry().toLowerCase());
			request = new HttpPut(completeURL);
			nameValuePairs.add(new BasicNameValuePair("identity[email]", value)); //characterNewMail	
			
		// change password
		} else {
			urlForRequest = parent.getString(R.string.changePasswordURL);
			completeURL = userCredentials.getHostname() + String.format(urlForRequest, Locale.getDefault().getCountry().toLowerCase());
			request = new HttpPost(completeURL);
			Log.d(TAG, "***** change password from '" + userCredentials.getPassword() + "' to '" + value + "'");
			nameValuePairs.add(new BasicNameValuePair("character[password]", value));	
		}

		Log.d(TAG, "completeURL: " + completeURL);
		StringBuilder sb = new StringBuilder();

		try {
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(nameValuePairs);
		    entity.setContentType("application/x-www-form-urlencoded;charset=UTF-8");
		    
		    request.getParams().setParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, Boolean.FALSE);
		    request.setHeader("Authorization", "Bearer " + userCredentials.getAccessToken().getToken());
		    request.setHeader("Accept", "application/json");
		    ((HttpEntityEnclosingRequestBase) request).setEntity(entity);  
		    
		    HttpResponse response = null;
		    DefaultHttpClient httpClient = new DefaultHttpClient();
		    HttpConnectionParams.setSoTimeout(httpClient.getParams(), 10*1000); 
		    HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), 10*1000); 

		    response = httpClient.execute((HttpUriRequest) request); 
		
		    InputStream in = response.getEntity().getContent();
		    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		    String line = null;
		    while((line = reader.readLine()) != null){
		        sb.append(line);
		    }
		    		    
		    jsonResponse = new JSONObject(sb.toString());
		    Log.d(TAG, "Account Manager Response: " + jsonResponse);
		    return true;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);		
		result = !jsonResponse.has("error_description") && result;
		listener.accountManagerCallback(type, result, value);
	}
}
