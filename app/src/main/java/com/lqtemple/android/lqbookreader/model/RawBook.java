package com.lqtemple.android.lqbookreader.model;

import java.util.List;

/**
 * Created by sundxing on 16/12/4.
 */

public class RawBook {
    private JBook book;
    private List<JContent> content;

    public JBook getBook() {
        return book;
    }

    public void setBook(JBook book) {
        this.book = book;
    }

    public List<JContent> getContent() {
        return content;
    }

    public void setContent(List<JContent> content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "[book:" + book +"\n"
                + "listCount:"+ (content == null ? "-1" : content.size()) +"]";
    }
}
