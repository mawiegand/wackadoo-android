package com.wackadoo.wackadoo_client.tasks;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.wackadoo.wackadoo_client.R;
import com.wackadoo.wackadoo_client.interfaces.CurrentGamesCallbackInterface;
import com.wackadoo.wackadoo_client.model.GameInformation;

public class GetCurrentGamesAsyncTask extends AsyncTask<String, Integer, Double> {
	
    private CurrentGamesCallbackInterface listener;
    private Context context;
    
    public GetCurrentGamesAsyncTask(CurrentGamesCallbackInterface callback, Context context) {
    	this.listener = callback;
    	this.context = context;
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

		HttpPost request = new HttpPost(context.getString(R.string.baseURL));
	    StringBuilder sb=new StringBuilder();

	    List < NameValuePair > nameValuePairs = new ArrayList < NameValuePair > (7);
		
		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(nameValuePairs);
	    entity.setContentType("application/x-www-form-urlencoded;charset=UTF-8");
	    
	    request.getParams().setParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, Boolean.FALSE);
	    request.setHeader("Accept", "application/json");
	    request.setEntity(entity);  
	    HttpResponse response = null;
	    DefaultHttpClient httpClient = new DefaultHttpClient();
	    HttpConnectionParams.setSoTimeout(httpClient.getParams(), 10*1000); 
	    HttpConnectionParams.setConnectionTimeout(httpClient.getParams(),10*1000); 
    	
	    try {
	    	response = httpClient.execute(request); 
    	
	    } catch (SocketException se) {
    		Log.e("SocketException", se+"");
    		throw se;
    	}
	
	    InputStream in = response.getEntity().getContent();
	    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
	    
	    String line = null;
	    while((line = reader.readLine()) != null) {
	        sb.append(line);
	    }
	    
 	    JSONObject jsonObj = new JSONObject(sb.toString());
    	ArrayList<GameInformation> games = new ArrayList<GameInformation>();
    	listener.getCurrentGamesCallback(games);
    }
}
