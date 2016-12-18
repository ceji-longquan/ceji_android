/*
 * Copyright (C) 2012 Alex Kuiper
 * 
 * This file is part of PageTurner
 *
 * PageTurner is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PageTurner is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with PageTurner.  If not, see <http://www.gnu.org/licenses/>.*
 */
package com.lqtemple.android.lqbookreader.activity;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import com.lqtemple.android.lqbookreader.Configuration;
import com.lqtemple.android.lqbookreader.MyApplication;
import com.lqtemple.android.lqbookreader.R;
import com.lqtemple.android.lqbookreader.fragment.ReadingFragment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReadingActivity extends AppCompatActivity {

    private ReadingFragment readingFragment;

    private Configuration config;

    private static final Logger LOG = LoggerFactory
            .getLogger("ReadingActivity");

    private int searchIndex = -1;

    protected int getMainLayoutResource() {
        return R.layout.activity_reading;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Configuration config = new Configuration(this);
        MyApplication.changeLanguageSetting(this, config);

        setTheme( getTheme(config) );
        super.onCreate(savedInstanceState);
        setContentView(getMainLayoutResource());


        readingFragment = (ReadingFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_reading);
    }

    protected int getTheme(Configuration config) {
        int theme = config.getTheme();

        if ( config.isFullScreenEnabled() ) {
            if (config.getColourProfile() == Configuration.ColourProfile.NIGHT) {
                theme = R.style.DarkFullScreen;
            } else {
                theme = R.style.LightFullScreen;
            }
        }

        return theme;
    }

    @Override
    public boolean onSearchRequested() {
        readingFragment.onSearchRequested();
        return true;
    }

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		readingFragment.onWindowFocusChanged(hasFocus);
	}

	public void onMediaButtonEvent(View view) {
		this.readingFragment.onMediaButtonEvent(view.getId());
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return readingFragment.onTouchEvent(event);
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {

        int action = event.getAction();
        int keyCode = event.getKeyCode();

//        if ( action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK && isDrawerOpen() ) {
//            closeNavigationDrawer();
//            return true;
//        }

        if ( readingFragment.dispatchKeyEvent(event) ) {
            return true;
        }

        return super.dispatchKeyEvent(event);
	}

//    @Override
//    protected void beforeLaunchActivity() {
//        readingFragment.saveReadingPosition();
//        readingFragment.getBookView().releaseResources();
//    }

}
