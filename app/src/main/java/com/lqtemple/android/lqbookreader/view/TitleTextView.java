package com.lqtemple.android.lqbookreader.view;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by ls on 2017/6/13.
 *
 * @version $Rev$
 * @des ${TODO}
 * @updateAuthor $Author$
 * @updateDate $Date$
 * @updateDes ${TODO}
 */

public class TitleTextView  extends TextView {

        public TitleTextView(Context context) {
            super(context);
            init(context);
        }

        public TitleTextView(Context context, AttributeSet attrs) {
            super(context, attrs);
            init(context);
        }

        public TitleTextView(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            init(context);
        }

        public void init(Context context) {
            Typeface newFont = Typeface.createFromAsset(context.getAssets(), "fonts/Libian-SC-Regular.ttf");
            setTypeface(newFont);
        }

    }