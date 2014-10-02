package com.wackadoo.wackadoo_client.model;

import java.util.Date;

import android.content.Context;
import android.content.SharedPreferences;

public class UserCredentials {
	
	private Context context;
	private int gameId;
	private String username, password, gcPlayerId, fbPlayerId, fbAccessToken, characterId, email, hostname;
	private AccessToken accessToken;
	private ClientCredentials clientCredentials;
	private boolean generatedEmail = true;
	private boolean generatedPassword = true;
	
	public UserCredentials(Context context) {
		this.context = context;
		accessToken = new AccessToken();
		clientCredentials = new ClientCredentials(context);
		loadCredentials();
	}

	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
		persistCredentials();
	}

	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
		generatedEmail = false;
		persistCredentials();
	}
	
	public String getPassword() {
		return this.password;
	}
	public void setPassword(String password) {
		this.password = password;
		generatedPassword = false;
		persistCredentials();
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
		return accessToken.getIdentifier();
	}
	public void setIdentifier(String identifier) {
		accessToken.setIdentifier(identifier);
		persistCredentials();
	}
	
	private void loadCredentials() {
		SharedPreferences myPrefs = context.getSharedPreferences("wad_prefs", 0);
		accessToken.setIdentifier(myPrefs.getString("identifier", ""));
		accessToken.setExpireCode(myPrefs.getString("expire_code", ""));
		accessToken.setToken(myPrefs.getString("accesstoken", ""));
		accessToken.restoreExpireDate(new Date(myPrefs.getLong("expire_date", 0)));
		characterId = myPrefs.getString("character_id", "");
		username = myPrefs.getString("username", "");
		email = myPrefs.getString("email", "");
		password = myPrefs.getString("password", "");
		generatedPassword = myPrefs.getBoolean("generatedPassword", true);
		hostname = myPrefs.getString("hostname", "");
		gameId = myPrefs.getInt("gameID", 0);
	}
	private void persistCredentials() {
		SharedPreferences myPrefs = context.getSharedPreferences("wad_prefs", 0);
		SharedPreferences.Editor e = myPrefs.edit();
		e.putString("identifier", accessToken.getIdentifier());
		e.putLong("expire_date", accessToken.getCreatedAt().getTime());
		e.putString("character_id", characterId);
		e.putString("username", username);
		e.putString("email", email);
		e.putString("password", password);
		e.putBoolean("generatedPassword", generatedPassword);
		e.putString("accesstoken", accessToken.getToken());
		e.putString("expire_code", accessToken.getExpireCode());
		e.putString("hostname", hostname);
		e.putInt("gameId", gameId);
		e.commit();
	}

	public String getCharacterId() {
		return characterId;
	}
	public void setCharacterId(String characterId) {
		this.characterId = characterId;
		persistCredentials();
	}
	
	public String getHostname() {
		return hostname;
	}
	public void setHostname(String hostname) {
		this.hostname = hostname;
		persistCredentials();
	}

	public int getGameId() {
		return gameId;
	}
	public void setGameId(int gameId) {
		this.gameId = gameId;
		persistCredentials();
	}

	public String getFbPlayerId() {
		return fbPlayerId;
	}
	public void setFbPlayerId(String fbPlayerId) {
		this.fbPlayerId = fbPlayerId;
	}
	
	public String getFbAccessToken() {
		return fbAccessToken;
	}
	public void setFbAccessToken(String fbAccessToken) {
		this.fbAccessToken = fbAccessToken;
	}

	public void generateNewAccessToken(String accessToken, String expiration) {
		this.accessToken = new AccessToken();
		this.accessToken.setToken(accessToken);
		this.accessToken.setExpireCode(expiration);
		this.accessToken.setCreatedAt(new Date());
		persistCredentials();
	}
	
	public boolean isEmailGenerated() {
		return generatedEmail;
	}
	public boolean isPasswordGenerated() {
		return generatedPassword;
	}

	public void clearAllCredentials() {
		SharedPreferences myPrefs = context.getSharedPreferences("wad_prefs", 0);
		SharedPreferences.Editor e = myPrefs.edit();
		e.clear();
		e.commit();
	}
}

