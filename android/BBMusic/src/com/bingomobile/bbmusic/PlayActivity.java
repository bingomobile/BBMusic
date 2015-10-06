package com.bingomobile.bbmusic;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class PlayActivity extends Activity {

	public class LyricItem {
		private int startTime;
		private String lyric;
		public int getStartTime() {
			return startTime;
		}
		
		public void setStartTime(int startTime) {
			this.startTime = startTime;
		}
		
		public String getLyric() {
			return lyric;
		}
		
		public void setLyric(String lyric) {
			this.lyric = lyric;
		}
		
	}
	private Button playButton;
	private Button pauseButton;
	private Button playPrevButton;
	private Button playNextButton;
	private SeekBar playProgressBar;
	private PlayAction playAction;
	private SongList songList = new SongList();
	private boolean isProgressBarTracking = false;
	private TextView timeTextView;
	private TextView songTitleView;
	private TextView songArtistView;
	private TextView lrcTextView;
	private Intent playIntent;
	List<LyricItem> lyrics = new ArrayList<LyricItem>();

	IntentFilter intentFilter;
	
	Handler handler = new Handler();
	Runnable runnable = new Runnable() {
		@Override
		public void run() {
		}
	};
	
	private ServiceConnection connection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            playAction = ((PlayAction.MyBinder)service).getService(); 
            startService(playIntent);
        }
        public void onServiceDisconnected(ComponentName className) {
        	playAction = null;
        }
    };
    
    public void startPlayService() {
    	if (playIntent == null) {
    		playIntent = new Intent(PlayActivity.this, PlayAction.class);
            bindService(playIntent, connection, Context.BIND_AUTO_CREATE);    
    	}
    	else {
    		startService(playIntent);
    	}
        	
    }
    
    
    public void stopPlayService() {
        stopService(new Intent(getBaseContext(), PlayAction.class));
    }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_play);

		startPlayService();
		playButton = (Button) findViewById(R.id.PlayButton);
		pauseButton = (Button) findViewById(R.id.PauseButton);
		playPrevButton = (Button) findViewById(R.id.PlayPrevButton);
		playNextButton = (Button) findViewById(R.id.PlayNextButton);
		timeTextView = (TextView) findViewById(R.id.PlayTime);
		songTitleView = (TextView) findViewById(R.id.SongTitle);
		songArtistView = (TextView) findViewById(R.id.SongArtist);
		lrcTextView = (TextView) findViewById(R.id.LrcTextView);
		playProgressBar = (SeekBar) findViewById(R.id.PlayProgressBar);
		playProgressBar.setProgress(0);
		playProgressBar.setSecondaryProgress(0);
		playProgressBar.setOnSeekBarChangeListener(new PlayProgressChangeListener());
	}
	
	BroadcastReceiver intentReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (PlayAction.SONG_CHANGED_ACTION.equals(action)) {
				songTitleView.setText(intent.getStringExtra("Title"));
				songArtistView.setText(intent.getStringExtra("Artist"));
				lyrics.clear();
				lrcTextView.setText("");
			} else if (PlayAction.PROGRESS_UPDATE_ACTION.equals(action)) {
				UpdateProgressFromAction();
				UpdateLyric();
			} else if (PlayAction.LYRIC_READY_ACTION.equals(action)) {
				LoadLrcFromString(intent.getStringExtra("Lyric"));
			}
		}
		
	};
	
	void LoadLrcFromString(String lyricText) {
		lyrics.clear();
		
		if (lyricText == null)
			return;
		
		String[] lyricTexts = lyricText.split("\n");
		for (int i = 0; i < lyricTexts.length; i++) {
			LyricItem item = ParseLyricItem(lyricTexts[i]);
			if (item != null) {
				lyrics.add(item);
			}
		}
		
		UpdateLyric();
	}
	
	void LoadLrc(String lrcPath) {
		lyrics.clear();
		if (lrcPath == null)
			return;
		
		try {
			FileInputStream inputStream = openFileInput(lrcPath);
			InputStreamReader inputReader = new InputStreamReader(inputStream);
			BufferedReader bufferedReader = new BufferedReader(inputReader);
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				LyricItem item = ParseLyricItem(line);
				if (item != null) {
					lyrics.add(item);
				}
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
			lyrics.clear();
		} catch (Exception e) {
			e.printStackTrace();
			lyrics.clear();
		}
		
		UpdateLyric();
	}
	
	LyricItem ParseLyricItem(String line) {
		line.trim();
		if (!line.matches("\\[(\\d+):(\\d+).(\\d+)\\][\\s\\S]*")) {
			return null;
		}
		
		int start = 0;
		int end = 0;
		start = line.indexOf("[", 0) + 1;
		start++;
		end = line.indexOf(":", start);
		for (; start < end; start++) {
			if (line.indexOf(start) != '0') {
				break;
			}
		}
		
		String subString = "";
		int time = 0;
		if (end > start) {
			subString = line.substring(start, end);
			time += Integer.parseInt(subString) * 60 * 1000;
		}
		
		
		start = end + 1;
		end = line.indexOf(".", start);
		for (; start < end; start++) {
			if (line.indexOf(start) != '0') {
				break;
			}
		}
		
		if (end > start) {
			subString = line.substring(start, end);
			time += Integer.parseInt(subString) * 1000;
		}
		
		start = end + 1;
		end = line.indexOf("]", start);
		for (; start < end; start++) {
			if (line.indexOf(start) != '0') {
				break;
			}
		}
		
		if (end > start) {
			subString = line.substring(start, end);
			time += Integer.parseInt(subString);
		}
		
		String lyric = "";
		if (line.length() > end + 1) {
			lyric = line.substring(end + 1);
		}
		
		LyricItem item = new LyricItem();
		item.setStartTime(time);
		item.setLyric(lyric);
		
		return item;
	}
	
	void UpdateLyric() {
		if (lyrics.isEmpty())
			return;
		
		int msec = playAction.getCurrentPosition();
		int i = 0;
		for (; i < lyrics.size() - 1; i++) {
			if (msec < lyrics.get(i + 1).getStartTime()) {
				break;
			}
		}
		
		lrcTextView.setText(lyrics.get(i).getLyric());
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(getBaseContext());
		intentFilter = new IntentFilter();
        intentFilter.addAction(PlayAction.SONG_CHANGED_ACTION);
        intentFilter.addAction(PlayAction.PROGRESS_UPDATE_ACTION);
        intentFilter.addAction(PlayAction.LYRIC_READY_ACTION);
        lbm.registerReceiver(intentReceiver, intentFilter);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(getBaseContext());
		lbm.unregisterReceiver(intentReceiver);
	}
	
	private int getRealProgress() {
		int position = playAction.getCurrentPosition();
		int duration = playAction.getDuration();
		if (position <= 0 || duration <= 0)
			return 0;
		
		int maxProgress = playProgressBar.getMax();
		int progress = position * maxProgress / duration;
		return progress;
	}
	
	private int getTimeFromProgressBar() {
		int duration = playAction.getDuration();
		if (duration <= 0) {
			return 0;
		}
		
		int progress = playProgressBar.getProgress();
		int maxProgress = playProgressBar.getMax();
		if (maxProgress <= 0)
			return 0;
		int msec = progress * duration / maxProgress;
		return msec;
	}
	
	void UpdateProgressFromAction() {
		if (isProgressBarTracking)
			return;
		
		updateProgressBar();
		updateTimeViewFromPlayAction();
	}
	
	private void updateProgressBar() {	
		int progress = getRealProgress();
		playProgressBar.setProgress(progress);
	}
	
	private void updateTimeViewFromPlayAction() {
		int msec = playAction.getCurrentPosition();
		int second = msec / 1000;
		String timeText = String.format("%02d:%02d", second / 60, second % 60); 
		timeTextView.setText(timeText);
	}
	
	private void updateTimeViewFromProgressBar() {
		int msec = getTimeFromProgressBar();
		int second = msec / 1000;
		String timeText = String.format("%02d:%02d", second / 60, second % 60); 
		timeTextView.setText(timeText);
	}
	
	class PlayProgressChangeListener implements SeekBar.OnSeekBarChangeListener {

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			if (seekBar == playProgressBar && fromUser) {
				updateTimeViewFromProgressBar();
			}
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			if (seekBar == playProgressBar) {
				isProgressBarTracking = true;
			}
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			if (seekBar == playProgressBar) {
				isProgressBarTracking = false;
				if (!playAction.isPlaying() && !playAction.isPaused()) {
					return;
				}
				
				int msec = getTimeFromProgressBar();
				playAction.seekTo(msec);
			}
		}
		
	}

	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.PlayButton:
			play();
			Toast.makeText(this, ((Button)view).getText().toString(), Toast.LENGTH_SHORT).show();
			break;
		case R.id.PauseButton:
			pause();
			Toast.makeText(this, ((Button)view).getText().toString(), Toast.LENGTH_SHORT).show();
			break;
		case R.id.PlayPrevButton:
			playPrev();
			break;
		case R.id.PlayNextButton:
			playNext();
			break;
		default:
			break;
		}
	}

	private void play() {
		playButton.setVisibility(View.GONE);
		pauseButton.setVisibility(View.VISIBLE);
		playAction.play(songList);
	}

	private void pause() {
		playButton.setVisibility(View.VISIBLE);
		pauseButton.setVisibility(View.GONE);
		playAction.pause();
	}

	private void playPrev() {
		playAction.playPrev();
	}

	private void playNext() {
		playAction.playNext();
	}
}
