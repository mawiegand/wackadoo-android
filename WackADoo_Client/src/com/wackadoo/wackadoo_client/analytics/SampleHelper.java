package com.wackadoo.wackadoo_client.analytics;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.content.SharedPreferences;

import com.fivedlab.sample.sample_java.Sample;

/**
 * Use this Helper class to track PSIORI events
 * @author danielband
 *
 */
public class SampleHelper {

	private static final String WAD_PREFS_ANALYITCS = "wad_preferences_analytics";
	
	private Timer autoPing;

	private static SampleHelper instance = null;
	
	private String sessionToken;
	private String installToken;
	private String appToken;
	private boolean debug;
	private boolean serverSide;
	
	private String platform = "android";
	private String client;
	private String clientVersion;
	private String module;
	
	private String userId;
	private String facebookId;
	
	public static Context context;
	
	protected SampleHelper() {
		// Exists only to defeat instantiation.
	}

	public static SampleHelper getInstance() {
		if (instance == null) {
			instance = new SampleHelper();
			SharedPreferences myPrefs = context.getSharedPreferences(WAD_PREFS_ANALYITCS, Context.MODE_PRIVATE);
			String installToken = myPrefs.getString("install_token", null);
			
			if (installToken == null) {
				SharedPreferences.Editor e = myPrefs.edit();
				installToken = instance.randomToken(24);
				e.putString("install_token", installToken);
				e.commit();
			}

			instance.setInstallToken(installToken);
			instance.setSessionToken(instance.randomToken(32));
		}
		return instance;
	}

	public void startAutoPing() {

		TimerTask pingEvent = new TimerTask() {

			@Override
			public void run() {
				track("ping", "session", null);
			}
		};

		autoPing = new Timer();
		autoPing.scheduleAtFixedRate(pingEvent, 0, 30000);
	}
	
	public void stopAutoPing() {
		autoPing.cancel();
		autoPing.purge();
	}
	
	public String randomToken(int length) 
	{
		StringBuilder builder = new StringBuilder();
		
		for (int i = 0; i < length; i++)
		{
			if (i > 0 && i % 4 == 0) {
				builder.append("-");
			}
			
			String hex = Integer.toHexString((int)Math.floor(16*Math.random()));
			builder.append(hex.toUpperCase(Locale.US));
		}
		
		return builder.toString();
	}
	
	@SuppressWarnings("rawtypes")
	public void track(String event_name, String event_category, Map args) {
		Map params = mergeParams(event_name, event_category, args);
		Sample.track(event_name, event_category, params);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Map mergeParams(String event_name, String event_category, Map args) {
		
		Map ret = new HashMap();
		
		addKey("install_token", getInstallToken(), ret);
		addKey("session_token", getSessionToken(), ret);
		addKey("app_token", getAppToken(), ret);
		addKey("user_id", getUserId(), ret);
		addKey("module", getModule(), ret);
		ret.put("server_side", isServerSide());
		ret.put("debug", isDebug());
		
		String platform;
		String client;
		String clientVers;
		if (args != null) {
			platform = args.containsKey("platform") ? (String)args.get("platform") : getPlatform();
			client = args.containsKey("client") ? (String)args.get("client") : getClient();
			clientVers = args.containsKey("client_version") ? (String)args.get("client_version") : getClientVersion();
		}
		else {
			platform = getPlatform();
			client = getClient();
			clientVers = getClientVersion();
		}

		addKey("platform", platform, ret);
		addKey("platform", client, ret);
		addKey("client_version", clientVers, ret);
		
		boolean isSessionEvent = event_name.equals("session_start") || event_name.equals("session_update");
		if (isSessionEvent || event_category.equals("account")) {
			
			boolean hasfbIdInArgs = (args != null && args.containsKey("facebook_id")) == true;
			String fbId = hasfbIdInArgs ? (String)args.get("facebook_id") : getFacebookId();
			addKey("facebook_id", fbId, ret);
		}
		
		
		if (args == null) {
			return ret;
		}

		// Starting this line, all keys are saved in the args 
		boolean isPurchase = "purchase".equals(event_name)
				|| "chargeback".equals(event_name);
		if (isPurchase == true) {
			
			String purProvider = (String) args.get("pur_provider");
			String purGross = (String) args.get("pur_gross");
			String purCurrency = (String) args.get("pur_currency");
			String purCountry = (String) args.get("pur_country");
			String pur_earnings = (String) args.get("pur_earnings");
			String purProductSku = (String) args.get("pur_product_sku");
			String purProductCat = (String) args.get("pur_product_category");
			String purReceiptIdentifier = (String) args
					.get("pur_receipt_identifier");

			addKey("pur_provider", purProvider, ret);
			addKey("pur_gross", purGross, ret);
			addKey("pur_currency", purCurrency, ret);
			addKey("pur_country", purCountry, ret);
			addKey("pur_earnings", pur_earnings, ret);
			addKey("pur_product_sku", purProductSku, ret);
			addKey("pur_product_category", purProductCat, ret);
			addKey("pur_receipt_identifier", purReceiptIdentifier, ret);
		}

		return ret;
	}
	
	private void addKey(String key, String value, Map<String, String> params) {
		if (key == null || value == null || params == null) {
			return;
		}
		
		params.put(key, value);
	}

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public String getFacebookId() {
		return facebookId;
	}

	public void setFacebookId(String facebookId) {
		this.facebookId = facebookId;
	}

	public String getSessionToken() {
		return sessionToken;
	}

	public void setSessionToken(String sessionToken) {
		this.sessionToken = sessionToken;
	}

	public String getInstallToken() {
		return installToken;
	}

	public void setInstallToken(String installToken) {
		this.installToken = installToken;
	}

	public String getAppToken() {
		return appToken;
	}

	public void setAppToken(String appToken) {
		this.appToken = appToken;
	}

	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public String getClient() {
		return client;
	}

	public void setClient(String client) {
		this.client = client;
	}

	public String getClientVersion() {
		return clientVersion;
	}

	public void setClientVersion(String clientVersion) {
		this.clientVersion = clientVersion;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public boolean isServerSide() {
		return serverSide;
	}

	public void setServerSide(boolean setServerSide) {
		this.serverSide = setServerSide;
	}
}
