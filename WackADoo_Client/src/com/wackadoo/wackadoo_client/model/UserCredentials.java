package com.wackadoo.wackadoo_client.model;

import java.util.Date;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class UserCredentials {
	
	private static final String WAD_PREFS_NAME = "wad_preferences_credentials";
	
	private Context context;
	private int gameId;
	private boolean isFbUser;
	private String username, password, fbPlayerId, accountId, email, gameHost, htmlHost, avatarString;
	private Date premiumExpiration;
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
	// set email and generatedEmail in one step!
	public void setEmail(String email) {
		this.email = email;
		if ((email.contains("generic") && email.contains("5dlab.com")) || 
				email.equals("")) {
			generatedEmail = true;
		} else {
			generatedEmail = false;
		}
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
		SharedPreferences myPrefs = context.getSharedPreferences(WAD_PREFS_NAME, Context.MODE_PRIVATE);
		accessToken.setIdentifier(myPrefs.getString("identifier", ""));
		accessToken.setToken(myPrefs.getString("accesstoken", ""));
		accessToken.setCreatedAt(new Date(myPrefs.getLong("created_at", 13)));
		accessToken.setExpireCode(myPrefs.getString("expire_code", ""));
		accessToken.setFbToken(myPrefs.getString("fb_token", ""));
		fbPlayerId = myPrefs.getString("fb_player_id", "");
		isFbUser = myPrefs.getBoolean("is_fb_user", false);
		accountId = myPrefs.getString("account_id", "");
		username = myPrefs.getString("username", "");
		email = myPrefs.getString("email", "");
		password = myPrefs.getString("password", "");
		generatedPassword = myPrefs.getBoolean("generatedPassword", true);
		generatedEmail = myPrefs.getBoolean("generatedEmail", true);
		gameHost = myPrefs.getString("gameHost", "");
		htmlHost = myPrefs.getString("htmlHost", "");
		gameId = myPrefs.getInt("gameId", 0);
		premiumExpiration = new Date();
		premiumExpiration.setTime(myPrefs.getLong("premiumExpiration", 0));
		avatarString = myPrefs.getString("avatarString", "");
	}
	private void persistCredentials() {
		SharedPreferences myPrefs = context.getSharedPreferences(WAD_PREFS_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor e = myPrefs.edit();
		e.putString("identifier", accessToken.getIdentifier());
		e.putString("accesstoken", accessToken.getToken());
		e.putLong("created_at", accessToken.getCreatedAt().getTime());
		e.putString("expire_code", accessToken.getExpireCode());
		e.putString("fb_token", accessToken.getFbToken());
		e.putString("fb_player_id", fbPlayerId);
		e.putBoolean("is_fb_user", isFbUser);
		e.putString("account_id", accountId);
		e.putString("username", username);
		e.putString("email", email);
		e.putString("password", password);
		e.putBoolean("generatedPassword", generatedPassword);
		e.putBoolean("generatedEmail", generatedEmail);
		e.putString("gameHost", gameHost);
		e.putString("htmlHost", htmlHost);
		e.putInt("gameId", gameId);
		if (premiumExpiration != null) {
			e.putLong("premiumExpiration", premiumExpiration.getTime());
		}
		e.putString("avatarString", avatarString);
		e.commit();
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
		persistCredentials();
	}
	
	public boolean isFbUser() {
		return isFbUser;
	}
	// set fbUser and generatedEmail/generatedPassword in one step
	public void setFbUser(boolean fbUser) {
		this.isFbUser = fbUser;
		if (fbUser) {
			generatedEmail = false;
			generatedPassword = false;
		}
		persistCredentials();
	}
	
	public String getAccountId() {
		return accountId;
	}
	public void setAccountId(String accountId) {
		this.accountId = accountId;
		persistCredentials();
	}

	public Date getPremiumExpiration() {
		return premiumExpiration;
	}
	public void setPremiumExpiration(Date premiumExpiration) {
		this.premiumExpiration = premiumExpiration;
		persistCredentials();
	}

	public String getAvatarString() {
		return avatarString;
	}
	public void setAvatarString(String avatarString) {
		this.avatarString = avatarString;
		persistCredentials();
	}

	public void generateNewAccessToken(String accessToken, String expiration) {
		this.accessToken = new AccessToken();
		this.accessToken.setToken(accessToken);
		this.accessToken.setExpireCode(expiration);
		this.accessToken.setCreatedAt(new Date());
		persistCredentials();
	}
	
	public boolean isGeneratedEmail() {
		return generatedEmail;
	}
	public void setGeneratedEmail(boolean generatedEmail) {
		this.generatedEmail = generatedEmail;
		persistCredentials();
	}

	public boolean isGeneratedPassword() {
		return generatedPassword;
	}
	public void setGeneratedPassword(boolean generatedPassword) {
		this.generatedPassword = generatedPassword;
	}

	public void clearAllCredentials() {
		SharedPreferences myPrefs = context.getSharedPreferences(WAD_PREFS_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor e = myPrefs.edit();
		e.clear();
		e.commit();
	}

	
	public String getGameHost() {
		return gameHost;
	}

	public void setGameHost(String gameHost) {
		this.gameHost = gameHost;
		persistCredentials();
	}
	

	public String getHtmlHost() {
		return htmlHost;
	}

	public void setHtmlHost(String htmlHost) {
		this.htmlHost = htmlHost;
		persistCredentials();
	}
}

