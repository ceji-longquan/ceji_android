package com.lqtemple.android.lqbookreader.read;

import android.content.Context;

import com.lqtemple.android.lqbookreader.DensityUtil;
import com.lqtemple.android.lqbookreader.MyApplication;

/**
 * Created by sun on 2017/1/1.
 */
public class PagetSizeConfig {

    private static final int TOP_TITLE_HEIGHT = DensityUtil.dip2px(MyApplication.getsContext(), 24);
    private static final int PAGE_PADDING = DensityUtil.dip2px(MyApplication.getsContext(), 4);
    private static final int BOTTOM_DECORATION = (int) (TOP_TITLE_HEIGHT * 1.2f);

    public static float getPageWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels - 2 * PAGE_PADDING;
    }

    public static float getPageHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels - TOP_TITLE_HEIGHT - BOTTOM_DECORATION - 2 * PAGE_PADDING;
    }
}
