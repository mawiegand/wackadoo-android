package com.wackadoo.wackadoo_client.analytics;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import com.fivedlab.sample.sample_java.Sample;


public class AutoPing {

	private Timer autoPing;

	private static AutoPing instance = null;

	protected AutoPing() {
		// Exists only to defeat instantiation.
	}

	public static AutoPing getInstance() {
		if (instance == null) {
			instance = new AutoPing();
		}
		return instance;
	}

	@SuppressWarnings("unchecked")
	public void startAutoPing() {
		@SuppressWarnings("rawtypes")
		final Map trackingParameter = new HashMap();
		trackingParameter.put("session_token", sessionToken());

		TimerTask pingEvent = new TimerTask() {

			@Override
			public void run() {
				Sample.track("ping", "session", trackingParameter);
			}
		};

		autoPing = new Timer();
		autoPing.scheduleAtFixedRate(pingEvent, 0, 30000);
	}
	
	public void stopAutoPing() {
		autoPing.cancel();
		autoPing.purge();
	}
	
	public String sessionToken() 
	{
		StringBuilder builder = new StringBuilder();
		
		for (int i = 0; i < 32; i++)
		{
			if (i % 4 == 0) {
				builder.append("-");
			}
			
			String hex = Integer.toHexString((int)Math.floor(16*Math.random()));
			builder.append(hex.toUpperCase());
		}
		
		return builder.toString();
	}
}
