package com.wackadoo.wackadoo_client.tasks;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.wackadoo.wackadoo_client.R;
import com.wackadoo.wackadoo_client.helper.StaticHelper;
import com.wackadoo.wackadoo_client.interfaces.BuyPlayStoreCallbackInterface;
import com.wackadoo.wackadoo_client.model.UserCredentials;

public class BuyPlayStoreAsyncTask extends AsyncTask<String, Integer, Boolean> {
	
	private static final String TAG = BuyPlayStoreAsyncTask.class.getSimpleName();
	
    private Context context;
    private String paymentToken, orderId, productId;
	private UserCredentials userCredentials;
    
    public BuyPlayStoreAsyncTask(Context context, UserCredentials userCredentials, String orderId, String paymentToken, String productId) {
    	this.context = context;
    	this.userCredentials = userCredentials;
    	this.orderId = orderId;
    	this.productId = productId;
    	this.paymentToken = paymentToken;
    }
	
	@Override
	protected Boolean doInBackground(String... params) {
		// TODO: correct url?
		String completeURL = StaticHelper.generateUrlForTask(context, false, context.getString(R.string.buyCreditsPath), userCredentials);
		StringBuilder sb = new StringBuilder();
		
		List < NameValuePair > nameValuePairs = new ArrayList <NameValuePair>(3);
		nameValuePairs.add(new BasicNameValuePair("google_verify_order_action[order_id]", orderId));
		nameValuePairs.add(new BasicNameValuePair("google_verify_order_action[product_id]", productId));
		nameValuePairs.add(new BasicNameValuePair("google_verify_order_action[payment_token]", paymentToken));

		try {
			HttpResponse response = StaticHelper.executeRequest(HttpPost.METHOD_NAME, completeURL, nameValuePairs, userCredentials.getAccessToken().getToken());
		    
		    String responseLine = response.getStatusLine().toString();
		    Log.d(TAG, "response line: " + responseLine);
		    
		    if(responseLine.contains("200 OK")) {
		    	return true;
		    } else if(responseLine.contains("403 Forbidden")) {
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
		((BuyPlayStoreCallbackInterface) context).buyPlayStoreCallback(result, null);
	}
}
