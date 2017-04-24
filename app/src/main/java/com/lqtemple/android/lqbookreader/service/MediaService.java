package com.lqtemple.android.lqbookreader.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;

import com.lqtemple.android.lqbookreader.R;
import com.lqtemple.android.lqbookreader.activity.FullScreenPlayerActivity;
import com.lqtemple.android.lqbookreader.model.MusicMedia;
import com.lqtemple.android.lqbookreader.util.CommonUtils;
import com.lqtemple.android.lqbookreader.util.ConstantValue;
import com.lqtemple.android.lqbookreader.util.HandlerManager;
import com.lqtemple.android.lqbookreader.util.LqBookConst;
import com.lqtemple.android.lqbookreader.util.MediaUtil;
import com.lqtemple.android.lqbookreader.util.PromptManager;

public class MediaService extends Service implements OnCompletionListener, OnSeekCompleteListener, OnErrorListener {
	private  final String TAG = "MediaService";
	private static final int NOTIFICATION_ID = 1; // 如果id设置为0,会导致不能设置为前台service
	public static Context mContext;
	// mediaplayer
	private static MediaPlayer player;
	// private static ProgressThread thread;
	private static ProgressTask task;
	private String file;
	private int postion = 0;
	public static int default_postion = 0;
	public static boolean isPlaying = false;
	public static final String TOGGLEPAUSE_ACTION = "com.lqtemple.android.lqbookreader.togglepause";
	public static final String NEXT_ACTION = "com.lqtemple.android.lqbookreader.next";
	public static final String STOP_ACTION = "com.lqtemple.android.lqbookreader.stop";


	private MusicMedia mMusicMedia;

	private Notification mNotification;
	private long mNotificationPostTime =0;
	private RemoteViews remoteViews;
	private int mServiceStartId = -1;
	private NotificationManager mNotificationManager;


	private static boolean  isAlive = true;

	// private Handler handler;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		mContext = getApplicationContext();
		mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		// Initialize the intent filter and each action
		final IntentFilter filter = new IntentFilter();
		filter.addAction(TOGGLEPAUSE_ACTION);
		filter.addAction(STOP_ACTION);
		filter.addAction(NEXT_ACTION);
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		// Attach the broadcast listener
		registerReceiver(mIntentReceiver, filter);

