package com.lqtemple.android.lqbookreader.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ListView;
import android.widget.TextView;

import com.lqtemple.android.lqbookreader.R;
import com.lqtemple.android.lqbookreader.activity.adapter.SettingAdapter;
import com.lqtemple.android.lqbookreader.annotation.InjectUtils;
import com.lqtemple.android.lqbookreader.annotation.InjectView;

import java.util.ArrayList;

/**
 * Created by ls on 2017/6/2.
 *
 * @version $Rev$
 * @des ${TODO}
 * @updateAuthor $Author$
 * @updateDate $Date$
 * @updateDes ${TODO}
 */

public class MyShareActvity extends BaseActivity {
    private static final String TAG = "MyShareActvity";

    private ArrayList my_setting_list;
    @InjectView(R.id.listView)
    private ListView mListView;
    private SettingAdapter mSettingAdapter;
    @InjectView(R.id.titleCenterbtn)
    private TextView titleCenterbtn;
    @InjectView(R.id.titleRightbtn)
    private TextView titleRightbtn;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_share);
        InjectUtils.bind(this);

        init();
    }

    private void init() {
        titleCenterbtn.setText(getText(R.string.my_share));
        titleRightbtn.setBackgroundResource(0);
    }
}
