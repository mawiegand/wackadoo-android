package JavaScriptInterfaces;

import java.util.Arrays;
import java.util.Locale;

import android.webkit.JavascriptInterface;

public class LoginJavaScriptHandler {

    	private String accessToken, expiration, userId, country;
    	private String[] serverSupportedLanguageCodes = {"en","de"};
    	
    	public LoginJavaScriptHandler (String accessToken, String expiration, String userId) {
        	this.accessToken = accessToken;
        	this.expiration = expiration;
        	this.userId = userId;
        	this.country = this.getCountry();
        }
        
    	private String getCountry() {
    		String country;
    		String temp = Locale.getDefault().getLanguage(); 
    		if(temp.length() == 2 && Arrays.asList(serverSupportedLanguageCodes).contains(temp)) {
    			country = temp + "_";
    			temp = Locale.getDefault().getCountry();
    			if(temp.length() == 2) {
    				country += temp;
    				return country;
    			}
    		}
    		//return default country code
    		return serverSupportedLanguageCodes[0] + "_US";
		}

		@JavascriptInterface
        public String getAccessToken() {
        	return this.accessToken;
        }
    	
    	@JavascriptInterface
        public String getExpiration() {
        	return this.expiration;
	    }
    	
    	@JavascriptInterface
        public String getUserId() {
	        return this.userId;
	    }
    	
    	@JavascriptInterface
        public String getCountryCode() {
	        return this.country;
	    }
}
