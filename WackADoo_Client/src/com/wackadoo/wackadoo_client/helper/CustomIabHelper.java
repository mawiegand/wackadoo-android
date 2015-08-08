package com.wackadoo.wackadoo_client.helper;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

import com.android.vending.billing.IabHelper;
import com.android.vending.billing.IabHelper.QueryInventoryFinishedListener;
import com.android.vending.billing.IabResult;
import com.android.vending.billing.Inventory;
import com.wackadoo.wackadoo_client.activites.ShopActivity;
import com.wackadoo.wackadoo_client.interfaces.CreditsFragmentCallbackInterface;

public class CustomIabHelper extends IabHelper implements QueryInventoryFinishedListener {
	
	private static final String TAG = CustomIabHelper.class.getSimpleName();
	private Context mContext;
	private ArrayList<InAppProduct> inAppProducts = new ArrayList<InAppProduct>();
	private Inventory inventory;
	
	public CustomIabHelper(Context ctx, String base64PublicKey) {
		super(ctx, base64PublicKey);
		mContext = ctx;
	}
	
	public void startUp() {
		startSetup(new IabHelper.OnIabSetupFinishedListener() {
			public void onIabSetupFinished(IabResult result) {
				if (!result.isSuccess()) {
					if (StaticHelper.debugEnabled) {
						Log.d(TAG, "error: " + result.getMessage());
					}
					return;
				}
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
					
					int response = skuDetails.getInt("RESPONSE_CODE");

					if (response == 0) {
						
						if (StaticHelper.debugEnabled) {
							Log.d(TAG, "products skuDetails: " + skuDetails.toString());
						}
						
						ArrayList<String> stringProductList = skuDetails.getStringArrayList("DETAILS_LIST");
						saveSkuDetails(stringProductList);
						
						if (inventory != null) {
							((ShopActivity) mContext).handleUnconsumedItems(inventory);
						}
						
						callback.getProductsCallback(stringProductList);
					}
					
				} catch (RemoteException e) {
					e.printStackTrace();
				}
            }
        })).start();
    }
    
    // handle response with purchased items
    public void onQueryInventoryFinished(IabResult result, Inventory inv) {
        this.inventory = inv;
    	// get platinum credit packages from play store
		getProductsAsyncInternal((CreditsFragmentCallbackInterface) mContext);
	}
	
    // store IDs of all available items on google play store
    private ArrayList<String> setUpSkuList() {
    	ArrayList<String> skuList = new ArrayList<String>();
    	skuList.add("platinum_credits_13");			// product id of 13 credits product
    	skuList.add("platinum_credits_30");			// product id of 30 credits product
    	skuList.add("platinum_credits_100");		// product id of 100 credits product
    	skuList.add("platinum_credits_250");		// product id of 250 credits product
    	skuList.add("platinum_credits_600");		// product id of 600 credits product
    	skuList.add("platinum_credits_1600");		// product id of 1600 credits product
//    	skuList.add("platinum_credits_4000");		// product id of 4000 credits product
    	return skuList;
    }

    // tracking:save sku details for tracking issues
    public void saveSkuDetails(ArrayList<String> stringProductList) {
    	inAppProducts = new ArrayList<InAppProduct>();
    	
    	try {
    		for (String temp : stringProductList) {
    			JSONObject jsonObj;
    			jsonObj = new JSONObject(temp);
    			inAppProducts.add(new InAppProduct(jsonObj.getString("price"), jsonObj.getString("productId"), jsonObj.getString("price_currency_code")));
    		}
    	} catch (JSONException e) {
			e.printStackTrace();
		}
    }
    
    public String getPrice(String sku) {
    	for (InAppProduct temp : inAppProducts) {
    		if (temp.getProductId().equals(sku)) {
    			return temp.getPrice();
    		}
    	}
    	return "";
    }

    // returns revenue of given sku from list inAppProducts
    public InAppProduct getInAppProduct(String sku) {
    	for (InAppProduct temp : inAppProducts) {
    		if (temp.getProductId().equals(sku)) {
    			return temp;
    		}
    	}
    	return null;
    }
}
