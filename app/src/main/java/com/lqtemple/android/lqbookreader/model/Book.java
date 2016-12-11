package com.lqtemple.android.lqbookreader.model;

import java.util.List;

/**
 * Uses in runtime, we can easy to navigate page, manage resources.
 */
public class Book {
    RawBook mRawBook;
    private String mTitle;

    public static Book from(RawBook rawBook) {
        Book book = new Book();
        book.mRawBook = rawBook;
        return book;
    }

    public List<JContent> getContent() {
        return mRawBook.getContent();
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
}
