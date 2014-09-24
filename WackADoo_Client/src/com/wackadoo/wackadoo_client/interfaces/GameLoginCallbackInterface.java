package com.wackadoo.wackadoo_client.interfaces;

public interface GameLoginCallbackInterface {
	public void loginCallback(String accessToken, String expiration, String userIdentifier, boolean requestAccount);
	public void loginCallbackError(String error, boolean restoreAccount);
}
