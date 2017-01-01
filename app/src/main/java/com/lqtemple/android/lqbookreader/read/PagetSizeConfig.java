package com.lqtemple.android.lqbookreader.read;

import android.content.Context;

/**
 * Created by sun on 2017/1/1.
 */
public class PagetSizeConfig {

    public static float getPageWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    public static float getPageHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }
}
