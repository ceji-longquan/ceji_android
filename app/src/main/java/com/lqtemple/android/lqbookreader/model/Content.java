package com.lqtemple.android.lqbookreader.model;

import android.text.Spanned;

import com.lqtemple.android.lqbookreader.Configuration;
import com.lqtemple.android.lqbookreader.read.ClickableImageSpan;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sundxing on 16/12/25.
 * 段落，包含文本索引
 */

public class Content {
    JContent mJContent;
    private int mOffset;
    private int mParagraphIndex;
    private int totalOffset;

    // 添加缩进和换行
    private String mFormatText;

    public List<Integer> getmUrlOffset() {
        return mUrlOffset;
    }

    private List<Integer> mUrlOffset;

    public Content(JContent JContent) {
        mJContent = JContent;
        mParagraphIndex = extractParagraphIndex(mJContent.getIndex());
    }

    private int extractParagraphIndex(String index) {
        return Integer.valueOf(index.substring(0, index.indexOf("-")));
    }

    public int getOffset() {
        return mOffset;
    }

    public void setOffset(int offset) {
        mOffset = offset;
    }

    public List<String> getImageUrl() {
        return mJContent.getImageUrl();
    }

    public int getAudioFrame() {
        return mJContent.getAudioTimeFrame();
    }

    public int getParagraphIndex() {
        return mParagraphIndex;
    }

    @Override
    public String toString() {
        return "Index" + mJContent.getIndex() + ", offset:" + mOffset;
    }

    public Type getTypeEnum() {
        return mJContent.getTypeEnum();
    }

    public String getIndex() {
        return mJContent.getIndex();
    }

    public CharSequence getText() {
        format();
        return mFormatText;
    }

    public Content format() {

        if (mFormatText == null) {
            StringBuilder sb = new StringBuilder(mFormatText);

            String text = mJContent.getText();
            if (getTypeEnum() == Type.Title) {
                text = "\n".concat(text).concat("\n");
            } else {
                // Paragraph leading
                text = Configuration.getInstance().getLeadingMarginText().concat(text);
            }
            text = text.concat("\n");
            mFormatText = text;
            mUrlOffset = new ArrayList<>(getImageUrl().size() + 1);
            for (String imageUrl : getImageUrl()) {
                // TODO 添加图片说明
                mUrlOffset.add(sb.length());
                String imageDesc = "图片";
                insertImage(sb, imageUrl, imageDesc);
            }
            mUrlOffset.add(sb.length());
        }

        return this;
    }

    private void insertImage(StringBuilder sb, String uri, String desc) {
        sb.append(Configuration.IMAGE_TAG);
        sb.append(uri);
        sb.append(Configuration.IMAGE_TAG);
        sb.append("\n").append(desc).append("\n");
    }

    public boolean isChapterStart() {
        if (mJContent.getType() != Type.Title.ordinal()) return false;
        String[] indexStr = getIndex().split("-");
        return indexStr.length == 2 && indexStr[1].equals("0");
    }

    public int getTotalOffset() {
        return totalOffset;
    }

    public void setTotalOffset(int totalOffset) {
        this.totalOffset = totalOffset;
    }

    public boolean isBodyType() {
        return getTypeEnum()== Type.Content;
    }

    public int getImageTagLength(String uri) {
        return (uri != null ? uri.length() : 0 ) + Configuration.IMAGE_TAG.length() * 2;
    }
}
