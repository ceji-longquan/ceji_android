package com.lqtemple.android.lqbookreader.model;

/**
 * Created by ls on 2017/6/2.
 *
 * @version $Rev$
 * @des ${TODO}
 * @updateAuthor $Author$
 * @updateDate $Date$
 * @updateDes ${TODO}
 */

public class BookMarkModel {

    private String  mark;
    private String markTime;
    private String minTitle;
    private String minTitleContent;
    private String bookMarkeContent;
    private String page;


    public String getMinTitle() {
        return minTitle;
    }

    public void setMinTitle(String minTitle) {
        this.minTitle = minTitle;
    }

    public String getMinTitleContent() {
        return minTitleContent;
    }

    public void setMinTitleContent(String minTitleContent) {
        this.minTitleContent = minTitleContent;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public String getMarkTime() {
        return markTime;
    }

    public void setMarkTime(String markTime) {
        this.markTime = markTime;
    }

    public String getBookMarkeContent() {
        return bookMarkeContent;
    }

    public void setBookMarkeContent(String bookMarkeContent) {
        this.bookMarkeContent = bookMarkeContent;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }
}
