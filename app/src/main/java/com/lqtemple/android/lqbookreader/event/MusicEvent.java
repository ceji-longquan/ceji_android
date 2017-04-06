package com.lqtemple.android.lqbookreader.event;

import com.lqtemple.android.lqbookreader.model.MusicPlayInfo;

/**
 * Created by Administrator on 2017/4/5 0005.
 */

public class MusicEvent {
        public final MusicPlayInfo message;
//    public ArrayList<MusicMedia> musicList;//音乐信息列表
//    public int position =0;

    public MusicEvent(MusicPlayInfo message) {
//        this.musicList = message;
//        this.position =position ;
        this.message= message;
    }
}
