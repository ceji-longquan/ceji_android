package com.lqtemple.android.lqbookreader.model;

/**
 * @author Administrator
 *         Created by Administrator on 2017/3/13 0013.
 * @version $Rev$
 * @updateDes ${TODO}
 */

public class LeftMenu {
    private int imageView;
    private String text;

    public LeftMenu(int imageView, String text) {
        super();
        this.imageView = imageView;
        this.text = text;
    }

    public int getImageView() {
        return imageView;
    }

    public void setImageView(int imageView) {
        this.imageView = imageView;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

}
