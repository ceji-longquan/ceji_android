package com.lqtemple.android.lqbookreader.activity;

import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lqtemple.android.lqbookreader.FileUtils;
import com.lqtemple.android.lqbookreader.R;
import com.lqtemple.android.lqbookreader.activity.adapter.LeftMenuAdapter;
import com.lqtemple.android.lqbookreader.model.LeftMenu;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private Toolbar toolbar;
    private DrawerLayout mDrawerLayout;
    private RelativeLayout leftLayout;
    private RelativeLayout rightLayout;
    private TextView titleLefttbtn, titleRightbtn;

    private LeftMenuAdapter mLeftMenuAdapter, mRightMenuAdapter;
    private ArrayList mLeftArrayList;
    private ArrayList mRightArrayList;
    private ListView leftListView;

    private File target;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initToolBar();

        StrictMode.enableDefaults();

        // 全屏
        initData();
        leftListView = (ListView) leftLayout.findViewById(R.id.left_listview);
        ListView rightListView = (ListView) rightLayout.findViewById(R.id.right_listview);

        mLeftMenuAdapter = new LeftMenuAdapter(this, mLeftArrayList);
        mRightMenuAdapter = new LeftMenuAdapter(this, mRightArrayList);
        leftListView.setAdapter(mLeftMenuAdapter);
        rightListView.setAdapter(mRightMenuAdapter);
        leftListView.setOnItemClickListener(new DrawerItemClickListener());

    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }

    }

    private void selectItem(int position) {
        Log.e(TAG, "selectItem: " + position);

    }

    private void initToolBar() {
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
    }


    private void initView() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerlayout);
        titleLefttbtn = (TextView) findViewById(R.id.titleLefttbtn);
        titleRightbtn = (TextView) findViewById(R.id.titleRightbtn);

        toolbar = (Toolbar) findViewById(R.id.titleBar);
        leftLayout = (RelativeLayout) findViewById(R.id.left);
        rightLayout = (RelativeLayout) findViewById(R.id.right);
        titleLefttbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
                    mDrawerLayout.closeDrawer(Gravity.LEFT);
                } else {
                    mDrawerLayout.openDrawer(Gravity.LEFT);
                    if (mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
                        mDrawerLayout.closeDrawer(Gravity.RIGHT);
                    }
                }
            }
        });
        titleRightbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //                if(mDrawerLayout.isDrawerOpen(Gravity.RIGHT)){
                //                    mDrawerLayout.closeDrawer(Gravity.RIGHT);
                //                }else{
                //                    mDrawerLayout.openDrawer(Gravity.RIGHT);
                //                    if(mDrawerLayout.isDrawerOpen(Gravity.LEFT)){
                //                        mDrawerLayout.closeDrawer(Gravity.LEFT);
                //                    }
                //
                //                }
                startActivity(new Intent(getApplicationContext(), SettingActivity.class));

            }
        });

    }

    private void initData() {
        AssetManager manager = getAssets();
        InputStream inputStream = null;
        target = new File(getExternalCacheDir(), "test.json");
        try {

            inputStream = manager.open("cj.json");
            FileUtils.writeFile(target, inputStream);
        } catch (Exception e) {

        }

        mLeftArrayList = new ArrayList<LeftMenu>();
        mLeftArrayList.add(new LeftMenu(R.drawable.setting, getString(R.string.menu_title1)));
        mLeftArrayList.add(new LeftMenu(R.drawable.setting, getString(R.string.menu_title1)));
        mLeftArrayList.add(new LeftMenu(R.drawable.setting, getString(R.string.menu_title1)));
        mLeftArrayList.add(new LeftMenu(R.drawable.setting, getString(R.string.menu_title1)));
        mLeftArrayList.add(new LeftMenu(R.drawable.setting, getString(R.string.menu_title1)));
        mLeftArrayList.add(new LeftMenu(R.drawable.setting, getString(R.string.menu_title1)));


        mRightArrayList = new ArrayList<LeftMenu>();
        mRightArrayList.add(new LeftMenu(R.drawable.setting, getString(R.string.menu_title1)));
        mRightArrayList.add(new LeftMenu(R.drawable.setting, getString(R.string.menu_title1)));
        mRightArrayList.add(new LeftMenu(R.drawable.setting, getString(R.string.menu_title1)));
        mRightArrayList.add(new LeftMenu(R.drawable.setting, getString(R.string.menu_title1)));
        mRightArrayList.add(new LeftMenu(R.drawable.setting, getString(R.string.menu_title1)));
        mRightArrayList.add(new LeftMenu(R.drawable.setting, getString(R.string.menu_title1)));
    }


    public void onStartRead(View v) {
        Intent i = new Intent(this, ReadingActivity.class);
        i.setData(Uri.fromFile(target));
        startActivity(i);
    }


}
