package com.wackadoo.wackadoo_client.model;

import java.util.Date;

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
		return this.expireCode;
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
		return (this.token != null) && !this.isExpired();
	}
	
	public boolean isExpired() {
		return this.isExpiredAt(new Date());
	}
	
	public boolean isExpiredAt(Date date) {
		double tokenExpiration = 3600.0 * 5.0;
		Date actualTime = new Date();
		return (this.createdAt != null) && ((actualTime.getTime() - this.createdAt.getTime()) > tokenExpiration);
	}
	
	public void restoreExpireDate(Date date) {
		this.createdAt = date;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

}
