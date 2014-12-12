package com.wackadoo.wackadoo_client.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;

public class AdjustProperties {
	private static final String ADJUST_PROPERTIES_FILENAME = "adjust_properties";

	public static final String REACHED_RANG_TWO = "reached_rang_two";
	public static final String FIVE_MINUTES_INGAME = "five_minutes_ingame";
	public static final String LOGIN_COUNT = "login_count";
	public static final String TUTORIAL_COMPLETED = "tutorial_completed";
	public static final String PLAYTIME = "playtime";
	
	public static final long FIVE_MINUTES = 300000;

	private Context context;

	private Map<String, Map<String, String>> properties;

	private static AdjustProperties instance;

	private ObjectOutputStream outputStream;

	public static AdjustProperties getInstance(Context context) {
		if (instance == null) {
			instance = new AdjustProperties(context);
		}
		return instance;
	}

	private AdjustProperties(Context context) {
		this.context = context;
		load();
	}

	public void load() {
		File file = getStoredFile();

		if (file.length() == 0) {
			setProperties(new HashMap<String, Map<String, String>>());
			return;
		}

		try {
			ObjectInputStream  inputStream = new ObjectInputStream(
					new FileInputStream(file));
			Map<String, Map<String, String>> map = (Map<String, Map<String, String>>) inputStream.readObject();
			setProperties(map);
		} catch (StreamCorruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void persist() {
		File file = getStoredFile();
		try {
			outputStream = new ObjectOutputStream(new FileOutputStream(file));
			outputStream.writeObject(getProperties());
			outputStream.flush();
			outputStream.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private File getStoredFile() {
		return new File(getDir(), ADJUST_PROPERTIES_FILENAME);
	}

	public File getDir() {
		return context.getDir("adjust", Context.MODE_PRIVATE);
	}

	public boolean didUserReachedRangTwo(String userId) {
		return getPropertiesForUser(userId).containsKey(REACHED_RANG_TWO);
	}

	public boolean IsUserFiveMinutesIngame(String userId) {
		return getPropertiesForUser(userId).containsKey(FIVE_MINUTES_INGAME);
	}

	public boolean DidUserCompleteTutorial(String userId) {
		return getPropertiesForUser(userId).containsKey(TUTORIAL_COMPLETED);
	}
	
	public long getPlayTimeForUser(String userId) {
		String playtime = getPropertiesForUser(userId).get(PLAYTIME);
		if (playtime == null) {
			return 0;
		}
		return Long.valueOf(playtime);
	}

	public int loginsForUser(String userId) {
		String logins = getPropertiesForUser(userId).get(LOGIN_COUNT);
		if (logins == null) {
			return 0;
		}
		return Integer.valueOf(logins);
	}

	public void setUserReachedRangTwo(String userId) {
		setPropertyForUser(REACHED_RANG_TWO, "t", userId);
	}

	public void setPlayTimeForUser(String userId, long playtime) {
		setPropertyForUser(PLAYTIME, String.valueOf(playtime), userId);
	}
	
	public void setUserPlayedForFiveMinutes(String userId) {
		setPropertyForUser(FIVE_MINUTES_INGAME, "t", userId);
	}

	public void setUserDidCompleteTutorial(String userId) {
		setPropertyForUser(TUTORIAL_COMPLETED, "t", userId);
	}

	public void incrementLoginCountForUser(String userId) {
		int logins = loginsForUser(userId) + 1;
		setPropertyForUser(LOGIN_COUNT, String.valueOf(logins), userId);
	}

	public void setPropertyForUser(String property, String value, String user) {
		Map<String, String> map = getProperties().get(user);
		if (map == null) {
			map = new HashMap<String, String>();
		}

		map.put(property, value);

		Map<String, Map<String, String>> root = getProperties();
		root.put(user, map);
		setProperties(root);

		persist();
	}

	public Map<String, String> getPropertiesForUser(String user) {
		Map<String, String> map = getProperties().get(user);
		if (map == null) {
			return new HashMap<String, String>();
		}
		return map;
	}

	private Map<String, Map<String, String>> getProperties() {
		return properties;
	}

	private void setProperties(Map<String, Map<String, String>> properties) {
		this.properties = properties;
	}
}
