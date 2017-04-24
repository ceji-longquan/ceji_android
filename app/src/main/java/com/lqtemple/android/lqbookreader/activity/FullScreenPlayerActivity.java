package com.lqtemple.android.lqbookreader.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.lqtemple.android.lqbookreader.R;
import com.lqtemple.android.lqbookreader.annotation.InjectUtils;
import com.lqtemple.android.lqbookreader.annotation.InjectView;
import com.lqtemple.android.lqbookreader.event.UpdateListColorEvent;
import com.lqtemple.android.lqbookreader.model.MusicMedia;
import com.lqtemple.android.lqbookreader.service.MediaService;
import com.lqtemple.android.lqbookreader.util.ConstantValue;
import com.lqtemple.android.lqbookreader.util.HandlerManager;
import com.lqtemple.android.lqbookreader.util.LqBookConst;
import com.lqtemple.android.lqbookreader.util.MediaUtil;
import com.lqtemple.android.lqbookreader.view.PlayerSeekBar;

import org.greenrobot.eventbus.EventBus;


/**
 * Created by Administrator on 2017/3/30 0030.
 */

public class FullScreenPlayerActivity extends BaseActivity{
    protected  final String TAG = "FullScreenPlayerActivity";
    @InjectView(R.id.playing_play)
    private ImageView mPalying;

    @InjectView(R.id.playing_pre)
    private ImageView mPlayPrev;

    @InjectView(R.id.playing_next)
    private ImageView mPlayNext;

    @InjectView(R.id.music_duration_played)
    private TextView music_duration_played;

    @InjectView(R.id.play_seek)
    private PlayerSeekBar play_seek;

    @InjectView(R.id.music_duration)
    private TextView music_duration;

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case ConstantValue.PLAY_END:
                    // 播放完成
                    // 播放模式：单曲循环、顺序播放、循环播放、随机播放
                    // 单曲循环:记录当前播放位置
                    // 顺序播放:当前播放位置上＋1
                    // 循环播放:判断如果，增加的结果大于songList的大小，修改播放位置为零
                    // 随机播放:Random.nextInt() songList.size();
                    Log.i(TAG,"MediaUtil.PLAY_END =" + ConstantValue.PLAY_END);
                    MediaUtil.CURRENTPOS++;

                    if (MediaUtil.CURRENTPOS < MediaUtil.getInstacen()
                            .getSongList().size()) {
                        MusicMedia music = MediaUtil.getInstacen().getSongList()
                                .get(MediaUtil.CURRENTPOS);
                        startSeekBarPlayService(music,LqBookConst.DEFAULT_PROGRESS, ConstantValue.OPTION_PLAY);

                    }else{
                        MediaUtil.CURRENTPOS = 0;
                        MusicMedia music = MediaUtil.getInstacen().getSongList()
                                .get(MediaUtil.CURRENTPOS);
                        startSeekBarPlayService(music,LqBookConst.DEFAULT_PROGRESS, ConstantValue.OPTION_PLAY);
                    }
                    MediaUtil.LAST_POS =  MediaUtil.CURRENTPOS;
                    EventBus.getDefault().post(new UpdateListColorEvent(MediaUtil.CURRENTPOS));
                    break;
                case ConstantValue.SEEKBAR_CHANGE:
                    // 根据播放时长改变SeekBar
                    int  arg1 = msg.arg1;
                    int  arg2 = msg.arg2
                            ;
//                    Log.i(TAG,"MediaUtil.arg2 =" + arg2);
                    music_duration_played.setText(LqBookConst.toTime(arg1));
                    play_seek.setMax(arg2);
                    play_seek.setProgress(arg1);

                    music_duration.setText(LqBookConst.toTime(arg2));

