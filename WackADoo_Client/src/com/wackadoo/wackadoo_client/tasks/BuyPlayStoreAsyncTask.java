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

import com.android.vending.billing.Purchase;
import com.wackadoo.wackadoo_client.R;
import com.wackadoo.wackadoo_client.helper.StaticHelper;
import com.wackadoo.wackadoo_client.interfaces.BuyPlayStoreCallbackInterface;
import com.wackadoo.wackadoo_client.model.UserCredentials;

public class BuyPlayStoreAsyncTask extends AsyncTask<String, Integer, Integer> {
	
	private static final String TAG = BuyPlayStoreAsyncTask.class.getSimpleName();
	
    private Context context;
	private UserCredentials userCredentials;
	private Purchase purchase;
    
    public BuyPlayStoreAsyncTask(Context context, UserCredentials userCredentials, Purchase purchase) {
    	this.context = context;
    	this.userCredentials = userCredentials;
    	this.purchase = purchase;
    }
	
	@Override
	protected Integer doInBackground(String... params) {
		String completeURL = StaticHelper.generateUrlForTask(context, false, context.getString(R.string.buyCreditsPath), userCredentials);
		StringBuilder sb = new StringBuilder();
		
		List < NameValuePair > nameValuePairs = new ArrayList <NameValuePair>(3);
		nameValuePairs.add(new BasicNameValuePair("google_verify_order_action[order_id]", purchase.getOrderId()));
		nameValuePairs.add(new BasicNameValuePair("google_verify_order_action[product_id]", purchase.getSku()));
		nameValuePairs.add(new BasicNameValuePair("google_verify_order_action[payment_token]", purchase.getToken()));

		try {
			HttpResponse response = StaticHelper.executeRequest(HttpPost.METHOD_NAME, completeURL, nameValuePairs, userCredentials.getAccessToken().getToken());
		    
		    String responseLine = response.getStatusLine().toString();
		    
		    if (StaticHelper.debugEnabled) {
		    	Log.d(TAG, "responseline: " + responseLine);
		    }
		    
		    if(responseLine.contains("200 OK")) {
		    	return 200; 
		    	
		    // product already validated by server 
		    } else if(responseLine.contains("403 Forbidden")) { 
		    	return 403; 
		    	
		    // wrong parameters in request
		    } else if(responseLine.contains("400 Bad Request")) { 
		    	return 400; 
		    }	    	
	    	
		} catch (Throwable e) {
			e.printStackTrace();
		}
		// technical problem = 422
		return 422;			
	}
	
	@Override
	protected void onPostExecute(Integer result) {
		super.onPostExecute(result);
		((BuyPlayStoreCallbackInterface) context).buyPlayStoreCallback(result, purchase, null);
	}

}