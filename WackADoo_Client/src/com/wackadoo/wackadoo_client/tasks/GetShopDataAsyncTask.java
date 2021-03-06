package com.wackadoo.wackadoo_client.tasks;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wackadoo.wackadoo_client.R;
import com.wackadoo.wackadoo_client.helper.StaticHelper;
import com.wackadoo.wackadoo_client.interfaces.ShopDataCallbackInterface;
import com.wackadoo.wackadoo_client.model.ShopRowItem;
import com.wackadoo.wackadoo_client.model.UserCredentials;

public class GetShopDataAsyncTask extends AsyncTask<String, Integer, Boolean> {
	
	private static final String TAG = GetShopDataAsyncTask.class.getSimpleName();
	
    private Context context;
    private UserCredentials userCredentials;
    private int offerType, data;
    private String offerURL, shopCharacterId;
    private List<ShopRowItem> rowItemList;
    
    public GetShopDataAsyncTask(Context context, UserCredentials userCredentials, int offerType, String shopCharacterId) {
    	this.context = context;
    	this.userCredentials = userCredentials;
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
	    	case 4: 		
	    		offerURL = context.getString(R.string.platinumCreditsServerPath);
	    		break;
	    	case 5: 	 	
	    		offerURL = context.getString(R.string.characterShopInfoPath) + userCredentials.getIdentifier();		
	    		break;
	    	case 6: 	 	
	    		offerURL = String.format(context.getString(R.string.resourceShopInfoPath), shopCharacterId);		
	    		break;
    	}
    }
	
	@Override
	protected Boolean doInBackground(String... params) {
		String completeURL = StaticHelper.generateUrlForTask(context, false, offerURL, userCredentials);
		StringBuilder sb = new StringBuilder();
		
		try {
			HttpResponse response = StaticHelper.executeRequest(HttpGet.METHOD_NAME, completeURL, null, userCredentials.getAccessToken().getToken());
		    
		    InputStream in = response.getEntity().getContent();
		    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		    
		    String line = null;
		    while((line = reader.readLine()) != null){
		        sb.append(line);
		    }
		    if (StaticHelper.debugEnabled) {
		    	Log.d(TAG, "response: " + sb);
		    }
		    
		    // product asynctask
 			if(offerType < 5) {
 				rowItemList = produceRowItemList(new JSONArray(sb.toString()));

 			// character data asynctask
 			} else if (offerType == 5) {
 				JSONObject jsonResponse = new JSONObject(sb.toString());
 				data = jsonResponse.getInt("credit_amount");
 				shopCharacterId = jsonResponse.getString("character_id");
 			} else {
 				JSONObject jsonResponse = new JSONObject(sb.toString());
 				data = jsonResponse.getInt("resource_cash_amount");
 			}
	    	return true;
	    	
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return false;
	}
	
	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);
		((ShopDataCallbackInterface) context).getShopDataCallback(result, rowItemList, data, shopCharacterId, offerType);
	}
	
	// returns list of offers for given array of json products (sorted by price)
	private List<ShopRowItem> produceRowItemList(JSONArray jsonArray) {
		rowItemList = new ArrayList<ShopRowItem>();
		
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
						ShopRowItem item = new ShopRowItem(id, R.drawable.resource_toad_big, title, 0, 0, jsonObject.getInt("price"), null);
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
						ShopRowItem item = new ShopRowItem(id, 0, title, 0, 0, price, userCredentials.getPremiumExpiration());
						rowItemList.add(item);
					}
					break;
					
				// bonus
				case 3: 
					Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
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
			
						Date expiresIn = jsonObject.isNull("resource_effect") ? null : gson.fromJson(JSONObject.quote(jsonObject.getJSONObject("resource_effect").getString("finished_at")), Date.class);
						ShopRowItem item = new ShopRowItem(id, resourceId, title, currency, bonus, price, expiresIn);
						rowItemList.add(item);
					}
					break;
					
				// special
				case 4: 
					for(int i=0; i<jsonArray.length(); i++) {
						JSONObject jsonObject = jsonArray.getJSONObject(i);
						int id = jsonObject.getInt("id");
						int price = jsonObject.getInt("price"); 
						String title = jsonObject.getString("title"); 
						ShopRowItem item = new ShopRowItem(id, 0, title, 0, 0, price, null);
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
		if (id == 1) {
			return R.drawable.resource_toad_big;
		} else {
			return R.drawable.resource_platinum_big;
		}
	}
	
	// get id for resource drawable
	private int getResourceImage(int id) {
		switch(id) {
			case 0:	return R.drawable.resource_stone;
			case 1: return R.drawable.resource_wood;
			case 2: return R.drawable.resource_fur;
			case 3: return R.drawable.resource_toad_small;
			default: return 0;
		}
	}
}
