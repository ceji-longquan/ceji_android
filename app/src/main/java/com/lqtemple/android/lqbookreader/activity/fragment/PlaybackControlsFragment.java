/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.lqtemple.android.lqbookreader.activity.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lqtemple.android.lqbookreader.R;
import com.lqtemple.android.lqbookreader.event.MusicEvent;
import com.lqtemple.android.lqbookreader.event.StartPlayEvent;
import com.lqtemple.android.lqbookreader.model.MusicPlayInfo;
import com.lqtemple.android.lqbookreader.service.MusicPlayerService;
import com.lqtemple.android.lqbookreader.util.LogHelper;
import com.lqtemple.android.lqbookreader.util.LqBookConst;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import static com.lqtemple.android.lqbookreader.service.MusicPlayerService.mediaPlayer;


/**
 * A class that shows the Media Queue to the user.
 */
public class PlaybackControlsFragment extends Fragment {

    private static final String TAG = LogHelper.makeLogTag(PlaybackControlsFragment.class);

    private ImageButton mPlayPause;
    private TextView mTitle;
    private TextView mSubtitle;
    private TextView mExtraInfo;
    private ImageView mAlbumArt;
    private String mArtUrl;

    private SeekBar audioSeekBar;
    private Intent intent = new Intent();
    protected int currentposition = 0;//当前播放列表里哪首音乐

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //绑定事件接受
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_playback_controls, container, false);

        mPlayPause = (ImageButton) rootView.findViewById(R.id.play_pause);
        mPlayPause.setEnabled(true);
        mPlayPause.setOnClickListener(mButtonListener);
        audioSeekBar = (SeekBar) rootView.findViewById(R.id.seekBar);
        mTitle = (TextView) rootView.findViewById(R.id.title);
        mSubtitle = (TextView) rootView.findViewById(R.id.artist);
        mExtraInfo = (TextView) rootView.findViewById(R.id.extra_info);
        mAlbumArt = (ImageView) rootView.findViewById(R.id.album_art);
        init();

        /*rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FullScreenPlayerActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                //                MediaControllerCompat controller = ((FragmentActivity) getActivity())
                //                        .getSupportMediaController();
                //                MediaMetadataCompat metadata = controller.getMetadata();
                //                if (metadata != null) {
                //                    intent.putExtra(MusicPlayerActivity.EXTRA_CURRENT_MEDIA_DESCRIPTION,
                //                        metadata.getDescription());
                //                }
                                startActivity(intent);
            }
        });*/
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        LogHelper.d(TAG, "fragment.onStart");

    }

    @Override
    public void onStop() {
        super.onStop();
        LogHelper.d(TAG, "fragment.onStop");

    }


    private final View.OnClickListener mButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.play_pause:
                    if (MusicPlayerService.isStartService) {
                        if (MusicPlayerService.isplay) {
                            Log.d(TAG, "pause");
                            pause();
                            MusicPlayerService.isplay = false;
                        } else {
                            player(LqBookConst.RE_START_PLAY);
                            MusicPlayerService.isplay = true;
                            Log.d(TAG, "player");
                        }
                    } else {
                        Toast.makeText(getActivity(), R.string.select_yinpin, Toast.LENGTH_SHORT).show();
                    }

                    break;
            }
        }
    };


    @Subscribe
    public void onMessageEvent(MusicEvent event) {
        MusicPlayInfo musicPlayInfo = event.message;
        audioSeekBar.setMax(musicPlayInfo.getMaxPos());
        audioSeekBar.setProgress(musicPlayInfo.getCurrentPos());
        mTitle.setText(musicPlayInfo.getName());
        mSubtitle.setText(musicPlayInfo.getCurrentPlayTime() + "  / " + musicPlayInfo.getAllTime());
    }


    @Subscribe
    public void onMessageEvent(StartPlayEvent event) {
        if (event.pos >= 0) {
            mPlayPause.setBackgroundResource(R.mipmap.ic_pause_black_36dp);
        }
    }

    private void init() {

        //播放进度监 ，使用静态变量时别忘了Service里面还有个进度条刷新
        audioSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (currentposition == -1) {
                    Log.i(TAG, "MusicActivity...showInfo(请选择要播放的音乐);.........");
                    //还没有选择要播放的音乐
                    showInfo("请选择要播放的音乐");
                } else {
                    //假设改变源于用户拖动
                    if (fromUser) {
                        //这里有个问题，如果播放时用户拖进度条还好说，但是如果是暂停时，拖完会自动播放，所以还需要把图标设置一下
                        mPlayPause.setBackgroundResource(R.mipmap.ic_pause_black_36dp);
                        MusicPlayerService.mediaPlayer.seekTo(progress);// 当进度条的值改变时，音乐播放器从新的位置开始播放
                    }

                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (mediaPlayer != null) {
                    mediaPlayer.pause();
                }

                //MusicPlayerService.mediaPlayer.pause(); // 开始拖动进度条时，音乐暂停播放
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mediaPlayer != null) {
                    mediaPlayer.start();
                }
                //MusicPlayerService.mediaPlayer.start(); // 停止拖动进度条时，音乐开始播放
            }
        });
    }


    protected void player(String info) {
        intent.putExtra(LqBookConst.START_PLAY_KEY, info);
        intent.setPackage(getActivity().getPackageName());//这里你需要设置你应用的包名
        mPlayPause.setBackgroundResource(R.mipmap.ic_pause_black_36dp);
        getActivity().startService(intent);
    }

    /*
       * MSG :
       *  0  未播放--->播放
       *  1    播放--->暂停
       *  2    暂停--->继续播放
       * */
    protected void pause() {
        intent.setPackage(getActivity().getPackageName());//这里你需要设置你应用的包名
        intent.putExtra(LqBookConst.START_PLAY_KEY, LqBookConst.START_PAUSE);
        mPlayPause.setBackgroundResource(R.mipmap.ic_play_arrow_black_36dp);
        getActivity().startService(intent);
    }

    private void showInfo(String info) {
        Toast.makeText(getActivity(), info, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        //注销事件接受
        EventBus.getDefault().unregister(this);
    }
}
