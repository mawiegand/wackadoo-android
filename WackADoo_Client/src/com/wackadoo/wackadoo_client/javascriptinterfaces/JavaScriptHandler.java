package com.wackadoo.wackadoo_client.javascriptinterfaces;

import java.util.Arrays;
import java.util.Locale;

import org.xwalk.core.JavascriptInterface;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.wackadoo.wackadoo_client.activites.MainActivity;
import com.wackadoo.wackadoo_client.activites.ShopActivity;
import com.wackadoo.wackadoo_client.activites.WebviewActivity;
import com.wackadoo.wackadoo_client.helper.SoundManager;
import com.wackadoo.wackadoo_client.model.UserCredentials;

public class JavaScriptHandler {

	private Context context;
	private String accessToken, expiration, userId, hostname, country, psioriSessionToken, psioriInstallToken;
	private String[] serverSupportedLanguageCodes = {"en","de"};
	private int width, height;
	
	public JavaScriptHandler (Context context, String accessToken, String expiration, String userId, String hostname, int width, int height, String psioriSessionToken, String psioriInstallToken) {
    	this.context = context;
		this.accessToken = accessToken;
    	this.expiration = expiration;
    	this.userId = userId;
    	this.hostname = hostname;
    	this.country = getCountry();
    	this.width = width;
    	this.height = height;
    	this.psioriSessionToken = psioriSessionToken;
    	this.psioriInstallToken = psioriInstallToken;
    }
    
	private String getCountry() {
		String countryCode = Locale.getDefault().getLanguage(); 
		if (countryCode.length() == 2 && Arrays.asList(serverSupportedLanguageCodes).contains(countryCode)) {
			if (countryCode.equals(serverSupportedLanguageCodes[1])) {
				countryCode += "_DE";
				return countryCode;
			} else if(countryCode.equals(serverSupportedLanguageCodes[0])) {
				countryCode += "_US";
				return countryCode;
			}
		}
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
	public String getPsioriSessionToken() {
		return psioriSessionToken;
	}
	
	@JavascriptInterface
	public String getPsioriInstallToken() {
		return psioriInstallToken;
	}

	@JavascriptInterface
	public void doEchoTest(String echo) {
	        Toast toast = Toast.makeText(context, echo, Toast.LENGTH_SHORT);
	        toast.show();
	}
	
	@JavascriptInterface
	public void logout() {
		new UserCredentials(context).clearAllCredentials();
		SoundManager.getInstance(context).setContinueMusic(true);
		context.startActivity(new Intent(((Activity) context), MainActivity.class));
		((Activity) context).finish();
		
	}
	
	@JavascriptInterface
	public void openShop() {
		SoundManager.getInstance(context).setContinueMusic(true);
		Intent intent = new Intent(context, ShopActivity.class);
		((Activity) context).startActivity(intent);
	}
}
