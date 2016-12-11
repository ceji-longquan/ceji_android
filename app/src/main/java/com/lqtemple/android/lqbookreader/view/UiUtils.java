package com.lqtemple.android.lqbookreader.view;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import jedi.option.Option;

import static jedi.option.Options.option;

/**
 * Created by alex on 10/11/14.
 */
public class UiUtils {

    public static interface Operation<A> {
        void thenDo(A arg);
    }

    public static interface Action {
        void perform();
    }



    public static Operation<Action> onMenuPress( android.view.MenuItem menuItem ) {
        return action -> menuItem.setOnMenuItemClickListener( item -> {
            action.perform();
            return true;
        });
    }


    public static Option<TextView> getTextView(View parent, int id ) {
        return getView( parent, id, TextView.class );
    }

    public static Option<ImageView> getImageView(View parent, int id ) {
        return getView( parent, id, ImageView.class );
    }

    public static <T extends View> Option<T> getView(View parent, int id, Class<T> viewType ) {
        return option( (T) parent.findViewById(id) );
    }

}
