package com.wackadoo.wackadoo_client.model;

import android.content.Context;
import android.content.SharedPreferences;

public class UserCredentials {
	
	private Context context;
	private String identifier;
	private String clientID;
	
	public UserCredentials(Context context) {
		this.context = context;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
		this.persistCredentials();
	}
	
	public void loadCredentials()
	{
		SharedPreferences myPrefs = context.getSharedPreferences("myPrefs", 0);
		this.identifier = myPrefs.getString("identifier", "");
		this.clientID = myPrefs.getString("client_id", "");
	}
	
	private void persistCredentials()
	{
		SharedPreferences myPrefs = context.getSharedPreferences("myPrefs", 0);
		SharedPreferences.Editor e = myPrefs.edit();
		e.putString("identifier", this.identifier);
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

}
