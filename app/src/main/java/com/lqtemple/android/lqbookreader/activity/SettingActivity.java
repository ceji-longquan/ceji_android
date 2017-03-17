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
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
        mSettingAdapter = new SettingAdapter(this, my_setting_list);
        mListView.setAdapter(mSettingAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e(TAG, "onItemClick: "+ position);
                startActivity(new Intent(getApplicationContext(),DownLoadActivity.class));

            }
        });



    }

    private void initData() {
        my_setting_list = new ArrayList();
        my_setting_list.add(new LeftMenu(R.drawable.setting, getString(R.string.my_note)));
        my_setting_list.add(new LeftMenu(R.drawable.setting, getString(R.string.my_note)));
        my_setting_list.add(new LeftMenu(R.drawable.setting, getString(R.string.my_note)));
        my_setting_list.add(new LeftMenu(R.drawable.setting, getString(R.string.my_note)));
        my_setting_list.add(new LeftMenu(R.drawable.setting, getString(R.string.my_note)));
        my_setting_list.add(new LeftMenu(R.drawable.setting, getString(R.string.my_note)));
    }


}
