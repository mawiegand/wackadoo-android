package com.wackadoo.wackadoo_client.model;

import android.content.Context;

public class ClientCredentials {
	
	private String uid, password, grantType, scope;
	private DeviceInformation deviceInformation;
	
	public ClientCredentials(Context context) {
		this.uid = "WACKADOO-IOS";
		this.password = "5d";
		this.scope = "payment 5dentity wackadoo-round3";
		this.grantType = "password";
		this.deviceInformation = new DeviceInformation(context);
	}
	
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getGrantType() {
		return grantType;
	}
	public void setGrantType(String grantType) {
		this.grantType = grantType;
	}
	public String getScope() {
		return scope;
	}
	public void setScope(String scope) {
		this.scope = scope;
	}
	public DeviceInformation getDeviceInformation() {
		return deviceInformation;
	}
	public void setDeviceInformation(DeviceInformation deviceInformation) {
		this.deviceInformation = deviceInformation;
	}
	
}
