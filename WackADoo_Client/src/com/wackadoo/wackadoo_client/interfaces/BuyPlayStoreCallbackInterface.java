package com.wackadoo.wackadoo_client.interfaces;

import com.android.vending.billing.Purchase;

public interface BuyPlayStoreCallbackInterface {
	void buyPlayStoreCallback(int responseCode, Purchase purchase, String message);
}
