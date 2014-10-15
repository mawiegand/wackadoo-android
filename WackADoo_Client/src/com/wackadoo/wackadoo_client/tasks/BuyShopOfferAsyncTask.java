package com.wackadoo.wackadoo_client.tasks;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.wackadoo.wackadoo_client.R;
import com.wackadoo.wackadoo_client.helper.StaticHelper;
import com.wackadoo.wackadoo_client.interfaces.BuyShopOfferCallbackInterface;

public class BuyShopOfferAsyncTask extends AsyncTask<String, Integer, Boolean> {
	
	private static final String TAG = BuyShopOfferAsyncTask.class.getSimpleName();
	
    private Context context;
    private int offerId;
    private String offerType, shopCharacterId, accessToken;
    
    public BuyShopOfferAsyncTask(Context context, String accessToken, int offerId, String shopCharacterId, String offerType) {
    	this.context = context;
    	this.accessToken = accessToken;
    	this.offerId = offerId;
    	this.offerType = offerType;
    	this.shopCharacterId = shopCharacterId;
    }
	
	@Override
	protected Boolean doInBackground(String... params) {
		// TODO: correct url?
		String completeURL = StaticHelper.generateUrlForTask(context, false, context.getString(R.string.buyShopItemPath));
		
		List <NameValuePair> nameValuePairs = new ArrayList <NameValuePair>(3);
		nameValuePairs.add(new BasicNameValuePair("shop_transaction[offer_id]", String.valueOf(offerId)));
		nameValuePairs.add(new BasicNameValuePair("shop_transaction[offer_type]", offerType));
		nameValuePairs.add(new BasicNameValuePair("shop_transaction[customer_identifier]", shopCharacterId));

		try {
			HttpResponse response = StaticHelper.executeRequest(HttpPost.METHOD_NAME, completeURL, nameValuePairs, accessToken);
		    
		    String responseLine = response.getStatusLine().toString();
		    Log.d(TAG, "response line: " + responseLine);
		    
		    if (responseLine.contains("20")) {
		    	return true;
		    } else if (responseLine.contains("403 Forbidden")) {
		    	// not enough credits
		    	return false;
		    }
	    	
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return false;
	}
	
	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);
		((BuyShopOfferCallbackInterface) context).buyShopOfferCallback(result, null);
	}
}
