package com.wackadoo.wackadoo_client.helper;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.Log;

import com.wackadoo.wackadoo_client.R;

public class SoundManager {

	private static final String TAG = SoundManager.class.getSimpleName();
	private static final long FADE_STEP_MS = 300;
	private static volatile SoundManager instance;
	
	private MediaPlayer backgroundMusicPlayer;
	private Context context;
	private boolean continueMusic;
	private boolean soundOn;
	private int nextSong;
	private float volume = 1;	// current volume of backgroundMusicPlayer
	
	// return the only instance of this class
	public static SoundManager getInstance(Context context) {
		if (instance == null) {
			synchronized (SoundManager.class) {
				if (instance == null) {
					instance = new SoundManager(context);
				}
			}
		}
		return instance;
	}
	
	// set up backgroundMusicPlayer if its null
	private SoundManager(Context context) {
		this.context = context;
		 
		if (backgroundMusicPlayer == null) {
			setUpPlayer();
		}
	}
	
	public boolean isContinueMusic() {
		return continueMusic;
	}
	public void setContinueMusic(boolean continueMusic) {
		this.continueMusic = continueMusic;
	}

	public boolean isSoundOn() {
		return soundOn;
	}
	public void setSoundOn(boolean soundOn) {
		this.soundOn = soundOn;
	}

	public int getNextSong() {
		return nextSong;
	}
	public void setNextSong(int nextSong) {
		this.nextSong = nextSong;
	}

	// start, pause or stop backgroundMusicPlayer
	public void start() {
		backgroundMusicPlayer.start();
	}
	public void pause() {
		backgroundMusicPlayer.pause();
	}
	public void stop() {
		backgroundMusicPlayer.stop();
	}
	
	// prepare backgroundMusicPlayer again, after fragment stopped it 
	public void prepare() {
		try {
			backgroundMusicPlayer.prepare();
		} catch (Exception e) {
			e.printStackTrace();
		} 
		backgroundMusicPlayer.start();
	}
	
	// is backgroundMusicPlayer currently playing
	public boolean isPlaying() {
		return backgroundMusicPlayer.isPlaying();
	}
	
	// set up and start backgroundMusicPlayer for background music 
	private void setUpPlayer() {
		if (nextSong != 0) {
			backgroundMusicPlayer = MediaPlayer.create(context, nextSong);
		} else {
			backgroundMusicPlayer = MediaPlayer.create(context, R.raw.themesong);
		}
		backgroundMusicPlayer.setLooping(true);
		backgroundMusicPlayer.setVolume(volume, volume);
		backgroundMusicPlayer.start();
		soundOn = true;
	}
	
	public boolean shouldPlayerStart() {
		if (!backgroundMusicPlayer.isPlaying() && soundOn) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean shouldPlayerStop() {
		if (!continueMusic && backgroundMusicPlayer.isPlaying()) {
			return true;
		} else {
			return false;
		}
	}
	
	// play a click sound, typically called when button in web app is clicked
	public void playClickSound(Context context) {
		MediaPlayer clickPlayer = MediaPlayer.create(context, R.raw.click);
		clickPlayer.start();
	}

	// add song to playlist and start fadeout of current song
	public void addSongToPlaylist(int songname) {
		// save songname 
		nextSong = songname;
		fadeOut();
	}
	
	// fade in backgroundMusicPlayer again
	private void fadeIn() {
		if (nextSong != 0) {
			setUpPlayer();
			nextSong = 0;
		}
		
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
        timer.schedule(timerTask, 0, FADE_STEP_MS);
	}
	
	// fade out backgroundMusicPlayer and start fadeIn afterwards 
	public void fadeOut() {
		final Timer timer = new Timer(true);
    	TimerTask timerTask = new TimerTask() {
            public void run() {
            	if (volume == 0.0) {
                	timer.cancel();
                	fadeIn();
                	
                } else {
                	if (volume > 0.1) {
                		volume -= 0.1f;
                	} else {
                		volume = 0.0f;
                	}
                	backgroundMusicPlayer.setVolume(volume, volume);
                }
            }
        };
        timer.schedule(timerTask, 0, 300);
	}
	
}

