package com.lqtemple.android.lqbookreader.model;

/**
 * Created by sundxing on 16/12/4.
 */

public class Spine {

    public class SpineEntry {

        private String title;
        private String href;

        private int size;

        public String getTitle() {
            return title;
        }

        public int getSize() {
            return size;
        }

        public String getHref() {
            return href;
        }
    }
}
