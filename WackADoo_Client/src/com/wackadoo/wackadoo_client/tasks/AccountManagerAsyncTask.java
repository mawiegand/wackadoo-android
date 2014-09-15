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
		
		if(type.equals("mail")) {
			urlForRequest = parent.getString(R.string.changeEmailURL);
		} else {
			urlForRequest = parent.getString(R.string.changePasswordURL);
		}
		String baseURL = parent.getString(R.string.baseURL);
		String completeURL = baseURL + String.format(urlForRequest, Locale.getDefault().getCountry().toLowerCase());
		
		HttpPost request = new HttpPost(completeURL);
		StringBuilder sb = new StringBuilder();
		
		List <NameValuePair> nameValuePairs = new ArrayList <NameValuePair> (6);
		if(type.equals("mail")) {
			nameValuePairs.add(new BasicNameValuePair("characterNewMail", value));
		} else {
			nameValuePairs.add(new BasicNameValuePair("characterNewPassword", value));
		}
		nameValuePairs.add(new BasicNameValuePair("grant_type", "bearer"));
		nameValuePairs.add(new BasicNameValuePair("access_token", userCredentials.getAccessToken().getToken()));

		try {
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(nameValuePairs);
		    entity.setContentType("application/x-www-form-urlencoded;charset=UTF-8");
		    
		    request.getParams().setParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, Boolean.FALSE);
		    request.setHeader("Accept", "application/json");
		    request.setEntity(entity);  
		    
		    HttpResponse response = null;
		    DefaultHttpClient httpClient = new DefaultHttpClient();
		    HttpConnectionParams.setSoTimeout(httpClient.getParams(), 10*1000); 
		    HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), 10*1000); 

		    Log.d(TAG, "Account Manager Request Type: " + type);
		    response = httpClient.execute(request); 
		
		    InputStream in = response.getEntity().getContent();
		    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		    String line = null;
		    while((line = reader.readLine()) != null){
		        sb.append(line);
		    }
		    
		    Log.d(TAG, "Account Manager Response: " + sb);
		    JSONObject jsonResponse = new JSONObject(sb.toString());
		    Log.d(TAG, "Account Manager Response: " + jsonResponse);
		    
		    if(jsonResponse.has("result")){
		    	if(jsonResponse.getBoolean("result")) {
		    		return true;
		    	}
		    }
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);
		
		if(result) {
			listener.accountManagerCallback("passwort", true, value);
		}
	}
}
