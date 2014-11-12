package com.wackadoo.wackadoo_client.helper;

import com.wackadoo.wackadoo_client.R;

import android.content.Context;
import android.media.MediaPlayer;

public class SoundManager {

	// static variables for background music
	public static MediaPlayer backgroundMusicPlayer;
	public static boolean continueMusic;
	public static boolean soundOn;
	
	// set up backgroundMusicPlayer for background music 
	public static void setUpPlayer(Context context) {
		backgroundMusicPlayer = MediaPlayer.create(context, R.raw.themesong);
		backgroundMusicPlayer.setLooping(true);
		backgroundMusicPlayer.setVolume(100, 100);
	}
	
	// static method to play a click sound, typically called when button is clicked
	public static void playClickSound(Context context) {
		MediaPlayer clickPlayer = MediaPlayer.create(context, R.raw.click);
		clickPlayer.start();
	}
}
