package com.lqtemple.android.lqbookreader.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.lqtemple.android.lqbookreader.util.DensityUtil;

/**
 * 电池电量图标
 */

public class BatteryView extends View {

    private RectF mRectF = new RectF();
    private RectF mRectFSolid = new RectF();
    private int radius;
    private Paint paintFrame;
    private Paint paintSolid;
    private int batteryColor;
    // 电池的触点
    private int contactWidth;
    private int oneDp = DensityUtil.dip2px(getContext(), 1);
    private float ratio;

    public BatteryView(Context context) {
        super(context);
        init();
    }

    public BatteryView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BatteryView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        radius = oneDp;
        contactWidth = oneDp;
        batteryColor = Color.DKGRAY;
        paintFrame = new Paint();
        paintSolid = new Paint();
        paintFrame.setColor(batteryColor);
        paintFrame.setStyle(Paint.Style.STROKE);
        // 电池边框
        paintFrame.setStrokeWidth(oneDp * 1);
        paintSolid.setColor(batteryColor);

        setBatteryLevel(0.8f);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w > contactWidth) {
            mRectF.set(0, 0, w - contactWidth, h);
            float padding = oneDp * 2;
            mRectF.inset(padding, padding);
            int strokeW = oneDp;
            mRectFSolid.set(mRectF.left, mRectF.top, mRectF.left + mRectF.width() * ratio, mRectF.bottom);
            mRectFSolid.inset(strokeW, strokeW);
        }
    }

    public void setBatteryLevel(float level) {
        if (level > 1) {
            throw new IllegalArgumentException("Battery level is less than one, like 0.35 (35%)");
        }
        ratio = level;
        mRectFSolid.set(mRectFSolid.left, mRectFSolid.top, mRectFSolid.left + mRectFSolid.width() * ratio, mRectFSolid.bottom);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawRoundRect(mRectF, radius, radius, paintFrame);
        canvas.drawRect(mRectF.right, mRectF.centerY() - oneDp, mRectF.right + oneDp, mRectF.centerY() + oneDp, paintFrame);

        canvas.drawRoundRect(mRectFSolid, radius, radius, paintSolid);
    }

}
