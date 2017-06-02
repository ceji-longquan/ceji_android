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

public class NoteModel {

    private String minTitle;
    private String minTitleContent;
    private String bookeContent;
    private String noteContent;
    private String noteTime;


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

    public String getBookeContent() {
        return bookeContent;
    }

    public void setBookeContent(String bookeContent) {
        this.bookeContent = bookeContent;
    }

    public String getNoteContent() {
        return noteContent;
    }

    public void setNoteContent(String noteContent) {
        this.noteContent = noteContent;
    }

    public String getNoteTime() {
        return noteTime;
    }

    public void setNoteTime(String noteTime) {
        this.noteTime = noteTime;
    }
}
