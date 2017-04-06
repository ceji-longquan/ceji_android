package com.lqtemple.android.lqbookreader.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import com.lqtemple.android.lqbookreader.util.LogHelper;

/**
 * @author Administrator
 *         Created by Administrator on 2017/3/16 0016.
 * @version $Rev$
 * @updateDes ${TODO}
 */

public class BaseActivity extends AppCompatActivity {
    private static final String TAG = LogHelper.makeLogTag(BaseActivity.class);


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    protected void onStart() {
        super.onStart();
        LogHelper.d(TAG, "Activity onStart");

    }


    public void onReturnLast(View v){
        finish();
    }
}
