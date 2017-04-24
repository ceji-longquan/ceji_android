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
import android.os.Handler;
import android.os.Message;
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
import com.lqtemple.android.lqbookreader.activity.FullScreenPlayerActivity;
import com.lqtemple.android.lqbookreader.event.AgainPlayEvent;
import com.lqtemple.android.lqbookreader.event.MusicEvent;
import com.lqtemple.android.lqbookreader.event.UpdateListColorEvent;
import com.lqtemple.android.lqbookreader.model.MusicMedia;
import com.lqtemple.android.lqbookreader.service.MediaService;
import com.lqtemple.android.lqbookreader.util.ConstantValue;
import com.lqtemple.android.lqbookreader.util.HandlerManager;
import com.lqtemple.android.lqbookreader.util.LogHelper;
import com.lqtemple.android.lqbookreader.util.LqBookConst;
import com.lqtemple.android.lqbookreader.util.MediaUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import static com.lqtemple.android.lqbookreader.service.MediaService.isPlaying;
import static com.lqtemple.android.lqbookreader.service.MediaService.mContext;
import static com.lqtemple.android.lqbookreader.util.MediaUtil.getInstacen;


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

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ConstantValue.STARTED:
                    // 开始刷新播放列表界面
                    Log.i(TAG, "MediaUtil.STARTED =" + ConstantValue.STARTED);

                    break;
                case ConstantValue.FINISHED:
                    // 结束刷新播放列表界面
                    Log.i(TAG, "MediaUtil.FINISHED =" + ConstantValue.FINISHED);

                    break;
                case ConstantValue.PLAY_END:
                    // 播放完成
                    // 播放模式：单曲循环、顺序播放、循环播放、随机播放
                    // 单曲循环:记录当前播放位置
                    // 顺序播放:当前播放位置上＋1
                    // 循环播放:判断如果，增加的结果大于songList的大小，修改播放位置为零
                    // 随机播放:Random.nextInt() songList.size();
                    Log.i(TAG, "MediaUtil.PLAY_END =" + ConstantValue.PLAY_END);
                    MediaUtil.CURRENTPOS++;

                    if (MediaUtil.CURRENTPOS < getInstacen()
                            .getSongList().size()) {
                        MusicMedia music = getInstacen().getSongList()
                                .get(MediaUtil.CURRENTPOS);
                        startSeekBarPlayService(music, LqBookConst.DEFAULT_PROGRESS, ConstantValue.OPTION_PLAY);

                    } else {
                        MediaUtil.CURRENTPOS = 0;
                        MusicMedia music = getInstacen().getSongList()
                                .get(MediaUtil.CURRENTPOS);
                        startSeekBarPlayService(music, LqBookConst.DEFAULT_PROGRESS, ConstantValue.OPTION_PLAY);
                    }

                    MediaUtil.LAST_POS = MediaUtil.CURRENTPOS;
                    mTitle.setText(MediaUtil.getInstacen().getCurrent().getTitle());
                    EventBus.getDefault().post(new UpdateListColorEvent(MediaUtil.CURRENTPOS));
                    break;
                case ConstantValue.SEEKBAR_CHANGE:
                    // 根据播放时长改变SeekBar
