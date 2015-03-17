package com.wackadoo.wackadoo_client.tasks;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wackadoo.wackadoo_client.R;
import com.wackadoo.wackadoo_client.helper.StaticHelper;
import com.wackadoo.wackadoo_client.interfaces.CharacterCallbackInterface;
import com.wackadoo.wackadoo_client.model.CharacterInformation;
import com.wackadoo.wackadoo_client.model.GameInformation;
import com.wackadoo.wackadoo_client.model.UserCredentials;

//Character is null if no character is found and createNew = false or if server timed out
public class GetCharacterAsyncTask extends AsyncTask<String, Integer, Boolean> {
	
	private static final String TAG = GetCharacterAsyncTask.class.getSimpleName();
	
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
		if (game.getGameHost() == null) return false;
		Activity parent = (Activity) this.listener;
		String urlForRequest = String.format(parent.getString(R.string.characterPath), Locale.getDefault().getCountry().toLowerCase(Locale.getDefault()));
		
		if (createNew) {
			urlForRequest += "no_login=true&create_if_new=true&referer=http://play.google.com";
		} else {
			urlForRequest += "no_login=true&referer=http://play.google.com";
		}
		String completeURL = game.getGameHost() + "/" + urlForRequest;
	    StringBuilder sb = new StringBuilder();
	    
	    try {
	    	HttpResponse response = StaticHelper.executeRequest(HttpGet.METHOD_NAME, completeURL, null, accessToken);
    	
		    InputStream in = response.getEntity().getContent();
		    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		    
		    String line = null;
		    while((line = reader.readLine()) != null) {
		        sb.append(line);
		    }
		    if (StaticHelper.debugEnabled) {
		    	Log.d(TAG, "response: " + sb.toString());
		    }
		    
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
		// character is null, if no character is found and createNew = false or if server timed out
		game.setJoined(character != null);
		game.setCharacter(character);
		listener.getCharacterCallback(game, createNew);
	}
	
}
