package com.wackadoo.wackadoo_client.javascriptinterfaces;

import java.util.Arrays;
import java.util.Locale;

import android.content.Context;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

public class LoginJavaScriptHandler {

	private Context context;
	private String accessToken, expiration, userId, hostname, country;
	private String[] serverSupportedLanguageCodes = {"en","de"};
	private int width, height;
	
	public LoginJavaScriptHandler (Context context, String accessToken, String expiration, String userId, String hostname, int width, int height) {
    	this.context = context;
		this.accessToken = accessToken;
    	this.expiration = expiration;
    	this.userId = userId;
    	this.hostname = hostname;
    	this.country = getCountry();
    	this.width = width;
    	this.height = height;
    }
    
	private String getCountry() {
		String country;
		String temp = Locale.getDefault().getLanguage(); 
		if (temp.length() == 2 && Arrays.asList(serverSupportedLanguageCodes).contains(temp)) {
			country = temp + "_";
			temp = Locale.getDefault().getCountry();
			if (temp.length() == 2) {
				country += temp;
				return country;
			}
		}
		//return default country code
		return serverSupportedLanguageCodes[0] + "_US";
	}
	
	@JavascriptInterface
	public String getAccessToken() {
		return accessToken;
	}
	
	@JavascriptInterface
	public String getExpiration() {
		return expiration;
	}
	
	@JavascriptInterface
	public String getUserId() {
	    return userId;
	}

	@JavascriptInterface
	public String getHostname() {
		return hostname;
	}
	
	@JavascriptInterface
	public String getCountryCode() {
	    return country;
	}
	
	@JavascriptInterface
	public int getWidth() {
		return width;
	}


	@JavascriptInterface

	public int getHeight() {
		return height;
	}



	@JavascriptInterface
	public void doEchoTest(String echo){
	        Toast toast = Toast.makeText(context, echo, Toast.LENGTH_SHORT);
	        toast.show();
	    }

}
