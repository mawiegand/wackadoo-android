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
import com.wackadoo.wackadoo_client.interfaces.CreateAccountCallbackInterface;

public class CreateAccountAsyncTask extends AsyncTask<String, Integer, Boolean> {
	
	private final static String TAG = CreateAccountAsyncTask.class.getSimpleName();
	
    private CreateAccountCallbackInterface listener;
    private ProgressDialog progressDialog;
    private JSONObject jsonResponse;
    
    public CreateAccountAsyncTask(CreateAccountCallbackInterface callback, ProgressDialog progressDialog) {
    	this.listener = callback;
    	this.progressDialog = progressDialog;
    }
	
	@Override
	protected Boolean doInBackground(String... params) {
		Activity parent = (Activity) listener;
		String urlForRequest = parent.getString(R.string.createAccountURL);
		String baseURL = parent.getString(R.string.baseURL);
		String completeURL = baseURL + String.format(urlForRequest, Locale.getDefault().getCountry().toLowerCase());
		
		HttpPost request = new HttpPost(completeURL);
		StringBuilder sb = new StringBuilder();
		
		List <NameValuePair> nameValuePairs = new ArrayList <NameValuePair>(6);
		nameValuePairs.add(new BasicNameValuePair("client_id", "WACKADOO-IOS"));
		nameValuePairs.add(new BasicNameValuePair("client_password", "5d"));
		nameValuePairs.add(new BasicNameValuePair("generic_password", "1"));
		nameValuePairs.add(new BasicNameValuePair("nickname_base", "WackyUser"));
		nameValuePairs.add(new BasicNameValuePair("password", "egjzdsgt"));
		nameValuePairs.add(new BasicNameValuePair("password_confirmation", "egjzdsgt"));

		try {
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(nameValuePairs);
		    entity.setContentType("application/x-www-form-urlencoded;charset=UTF-8");
		    
		    request.getParams().setParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, Boolean.FALSE);
		    request.setHeader("Accept", "application/json");
		    request.setEntity(entity);  
		    
		    HttpResponse response = null;
		    DefaultHttpClient httpClient = new DefaultHttpClient();
		    HttpConnectionParams.setSoTimeout(httpClient.getParams(), 10*1000); 
		    HttpConnectionParams.setConnectionTimeout(httpClient.getParams(),10*1000); 

		    Log.d(TAG, "Create Account Request");
		    response = httpClient.execute(request); 
		
		    InputStream in = response.getEntity().getContent();
		    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		    String line = null;
		    while((line = reader.readLine()) != null){
		        sb.append(line);
		    }
		    
		    jsonResponse = new JSONObject(sb.toString());
		    Log.d(TAG, "Create Account Response:" + jsonResponse);
		    return true;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);
		
		if (progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
		
		if (result) {
			try {
				String identifier = jsonResponse.getString("identifier");
				String username = jsonResponse.getString("nickname");
				String accountId = jsonResponse.getString("id");
				listener.onRegistrationCompleted(identifier, username, accountId);
				
			} catch(Exception e) {
				e.printStackTrace();
			}
			
		} else {
		}
	}
}
