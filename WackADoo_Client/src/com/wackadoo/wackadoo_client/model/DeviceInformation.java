package com.wackadoo.wackadoo_client.model;

import java.math.BigInteger;
import java.security.SecureRandom;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

public class DeviceInformation {
	
	private String os, bundleVersion, bundleBuild, hardware, uniqueTrackingToken;
	private int platformId;
	private Context context;
	
	public DeviceInformation(Context context) {
		this.context = context;
		this.loadAppToken();
		this.collectDeviceInformation();
	}
	
	private void loadAppToken() {
		SharedPreferences myPrefs = context.getSharedPreferences("myPrefs", 0);
		this.uniqueTrackingToken = myPrefs.getString("uniquetrackingtoken", "");
		if (this.uniqueTrackingToken.length() <= 0)
		{
			this.createAppToken();
			this.saveAppToken();
		}
	}
	@SuppressLint("TrulyRandom")
	private void createAppToken() {
		SecureRandom random = new SecureRandom();
		this.uniqueTrackingToken = new BigInteger(80, random).toString(16);
	}
	private void saveAppToken() {
		SharedPreferences myPrefs = context.getSharedPreferences("myPrefs", 0);
		SharedPreferences.Editor e = myPrefs.edit();
		e.putString("uniquetrackingtoken", this.uniqueTrackingToken);
		e.commit();
	}
	
	private void collectDeviceInformation() { 
		this.bundleVersion = android.os.Build.VERSION.RELEASE;
		this.os = "Android" + this.bundleVersion;
		this.bundleBuild = android.os.Build.FINGERPRINT.toString();
		this.hardware = android.os.Build.MANUFACTURER + " " + android.os.Build.MODEL;
	}
	
	public String getOs() {
		return os;
	}

	public String getBundleVersion() {
		return bundleVersion;
	}

	public String getBundleBuild() {
		return bundleBuild;
	}

	public String getHardware() {
		return hardware;
	}

	public String getUniqueTrackingToken() {
		return uniqueTrackingToken;
	}

}
