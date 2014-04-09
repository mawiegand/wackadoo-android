package com.wackadoo.wackadoo_client.model;

import android.content.Context;
import android.content.SharedPreferences;

public class UserCredentials {
	
	private Context context;
	private String username, password, gcPlayerId, fbPlayerId, fbAccessToken, clientID;
	private AccessToken accessToken;
	private ClientCredentials clientCredentials;
	
	public UserCredentials(Context context) {
		this.context = context;
		this.accessToken = new AccessToken();
		this.clientCredentials = new ClientCredentials(context);
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
		this.persistCredentials();
	}

	public AccessToken getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(AccessToken accessToken) {
		this.accessToken = accessToken;
	}

	public ClientCredentials getClientCredentials() {
		return clientCredentials;
	}

	public void setClientCredentials(ClientCredentials clientCredentials) {
		this.clientCredentials = clientCredentials;
	}

	public String getIdentifier() {
		return this.accessToken.getIdentifier();
	}

	public void setIdentifier(String identifier) {
		this.accessToken.setIdentifier(identifier);
		this.persistCredentials();
	}
	
	public void loadCredentials()
	{
		SharedPreferences myPrefs = context.getSharedPreferences("myPrefs", 0);
		this.accessToken.setIdentifier(myPrefs.getString("identifier", "")) ;
		this.clientID = myPrefs.getString("client_id", "");
	}
	
	private void persistCredentials()
	{
		SharedPreferences myPrefs = context.getSharedPreferences("myPrefs", 0);
		SharedPreferences.Editor e = myPrefs.edit();
		e.putString("identifier", this.accessToken.getIdentifier());
		e.putString("client_id", this.clientID);
		e.commit();
	}

	public String getClientID() {
		return clientID;
	}

	public void setClientID(String clientID) {
		this.clientID = clientID;
		this.persistCredentials();
	}
	
	public void generateNewAccessToken(String accessToken, String expiration) {
		this.accessToken = new AccessToken();
		this.accessToken.setToken(accessToken);
		this.accessToken.setExpireCode(expiration);
	}

}

