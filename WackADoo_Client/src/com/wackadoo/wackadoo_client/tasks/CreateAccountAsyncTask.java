package com.wackadoo.wackadoo_client.tasks;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.SocketException;
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

import com.example.wackadoo_webview.R;
import com.wackadoo.wackadoo_client.interfaces.CreateAccountCallbackInterface;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

public class CreateAccountAsyncTask extends AsyncTask<String, Integer, Double> {
	
    private CreateAccountCallbackInterface listener;
    
    public CreateAccountAsyncTask(CreateAccountCallbackInterface callback) {
    	this.listener = callback;
    }
	
	@Override
	protected Double doInBackground(String... params) {
		try {
			postDataToServer();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public  void postDataToServer() throws Throwable {
		
		Activity parent = (Activity) this.listener;
		String urlForRequest = parent.getString(R.string.createAccountURL);
		String baseURL = parent.getString(R.string.baseURL);
		urlForRequest = baseURL + String.format(urlForRequest, Locale.getDefault().getCountry().toLowerCase());
		
		HttpPost request = new HttpPost(urlForRequest);
		StringBuilder sb=new StringBuilder();
		
		List < NameValuePair > nameValuePairs = new ArrayList < NameValuePair > (6);
		nameValuePairs.add(new BasicNameValuePair("client_id", "WACKADOO-IOS"));
		nameValuePairs.add(new BasicNameValuePair("client_password", "5d"));
		nameValuePairs.add(new BasicNameValuePair("generic_password", "1"));
		nameValuePairs.add(new BasicNameValuePair("nickname_base", "WackyUser"));
		nameValuePairs.add(new BasicNameValuePair("password", "egjzdsgt"));
		nameValuePairs.add(new BasicNameValuePair("password_confirmation", "egjzdsgt"));
	    
		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(nameValuePairs);
	    entity.setContentType("application/x-www-form-urlencoded;charset=UTF-8");
	    
	    request.getParams().setParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, Boolean.FALSE);
	    request.setHeader("Accept", "application/json");
	    request.setEntity(entity);  
	    HttpResponse response =null;
	    DefaultHttpClient httpClient = new DefaultHttpClient();
	    HttpConnectionParams.setSoTimeout(httpClient.getParams(), 10*1000); 
	    HttpConnectionParams.setConnectionTimeout(httpClient.getParams(),10*1000); 
	    	try{
	             response = httpClient.execute(request); 
	    	}
	    	catch(SocketException se)
	    	{
	    		Log.e("SocketException", se+"");
	    		throw se;
	    	}
	        finally{}
	
	    InputStream in = response.getEntity().getContent();
	    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
	    String line = null;
	    while((line = reader.readLine()) != null){
	        sb.append(line);
	    }
	    JSONObject jsonObj = new JSONObject(sb.toString());
	    this.listener.onRegistrationCompleted(jsonObj.get("identifier").toString(), jsonObj.get("id").toString(), jsonObj.get("nickname").toString());
	}

}
