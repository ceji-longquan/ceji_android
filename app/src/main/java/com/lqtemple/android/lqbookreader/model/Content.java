package com.lqtemple.android.lqbookreader.model;

import android.util.Log;

import com.lqtemple.android.lqbookreader.Configuration;

import java.util.List;

/**
 * Created by sundxing on 16/12/25.
 * 段落，包含文本索引
 */

public class Content {
    private static final String TAG = "Content";
    JContent mJContent;
    private int mOffset;
    private int mParagraphIndex;
    private int totalOffset;

    // 添加缩进和换行
    private CharSequence mFormatText;

    private int[] imageStartOffsets;
    private int[] imageEndOffsets;

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
            Log.d(TAG, "Format string : ");
            StringBuilder sb = new StringBuilder();

            final String text = mJContent.getText();
            if (getTypeEnum() == Type.Title) {
                sb.append("\n")
                        .append(text)
                        .append("\n");
            } else {
                // Paragraph leading
                sb.append(Configuration.getInstance().getLeadingMarginText())
                        .append(text)
                        .append("\n");
            }

            // Insert image tag.
            imageStartOffsets = new int[getImageUrl().size()];
            imageEndOffsets = new int[imageStartOffsets.length];
            int index = 0;
            for (String imageUrl : getImageUrl()) {
                // TODO 添加图片说明
                String imageDesc = "图片";
                imageStartOffsets[index] = sb.length();
                Log.d(TAG, "image add :" + imageStartOffsets[index]);

                sb.append(Configuration.IMAGE_TAG);
                sb.append(imageUrl);
                sb.append(Configuration.IMAGE_TAG);
                imageEndOffsets[index] = sb.length();

                sb.append("\n").append(imageDesc).append("\n");
                index++;
            }
            mFormatText = sb.toString();
        }

        return this;
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

    public int[] getImageStartOffsets() {
        return imageStartOffsets;
    }

    public int[] getImageEndOffsets() {
        return imageEndOffsets;
    }
}
