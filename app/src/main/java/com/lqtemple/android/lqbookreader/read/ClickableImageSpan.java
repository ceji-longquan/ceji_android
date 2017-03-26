package com.lqtemple.android.lqbookreader.read;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
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
    private Drawable mFakeDrawable;
    private int[] mFakeSize;

    public ClickableImageSpan(Context context, String imageUri, int[] fakeSize) {
        super((Drawable) null);
        mFakeSize = fakeSize;
        if (fakeSize != null) {
            mInWarmUpMode = true;
        }
        mContext = context;
        mImageUri = imageUri;
    }


    @Override
    public Drawable getDrawable() {
        if (mInWarmUpMode) {
            return mFakeDrawable;
        } else {
            return super.getDrawable();
        }
    }

    private Drawable getFakeDrawable() {
        if (mFakeDrawable == null) {
            Bitmap b = Bitmap.createBitmap(mFakeSize[0], mFakeSize[1], Bitmap.Config.RGB_565);
            mFakeDrawable = new BitmapDrawable(b);
            int width = mFakeDrawable.getIntrinsicWidth();
            int height = mFakeDrawable.getIntrinsicHeight();
            mFakeDrawable.setBounds(0, 0, width > 0 ? width : 0, height > 0 ? height : 0);
        }
        return mFakeDrawable;
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
