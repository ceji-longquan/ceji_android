package com.lqtemple.android.lqbookreader.read;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.style.ImageSpan;

/**
 * Created by sun on 2017/3/26.
 */

public class ClickableImageSpan extends ImageSpan{

    private final Context mContext;
    private final String mImageUri;
    /**
     * In warm up, we use fake image for page number calculation.
     * If we draw text in real canvas, we should disable this flag.
     */
    private boolean mInWarmUpMode;


    public ClickableImageSpan(Context context, String imageUri, Bitmap bitmapForMeasure) {
        super(context, bitmapForMeasure);
        if (bitmapForMeasure != null) {
            mInWarmUpMode = true;
        }
        mContext = context;
        mImageUri = imageUri;
    }


    @Override
    public Drawable getDrawable() {
        if (mInWarmUpMode) {
            return super.getDrawable();
        } else {
            // TODO return our real drawable
            return null;
        }
    }


    public boolean ismInWarmUpMode() {
        return mInWarmUpMode;
    }

    /**
     * Set false when finish warm up.
     * @param mInWarmUpMode
     */
    public void setmInWarmUpMode(boolean mInWarmUpMode) {
        this.mInWarmUpMode = mInWarmUpMode;
    }
}
