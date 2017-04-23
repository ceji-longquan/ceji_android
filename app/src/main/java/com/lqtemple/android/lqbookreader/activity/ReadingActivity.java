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
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.lqtemple.android.lqbookreader.Configuration;
import com.lqtemple.android.lqbookreader.MyApplication;
import com.lqtemple.android.lqbookreader.R;
import com.lqtemple.android.lqbookreader.fragment.ReadingFragment;
import com.lqtemple.android.lqbookreader.model.Book;
import com.lqtemple.android.lqbookreader.model.Content;
import com.lqtemple.android.lqbookreader.read.BookView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class ReadingActivity extends AppCompatActivity {

    private ReadingFragment readingFragment;

    private Configuration config;

    private static final Logger LOG = LoggerFactory
            .getLogger("ReadingActivity");

    private int searchIndex = -1;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;

    protected int getMainLayoutResource() {
        return R.layout.activity_reading;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Configuration config = Configuration.getInstance();
        MyApplication.changeLanguageSetting(this, config);

        setTheme( getTheme(config) );
        setContentView(getMainLayoutResource());

        initLayoutDrawer();

    }

    private void initLayoutDrawer() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        readingFragment = (ReadingFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_reading);

        mDrawerList = (ListView) findViewById(R.id.left_drawer_list);
        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener

    }

    public void showChapter() {
        // TODO init draw tab
        ViewGroup drawerTabs = (ViewGroup) findViewById(R.id.drawer_tab);
        drawerTabs.getChildAt(0).setSelected(true);

        Book book = readingFragment.getBook();
        BookView bookView = readingFragment.getBookView();

        List<List<Content>> chapters = book.getChapters();
        List<CharSequence> chapterStr = new ArrayList<>();
        for (List<Content> contents : chapters) {
            Content firstContent = contents.get(0);
            int pageNumber = bookView.getPageNumberFor(firstContent);

            chapterStr.add(firstContent.getText() + " --- " + pageNumber);
        }
        mDrawerList.setAdapter(new ArrayAdapter<>(this,
                R.layout.drawer_list_item, chapterStr));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        mDrawerLayout.openDrawer(GravityCompat.START, true);
    }

    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            clickItem(position);
        }
    }

    private void clickItem(int position) {

        // TODO jump to target page index

        // update selected item and title, then close the drawer
        mDrawerList.setItemChecked(position, true);
        mDrawerLayout.closeDrawer(GravityCompat.START, true);
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
        // TODO
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
