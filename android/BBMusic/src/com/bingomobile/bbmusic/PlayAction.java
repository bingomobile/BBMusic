package com.bingomobile.bbmusic;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;

public class PlayAction extends Service {
	
	public static final String SONG_CHANGED_ACTION = "com.bingomobile.bbmusic.SONG_CHANGED_ACTION";
	public static final String PROGRESS_UPDATE_ACTION = "com.bingomobile.bbmusic.PROGRESS_UPDATE_ACTION";
	public static final String LYRIC_READY_ACTION = "com.bingomobile.bbmusic.LYRIC_READY_ACTION";
	
	public enum State {
	    STOPPED,
	    PLAYING,
	    PAUSED,
	    ERROR,
	}
	
	private State state = State.STOPPED;
	private MediaPlayer player;
	private SongList currentSongList;
	private SongInfo currentSongInfo;
	private Timer timer;
	private static final int UPDATE_PROGRESS_TIME = 1000;
	
	LyricHandler lyricHandler;
	
	public PlayAction() {
		initPlayer();
		lyricHandler = new LyricHandler();
	}
	
	private void initPlayer() {
		player = new MediaPlayer();
		player.setOnCompletionListener(new PlayCompletionListener());
		player.setOnErrorListener(new PlayerErrorListener());
		player.setOnBufferingUpdateListener(new PlayerBufferingUpdateListener());
		player.setOnSeekCompleteListener(new PlayerSeekCompleteListener());
		
		state = State.STOPPED;
	}
	
	private void stopPlayer() {
		player.stop();
		player.reset();
		state = State.STOPPED;
	}
	
	private void pausePlayer() {
		if (state != State.PLAYING) {
			return;
		}
		
		player.pause();
		state = State.PAUSED;
		
		stopProgressTimer();
	}
	
	private void resumePlayer() {
		if (state != State.PAUSED) {
			return;
		}
		
		player.start();
		state = State.PLAYING;
		
		startProgressTimer();
	}
	
	private void playNewSong(SongInfo songInfo) {
		if (state != State.STOPPED) {
			stopPlayer();
		}
		
		if (songInfo == null)
			return;
		
		String url  = songInfo.getUrl();
		if (url == null)
			return;
		
		this.currentSongInfo = songInfo;
		
		try {
			player.setDataSource(url);
			player.prepare();
			player.start();
			state = State.PLAYING;
		} catch (IllegalStateException e) {
			state = State.ERROR;
		} catch (IOException e) {
			state = State.ERROR;
		} catch (IllegalArgumentException e) {
			state = State.ERROR;
		} catch (SecurityException e) {
			state = State.ERROR;
		}	
		
		startProgressTimer();
		
		Intent intent = new Intent();
    	intent.setAction(SONG_CHANGED_ACTION);
    	intent.putExtra("Title", currentSongInfo.getTitle());
    	intent.putExtra("Artist", currentSongInfo.getArtist());
    	intent.putExtra("LrcPath", currentSongInfo.getLrcPath());
    	LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(getBaseContext());
    	lbm.sendBroadcast(intent);
    	
    	Thread t = new Thread(new LyricThread(currentSongInfo.getlrcUrl()));
    	t.start();
	}
	
	class LyricThread implements Runnable {

		private String url;
		public LyricThread(String url) {
			this.url = url;
		}
		
		@Override
		public void run() {
			if (url == null && url.length() <= 0)
				return;
			
			HttpGet getMethod = new HttpGet(url);
			HttpClient httpClient = new DefaultHttpClient();
			
			try {  
			    HttpResponse response = httpClient.execute(getMethod);
			    if (response.getStatusLine().getStatusCode() == 200) {
			    	String result = EntityUtils.toString(response.getEntity(), "utf-8");
			    	Message msg = lyricHandler.obtainMessage();
			    	Bundle data = new Bundle();
			        data.putString("Lyric", result);
			        msg.setData(data);
			    	lyricHandler.sendMessage(msg);
			    }
			} catch (Exception e) {  
			    e.printStackTrace();  
			}  
		}
		
	}
	
