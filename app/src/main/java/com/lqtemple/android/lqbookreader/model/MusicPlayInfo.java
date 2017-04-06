package com.lqtemple.android.lqbookreader.model;

/**
 * Created by Administrator on 2017/4/5 0005.
 */

public class MusicPlayInfo {
    public String name;
    public int currentPos;
    public int maxPos;
    public String currentPlayTime;
    public String allTime;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCurrentPos() {
        return currentPos;
    }

    public void setCurrentPos(int currentPos) {
        this.currentPos = currentPos;
    }

    public String getAllTime() {
        return allTime;
    }

    public void setAllTime(String allTime) {
        this.allTime = allTime;
    }

    public String getCurrentPlayTime() {
        return currentPlayTime;
    }

    public void setCurrentPlayTime(String currentPlayTime) {
        this.currentPlayTime = currentPlayTime;
    }

    public int getMaxPos() {
        return maxPos;
    }

    public void setMaxPos(int maxPos) {
        this.maxPos = maxPos;
    }
}
