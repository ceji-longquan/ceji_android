package com.lqtemple.android.lqbookreader;

import android.app.Application;
import android.content.Context;

/**
 * Created by sundxing on 16/12/18.
 */

public class MyApplication extends Application{
    public static void changeLanguageSetting(Context context, Configuration pageTurnerConfig) {
        android.content.res.Configuration config = new android.content.res.Configuration(
                context.getResources().getConfiguration());

        // TODO too much times
        config.locale = pageTurnerConfig.getLocale();
        context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
    }
}
