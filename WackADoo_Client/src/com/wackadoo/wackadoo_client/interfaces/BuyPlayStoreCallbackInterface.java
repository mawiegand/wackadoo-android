package com.wackadoo.wackadoo_client.interfaces;

import com.android.vending.billing.Purchase;

public interface BuyPlayStoreCallbackInterface {
	void buyPlayStoreCallback(boolean result, Purchase purchase, String message);
}
