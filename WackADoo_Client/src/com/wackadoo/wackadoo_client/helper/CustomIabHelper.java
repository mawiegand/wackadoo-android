package com.wackadoo.wackadoo_client.helper;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.util.Log;

import com.android.vending.billing.IabHelper;
import com.android.vending.billing.IabHelper.OnConsumeMultiFinishedListener;
import com.android.vending.billing.IabHelper.OnIabPurchaseFinishedListener;
import com.android.vending.billing.IabHelper.QueryInventoryFinishedListener;
import com.android.vending.billing.IabResult;
import com.android.vending.billing.Inventory;
import com.android.vending.billing.Purchase;
import com.wackadoo.wackadoo_client.activites.ShopActivity;
import com.wackadoo.wackadoo_client.interfaces.CreditsFragmentCallbackInterface;

public class CustomIabHelper extends IabHelper implements QueryInventoryFinishedListener {
	
	private static final String TAG = CustomIabHelper.class.getSimpleName();
	private Context mContext;
	
	public CustomIabHelper(Context ctx, String base64PublicKey) {
		super(ctx, base64PublicKey);
		mContext = ctx;
	}
	
	public void startUp() {
		startSetup(new IabHelper.OnIabSetupFinishedListener() {
			public void onIabSetupFinished(IabResult result) {
				if (!result.isSuccess()) {
					Log.d(TAG, "Error: " + result.getMessage());
					return;
				}
				// IAB is fully set up 
				Log.d(TAG, "Setup successful");
				
				// error handling: get purchased items, if consumption was not successful when shop was used the last time
				queryInventoryAsync(false, null, CustomIabHelper.this);
			}
		});
	}
	
    // get platinum credit products from play store
    public void getProductsAsyncInternal(final CreditsFragmentCallbackInterface callback) {
        checkNotDisposed();
        checkSetupDone("getProducts");
        flagStartAsync("getProducts");
        (new Thread(new Runnable() {
            public void run() {
        		Bundle querySkus = new Bundle();
        		querySkus.putStringArrayList("ITEM_ID_LIST", setUpSkuList());
        		try {
					Bundle skuDetails = mService.getSkuDetails(3, mContext.getPackageName(), "inapp", querySkus);
					flagEndAsync();
					Log.d(TAG, "---> vor callback!");
					callback.getProductsCallback(skuDetails);
					
				} catch (RemoteException e) {
					e.printStackTrace();
				}
            }
        })).start();
    }
    
    // handles response with purchased items
    public void onQueryInventoryFinished(IabResult result, Inventory inv) {
        ((ShopActivity) mContext).handleUnconsumedItems(inv);

        // get platinum credit packages from play store
		getProductsAsyncInternal((CreditsFragmentCallbackInterface) mContext);
	}
	
    // stores IDs of all available items on google play store
    private ArrayList<String> setUpSkuList() {
    	ArrayList<String> skuList = new ArrayList<String>();
    	skuList.add("platinum_credits_13");			// product id of 13 credits product
    	skuList.add("platinum_credits_30");			// product id of 30 credits product
    	skuList.add("platinum_credits_100");		// product id of 100 credits product
    	skuList.add("platinum_credits_250");		// product id of 250 credits product
    	skuList.add("platinum_credits_600");		// product id of 600 credits product
    	skuList.add("platinum_credits_1600");		// product id of 1600 credits product
    	skuList.add("platinum_credits_4000");		// product id of 4000 credits product
    	return skuList;
    }

}