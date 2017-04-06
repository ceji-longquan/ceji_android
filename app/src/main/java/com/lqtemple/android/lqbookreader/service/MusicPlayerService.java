package com.lqtemple.android.lqbookreader.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import com.lqtemple.android.lqbookreader.R;
import com.lqtemple.android.lqbookreader.activity.MusicPlayerActivity;
import com.lqtemple.android.lqbookreader.model.MusicMedia;
import com.lqtemple.android.lqbookreader.util.LqBookConst;

import java.io.IOException;

public class MusicPlayerService extends Service {//implements Runnable {
    public static final String EXTRA_CONNECTED_CAST = "com.example.android.uamp.CAST_NAME";
    public static final String PLAY_ACTION = "com.lqtemple.android.lqbookreader.service.MusicPlayerService.player";

    private static final String TAG = "MusicPlayerService";
    private static final int NOTIFICATION_ID = 1; // 如果id设置为0,会导致不能设置为前台service
    public static MediaPlayer mediaPlayer = null;
    private String url = null;
    private String MSG = null;

    private MusicMedia mMusicMedia;
    private static int curposition;//第几首音乐
    private musicBinder musicbinder = null;
    private int currentPosition = 0;// 设置默认进度条当前位置
    public static boolean isplay = false;//音乐是否在播放
    public static boolean isStartService = false;//音乐是否在播放

    public MusicPlayerService() {
        Log.i(TAG,"MusicPlayerService......1");
        musicbinder = new musicBinder();
    }

    //通过bind 返回一个IBinder对象，然后改对象调用里面的方法实现参数的传递
    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG,"onBind......");
       return musicbinder;
    }
    /**
     * 自定义的 Binder对象
     */
    public class musicBinder extends Binder {
        public MusicPlayerService getPlayInfo(){
            return MusicPlayerService.this;
        }
    }
    //得到当前播放位置
    public  int getCurrentPosition(){

        if(mediaPlayer != null){
            int total = mediaPlayer.getDuration();// 总时长
            if( currentPosition < total){
                currentPosition = mediaPlayer.getCurrentPosition();
            }
        }
        return currentPosition;
    }
    //得到当前播放位置
    public  int getDuration(){
        return mediaPlayer.getDuration();// 总时长
    }

    //得到 mediaPlayer
    public MediaPlayer getMediaPlayer(){
//        if(mediaPlayer != null){
//            return mediaPlayer;
//        }
        return mediaPlayer;
    }
    //得到 当前播放第几个音乐
    public int getCurposition(){
        return curposition;
    }
    @Override
    public void onCreate() {
        Log.i(TAG, "onCreate......2");
        super.onCreate();
        if (mediaPlayer == null) {
           /* mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;*/
            mediaPlayer = new MediaPlayer();
        }
         // 监听播放是否完成
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                //我目前也不知道该干嘛

            }
        });



    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand......3");
        // /storage/emulated/0/Music/Download/Selena Gomez - Revival/Hands to Myself.mp3
        if(intent != null){
            MSG = intent.getStringExtra(LqBookConst.START_PLAY_KEY);
            if(MSG.equals("0")){
                mMusicMedia = (MusicMedia)intent.getSerializableExtra("MusicMedia");
                url = mMusicMedia.getUrl();
                curposition = intent.getIntExtra("curposition",0);
                Log.i(TAG, "mMusicMedia =Title" + mMusicMedia.getTitle());
                Log.i(TAG, "mMusicMedia Artist=" + mMusicMedia.getArtist());
                Log.i(TAG, "mMusicMedia Album =" + mMusicMedia.getAlbum());
                palyer();
            }else if(MSG.equals("1")){
                mediaPlayer.pause();
            }else if(MSG.equals("2")){
                mediaPlayer.start();
            }

//            String name = getString(R.string.current_play) + url.substring(url.lastIndexOf("/") + 1 , url.lastIndexOf("."));
//            Log.i(TAG,name);
//        //开启前台service
            Notification notification = null;
            if (Build.VERSION.SDK_INT < 16) {
                notification = new Notification.Builder(this)
                        .setContentTitle(mMusicMedia.getTitle()).setContentText(mMusicMedia.getArtist())
                        .setSmallIcon(R.drawable.musicfile).getNotification();
            } else {
                Notification.Builder builder = new Notification.Builder(this);
                Intent startIntent = new Intent(this, MusicPlayerActivity.class);
                startIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                PendingIntent contentIntent = PendingIntent.getActivity(this, 0,startIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                builder.setContentIntent(contentIntent);
                builder.setSmallIcon(R.drawable.musicfile);
//        builder.setTicker("Foreground Service Start");
                builder.setContentTitle(mMusicMedia.getTitle());
                builder.setContentText(mMusicMedia.getArtist());
                notification = builder.build();
            }

            startForeground(NOTIFICATION_ID, notification);
        }

        return super.onStartCommand(intent, flags, startId);
    }


    private void palyer() {
        Log.i(TAG,"palyer......");
        //如果正在播放，先停止再播放新的
       /* if(mediaPlayer.isPlaying()){
            Log.i(TAG,"palyer......running....");
            // 暂停
            mediaPlayer.pause();
            mediaPlayer.reset();
        }*/
        //还有就是用户在暂停是点击其他的音乐，所以不管当前状态，都重置一下
        //下面这段代码可以实现简单的音乐播放
        try {
//            Log.i(TAG,"palyer......new....");
            mediaPlayer.reset();

            mediaPlayer.setDataSource(url);
            mediaPlayer.setLooping(true);
//            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.prepare();
            mediaPlayer.start();
            // 设置进度条最大值
//            MusicActivity.audioSeekBar.setMax(mediaPlayer.getDuration());
            //开启新线程
//            new Thread(this).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // 刷新进度条 ,时间

/*
    @Override
    public void run() {

        Log.i(TAG,Thread.currentThread().getName()+"......run...");

        int total = mediaPlayer.getDuration();// 总时长
        while (mediaPlayer != null && currentPosition < total) {
            try {
                Thread.sleep(1000);
                if (mediaPlayer != null) {
                    currentPosition = mediaPlayer.getCurrentPosition();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
//            MusicActivity.audioSeekBar.setProgress(CurrentPosition);

        }


    }
*/


    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(TAG,"onUnbind......");
        return super.onUnbind(intent);

    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        Log.i(TAG, "onRebind......");
    }

    @Override
    public void onDestroy() {
        Log.i(TAG,"onDestroy......");
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        //关闭线程
        Thread.currentThread().interrupt();
        stopForeground(true);
    }
    public String toTime(int time){
        time /= 1000;
        int minute = time / 60;
        int hour = minute / 60;
        int second = time % 60;
        minute %= 60;
        return String.format("%02d:%02d", minute, second);
    }


}
