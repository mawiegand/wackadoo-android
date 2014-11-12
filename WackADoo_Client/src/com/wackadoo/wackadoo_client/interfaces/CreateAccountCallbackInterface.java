package com.wackadoo.wackadoo_client.interfaces;

public interface CreateAccountCallbackInterface {
	public void onRegistrationCompleted(boolean success, String identifier, String nickname, String accountId, String email);
}
