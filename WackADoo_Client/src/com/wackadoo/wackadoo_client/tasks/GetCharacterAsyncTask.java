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
import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wackadoo.wackadoo_client.R;
import com.wackadoo.wackadoo_client.interfaces.CharacterCallbackInterface;
import com.wackadoo.wackadoo_client.model.CharacterInformation;
import com.wackadoo.wackadoo_client.model.GameInformation;
import com.wackadoo.wackadoo_client.model.UserCredentials;

//Character is null if no character is found and createNew = false or if server timed out
public class GetCharacterAsyncTask extends AsyncTask<String, Integer, Boolean> {
	
    private CharacterCallbackInterface listener;
	private String accessToken;
	private CharacterInformation character;
	private boolean createNew;
	private GameInformation game;
    
    public GetCharacterAsyncTask(CharacterCallbackInterface callback, UserCredentials userCredentials, GameInformation game, boolean createNew) {
    	this.listener = callback;
    	this.game = game;
    	this.createNew = createNew;
    	this.accessToken = userCredentials.getAccessToken().getToken();
    }
	
	@Override
	protected Boolean doInBackground(String... params) {
		
		Activity parent = (Activity) this.listener;
		String urlForRequest = String.format(parent.getString(R.string.characterURL), Locale.getDefault().getCountry().toLowerCase(Locale.getDefault()));
		if (createNew) urlForRequest += "create_if_new=true";
		String completeURL = game.getServer() + "/"+ urlForRequest;
		Log.d("Server Request", completeURL);
		HttpGet request = new HttpGet(completeURL);
		
	    StringBuilder sb=new StringBuilder();
	    HttpResponse response = null;
	    
	    
	    try {
		    request.getParams().setParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, Boolean.FALSE);
		    request.setHeader("Authorization", "Bearer " + accessToken);
		    request.setHeader("Accept", "application/json");
		    
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
		    
		    Log.d("Server Response", response.getStatusLine().toString());
		    Log.d("JSON character", sb.toString());
		  		    
		    //Get the character information object from the string with gson
	 	    Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
	 	    character = gson.fromJson(sb.toString(), CharacterInformation.class);
	 	    
	    } catch (Exception e) {
    		Log.e("SocketException", e + "");
    	}
		return null;
	}
	
	@Override
	protected void onPostExecute(Boolean result) {
		//Character is null if no character is found and createNew = false or if server timed out
		game.setCharacter(character);
		listener.getCharacterCallback(game);
	}
	
}
