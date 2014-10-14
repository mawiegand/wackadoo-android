package com.wackadoo.wackadoo_client.tasks;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.json.JSONArray;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wackadoo.wackadoo_client.R;
import com.wackadoo.wackadoo_client.helper.StaticHelper;
import com.wackadoo.wackadoo_client.interfaces.CurrentGamesCallbackInterface;
import com.wackadoo.wackadoo_client.model.GameInformation;
import com.wackadoo.wackadoo_client.model.UserCredentials;

public class GetCurrentGamesAsyncTask extends AsyncTask<String, Integer, Boolean> {
	
	private static final String TAG = GetCurrentGamesAsyncTask.class.getSimpleName();
	
    private CurrentGamesCallbackInterface listener;
    private ArrayList<GameInformation> games;
	private String accessToken;
    
    public GetCurrentGamesAsyncTask(CurrentGamesCallbackInterface callback, UserCredentials userCredentials) {
    	this.listener = callback;
    	this.accessToken = userCredentials.getAccessToken().getToken();
    }
	
	@Override
	protected Boolean doInBackground(String... params) {
		Activity parent = (Activity) this.listener;
		String completeURL = StaticHelper.generateUrlForTask(parent, true, parent.getString(R.string.gamesPath));
		HttpGet request = new HttpGet(completeURL);
		
	    StringBuilder sb = new StringBuilder();
	    HttpResponse response = null;
	    
	    games = new ArrayList<GameInformation>();
	    
	    request.getParams().setParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, Boolean.FALSE);
		request.setHeader("Authorization", "Bearer " + accessToken);
		request.setHeader("Accept", "application/json");
		    
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpConnectionParams.setSoTimeout(httpClient.getParams(), 10*1000); 
		HttpConnectionParams.setConnectionTimeout(httpClient.getParams(),10*1000); 
		
	    try {
	    	response = httpClient.execute(request); 
		    InputStream in = response.getEntity().getContent();
		    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		    
		    String line = null;
		    while((line = reader.readLine()) != null) {
		        sb.append(line);
		    }
		    Log.d(TAG, "games response: " + sb.toString());
		    
		    // get the game information objects from the string with gson
	 	    Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
	 	    games = new ArrayList<GameInformation>(Arrays.asList(gson.fromJson(sb.toString(), GameInformation[].class)));
	 	    JSONArray jsonArray = new JSONArray(sb.toString());
	 	    
	 	    // get hostname from game server for requesting the character
	 	    for (int i = 0; i < jsonArray.length(); i++) {		
	 	    	games.get(i).setServer("https://"+jsonArray.getJSONObject(i).getJSONObject("random_selected_servers").getJSONObject("game").getString("hostname"));
	 	    }
			
	    } catch (Exception e) {
    		Log.e("SocketException", e + "");
    	}
		return null;
	}
	
	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);
		listener.getCurrentGamesCallback(games);
	}
}
