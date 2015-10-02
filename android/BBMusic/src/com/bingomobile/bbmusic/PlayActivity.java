package com.bingomobile.bbmusic;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

public class PlayActivity extends Activity {

	private Button playButton;
	private Button pauseButton;
	private Button playPrevButton;
	private Button playNextButton;
	private SeekBar playProgressBar;
	private PlayAction playAction = new PlayAction();

	private int UPDATE_PROGRESS_TIME = 1000; 
	Handler handler = new Handler();
	Runnable runnable = new Runnable() {
		@Override
		public void run() {
			try {
				int position = playAction.getCurrentPosition();
				int duration = playAction.getDuration();
				int progress = 0;
				if (duration > 0) {
					int maxProgress = playProgressBar.getMax();
					progress = position * maxProgress / duration;
				}
				playProgressBar.setProgress(progress);
				handler.postDelayed(this, UPDATE_PROGRESS_TIME);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_play);

		playButton = (Button) findViewById(R.id.PlayButton);
		pauseButton = (Button) findViewById(R.id.PauseButton);
		playPrevButton = (Button) findViewById(R.id.PlayPrevButton);
		playNextButton = (Button) findViewById(R.id.PlayNextButton);
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
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			if (seekBar == playProgressBar) {
				if (!playAction.isPlaying()) {
					return;
				}
				
				int duration = playAction.getDuration();
				if (duration <= 0) {
					return;
				}
				
				int progress = playProgressBar.getProgress();
				int maxProgress = playProgressBar.getMax();
				int msec = progress * duration / maxProgress;
				playAction.SeekTo(msec);
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
	}

	private void pause() {
		playButton.setVisibility(View.VISIBLE);
		pauseButton.setVisibility(View.GONE);
	}

	private void playPrev() {
		String url = Environment.getExternalStorageDirectory() + "/Music/a.mp3";
		playAction.play(url);
	}

	private void playNext() {
		String url = Environment.getExternalStorageDirectory() + "/Music/b.mp3";
		playAction.play(url);
	}
}
