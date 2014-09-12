package com.wackadoo.wackadoo_client.interfaces;

public interface GameLoginCallbackInterface {
	public void loginCallback(String accessToken, String expiration);
	public void loginCallbackError(String error);
}
