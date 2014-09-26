package com.wackadoo.wackadoo_client.tasks;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.JsonObject;
import com.wackadoo.wackadoo_client.R;
import com.wackadoo.wackadoo_client.adapter.ShopRowItem;
import com.wackadoo.wackadoo_client.interfaces.ShopOffersCallbackInterface;

public class GetShopOffersAsyncTask extends AsyncTask<String, Integer, Boolean> {
	
	private static final String TAG = GetShopOffersAsyncTask.class.getSimpleName();
	
    private ShopOffersCallbackInterface listener;
    private Context context;
    private String accessToken;
    private int offerType;
    private String offerURL;
    private JSONArray jsonArray;
    
    public GetShopOffersAsyncTask(ShopOffersCallbackInterface callback, Context context, String accessToken, int offerType) {
    	this.listener = callback;
    	this.context = context;
    	this.accessToken = accessToken;
    	this.offerType = offerType;
    	
    	// get url for offerType
    	switch(offerType) {
	    	case 1:
	    		offerURL = context.getString(R.string.goldFrogsServerPath);
	    		break;
	    	case 2:
	    		offerURL = context.getString(R.string.platinumAccountServerPath);
	    		break;
	    	case 3: 
	    		offerURL = context.getString(R.string.bonusOffersServerPath);
	    		break;
    	}
    }
	
	@Override
	protected Boolean doInBackground(String... params) {
		// TODO: correct url?
		String baseURL = context.getString(R.string.baseURL);
		baseURL = "https://gs06.wack-a-doo.de";
		HttpGet request = new HttpGet(baseURL + offerURL);
		StringBuilder sb = new StringBuilder();
		
		try {
		    request.getParams().setParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, Boolean.FALSE);
		    request.setHeader("Authorization", "Bearer " + accessToken);
		    request.setHeader("Accept", "application/json");
		    
		    HttpResponse response = null;
		    DefaultHttpClient httpClient = new DefaultHttpClient();
		    HttpConnectionParams.setSoTimeout(httpClient.getParams(), 10*1000); 
		    HttpConnectionParams.setConnectionTimeout(httpClient.getParams(),10*1000); 
	    	
		    try {
	             response = httpClient.execute(request); 
	    	
		    } catch (SocketException se) {
	    		Log.e("SocketException", se+"");
	    		throw se;
	    	}
		    
		    InputStream in = response.getEntity().getContent();
		    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		    
		    String line = null;
		    while((line = reader.readLine()) != null){
		        sb.append(line);
		    }
		    
		    Log.d(TAG, "response: " + sb);
	 	    jsonArray = new JSONArray(sb.toString());
	    	return true;
	    	
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return false;
	}
	
	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);
		
//		if(progressDialog.isShowing()) {
//			progressDialog.dismiss();
//		}
		
		if(result) {
			listener.getShopOffersCallback(produceRowItemList(), offerType);
		}
	}
	
	// returns list of offers for given array of json products (sorted by price)
	private List<ShopRowItem> produceRowItemList() {
		List<ShopRowItem> rowItemList = new ArrayList<ShopRowItem>();
		
		// sort by price, cheapest item first
		/*Comparator<String> comparator = new Comparator<String>() {
			@Override
			public int compare(String itemA, String itemB) {
				try {
					// compare and sort by price in micro-format
					int priceA = Integer.valueOf(new JSONObject(itemA).getString("price_amount_micros"));
					int priceB = Integer.valueOf(new JSONObject(itemB).getString("price_amount_micros"));
				
					if(priceA > priceB) {
						return 1;
					} else if (priceA < priceB) {
						return -1;
					} 
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				return 0;
			}
		};
		Collections.sort(jsonR, comparator);*/
		
		// create list of ShopRowItem out of Strings
		try {
			switch(offerType) {
				// golden frogs
				case 1:
					for(int i=0; i<jsonArray.length(); i++) {
						JSONObject jsonObject = jsonArray.getJSONObject(i);
						int id = jsonObject.getInt("id");
						String title = context.getString(R.string.list_gold_text);
						title = String.format(title, jsonObject.getInt("amount"), jsonObject.getInt("price"));
						ShopRowItem item = new ShopRowItem(id, R.drawable.goldkroete_128px, title, 0);
						rowItemList.add(item);
					}
					break;
				
				// platinum account
				case 2: 
					for(int i=0; i<jsonArray.length(); i++) {
						JSONObject jsonObject = jsonArray.getJSONObject(i);
						int id = jsonObject.getInt("id");
						int hours = jsonObject.getInt("duration") / 24;
						int price = jsonObject.getInt("price");
						String title = context.getString(R.string.list_platinum_account_text);
						title = String.format(title, hours, price);
						ShopRowItem item = new ShopRowItem(id, 0, title, 0);
						rowItemList.add(item);
					}
					break;
					
				// bonus
				case 3: 
					for(int i=0; i<jsonArray.length(); i++) {
						JSONObject jsonObject = jsonArray.getJSONObject(i);
						int id = jsonObject.getInt("id");
						int resourceId = getResourceImage(jsonObject.getInt("resource_id"));
						int bonus = (int) ((Float.parseFloat(jsonObject.getString("bonus")))*100);
						int hours = jsonObject.getInt("duration");
						int price = jsonObject.getInt("price"); 
						int currency = getCurrencyImage(jsonObject.getInt("currency")); 
						String title = context.getString(R.string.list_bonus_text);
						title = String.format(title, bonus, hours, price);
						ShopRowItem item = new ShopRowItem(id, resourceId, title, currency);
						rowItemList.add(item);
					}
					break;
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		return rowItemList;
	}
	
	// get id for currency drawable
	private int getCurrencyImage(int id) {
		if(id == 1) {
			return R.drawable.goldkroete_128px;
		} else {
			return R.drawable.platinum_small;
		}
	}
	
	// get id for resource drawable
	private int getResourceImage(int id) {
		switch(id) {
			case 0:	return R.drawable.resource_stone;
			case 1: return R.drawable.resource_wood;
			case 2: return R.drawable.resource_fur;
			default: return 0;
		}
	}
}
