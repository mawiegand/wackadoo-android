package com.wackadoo.wackadoo_client.interfaces;

import java.util.ArrayList;

import android.os.Bundle;

import com.android.vending.billing.SkuDetails;

public interface CreditsFragmentCallbackInterface {
	public void getProductsCallback(ArrayList<String> productList);
}
