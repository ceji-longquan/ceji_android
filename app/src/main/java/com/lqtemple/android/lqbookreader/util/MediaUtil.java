package com.lqtemple.android.lqbookreader.util;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import com.lqtemple.android.lqbookreader.model.MusicMedia;

import java.util.ArrayList;
import java.util.List;

public class MediaUtil {
	private List<MusicMedia> songList = new ArrayList<MusicMedia>();
	// 当前正在播放
	public static int CURRENTPOS = -1;

	public static int LAST_POS = -1;

	public static int PLAYSTATE = ConstantValue.OPTION_PAUSE;

	private static MediaUtil instance = new MediaUtil();

	private MediaUtil() {
	}

	public static MediaUtil getInstacen() {
		return instance;
	}

	public List<MusicMedia> getSongList() {
		return songList;
	}

	public void initMusics(Context context) {
		songList.clear();
		Cursor cur = context.getContentResolver().query(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
				new String[] { MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.DURATION, MediaStore.Audio.Media.ARTIST,
						MediaStore.Audio.Media._ID, MediaStore.Audio.Media.DATA }, null, null, null);
		try {
			if (cur != null) {

				while (cur.moveToNext()) {
					MusicMedia m = new MusicMedia();
					m.setTitle(cur.getString(0));
					m.setDuration(cur.getInt(1));
					m.setArtist(cur.getString(2));
					m.setId(cur.getInt(3));
					m.setUrl(cur.getString(4));
					songList.add(m);
				}
			}
		} catch (Exception e) {
		} finally {
			if (cur != null)
				cur.close();
		}

	}

	public MusicMedia getCurrent() {
		return songList.get(CURRENTPOS);
	}

}
