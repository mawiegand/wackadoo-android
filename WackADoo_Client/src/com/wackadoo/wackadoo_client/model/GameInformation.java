package com.wackadoo.wackadoo_client.model;

import java.util.Date;

public class GameInformation {
	
	private int uId, presentPlayers, maxPlayers;
	private String name, scope;
	private Date startedAt, endedAt, availableSince;
	private boolean signupEnabled, signinEnabled, joined;
	
	public int getuId() {
		return uId;
	}
	public void setuId(int uId) {
		this.uId = uId;
	}
	
	public int getPresentPlayers() {
		return presentPlayers;
	}
	public void setPresentPlayers(int presentPlayers) {
		this.presentPlayers = presentPlayers;
	}
	
	public int getMaxPlayers() {
		return maxPlayers;
	}
	public void setMaxPlayers(int maxPlayers) {
		this.maxPlayers = maxPlayers;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getScope() {
		return scope;
	}
	public void setScope(String scope) {
		this.scope = scope;
	}
	
	public Date getStartedAt() {
		return startedAt;
	}
	public void setStartedAt(Date startedAt) {
		this.startedAt = startedAt;
	}
	
	public Date getEndedAt() {
		return endedAt;
	}
	public void setEndedAt(Date endedAt) {
		this.endedAt = endedAt;
	}
	
	public Date getAvailableSince() {
		return availableSince;
	}
	public void setAvailableSince(Date availableSince) {
		this.availableSince = availableSince;
	}
	
	public boolean isSignupEnabled() {
		return signupEnabled;
	}
	public void setSignupEnabled(boolean signupEnabled) {
		this.signupEnabled = signupEnabled;
	}
	
	public boolean isSigninEnabled() {
		return signinEnabled;
	}
	public void setSigninEnabled(boolean signinEnabled) {
		this.signinEnabled = signinEnabled;
	}
	
	
	public boolean isJoined() {
		return joined;
	}
	public void setJoined(boolean joined) {
		this.joined = joined;
	}
}
