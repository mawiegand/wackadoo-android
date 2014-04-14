package com.wackadoo.wackadoo_client.model;

import java.util.Date;

import android.content.Context;
import android.content.SharedPreferences;

public class UserCredentials {
	
	private Context context;
	private String username, password, gcPlayerId, fbPlayerId, fbAccessToken, clientID, email;
	private AccessToken accessToken;
	private ClientCredentials clientCredentials;
	private boolean generatedPassword=true;
	
	public UserCredentials(Context context) {
		this.context = context;
		this.accessToken = new AccessToken();
		this.clientCredentials = new ClientCredentials(context);
		this.loadCredentials();
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
	
	private void loadCredentials()
	{
		SharedPreferences myPrefs = context.getSharedPreferences("myPrefs", 0);
		this.accessToken.setIdentifier(myPrefs.getString("identifier", "")) ;
		this.accessToken.restoreExpireDate(new Date(myPrefs.getLong("expire_date", 0)));
		this.clientID = myPrefs.getString("client_id", "");
		this.username = myPrefs.getString("username", "");
		this.email = myPrefs.getString("email", "");
		this.password = myPrefs.getString("password", "");
		this.generatedPassword = myPrefs.getBoolean("generatedPassword", true);
	}
	
	private void persistCredentials()
	{
		SharedPreferences myPrefs = context.getSharedPreferences("myPrefs", 0);
		SharedPreferences.Editor e = myPrefs.edit();
		e.putString("identifier", this.accessToken.getIdentifier());
		e.putLong("expire_date", this.accessToken.getCreatedAt().getTime());
		e.putString("client_id", this.clientID);
		e.putString("username", this.username);
		e.putString("email", this.email);
		e.putString("password", this.password);
		e.putBoolean("generatedPassword", this.generatedPassword);
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
		this.accessToken.setCreatedAt(new Date());
	}

	public void setPassword(String password) {
		this.password = password;
		this.generatedPassword = false;
		this.persistCredentials();
	}
	
	public void setEmail(String email) {
		this.email = email;
		this.persistCredentials();
	}
	
	public boolean isPasswordGenerated() {
		return this.generatedPassword;
	}

	public String getEmail() {
		return email;
	}

	public void clearAllCredentials() {
		SharedPreferences myPrefs = context.getSharedPreferences("myPrefs", 0);
		SharedPreferences.Editor e = myPrefs.edit();
		e.clear();
		e.commit();
	}

	public String getPassword() {
		return this.password;
	}

}

