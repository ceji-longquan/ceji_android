package com.lqtemple.android.lqbookreader.model;

/**
 * Created by sundxing on 16/12/4.
 */

public class PageIndex {
    private String mIndex; // paragraph
    private int offset; // the char offset of paragraph

    private int totalOffset;

    public int getSpanedTextOffset() {
        return spanedTextOffset;
    }

    public void setSpanedTextOffset(int spanedTextOffset) {
        this.spanedTextOffset = spanedTextOffset;
    }

    private int spanedTextOffset; // the char offset of paragraph

    public String getIndex() {
        return mIndex;
    }

    public void setIndex(String index) {
        this.mIndex = index;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getTotalOffset() {
        return totalOffset;
    }

    public void setTotalOffset(int totalOffset) {
        this.totalOffset = totalOffset;
    }
}
