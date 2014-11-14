package com.wackadoo.wackadoo_client.helper;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.Log;

import com.wackadoo.wackadoo_client.R;

public class SoundManager {

	private static final String TAG = SoundManager.class.getSimpleName();
	
	public static MediaPlayer backgroundMusicPlayer;
	public static boolean continueMusic;
	public static boolean soundOn;
	private static String nextSong;
	
	private static float volume = 1;	// current volume of backgroundMusicPlayer
	private static Handler fadeHandler = new Handler();
	private static Runnable fade;
	
	// set up and start backgroundMusicPlayer for background music 
	public static void setUpPlayer(Context context) {
		backgroundMusicPlayer = MediaPlayer.create(context, R.raw.themesong);
		backgroundMusicPlayer.setLooping(true);
		backgroundMusicPlayer.setVolume(volume, volume);
		backgroundMusicPlayer.start();
		soundOn = true;
	}
	
	// stop current player
	public static void stop() {
		backgroundMusicPlayer.stop();
		soundOn = false;
	}
	
	public static boolean shouldPlayerStart() {
		if (!backgroundMusicPlayer.isPlaying() && soundOn) {
			return true;
		} else {
			return false;
		}
	}
	
	public static boolean shouldPlayerStop() {
		if (!continueMusic && backgroundMusicPlayer.isPlaying()) {
			return true;
		} else {
			return false;
		}
	}
	
	// play a click sound, typically called when button in web app is clicked
	public static void playClickSound(Context context) {
		MediaPlayer clickPlayer = MediaPlayer.create(context, R.raw.click);
		clickPlayer.start();
	}

	// add song to playlist
	public static void addSongToPlaylist(String songname) {
		// save songname 
		nextSong = songname;
		fadeOut();
	}
	
	// fade in backgroundMusicPlayer again
	private static void fadeIn() {
		final Timer timer = new Timer(true);
    	TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
            	Log.i(TAG, "fadeIn: " + volume);
            	
            	// completely faded in
            	if (volume == 1.0) {
                	timer.cancel();
                	timer.purge();
                	
                } else {
                	if (volume > 0.9) {
                		// last step
                		volume = 1.0f;
                	
                	} else {
                		// fade in by 10%
                		volume += 0.1f;
                	}
                	backgroundMusicPlayer.setVolume(volume, volume);
                }
            }
        };
        timer.schedule(timerTask, 0, 500);
	}
	
	// fade out backgroundMusicPlayer and start fadeIn afterwards 
	public static void fadeOut() {
		final Timer timer = new Timer(true);
    	TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
            	Log.i(TAG, "fadeOut: " + volume);
            	
            	if (volume == 0.0) {
            		// completely faded out
                	timer.cancel();
                	timer.purge();
                	fadeIn();
                	
                } else {
                	if (volume > 0.1) {
                		// fade out by 10%
                		volume -= 0.1f;
                		
                	} else {
                		// last step
                		volume = 0.0f;
                	}
                	backgroundMusicPlayer.setVolume(volume, volume);
                }
            }
        };
        timer.schedule(timerTask, 0, 500);
	}
	
}

