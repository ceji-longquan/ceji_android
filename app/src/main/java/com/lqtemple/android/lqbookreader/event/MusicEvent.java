package com.lqtemple.android.lqbookreader.event;

import com.lqtemple.android.lqbookreader.model.MusicMedia;

/**
 * Created by Administrator on 2017/4/5 0005.
 */

public class MusicEvent {
        public final MusicMedia message;

    public MusicEvent(MusicMedia message) {
        this.message= message;
    }
}
