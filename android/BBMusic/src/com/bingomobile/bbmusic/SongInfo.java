package com.bingomobile.bbmusic;

public class SongInfo {
	private String url;
	private String title;
	private String album;
	private String artist;
	private int duration;
	private int fileSize;
	private String lrcUrl;
	private String lrcPath;
		
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getAlbum() {
		return album;
	}
	
	public void setAlbum(String album) {
		this.album = album;
	}
	
	public String getArtist() {
		return artist;
	}
	
	public void setArtist(String artist) {
		this.artist = artist;
	}
	
	public int getDuration() {
		return duration;
	}
	
	public void setDuration(int duration) {
		this.duration = duration;
	}
	
	public int getFileSize() {
		return fileSize;
	}
	
	public void setFileSize(int fileSize) {
		this.fileSize = fileSize;
	}
	
	public String getlrcUrl() {
		return lrcUrl;
	}
	
	public void setLrcUrl(String lrcUrl) {
		this.lrcUrl = lrcUrl;
	}
	
	public String getLrcPath() {
		return lrcPath;
	}
	
	public void setLrcPath(String lrcPath) {
		this.lrcPath = lrcPath;
	}
	
}
