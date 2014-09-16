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
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.wackadoo.wackadoo_client.R;
import com.wackadoo.wackadoo_client.interfaces.CurrentGamesCallbackInterface;
import com.wackadoo.wackadoo_client.model.GameInformation;
import com.wackadoo.wackadoo_client.model.UserCredentials;

public class GetCurrentGamesAsyncTask extends AsyncTask<String, Integer, Boolean> {
	
    private CurrentGamesCallbackInterface listener;
    private Context context;
    private ArrayList<GameInformation> games;
	private String accessToken;
    
    public GetCurrentGamesAsyncTask(CurrentGamesCallbackInterface callback, Context context, UserCredentials userCredentials) {
    	this.listener = callback;
    	this.context = context;
    	this.accessToken = userCredentials.getAccessToken().getToken();
    }
	
	@Override
	protected Boolean doInBackground(String... params) {
		
		Activity parent = (Activity) this.listener;
		String urlForRequest = parent.getString(R.string.gamesURL);
		String baseURL = parent.getString(R.string.baseURL);
		String completeURL = baseURL + String.format(urlForRequest, Locale.getDefault().getCountry().toLowerCase());
		HttpGet request = new HttpGet(completeURL);
		
	    StringBuilder sb=new StringBuilder();
	    HttpResponse response = null;
	    
	    games = new ArrayList<GameInformation>();
	    
	    List < NameValuePair > nameValuePairs = new ArrayList < NameValuePair > (2);
	    
	    try {
		    request.getParams().setParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, Boolean.FALSE);
		    request.setHeader("Authorization", "Bearer " + accessToken);
		    request.setHeader("Accept", "application/json");
			
		    UrlEncodedFormEntity entity = new UrlEncodedFormEntity(nameValuePairs);
			entity.setContentType("application/x-www-form-urlencoded;charset=UTF-8");
		    
		    DefaultHttpClient httpClient = new DefaultHttpClient();
		    HttpConnectionParams.setSoTimeout(httpClient.getParams(), 10*1000); 
		    HttpConnectionParams.setConnectionTimeout(httpClient.getParams(),10*1000); 
	    	response = httpClient.execute(request); 
    	
		    InputStream in = response.getEntity().getContent();
		    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		    
		    String line = null;
		    while((line = reader.readLine()) != null) {
		        sb.append(line);
		    }
		    
	 	    JSONArray jsonArray = new JSONArray(sb.toString());
	 	    Gson gson = new Gson();
			GameInformation gameInformation = gson.fromJson(jsonArray.getJSONObject(0).toString(), GameInformation.class);
	 	    games.add(gameInformation);
			
	    } catch (Exception e) {
    		Log.e("SocketException", e + "");
    	}
		return null;
	}
	
	@Override
	protected void onPostExecute(Boolean result) {
		listener.getCurrentGamesCallback(games);
	}
	
}
