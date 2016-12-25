package com.lqtemple.android.lqbookreader.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Uses in runtime, we can easy to navigate page, manage resources.
 */
public class Book {
    RawBook mRawBook;
    private String mTitle;

    /**
     * 段落分割点，pos = 段落， value = Content pos
     */
    private List<Integer> chapterOffsets = new ArrayList<>();

    public List<List<Content>> getChapters() {
        return mChapters;
    }

    private List<List<Content>> mChapters = new ArrayList<>();

    private List<Content> mContents ;

    private Book(RawBook rawBook) {
        mRawBook = rawBook;
        mContents = new ArrayList<>(rawBook.getContent().size());
        initChapters();

    }

    public static Book from(RawBook rawBook) {
        Book book = new Book(rawBook);
        return book;
    }
    private void initChapters() {
        int offset = 0;
        int chapterOffset = 0;
        List<Content> chapter = null;
        for (JContent content : mRawBook.getContent()) {
            Content c = new Content(content);
            c.setOffset(offset);
            offset += content.getText().length();
            mContents.add(c);

            chapterOffset++;
            offset++;
            if (c.isChapterStart()) {
                chapter = new ArrayList<>();
                offset = 0;
                chapterOffsets.add(chapterOffset);
                mChapters.add(chapter);
            }

            if (chapter != null) {
                chapter.add(c);
            }
        }
    }

    public List<Content> getContents() {
        return mContents;
    }

    public List<JContent> getJContent() {
        return mRawBook.getContent();
    }

    public List<Integer> getChapterOffsets() {
        return chapterOffsets;
    }

    public String getTitle() {
        return mRawBook.getBook().getName();
    }

    public String getLanguage() {
        return mRawBook.getBook().getLanguage();
    }

    public String getAuthorName() {
        // TODO 多语言翻译的名称不同
        return "XueCheng";
    }

    public JContent getContentByIndex(String index) {
        for (JContent content : getJContent()) {
            content.getIndex().equals(index);
            return content;
        }
        return null;
    }
}
