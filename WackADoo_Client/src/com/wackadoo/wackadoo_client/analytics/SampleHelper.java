package com.wackadoo.wackadoo_client.analytics;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.content.BroadcastReceiver;
import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import com.fivedlab.sample.sample_java.Sample;

/**
 * Use this Helper class to track PSIORI events.
 * Use the methods track(String,String) or track(String,String,Map) to send events.
 * If you want to top the tracking call stopTracking but don´t forget to call resumeTracking we you want to restart it.
 * 
 * Each event will be send asynchronously to the server so it won´t block your MainThread.
 * 
 * @author danielband
 *
 */
public class SampleHelper extends BroadcastReceiver implements ComponentCallbacks2 {

	private static final String WAD_PREFS_ANALYTICS = "wad_preferences_analytics";
	
	private boolean trackingStoped = true;
	
	private Timer autoPing;
	private LinkedList<Event> queue;
	private Timer connector;
	
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
	
	protected SampleHelper() {
		queue = new LinkedList<Event>();
	}

	public static SampleHelper getInstance(Context context) {
		if (instance == null) {
			instance = new SampleHelper();
			SharedPreferences myPrefs = context.getSharedPreferences(WAD_PREFS_ANALYTICS, Context.MODE_PRIVATE);
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
	
	public void startTracking() {
		if (isTrackingStoped() == false) {
			return;
		}
		
		TimerTask connectorTask = new TimerTask() {

			@Override
			public void run() {
				sendNext();
			}
		};
		
		setTrackingStoped(false);
		connector = new Timer();
		connector.scheduleAtFixedRate(connectorTask, 0, 1000);
	}
	
	public void stopTracking() {
		stopAutoPing();
		connector.cancel();
		connector.purge();
		setTrackingStoped(true);
	}
	
	private void sendNext() {
		if (queue.isEmpty()) {
			return;
		}
		
		synchronized(queue) {
			Event nextEvent = queue.removeFirst();
			Sample.track(nextEvent.getEventName(), nextEvent.getEventCategory(), nextEvent.getBody());
		}
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
	public synchronized void track(String eventName, String eventCategory) {
		Map params = mergeParams(eventName, eventCategory, null);
		queue.add(new Event(eventName, eventCategory, params));
	}
	
	@SuppressWarnings("rawtypes")
	public synchronized void track(String eventName, String eventCategory, Map args) {
		Map params = mergeParams(eventName, eventCategory, args);
		queue.add(new Event(eventName, eventCategory, params));
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
			
			boolean hasfbIdInArgs = ( args != null && args.containsKey("facebook_id") );
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
	
	public boolean isTrackingStoped() {
		return trackingStoped;
	}

	private void setTrackingStoped(boolean trackingStoped) {
		this.trackingStoped = trackingStoped;
	}



	/**
	 * Container class for each event. Better than putting each event in a map.
	 * @author danielband
	 *
	 */
	private class Event {
		private String eventName;
		private String eventCategory;
		@SuppressWarnings("rawtypes")
		private Map body;
		
		public Event(String eventName, String eventCategory, Map body) {
			this.eventName = eventName;
			this.eventCategory = eventCategory;
			this.body = body;
		}

		public String getEventName() {
			return eventName;
		}

		public void setEventName(String eventName) {
			this.eventName = eventName;
		}

		public String getEventCategory() {
			return eventCategory;
		}

		public void setEventCategory(String eventCategory) {
			this.eventCategory = eventCategory;
		}

		public Map getBody() {
			return body;
		}

		public void setBody(Map body) {
			this.body = body;
		}
	}

	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLowMemory() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTrimMemory(int level) {
		if (level == ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN) {
			stopTracking();
		}
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		if(intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
			stopTracking();
		} 
	}
}
