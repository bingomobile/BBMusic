package com.bingomobile.bbmusic;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Environment;
import android.provider.MediaStore;

public class LocalSongList extends SongList {

	public LocalSongList() {
	}
	
	public List<SongInfo> scanMusicFileFromSdcard(Context context) {
		List<SongInfo> songs = new ArrayList<SongInfo>();
		if (!isSdcardValid())
			return songs;

		
		
		ContentResolver resolver = context.getContentResolver();
		Cursor cursor = resolver.query(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null,
				MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
		while (cursor.moveToNext()) {
			// ����ID
			int id = cursor.getInt(cursor
					.getColumnIndex(MediaStore.Audio.Media._ID));
			// ��������
			String title = cursor.getString(cursor
					.getColumnIndex(MediaStore.Audio.Media.TITLE));
			// ����ר����
			String album = cursor.getString(cursor
					.getColumnIndex(MediaStore.Audio.Media.ALBUM));
			// ����������
			String artist = cursor.getString(cursor
					.getColumnIndex(MediaStore.Audio.Media.ARTIST));
			// �����ļ�·��
			String url = cursor.getString(cursor
					.getColumnIndex(MediaStore.Audio.Media.DATA));
			// ����ʱ��
			int duration = cursor.getInt(cursor
					.getColumnIndex(MediaStore.Audio.Media.DURATION));
			// ������С
			int size = cursor.getInt(cursor
					.getColumnIndex(MediaStore.Audio.Media.SIZE));
			
			SongInfo song = new SongInfo();
			song.setUrl(url);
			song.setTitle(title);
			song.setAlbum(album);
			song.setArtist(artist);
			song.setDuration(duration);
			song.setFileSize(size);
			songs.add(song);
		}
		cursor.close();

		return songs;
	}

	private static boolean isSdcardValid() {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			return true;
		}
		return false;
	}
}
