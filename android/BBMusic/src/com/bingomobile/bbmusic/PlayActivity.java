package com.bingomobile.bbmusic;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class PlayActivity extends Activity {

	private Button playButton;
	private Button pauseButton;
	private Button playPrevButton;
	private Button playNextButton;
	private SeekBar playProgressBar;
	private PlayAction playAction = new PlayAction();
	private SongList songList = new SongList();
	private boolean isProgressBarTracking = false;
	private TextView timeTextView;
	private TextView songTitleView;
	private TextView songArtistView;

	private int UPDATE_PROGRESS_TIME = 1000; 
	Handler handler = new Handler();
	Runnable runnable = new Runnable() {
		@Override
		public void run() {
			
			if (!isProgressBarTracking) {
				updateProgressBar();
				updateTimeViewFromPlayAction();
			}
			handler.postDelayed(this, UPDATE_PROGRESS_TIME);
		}
	};
	
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_play);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		playButton = (Button) findViewById(R.id.PlayButton);
		pauseButton = (Button) findViewById(R.id.PauseButton);
		playPrevButton = (Button) findViewById(R.id.PlayPrevButton);
		playNextButton = (Button) findViewById(R.id.PlayNextButton);
		timeTextView = (TextView) findViewById(R.id.PlayTime);
		songTitleView = (TextView) findViewById(R.id.SongTitle);
		songArtistView = (TextView) findViewById(R.id.SongArtist);
		playProgressBar = (SeekBar) findViewById(R.id.PlayProgressBar);
		playProgressBar.setProgress(0);
		playProgressBar.setSecondaryProgress(0);
		playProgressBar.setOnSeekBarChangeListener(new PlayProgressChangeListener());
		 
		handler.postDelayed(runnable, UPDATE_PROGRESS_TIME);
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