	class LyricHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			String lyric = msg.getData().getString("Lyric");		
			Intent intent = new Intent();
	    	intent.setAction(LYRIC_READY_ACTION);
	    	intent.putExtra("Lyric", lyric);
	    	LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(getBaseContext());
	    	lbm.sendBroadcast(intent);
	    }
	}
	
	public void play(SongList songList) {
		if (songList == null)
			return;
		
		SongInfo songInfo = songList.getCurrentSong();
		if (songInfo == null)
			return;
		
		switch (state) {
			case STOPPED:
				this.currentSongList = songList;
				playNewSong(songInfo);
				break;
			case PLAYING:
				if (this.currentSongList == songList && this.currentSongInfo == songInfo) {
					return;
				} else {
					this.currentSongList = songList;
					playNewSong(songInfo);
				}
				break;
			case PAUSED:
				if (this.currentSongList == songList && this.currentSongInfo == songInfo) {
					resumePlayer();
				} else {
					this.currentSongList = songList;
					playNewSong(songInfo);
				}
				break;
			case ERROR:
				break;
			default:
				break;
		}
	}
	
	public int getDuration() {
		if (state == State.PLAYING || state == State.PAUSED) {
			return player.getDuration();
		} else {
			return 0;
		}
	}
	
	public int getCurrentPosition() {
		if (state == State.PLAYING || state == State.PAUSED) {
			return player.getCurrentPosition();
		} else {
			return 0;
		}
	}
	
	public boolean isPlaying() {
		return state == State.PLAYING;
	}
	
	public boolean isPaused() {
		return state == State.PAUSED;
	}
	
	public void seekTo(int msec) {
		if (state == State.PLAYING || state == State.PAUSED) {
			player.seekTo(msec);
		}
	}
	
	public void pause() {
		pausePlayer();
	}
	
	public void playPrev() {
		if (this.currentSongList == null)
			return;
		
		stopPlayer();
		SongInfo songInfo = this.currentSongList.SkipToPrevSong();
		if (songInfo != null) {
			playNewSong(songInfo);
		}
	}
	
	public void playNext() {
		if (this.currentSongList == null)
			return;
		
		stopPlayer();
		SongInfo songInfo = this.currentSongList.SkipToNextSong();
		if (songInfo != null) {
			playNewSong(songInfo);
		}
	}

	public class PlayCompletionListener implements OnCompletionListener {

		@Override
		public void onCompletion(MediaPlayer mp) {
			playNext();
		}
		
	}
	
	public class PlayerErrorListener implements OnErrorListener {

		@Override
		public boolean onError(MediaPlayer mp, int what, int extra) {
			return false;
		}
		
	}
	
	public class PlayerBufferingUpdateListener implements OnBufferingUpdateListener {

		@Override
		public void onBufferingUpdate(MediaPlayer mp, int percent) {
		}
		
	}
	
	public class PlayerSeekCompleteListener implements OnSeekCompleteListener {

		@Override
		public void onSeekComplete(MediaPlayer mp) {
		}
		
	}

	private final IBinder binder = new MyBinder();

    public class MyBinder extends Binder {
        PlayAction getService() {
            return PlayAction.this;
        }
    }
    
	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}
	
	@Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		stopProgressTimer();
	}
	
	private void startProgressTimer() {
		if (timer != null)
			return;
		timer = new Timer();
        timer.scheduleAtFixedRate( new TimerTask() {
            public void run() {
            	Intent intent = new Intent();
            	intent.setAction(PROGRESS_UPDATE_ACTION);
            	LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(getBaseContext());
            	lbm.sendBroadcast(intent);
            }
        }, UPDATE_PROGRESS_TIME, UPDATE_PROGRESS_TIME);
    }
	
	private void stopProgressTimer() {
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
	}
}