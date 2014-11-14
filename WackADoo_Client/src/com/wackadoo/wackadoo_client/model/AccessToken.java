package com.wackadoo.wackadoo_client.model;

import java.util.Date;

import android.util.Log;

public class AccessToken {
	
	private String token, fbToken, identifier, expireCode;
	private Date createdAt;
	
	public AccessToken() {
	
	}
	public AccessToken(String token, String fbToken, String identifier) {
		this.token = token;
		this.fbToken = fbToken;
		this.identifier = identifier;
	}
	
	public String getExpireCode() {
		return expireCode;
	}
	public void setExpireCode(String expireCode) {
		this.expireCode = expireCode;
	}

	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}

	public String getFbToken() {
		return fbToken;
	}
	public void setFbToken(String fbToken) {
		this.fbToken = fbToken;
	}

	public String getIdentifier() {
		return identifier;
	}
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	
	public boolean isValid() {
		return (token != null) && !isExpired();
	}
	public boolean isExpired() {
		return isExpiredAt();
	}
	
	public boolean isExpiredAt() {
		long tokenExpiration = 1000 * 60 * 60 * 5; // 1000 milliseconds * 60 seconds * 60 = minutes * 5 hours
		Date actualTime = new Date();
		return (createdAt != null) && ((actualTime.getTime() - createdAt.getTime()) > tokenExpiration);
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
	public Date getCreatedAt() {
		return createdAt;
	}

}
