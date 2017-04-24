package com.lqtemple.android.lqbookreader.event;

import com.lqtemple.android.lqbookreader.model.MusicMedia;

/**
 * Created by Administrator on 2017/4/6 0006.
 */

public class AgainPlayEvent {
    public final MusicMedia message;

    public AgainPlayEvent(MusicMedia message){
        this.message= message;
    }
}
