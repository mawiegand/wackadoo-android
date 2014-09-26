package com.wackadoo.wackadoo_client.interfaces;

public interface GameLoginCallbackInterface {
	public void loginCallback(boolean result, String accessToken, String expiration, String userIdentifier, boolean requestAccount, boolean refresh);
	public void loginCallbackError(String error, boolean restoreAccount, boolean refresh);
}
