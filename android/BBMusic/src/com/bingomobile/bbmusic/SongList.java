package com.bingomobile.bbmusic;

import java.util.ArrayList;
import java.util.List;

import android.os.Environment;

public class SongList {
	private List<SongInfo> songs = new ArrayList<SongInfo>();
	private int currentIndex = 0;
	
	private void InitTestData() {
		if (!songs.isEmpty())
			return;
		
		SongInfo song1 = new SongInfo();
		song1.setUrl(Environment.getExternalStorageDirectory() + "/Music/a.mp3");
		songs.add(song1);
		
		SongInfo song2 = new SongInfo();
		song2.setUrl(Environment.getExternalStorageDirectory() + "/Music/b.mp3");
		songs.add(song2);
	}
	
	public SongList() {
		InitTestData();
	}
	
	public List<SongInfo> getSongs() {
		return songs;
	}
	
	public SongInfo getCurrentSong() {
		if (currentIndex < 0 || currentIndex >= songs.size()) {
			return null;
		}
		
		return songs.get(currentIndex);
	}
	
	public SongInfo SkipToNextSong() {
		currentIndex++;
		if (currentIndex < 0 || currentIndex >= songs.size()) {
			currentIndex = 0;
		}
		
		return getCurrentSong();
	}
	
	public SongInfo SkipToPrevSong() {
		currentIndex--;
		if (currentIndex < 0 || currentIndex >= songs.size()) {
			currentIndex = songs.size() - 1;
		}
		
		return getCurrentSong();
	}
	
}
