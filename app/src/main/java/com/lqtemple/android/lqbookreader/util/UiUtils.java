package com.lqtemple.android.lqbookreader.util;

import android.view.View;

import jedi.option.Option;

import static jedi.option.Options.option;

/**
 * Created by alex on 10/11/14.
 */
public class UiUtils {

    public static interface Action {
        void perform();
    }

    public static <T extends View> Option<T> getView(View parent, int id, Class<T> viewType ) {
        return option( (T) parent.findViewById(id) );
    }

}
