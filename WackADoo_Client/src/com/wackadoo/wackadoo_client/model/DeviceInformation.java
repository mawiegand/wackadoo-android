package com.wackadoo.wackadoo_client.model;

import java.math.BigInteger;
import java.security.SecureRandom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class DeviceInformation {
	
	private String os, bundleVersion, bundleBuild, hardware, uniqueTrackingToken;
	private Context context;
	
	public DeviceInformation(Context context) {
		this.context = context;
		loadAppToken();
		collectDeviceInformation();
	}
	
	private void loadAppToken() {
		SharedPreferences myPrefs = context.getSharedPreferences("myPrefs", 0);
		uniqueTrackingToken = myPrefs.getString("uniquetrackingtoken", "");
		if (uniqueTrackingToken.length() <= 0) {
			createAppToken();
			saveAppToken();
		}
	}
	@SuppressLint("TrulyRandom")
	private void createAppToken() {
		SecureRandom random = new SecureRandom();
		uniqueTrackingToken = new BigInteger(80, random).toString(16);
		Log.i("CREATE TOKEN", "unique: " + uniqueTrackingToken);
	}
	private void saveAppToken() {
		SharedPreferences myPrefs = context.getSharedPreferences("myPrefs", 0);
		SharedPreferences.Editor e = myPrefs.edit();
		e.putString("uniquetrackingtoken", this.uniqueTrackingToken);
		e.commit();
	}
	
	private void collectDeviceInformation() { 
		bundleVersion = android.os.Build.VERSION.RELEASE;
		os = "Android " + this.bundleVersion;
		bundleBuild = android.os.Build.FINGERPRINT.toString();
		hardware = android.os.Build.MANUFACTURER + " " + android.os.Build.MODEL;
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
