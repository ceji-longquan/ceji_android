package com.lqtemple.android.lqbookreader.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.lqtemple.android.lqbookreader.R;

/**
 * Created by ls on 2017/6/13.
 *
 * @version $Rev$
 * @des ${TODO}
 * @updateAuthor $Author$
 * @updateDate $Date$
 * @updateDes ${TODO}
 */

public class RoundView extends View {
    int  innerCircle,innerringWidth,ringWidth;
    RectF rectfRed;
    float start=-90f;
    float end=360f;

    private final Paint paint;

    private final Context context;
    public RoundView(Context context) {
        this(context, null);
        innerCircle  = dip2px(context, 45); //设置内圆半径
        innerringWidth= dip2px(context, 2); //设置内圆半径
        ringWidth  = dip2px(context, 4); //设置圆环宽度

    }

    public RoundView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.paint = new Paint();
        this.paint.setAntiAlias(true); //消除锯齿
        this.paint.setStyle(Paint.Style.STROKE); //绘制空心圆
    }

    @Override
    protected void onDraw(Canvas canvas) {

        int center = getWidth() / 2;


        //绘制内圆
        paint.setColor(getResources().getColor(R.color.feedcolor));
        this.paint.setStrokeWidth(innerringWidth);
        canvas.drawCircle(center, center, innerCircle, this.paint);
        //绘制圆环
//        paint.setColor(getResources().getColor(R.color.feedcolor));
//        this.paint.setStrokeWidth(ringWidth);
//        canvas.drawCircle(center, center, innerCircle + 2 + ringWidth / 2, this.paint);


        //        canvas.drawArc();

        //绘制外圆
/*        this.paint.setARGB(155, 167, 190, 206);
        this.paint.setStrokeWidth(2);
        canvas.drawCircle(center, center, innerCircle + ringWidth, this.paint);*/


        super.onDraw(canvas);
    }


    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}