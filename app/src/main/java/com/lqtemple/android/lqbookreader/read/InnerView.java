package com.lqtemple.android.lqbookreader.read;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.support.v7.widget.AppCompatTextView;
import android.text.Layout;
import android.util.AttributeSet;
import android.view.ActionMode;

import com.lqtemple.android.lqbookreader.StaticLayoutFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InnerView extends AppCompatTextView {
    private static final Logger LOG = LoggerFactory.getLogger("InnerView");

        private BookView bookView;

        private long blockUntil = 0l;
        private Layout mLayout;

        public InnerView(Context context) {
            super(context);
        }

        public InnerView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public InnerView(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
        }


        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
            bookView.onInnerViewResize();
        }

        @Override
        public void requestLayout() {
            super.requestLayout();
            ensureLayout(getText());
        }

        private void ensureLayout(CharSequence text) {
            mLayout = StaticLayoutFactory.create(text, getPaint(), getWidth(), bookView!= null ? bookView.getLineSpacing() : 0);
        }

    @Override
    public boolean onPreDraw() {
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

        public void setBlockUntil( long blockUntil ) {
            this.blockUntil = blockUntil;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            if (mLayout != null) {
                canvas.save();
                canvas.translate(getPaddingLeft(), getPaddingBottom());
                mLayout.draw(canvas);
                canvas.restore();
            } else  {
                super.onDraw(canvas);
            }
        }

        @TargetApi( Build.VERSION_CODES.HONEYCOMB )
        @Override
        public ActionMode startActionMode(ActionMode.Callback callback) {

            if ( System.currentTimeMillis() > blockUntil ) {

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