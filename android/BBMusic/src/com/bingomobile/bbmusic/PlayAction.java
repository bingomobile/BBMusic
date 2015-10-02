package com.bingomobile.bbmusic;

import java.io.IOException;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;

public class PlayAction {
	
	private MediaPlayer player;
	private String url;
	
	public void play(String url) {
		if (player != null) {
			player.stop();
			player.release();
			player = null;
		}
		this.url = url;
		initPlayer(url);
		player.start();
	}
	
	public int getDuration() {
		if (player != null && player.isPlaying()) {
			return player.getDuration();
		} else {
			return 0;
		}
	}
	
	public int getCurrentPosition() {
		if (player != null && player.isPlaying()) {
			return player.getCurrentPosition();
		} else {
			return 0;
		}
	}
	
	public boolean isPlaying() {
		return player != null && player.isPlaying();
	}
	
	public void SeekTo(int msec) {
		if (player != null) {
			player.seekTo(msec);
		}
	}
	
	public void pause() {
		
	}
	
	public void playPrev() {
		
	}
	
	public void playNext() {
		play(url);
	}

	public class PlayCompletionListener implements OnCompletionListener {

		@Override
		public void onCompletion(MediaPlayer mp) {
			playNext();
		}
		
	}
	private void initPlayer(String url) {
		try {
			player = new MediaPlayer();
			player.setDataSource(url);
			player.prepare();
			player.setOnCompletionListener(new PlayCompletionListener());
		} catch (IllegalArgumentException e) {
			// Log.d(TAG, "Illegal Argument Exception: " + e.getMessage());
		} catch (SecurityException e) {
			// Log.d(TAG, "Security Exception: " + e.getMessage());
		} catch (IllegalStateException e) {
			// Log.d(TAG, "Illegal State Exception: " + e.getMessage());
		} catch (IOException e) {
			// Log.d(TAG, "IO Exception: " + e.getMessage());
		}
	}
}