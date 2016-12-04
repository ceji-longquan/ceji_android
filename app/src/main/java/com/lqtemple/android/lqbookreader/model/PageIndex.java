package com.lqtemple.android.lqbookreader.model;

/**
 * Created by sundxing on 16/12/4.
 */

public class PageIndex {
    private int para; // paragraph
    private int index; // the last char index of paragraph

    public int getPara() {
        return para;
    }

    public void setPara(int para) {
        this.para = para;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
