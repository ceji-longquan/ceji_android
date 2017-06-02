package com.lqtemple.android.lqbookreader.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.lqtemple.android.lqbookreader.R;
import com.lqtemple.android.lqbookreader.activity.adapter.SettingAdapter;
import com.lqtemple.android.lqbookreader.annotation.InjectUtils;
import com.lqtemple.android.lqbookreader.annotation.InjectView;
import com.lqtemple.android.lqbookreader.model.LeftMenu;

import java.util.ArrayList;

/**
 * @author Administrator
 *         Created by Administrator on 2017/3/14 0014.
 * @version $Rev$
 * @updateDes ${TODO}
 */

public class SettingActivity extends BaseActivity {
    private static final String TAG = "SettingActivity";

    private ArrayList my_setting_list;
    @InjectView(R.id.setting_listview)
    private ListView mListView;
    private SettingAdapter mSettingAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);


        InjectUtils.bind(this);

        initData();

        mSettingAdapter = new SettingAdapter(this, my_setting_list);
        mListView.setAdapter(mSettingAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e(TAG, "onItemClick: " + position);
                switch (position) {
                    case 0:
                        startActivity(new Intent(getApplicationContext(), DownLoadActivity.class));

                        break;

                    case 1:

                        break;
                    case 2:

                        break;
                    case 3:
                        startActivity(new Intent(getApplicationContext(), MusicDownLoadActivity.class));
                        break;
                    case 4:

                        break;
                    case 5:

                        break;

                }

            }
        });


    }

    private void initData() {
        my_setting_list = new ArrayList();
        my_setting_list.add(new LeftMenu(R.mipmap.note_icon, getString(R.string.my_note)));
        my_setting_list.add(new LeftMenu(R.mipmap.bookmark_icon, getString(R.string.my_bookmark)));
        my_setting_list.add(new LeftMenu(R.mipmap.lanuage_icon, getString(R.string.my_lanuage)));
        my_setting_list.add(new LeftMenu(R.mipmap.xiazai, getString(R.string.my_music)));
        my_setting_list.add(new LeftMenu(R.mipmap.share_icon, getString(R.string.my_share)));
        my_setting_list.add(new LeftMenu(R.mipmap.feedback_icon, getString(R.string.my_feedback)));
    }


}