//                    Log.i(TAG, "PlaybackControlsFragment.arg2 =" + msg.arg2);
                    mSubtitle.setText(LqBookConst.toTime(msg.arg1));
                    audioSeekBar.setMax(msg.arg2);
                    audioSeekBar.setProgress(msg.arg1);
                    //
                    mExtraInfo.setText(LqBookConst.toTime(msg.arg2));

                    break;
            }

        }
    };

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

        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent playIntent = new Intent(getActivity(), FullScreenPlayerActivity.class);
                playIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                playIntent.putExtra("music", getInstacen().getSongList().get(MediaUtil.CURRENTPOS));
                startActivity(playIntent);

                //                playIntent.putExtra("position", position);
            }
        });
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();
        LogHelper.d(TAG, "fragment.onStop");

    }

    @Override
    public void onResume() {
        super.onResume();
        HandlerManager.putHandler(handler);
        LogHelper.d(TAG, "MediaUtil.CURRENTPOS=" + MediaUtil.CURRENTPOS);
        LogHelper.d(TAG, "MediaUtil.size=" + MediaUtil.getInstacen().getSongList().size());
        if(MediaUtil.getInstacen().getSongList().size()>0){
            if (MediaUtil.CURRENTPOS >= 0 && MediaUtil.CURRENTPOS<=MediaUtil.getInstacen().getSongList().size() ) {
                MusicMedia music = MediaUtil.getInstacen().getCurrent();
                if (music != null) {
                    mTitle.setText(music.getTitle());
                    mSubtitle.setText(LqBookConst.toTime(MediaService.default_postion));
                    audioSeekBar.setMax(music.getDuration());
                    audioSeekBar.setProgress(MediaService.default_postion);
                    //
                    mExtraInfo.setText(LqBookConst.toTime(music.getDuration()));

                }
            }
        }

        if (isPlaying) {
            mPlayPause.setBackgroundResource(R.mipmap.ic_pause_black_36dp);
        } else {
            mPlayPause.setBackgroundResource(R.mipmap.ic_play_arrow_black_36dp);
        }

    }

    private final View.OnClickListener mButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.play_pause:
                    Log.i(TAG, "play_pause =" + isPlaying);
                    Log.i(TAG, "CURRENTPOS =" + MediaUtil.CURRENTPOS);

                    if(MediaUtil.CURRENTPOS<0 ){
                        Toast.makeText(getActivity(), R.string.select_yinpin, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (isPlaying) {
                        startSeekBarPlayService(null, LqBookConst.DEFAULT_PROGRESS, ConstantValue.OPTION_PAUSE);
                        mPlayPause.setBackgroundResource(R.mipmap.ic_play_arrow_black_36dp);

                    } else {
                        startSeekBarPlayService(null, LqBookConst.DEFAULT_PROGRESS, ConstantValue.OPTION_CONTINUE);
                        mPlayPause.setBackgroundResource(R.mipmap.ic_pause_black_36dp);


                    }
                    break;
            }
        }
    };


    private void startSeekBarPlayService(MusicMedia music, int progress, int option) {
        try {
            if(mContext!=null){
                Intent intent = new Intent(mContext, MediaService.class);
                if (music != null) {
                    intent.putExtra("music", music);
                    intent.putExtra("file", music.getUrl());
                }
                intent.putExtra("option", option);
                intent.putExtra("progress", progress);
                mContext.startService(intent);
            }else{
                Intent intent = new Intent(getActivity().getApplicationContext(), MediaService.class);
                if (music != null) {
                    intent.putExtra("music", music);
                    intent.putExtra("file", music.getUrl());
                }
                intent.putExtra("option", option);
                intent.putExtra("progress", progress);
                getActivity().getApplicationContext().startService(intent);
            }


            if (isPlaying) {
                mPlayPause.setBackgroundResource(R.mipmap.ic_pause_black_36dp);
            } else {
                mPlayPause.setBackgroundResource(R.mipmap.ic_play_arrow_black_36dp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Subscribe
    public void onMessageEvent(MusicEvent event) {
        MusicMedia musicPlayInfo = event.message;
        audioSeekBar.setMax(musicPlayInfo.getDuration());
        mTitle.setText(musicPlayInfo.getTitle());
        startSeekBarPlayService(musicPlayInfo, LqBookConst.DEFAULT_PROGRESS, ConstantValue.OPTION_PLAY);
        mPlayPause.setBackgroundResource(R.mipmap.ic_pause_black_36dp);

    }


    @Subscribe
    public void onMessageEvent(AgainPlayEvent event) {
        MusicMedia musicPlayInfo = event.message;
        audioSeekBar.setMax(musicPlayInfo.getDuration());
        mTitle.setText(musicPlayInfo.getTitle());
        startSeekBarPlayService(null, LqBookConst.DEFAULT_PROGRESS, ConstantValue.OPTION_DEFAULT);
        if (isPlaying) {
            mPlayPause.setBackgroundResource(R.mipmap.ic_pause_black_36dp);
        } else {
            mPlayPause.setBackgroundResource(R.mipmap.ic_play_arrow_black_36dp);
        }
    }

    private void init() {

        //播放进度监 ，使用静态变量时别忘了Service里面还有个进度条刷新
        audioSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                startSeekBarPlayService(null, seekBar.getProgress(), ConstantValue.OPTION_UPDATE_PROGESS);
            }
        });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        //注销事件接受
        EventBus.getDefault().unregister(this);
    }
}