		super.onCreate();
		if (player == null) {
			player = new MediaPlayer();
			player.setOnSeekCompleteListener(this);
			player.setOnCompletionListener(this);
			player.setOnErrorListener(this);
		}
	}



	private final BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(final Context context, final Intent intent) {
			int  flag = intent.getIntExtra("FLAG" ,-1);
			Log.i(TAG, "flag=" + flag);

			switch (flag){


				case 1 :
					if(isPlaying){
						pause();
						remoteViews.setImageViewResource(R.id.iv_pause, R.drawable.note_btn_play);
					}else{
						player.start();
						isPlaying = true;
						remoteViews.setImageViewResource(R.id.iv_pause, R.drawable.note_btn_pause);

					}
					mNotificationManager.notify(NOTIFICATION_ID, mNotification);

					break;

				case 2 :
					MediaUtil.CURRENTPOS++;
					MusicMedia musicMedia = null;
					if(MediaUtil.CURRENTPOS>0 && MediaUtil.CURRENTPOS<MediaUtil.getInstacen().getSongList().size()){
						musicMedia = MediaUtil.getInstacen().getCurrent();

					}else{
						MediaUtil.CURRENTPOS = 0;
						musicMedia = MediaUtil.getInstacen().getCurrent();
					}
					if(musicMedia!=null){
						play(musicMedia.getUrl());
					}

					String text = TextUtils.isEmpty(musicMedia.getAlbum()) ? musicMedia.getArtist() : musicMedia.getArtist()  + " - " + musicMedia.getAlbum();
					remoteViews.setTextViewText(R.id.title, musicMedia.getTitle());
					remoteViews.setTextViewText(R.id.text, text);
					remoteViews.setImageViewResource(R.id.iv_pause, R.drawable.note_btn_pause);

					mNotificationManager.notify(NOTIFICATION_ID, mNotification);

					break;

				case 3 :
//					Log.i(TAG, "mServiceStartId=" + mServiceStartId);
//					isAlive = false;
//					task =null;
//					stop();
					MediaUtil.CURRENTPOS =-1;
					cancelNotification();
					stop();
					player = null;
					isAlive = false;
					task = null;
					break;

			}

		}
	};

	@Override
	public void onStart(Intent intent, int startId) {
		int option = intent.getIntExtra("option", LqBookConst.DEFAULT_PROGRESS);
		int progress = intent.getIntExtra("progress", LqBookConst.DEFAULT_PROGRESS);
		if (progress != LqBookConst.DEFAULT_PROGRESS) {
			this.postion = progress;
			// this.postion = progress * player.getDuration() / 100;
		}
		switch (option) {
			case ConstantValue.OPTION_PLAY:
				file = intent.getStringExtra("file");
				play(file);
				MediaUtil.PLAYSTATE = option;

				break;
			case ConstantValue.OPTION_PAUSE:
				postion = player.getCurrentPosition();
				pause();
				MediaUtil.PLAYSTATE = option;
				Log.i(TAG, "OPTION_PAUSE=" + ConstantValue.OPTION_PAUSE);
				break;
			case ConstantValue.OPTION_CONTINUE:
				Log.i(TAG, "OPTION_CONTINUE=" + ConstantValue.OPTION_CONTINUE);

				playerToPosiztion(postion);
				// if (MediaUtil.PLAYSTATE == ConstantValue.OPTION_PLAY)
				if (file == "" || file == null) {
					file = intent.getStringExtra("file");
					play(file);
				} else {
					if(player!=null){
						player.start();
						isPlaying = true;
					}
				}
				MediaUtil.PLAYSTATE = option;

				break;
			case ConstantValue.OPTION_UPDATE_PROGESS:
				playerToPosiztion(postion);
				break;
			case ConstantValue.OPTION_DEFAULT:
				playerToPosiztion(default_postion);
				break;
		}
	}


	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i(TAG, "onStartCommand isAlive=" + isAlive);
		isAlive = true;
		mServiceStartId = startId;
		mMusicMedia = (MusicMedia)intent.getSerializableExtra("music");
		Log.i(TAG, "mMusicMedia=" + mMusicMedia);

		if(mMusicMedia!=null){
			//开启前台service
			if (Build.VERSION.SDK_INT < 16) {
				mNotification = new Notification.Builder(this)
						.setContentTitle(mMusicMedia.getTitle()).setContentText(mMusicMedia.getArtist())
						.setSmallIcon(R.drawable.musicfile).getNotification();
			} else {
				Notification.Builder builder = new Notification.Builder(this);
				Intent startIntent = new Intent(this, FullScreenPlayerActivity.class);
				startIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				PendingIntent contentIntent = PendingIntent.getActivity(this, 0,startIntent, PendingIntent.FLAG_UPDATE_CURRENT);
				builder.setContentIntent(contentIntent);
				builder.setSmallIcon(R.drawable.musicfile);
				//        builder.setTicker("Foreground Service Start");
				builder.setContentTitle(mMusicMedia.getTitle());
				builder.setContentText(mMusicMedia.getArtist());
				mNotification = builder.build();
				mNotification = getNotification(mMusicMedia);
			}

			startForeground(NOTIFICATION_ID, mNotification);
		}


		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		unregisterReceiver(mIntentReceiver);
		task = null;
		stop();
		super.onDestroy();

	}

	private void play(String path) {

		if (player == null) {
			player = new MediaPlayer();
		}

		try {
			player.reset();
			player.setDataSource(path);
			player.prepare();
			player.start();
			isPlaying = true;
			/*
			 * Message msg = Message.obtain(); msg.what = ConstantValue.SEEKBAR_MAX; msg.arg1 = player.getDuration();
			 * 
			 * HandlerManager.getHandler().sendMessage(msg);
			 */
			/*
			 * if (thread == null) { thread = new ProgressThread(); thread.start(); }
			 */
			if (task == null) {
				task = new ProgressTask();
				task.execute();
			}

		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	private void pause() {
		if (player != null && player.isPlaying()) {
			player.pause();
			isPlaying =false;
		}
	}

	private void stop() {
		if (player != null) {
			player.stop();
			player.release();
			isPlaying =false;
		}
	}

	private void playerToPosiztion(int posiztion) {

		try {
			if(player!=null){
				if (posiztion > 0 && posiztion < player.getDuration()) {
					player.seekTo(posiztion);
				}
			}

		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onCompletion(MediaPlayer arg0) {
		Log.i(TAG, "onCompletion=" + arg0);
		HandlerManager.getHandler().sendEmptyMessage(ConstantValue.PLAY_END);
	}

	@Override
	public void onSeekComplete(MediaPlayer mp) {
		Log.i(TAG, "onSeekComplete=" + mp);
		if (player.isPlaying()) {
			player.start();
			isPlaying = true;
		}
	}



	private class ProgressTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			while (isAlive) {
//				Log.i(TAG, "isPlaying:" + player.isPlaying());
				SystemClock.sleep(1000);
				publishProgress();
			}
			return null;
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			try {
				if (player.isPlaying()) {
                    Message msg = Message.obtain();
                    msg.what = ConstantValue.SEEKBAR_CHANGE;
                    msg.arg1 = player.getCurrentPosition() + 1000;
                    msg.arg2 = player.getDuration();
                    default_postion = msg.arg1;
                    HandlerManager.getHandler().sendMessage(msg);
                    Log.i(TAG, "isPlaying:=" + isPlaying);
                    Log.i(TAG, "onProgressUpdate:arg1=" + msg.arg1);
    //				Log.i(TAG, "onProgressUpdate:arg2=" + msg.arg2);
					super.onProgressUpdate(values);

				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		PromptManager.showToast(getApplicationContext(), "亲，音乐文件加载出错了");
		return false;
	}



	private Notification getNotification(MusicMedia musicMedia) {
		final int PAUSE_FLAG = 0x1;
		final int NEXT_FLAG = 0x2;
		final int STOP_FLAG = 0x3;
		final String albumName = musicMedia.getAlbum();
		final String artistName = musicMedia.getArtist();

		remoteViews = new RemoteViews(this.getPackageName(), R.layout.notification);
		String text = TextUtils.isEmpty(albumName) ? artistName : artistName + " - " + albumName;
		remoteViews.setTextViewText(R.id.title, musicMedia.getTitle());
		remoteViews.setTextViewText(R.id.text, text);

		//此处action不能是一样的 如果一样的 接受的flag参数只是第一个设置的值
		Intent pauseIntent = new Intent(TOGGLEPAUSE_ACTION);
		pauseIntent.putExtra("FLAG", PAUSE_FLAG);
		PendingIntent pausePIntent = PendingIntent.getBroadcast(this, 0, pauseIntent, 0);

		remoteViews.setImageViewResource(R.id.iv_pause,  R.drawable.note_btn_pause);
		remoteViews.setOnClickPendingIntent(R.id.iv_pause, pausePIntent);

		Intent nextIntent = new Intent(NEXT_ACTION);
		nextIntent.putExtra("FLAG", NEXT_FLAG);
		PendingIntent nextPIntent = PendingIntent.getBroadcast(this, 0, nextIntent, 0);
		remoteViews.setOnClickPendingIntent(R.id.iv_next, nextPIntent);

		Intent preIntent = new Intent(STOP_ACTION);
		preIntent.putExtra("FLAG", STOP_FLAG);
		PendingIntent prePIntent = PendingIntent.getBroadcast(this, 0, preIntent, 0);
		remoteViews.setOnClickPendingIntent(R.id.iv_stop, prePIntent);

		//        PendingIntent pendingIntent = PendingIntent.getActivity(this.getApplicationContext(), 0,
		//                new Intent(this.getApplicationContext(), PlayingActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
		final Intent nowPlayingIntent = new Intent();
		//nowPlayingIntent.setAction("com.wm.remusic.LAUNCH_NOW_PLAYING_ACTION");
		nowPlayingIntent.setComponent(new ComponentName("com.wm.remusic","com.wm.remusic.activity.PlayingActivity"));
		nowPlayingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		PendingIntent clickIntent = PendingIntent.getBroadcast(this, 0, nowPlayingIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		PendingIntent click = PendingIntent.getActivity(this,0,nowPlayingIntent,PendingIntent.FLAG_UPDATE_CURRENT);

		if (mNotificationPostTime == 0) {
			mNotificationPostTime = System.currentTimeMillis();
		}

		if(mNotification == null){
			NotificationCompat.Builder builder = new NotificationCompat.Builder(this).setContent(remoteViews)
					.setSmallIcon(R.drawable.ic_notification)
					.setContentIntent(click)
					.setWhen(mNotificationPostTime);
			if (CommonUtils.isJellyBeanMR1()) {
				builder.setShowWhen(false);
			}
			mNotification = builder.build();
		}else {
			mNotification.contentView = remoteViews;
		}

		return mNotification;
	}



	private void cancelNotification() {
		stopForeground(true);
		//mNotificationManager.cancel(hashCode());
		mNotificationManager.cancel(NOTIFICATION_ID);
		mNotificationPostTime = 0;
	}





}
