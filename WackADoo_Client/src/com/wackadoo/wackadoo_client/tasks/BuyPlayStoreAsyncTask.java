package com.wackadoo.wackadoo_client.tasks;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.wackadoo.wackadoo_client.R;
import com.wackadoo.wackadoo_client.interfaces.BuyPlayStoreCallbackInterface;
import com.wackadoo.wackadoo_client.interfaces.BuyShopOfferCallbackInterface;

public class BuyPlayStoreAsyncTask extends AsyncTask<String, Integer, Boolean> {
	
	private static final String TAG = BuyPlayStoreAsyncTask.class.getSimpleName();
	
    private Context context;
    private String paymentToken, accessToken, orderId, productId;
    
    public BuyPlayStoreAsyncTask(Context context, String accessToken, String orderId, String paymentToken, String productId) {
    	this.context = context;
    	this.accessToken = accessToken;
    	this.orderId = orderId;
    	this.productId = productId;
    	this.paymentToken = paymentToken;
    }
	
	@Override
	protected Boolean doInBackground(String... params) {
		// TODO: correct url?
		String baseURL = context.getString(R.string.baseGameServerURL);
		HttpPost request = new HttpPost("https://test1.wack-a-doo.de/game_server/action/shop/google_verify_order_actions");
		Log.d(TAG, "complete URL: " + request.getURI());
		StringBuilder sb = new StringBuilder();
		
		List < NameValuePair > nameValuePairs = new ArrayList <NameValuePair>(3);
		nameValuePairs.add(new BasicNameValuePair("google_verify_order_action[order_id]", String.valueOf(orderId)));
		nameValuePairs.add(new BasicNameValuePair("google_verify_order_action[product_id]", String.valueOf(productId)));
		nameValuePairs.add(new BasicNameValuePair("google_verify_order_action[payment_token]", paymentToken));

		try {
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(nameValuePairs);
		    entity.setContentType("application/x-www-form-urlencoded;charset=UTF-8");
		    
		    request.getParams().setParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, Boolean.FALSE);
		    request.setHeader("Authorization", "Bearer " + accessToken);
		    request.setHeader("Accept", "application/json");
		    request.setEntity(entity); 
		    
		    
		    HttpResponse response = null;
		    DefaultHttpClient httpClient = new DefaultHttpClient();
		    HttpConnectionParams.setSoTimeout(httpClient.getParams(), 10*1000); 
		    HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), 10*1000); 
	    	
		    try {
		    	response = httpClient.execute(request); 
	    	
		    } catch (SocketException se) {
	    		Log.e("SocketException", se+"");
	    		throw se;
	    	}
		    
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
