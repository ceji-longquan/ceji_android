package com.lqtemple.android.lqbookreader.read;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.support.v7.widget.AppCompatTextView;
import android.text.Layout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ActionMode;

import com.lqtemple.android.lqbookreader.BuildConfig;
import com.lqtemple.android.lqbookreader.StaticLayoutFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.lzy.imagepicker.ImagePicker.TAG;

public class InnerView extends AppCompatTextView {
    private static final boolean DEBUG_VIEW = true && BuildConfig.DEBUG;
    private static final Logger LOG = LoggerFactory.getLogger("InnerView");

    private BookView bookView;

    private long blockUntil = 0l;
    private Layout mLayout;
    private CharSequence mRawText;

    public InnerView(Context context) {
        super(context);
        initView();
    }

    public InnerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public InnerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();

    }

    private void initView() {
    }


    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        bookView.onInnerViewResize();
    }

    @Override
    public void setTextSize(float size) {
        super.setTextSize(size);
        mLayout = null;
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(text, type);
        mRawText = text;
        mLayout = null;
    }

    @Override
    public void requestLayout() {
        super.requestLayout();
        Log.d(TAG, "requestLayout : \n getText : " + getText());
        Log.d(TAG, "raw Text: " + mRawText);
        if (mRawText != null) {
            mLayout = StaticLayoutFactory.create(mRawText, getPaint(),
                    (int) PagetSizeConfig.getPageWidth(getContext()), PagetSizeConfig.getLineSpacing());
        } else {
            mLayout = null;
        }
    }

    @Override
    public boolean onPreDraw() {
        Log.d(TAG, "preDraw, width : " + getWidth());
        return super.onPreDraw();
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
            /*
            We override this method to do nothing, since the base
            implementation closes the ActionMode.

            This means that when the user clicks the overflow menu,
            the ActionMode is stopped and text selection is ended.
             */
    }

    public void setBookView(BookView bookView) {
        this.bookView = bookView;
    }

    public void setBlockUntil(long blockUntil) {
        this.blockUntil = blockUntil;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mLayout != null) {
            canvas.save();
            canvas.translate(getPaddingLeft(), getPaddingBottom());
            mLayout.draw(canvas);
            canvas.restore();
        } else {
            super.onDraw(canvas);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public ActionMode startActionMode(ActionMode.Callback callback) {

        if (System.currentTimeMillis() > blockUntil) {

            LOG.debug("InnerView starting action-mode");
            return super.startActionMode(callback);

        } else {
            LOG.debug("Not starting action-mode yet, since block time hasn't expired.");
            clearFocus();
            return null;
        }
    }

    public void setLayout(Layout mLayout) {
        this.mLayout = mLayout;
    }
}