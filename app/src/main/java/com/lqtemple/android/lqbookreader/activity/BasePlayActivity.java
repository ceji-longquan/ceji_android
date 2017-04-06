package com.lqtemple.android.lqbookreader.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lqtemple.android.lqbookreader.R;
import com.lqtemple.android.lqbookreader.activity.fragment.PlaybackControlsFragment;
import com.lqtemple.android.lqbookreader.event.MusicEvent;
import com.lqtemple.android.lqbookreader.model.MusicMedia;
import com.lqtemple.android.lqbookreader.model.MusicPlayInfo;
import com.lqtemple.android.lqbookreader.service.MusicPlayerService;
import com.lqtemple.android.lqbookreader.util.LogHelper;
import com.lqtemple.android.lqbookreader.util.LqBookConst;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;


/**
 * @author Administrator
 *         Created by Administrator on 2017/3/16 0016.
 * @version $Rev$
 * @updateDes ${TODO}
 */

public class BasePlayActivity extends AppCompatActivity implements ServiceConnection {
    private static final String TAG = LogHelper.makeLogTag(BasePlayActivity.class);
    private ArrayList<MusicMedia> musicList = new ArrayList<>(); //音乐信息列表

    private PlaybackControlsFragment mControlsFragment;
    private boolean isExit = false;//返回键
    public MusicPlayerService musicPlayerService = null;
    private MediaPlayer mediaPlayer = null;
    public Handler handler = null;//处理界面更新，seekbar ,textview
    public boolean mIsBound = false;
    protected int currentposition = 0;//当前播放列表里哪首音乐

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new Handler();

    }


    @Override
    protected void onStart() {
        super.onStart();
        LogHelper.d(TAG, "Activity onStart");

        mControlsFragment = (PlaybackControlsFragment) getFragmentManager().findFragmentById(R.id.fragment_playback_controls);
        if (mControlsFragment == null) {
            throw new IllegalStateException("Mising fragment with id 'controls'. Cannot continue.");
        }
        showQuickControl(true);
        //        init();


    }

    protected void startlayer(int position) {
        Log.i(TAG, "URL=" + musicList.get(position).getUrl());
        Intent intent = new Intent();
        intent.setPackage(getPackageName());//这里你需要设置你应用的包名
        intent.putExtra("curposition", position);//把位置传回去，方便再启动时调用
        intent.putExtra("MusicMedia", musicList.get(position));
        intent.putExtra(LqBookConst.START_PLAY_KEY, LqBookConst.START_PLAY);
        intent.setAction(MusicPlayerService.PLAY_ACTION);

        //播放时就改变btn_play_pause图标，下面这个过期了
        //        btn_play_pause.setBackgroundDrawable(getResources().getDrawable(R.drawable.pause));
        //        btn_play_pause.setBackgroundResource(R.mipmap.ic_pause_black_36dp);

        startService(intent);
        bindService(intent, this, Context.BIND_AUTO_CREATE);
        mIsBound = true;
        Log.i(TAG, "MusicActivity...bindService.......");
        MusicPlayerService.isStartService = true;
        MusicPlayerService.isplay = true;

    }


    private void showInfo(String info) {
        Toast.makeText(this, info, Toast.LENGTH_SHORT).show();
    }

    protected void hidePlaybackControls() {
        LogHelper.d(TAG, "hidePlaybackControls");
        getFragmentManager().beginTransaction()
                .hide(mControlsFragment)
                .commit();
    }

    /**
     * @param show 显示或关闭底部播放控制栏
     */
    protected void showQuickControl(boolean show) {
        if (show) {
            if (mControlsFragment == null) {
                mControlsFragment = (PlaybackControlsFragment) getFragmentManager().findFragmentById(R.id.fragment_playback_controls);
            } else {
                getFragmentManager().beginTransaction()
                        .show(mControlsFragment)
                        .commit();
            }
        } else {
            if (mControlsFragment != null)
                getFragmentManager().beginTransaction()
                        .hide(mControlsFragment)
                        .commit();
        }
    }


    public void onReturnLast(View v) {
        finish();
    }


    /**
     * 获取服务对象时的操作
     */
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        // TODO Auto-generated method stub
        musicPlayerService = ((MusicPlayerService.musicBinder) service).getPlayInfo();
        mediaPlayer = musicPlayerService.getMediaPlayer();
        Log.i(TAG, "MusicActivity...onServiceConnected.......");
        currentposition = musicPlayerService.getCurposition();
        //设置进度条最大值
        //        audioSeekBar.setMax(mediaPlayer.getDuration());
        //这里开了一个线程处理进度条,这个方式官方貌似不推荐，说违背什么单线程什么鬼
        //            new Thread(seekBarThread).start();
        //使用runnable + handler
        handler.post(seekBarHandler);
    }

    /**
     * 无法获取到服务对象时的操作
     */
    @Override
    public void onServiceDisconnected(ComponentName name) {
        // TODO Auto-generated method stub
        musicPlayerService = null;
    }

    Runnable seekBarHandler = new Runnable() {
        @Override
        public void run() {

            MusicPlayInfo musicPlayInfo = new MusicPlayInfo();
            musicPlayInfo.setMaxPos(mediaPlayer.getDuration());
            musicPlayInfo.setName(musicList.get(currentposition).getTitle());
            musicPlayInfo.setCurrentPlayTime(musicPlayerService.toTime(musicPlayerService.getCurrentPosition()));
            musicPlayInfo.setCurrentPos(musicPlayerService.getCurrentPosition());
            musicPlayInfo.setAllTime(musicPlayerService.toTime(musicPlayerService.getDuration()));

            EventBus.getDefault().post(new MusicEvent(musicPlayInfo));

            handler.postDelayed(seekBarHandler, 1000);

        }
    };

    public ArrayList<MusicMedia> getMusicList() {
        return musicList;
    }

    public void setMusicList(ArrayList<MusicMedia> musicList) {
        this.musicList = musicList;
    }

    @Override
    protected void onDestroy() {
        // TODO 自动生成的方法存根
        super.onDestroy();
        if (mIsBound) {
            unbindService(this);
        }
    }
}
