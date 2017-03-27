package com.lqtemple.android.lqbookreader.read;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextPaint;

import com.lqtemple.android.lqbookreader.Configuration;
import com.lqtemple.android.lqbookreader.DensityUtil;
import com.lqtemple.android.lqbookreader.MyApplication;

/**
 * Created by sun on 2017/1/1.
 */
public class PagetSizeConfig {

    private static final int TOP_TITLE_HEIGHT = DensityUtil.dip2px(MyApplication.getsContext(), 24);
    private static final int BOTTOM_DECORATION = (int) (TOP_TITLE_HEIGHT * 1.2f);
    private static int sSpacing = 0;
    private static TextPaint sPaint = new TextPaint();

    public static float getPageWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels - 2 * Configuration.getInstance().getHorizontalMargin();
    }

    public static float getPageHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels - TOP_TITLE_HEIGHT - BOTTOM_DECORATION - 2 * Configuration.getInstance().getVerticalMargin();
    }

    public static int getLineSpacing() {
        return sSpacing;
    }

    public static TextPaint getPaint() {
        return sPaint;
    }

    public static void getFakeBitmap(int[] fakeSize) {
        Bitmap b = Bitmap.createBitmap(fakeSize[0], fakeSize[1], Bitmap.Config.RGB_565);
    }
}
