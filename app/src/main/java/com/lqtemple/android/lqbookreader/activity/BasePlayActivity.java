package com.lqtemple.android.lqbookreader.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.lqtemple.android.lqbookreader.R;
import com.lqtemple.android.lqbookreader.activity.fragment.PlaybackControlsFragment;
import com.lqtemple.android.lqbookreader.util.LogHelper;


/**
 * @author Administrator
 *         Created by Administrator on 2017/3/16 0016.
 * @version $Rev$
 * @updateDes ${TODO}
 */

public class BasePlayActivity extends AppCompatActivity {
    private  final String TAG = LogHelper.makeLogTag(BasePlayActivity.class);
    protected  boolean isShow = false;
    private PlaybackControlsFragment mControlsFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    protected void onStart() {
        super.onStart();
        LogHelper.d(TAG, "Activity onStart");

        mControlsFragment = (PlaybackControlsFragment) getFragmentManager().findFragmentById(R.id.fragment_playback_controls);
        if (mControlsFragment == null) {
            throw new IllegalStateException("Mising fragment with id 'controls'. Cannot continue.");
        }
        showQuickControl();
    }


    protected void hidePlaybackControls() {
        LogHelper.d(TAG, "hidePlaybackControls");
        if (mControlsFragment != null){
            getFragmentManager().beginTransaction()
                    .hide(mControlsFragment)
                    .commit();
         }
        isShow = false;
    }

    /**
     * 显示或关闭底部播放控制栏
     */
    protected void showQuickControl() {
        if (mControlsFragment == null) {
            mControlsFragment = (PlaybackControlsFragment) getFragmentManager().findFragmentById(R.id.fragment_playback_controls);
        } else {
            getFragmentManager().beginTransaction()
                    .show(mControlsFragment)
                    .commit();
        }

        isShow = true;
    }


    public void onReturnLast(View v) {
        finish();
    }


}