                    break;
            }

        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playing);
        InjectUtils.bind(this);
        HandlerManager.putHandler(handler);


        init();

    }

    private void init() {
        play_seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                startSeekBarPlayService(null, seekBar.getProgress(),ConstantValue.OPTION_UPDATE_PROGESS);

            }
        });
        int position = getIntent().getIntExtra("position",-1);
        MusicMedia music = (MusicMedia) getIntent().getSerializableExtra("music");
        Log.i(TAG,"MediaUtil.PLAYSTATE =" + MediaUtil.PLAYSTATE);
        Log.i(TAG,"MediaUtil.LAST_POS =" + MediaUtil.LAST_POS);
        Log.i(TAG,"position =" + position);
        if(MediaUtil.LAST_POS == position ){
            startSeekBarPlayService(music, LqBookConst.DEFAULT_PROGRESS,ConstantValue.OPTION_DEFAULT);
            music_duration_played.setText(LqBookConst.toTime(MediaService.default_postion));
            play_seek.setMax(music.getDuration());
            play_seek.setProgress(MediaService.default_postion);
            music_duration.setText(LqBookConst.toTime(music.getDuration()));
            if(MediaService.isPlaying){
                mPalying.setImageResource(R.drawable.play_btn_pause);
            }else {
                mPalying.setImageResource(R.drawable.play_btn_play);
            }
        }else{
            if(music!=null){
                startSeekBarPlayService(music, LqBookConst.DEFAULT_PROGRESS, ConstantValue.OPTION_PLAY);
                mPalying.setImageResource(R.drawable.play_btn_pause);
                MediaUtil.LAST_POS = position;
                MediaService.isPlaying = true;
            }
        }

        setLitener();
    }


//    private void startPlayService(MusicMedia music, int option) {
//        Intent intent = new Intent(getApplicationContext(), MediaService.class);
//        if (music != null) {
//            intent.putExtra("file", music.getUrl());
//        }
//        intent.putExtra("option", option);
//        startService(intent);
//    }

    private void startSeekBarPlayService(MusicMedia music,int progress, int option) {
        Intent intent = new Intent(getApplicationContext(), MediaService.class);
        if (music != null) {
            intent.putExtra("music", music);
            intent.putExtra("file", music.getUrl());
        }
        intent.putExtra("option", option);
        intent.putExtra("progress", progress);
        startService(intent);
    }

    private void setLitener() {
        mPalying.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.i(TAG,"MediaUtil.PLAYSTATE =" + MediaUtil.PLAYSTATE);
                switch (MediaUtil.PLAYSTATE) {
                    case ConstantValue.OPTION_PLAY:
                    case ConstantValue.OPTION_CONTINUE:
                        startSeekBarPlayService(null, LqBookConst.DEFAULT_PROGRESS,ConstantValue.OPTION_PAUSE);
                        mPalying.setImageResource(R.drawable.play_btn_play);
                        break;
                    case ConstantValue.OPTION_PAUSE:
                        Log.i(TAG,"MediaUtil.CURRENTPOS =" + MediaUtil.CURRENTPOS);

                        if (MediaUtil.CURRENTPOS >= 0
                                && MediaUtil.CURRENTPOS < MediaUtil.getInstacen()
                                .getSongList().size()) {
                            startSeekBarPlayService(MediaUtil.getInstacen().getSongList()
                                            .get(MediaUtil.CURRENTPOS),LqBookConst.DEFAULT_PROGRESS,
                                    ConstantValue.OPTION_CONTINUE);
                            mPalying.setImageResource(R.drawable.play_btn_pause);
                        }
                        break;
                }
            }
        });

        mPlayNext.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // int temp=MediaUtil.CURRENTPOS;
                if (MediaUtil.getInstacen().getSongList().size() > MediaUtil.CURRENTPOS + 1) {
                    MediaUtil.CURRENTPOS++;
                    startSeekBarPlayService(
                            MediaUtil.getInstacen().getSongList()
                                    .get(MediaUtil.CURRENTPOS),LqBookConst.DEFAULT_PROGRESS,
                            ConstantValue.OPTION_PLAY);
                    mPalying.setImageResource(R.drawable.play_btn_pause);
                    MediaUtil.PLAYSTATE = ConstantValue.OPTION_PLAY;
                }

            }
        });
        mPlayPrev.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (MediaUtil.CURRENTPOS > 0) {
                    MediaUtil.CURRENTPOS--;
                    startSeekBarPlayService(
                            MediaUtil.getInstacen().getSongList()
                                    .get(MediaUtil.CURRENTPOS),LqBookConst.DEFAULT_PROGRESS,
                            ConstantValue.OPTION_PLAY);
                    mPalying.setImageResource(R.drawable.play_btn_pause);
                    MediaUtil.PLAYSTATE = ConstantValue.OPTION_PLAY;
                }

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG,"onResume =" + MediaService.isPlaying);



        if(MediaService.isPlaying){
            mPalying.setImageResource(R.drawable.play_btn_pause);
        }else {
            mPalying.setImageResource(R.drawable.play_btn_play);
        }
    }
}
