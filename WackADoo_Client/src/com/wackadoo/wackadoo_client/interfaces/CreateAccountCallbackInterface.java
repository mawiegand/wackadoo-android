package com.wackadoo.wackadoo_client.interfaces;

public interface CreateAccountCallbackInterface {
	public void onRegistrationCompleted(String identifier, String clientID, String nickname);
}