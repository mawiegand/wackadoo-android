package com.wackadoo.wackadoo_client.tasks;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.json.JSONObject;

import com.wackadoo.wackadoo_client.interfaces.ShopOffersCallbackInterface;
import com.wackadoo.wackadoo_client.model.DeviceInformation;
import com.wackadoo.wackadoo_client.model.UserCredentials;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class GetShopOffersAsyncTask extends AsyncTask<String, Integer, Double> {
	
    private ShopOffersCallbackInterface listener;
    private Context context;
    private String offerType;
    
    public GetShopOffersAsyncTask(ShopOffersCallbackInterface callback, Context context, String offerType) {
    	this.listener = callback;
    	this.context = context;
    	this.offerType = offerType;
    }
	
	@Override
	protected Double doInBackground(String... params) {
		try {
			postDataToServer();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public  void postDataToServer() throws Throwable
    {

	    DeviceInformation deviceInformation = new DeviceInformation(context);

	    HttpPost request = new HttpPost("https://wack-a-doo.de" + offerType);
	    StringBuilder sb=new StringBuilder();
	
	/*    String username, password;
	    if(this.userCredentials.getEmail().length() > 0 && this.userCredentials.getPassword().length() > 0) {
	    	username = this.userCredentials.getEmail();
	    	password = this.userCredentials.getPassword();
	    } else {
	    	username = this.userCredentials.getIdentifier();
	    	password = "egjzdsgt";
	    }
	    */
	    List < NameValuePair > nameValuePairs = new ArrayList < NameValuePair > (7);
		/*nameValuePairs.add(new BasicNameValuePair("client_id", "WACKADOO-IOS"));
		nameValuePairs.add(new BasicNameValuePair("client_password", "5d"));
		nameValuePairs.add(new BasicNameValuePair("username", username));
		nameValuePairs.add(new BasicNameValuePair("password", password));
		nameValuePairs.add(new BasicNameValuePair("grant_type", "password"));
		nameValuePairs.add(new BasicNameValuePair("scope", ""));
		nameValuePairs.add(new BasicNameValuePair("operating_system", deviceInformation.getOs()));
		nameValuePairs.add(new BasicNameValuePair("app_token", deviceInformation.getUniqueTrackingToken()));
		nameValuePairs.add(new BasicNameValuePair("hardware_string", deviceInformation.getHardware()));
		nameValuePairs.add(new BasicNameValuePair("hardware_token", deviceInformation.getBundleBuild()));
		nameValuePairs.add(new BasicNameValuePair("version", deviceInformation.getBundleVersion())); */
		
		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(nameValuePairs);
	    entity.setContentType("application/x-www-form-urlencoded;charset=UTF-8");
	    
	    request.getParams().setParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, Boolean.FALSE);
	    request.setHeader("Accept", "application/json");
	    request.setEntity(entity);  
	    HttpResponse response =null;
	    DefaultHttpClient httpClient = new DefaultHttpClient();
	    HttpConnectionParams.setSoTimeout(httpClient.getParams(), 10*1000); 
	    HttpConnectionParams.setConnectionTimeout(httpClient.getParams(),10*1000); 
	    	try{
	             response = httpClient.execute(request); 
	    	}
	    	catch(SocketException se)
	    	{
	    		Log.e("SocketException", se+"");
	    		throw se;
	    	}
	        finally{}
	
	    InputStream in = response.getEntity().getContent();
	    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
	    String line = null;
	    while((line = reader.readLine()) != null){
	        sb.append(line);
	    }
	    System.out.println("asdf: "+sb.toString());
	    JSONObject jsonObj = new JSONObject(sb.toString());
	    List<String> offers = new ArrayList<String>();
	    this.listener.getShopOffersCallback(offers, this.offerType);
    }

}
