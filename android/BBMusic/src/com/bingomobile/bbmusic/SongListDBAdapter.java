package com.bingomobile.bbmusic;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SongListDBAdapter {
	
	static final String KEY_SONG_ID = "_id";
	static final String KEY_TITLE = "title";
	static final String KEY_URL = "url";
	static final String KEY_ALBUM = "album";
	static final String KEY_ARTIST = "artist";
	static final String KEY_DURATION = "duration";
	static final String KEY_SIZE = "size";
	

	static final String DATABASE_NAME = "SongList";
	static final String LOCAL_SONG_LIST_TABLE = "local_song_list";
	static final int DATABASE_VERSION = 1;

	static final String SONG_LIST_DETAIL = "(_id integer primary key autoincrement,"
			+ "title text not null,"
			+ "url text not null,"
			+ "album text not null,"
			+ "artist text not null,"
			+ "duration integer not null,"
			+ "size integer not null)";
	
	static final String LOCAL_SONG_LIST_CREATE = "create table " 
			+ LOCAL_SONG_LIST_TABLE + " " + SONG_LIST_DETAIL + ";";
	
	static final String LOCAL_SONG_LIST_DROP = "DROP TABLE IF EXISTS " + LOCAL_SONG_LIST_TABLE;

	final Context context;

	DatabaseHelper DBHelper;
	SQLiteDatabase db;

	public SongListDBAdapter(Context ctx) {
		this.context = ctx;
		DBHelper = new DatabaseHelper(context);
	}

	private static class DatabaseHelper extends SQLiteOpenHelper {
		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			try {
				db.execSQL(LOCAL_SONG_LIST_CREATE);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL(LOCAL_SONG_LIST_DROP);
			onCreate(db);
		}
	}

	// ---opens the database---
	public SongListDBAdapter open() throws SQLException {
		db = DBHelper.getWritableDatabase();
		return this;
	}

	// ---closes the database---
	public void close() {
		DBHelper.close();
	}

	// ---insert a contact into the database---
	public long insertSong(SongInfo song) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_TITLE, song.getTitle());
		initialValues.put(KEY_URL, song.getUrl());
		initialValues.put(KEY_ALBUM, song.getAlbum());
		initialValues.put(KEY_ARTIST, song.getArtist());
		initialValues.put(KEY_DURATION, song.getDuration());
		initialValues.put(KEY_SIZE, song.getFileSize());
		return db.insert(LOCAL_SONG_LIST_TABLE, null, initialValues);
	}

	/*
	// ---deletes a particular contact---
	public boolean deleteSong(long rowId) {
		return db.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
	}
	
	*/

	// ---retrieves all the contacts---
	public Cursor getAllSongs() {
		return db.query(LOCAL_SONG_LIST_TABLE, new String[] { KEY_SONG_ID, KEY_TITLE,
				KEY_URL, KEY_ALBUM, KEY_ARTIST, KEY_DURATION, KEY_SIZE}, null, null, null, null, null);
	}

	/*
	// ---retrieves a particular contact---
	public Cursor getContact(long rowId) throws SQLException {
		Cursor mCursor = db.query(true, DATABASE_TABLE, new String[] {
				KEY_ROWID, KEY_NAME, KEY_EMAIL }, KEY_ROWID + "=" + rowId,
				null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	// ---updates a contact---
	public boolean updateContact(long rowId, String name, String email) {
		ContentValues args = new ContentValues();
		args.put(KEY_NAME, name);
		args.put(KEY_EMAIL, email);
		return db.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
	}
	*/
}
